require 'rubygems'
require 'activesupport'

class FinishedProcessor
  def process_line(line)
    self
  end
end

class CommentProcessor 
  def initialize(parent, line)
    @parent=parent
    process_line(line)
  end
  
  def process_line(line)
    if line =~ /.*\/\*\*(.*)/
      @parent.write("<p class='commentary'>")
      @parent.write($1)
      self
    elsif line =~ /.*\* (.*)/   
      @parent.write("#{markup($1)} ")
      self
    elsif line =~ /.*\*\/(.*)/   
      @parent.write($1)
      @parent.write("</p>")
      @parent.comment_block_finished
    else
      puts "Do not know how to process '#{line}'"
    end
  end
  
  def markup(line)
    ['field','method','class','interface','param','code','domaincode', 'package'].each do |m|
      line=replace_with_span(line,m)
    end
    line
  end

  def replace_with_span(line, markup)
    some_possible_changes_remain=true
    while(some_possible_changes_remain)
      if line =~ /<#{markup}>(.*?)<\/#{markup}>/
        line=line.sub(/<#{markup}>(.*?)<\/#{markup}>/,"<span class='#{markup}'>#{$1}</span>")
      else
        some_possible_changes_remain=false
      end
    end
    line
  end
end

class WriteHtmlOutputFile
  def initialize(name)
    @name=name
    @menu_lines=[]
    @body_lines=[]
    @processing_code_block=false
    @found_first_line_with_content_aleady=false
    @amount_of_indent_to_remove=0
  end

  def write_header(file)
    write_to_file(file,'<link rel="stylesheet" href="../style.css" type="text/css">')
    write_to_file(file,"<div id='main'>")
    write_to_file(file,"<h2>#{@name}</h2>")
  end

  def finish
    file=File.open("#{@name}.html", "w")

    write_header(file)
    write_menu(file)
    write_body(file)
    write_footer(file)

    file.close
  end

  def write_menu(file)
    write_to_file(file,"<ul>")
    @menu_lines.each do |line|
      write_to_file(file,"<li><a href=\"##{line}\">#{line}</a></li>")
    end
    write_to_file(file,"</ul>")
  end

  def write_body(file)
    @body_lines.each do |line|
      write_to_file(file,line)
    end
  end

  def write_footer(file)
    write_to_file(file,"</div id='main'>")
  end

  def write(line)
    @body_lines << line
  end
  
  def process_line(line)
    if line =~ /.*\/\*\*.*/
      if(@processing_code_block) 
        @processing_code_block=false
        @found_first_line_with_content_aleady=false
        write("</pre></p>")        
      end
      CommentProcessor.new(self, line)
    elsif line=~ /\/\*END\*\//
      finish
      FinishedProcessor.new
    else
      if(!ignore?(line))
        @processing_code_block=true
        if(is_section_header?(line))
          write_section_header(line)
        else
          if(first_line_with_content?(line))
            store_amount_of_indent_to_remove(line)
          @found_first_line_with_content_aleady=true
            write("<p><pre class='code'>")
          end
          write(markup(remove_indent(line)))
        end
      end
      self
    end
  end
  
  def is_section_header?(line)
    return line =~ /public static class Section_.*/
  end
  
  def write_section_header(line)
    line =~ /public static class Section_(.*) \{/
    @menu_lines << $1
    write("<a name='#{$1}'/>")
    write("<div class='section'>#{$1}</div>")
  end
  
  def remove_indent(line)
    if(line.length > @amount_of_indent_to_remove)
      return line[@amount_of_indent_to_remove, line.length]
    end
    line
  end
  
  def store_amount_of_indent_to_remove(line)
    @amount_of_indent_to_remove=line.size - line.lstrip.size
  end
  
  def first_line_with_content?(line)
    !@found_first_line_with_content_aleady && line.chomp.strip.size > 0
  end
  
  def markup(line)
    line=line.sub("public static class","public class")
    line.gsub("<","&lt;").gsub(">","&gt;")
  end
  
  def write_to_file(file,line)
    if(!ignore?(line))
      file << line
    end
  end
  
  def ignore?(line)
    line.include?("//ignore") || line.include?("// ignore")
  end
  
  def comment_block_finished
    self
  end
end

class WaitForOpeningClassProcessor
  def process_line(line)
    if line =~ /public class (\w+) .*/
      WriteHtmlOutputFile.new($1)
    else
      self
    end
  end
end

class ToCodeHtml
  def initialize
    @line_processor=WaitForOpeningClassProcessor.new    
  end

  def process(stream)
    stream.each do |line|
      @line_processor=@line_processor.process_line(line)
    end
  end
end

if ARGV.size == 1
   f=File.open(ARGV[0])
   ToCodeHtml.new.process(f)   
else
    ToCodeHtml.new.process(STDIN)
end
