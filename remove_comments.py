import re
import os
import sys

def strip_comments(code):
    # Pattern matches:
    # 1. Double quoted strings: "(?:\\.|[^"])*"
    # 2. Single quoted characters: '(?:\\.|[^'])'
    # 3. Multi-line comments: /\*[\s\S]*?\*/
    # 4. Single-line comments: //.*
    pattern = r'("(?:\\.|[^"])*"|\'(?:\\.|[^\'])\'|/\*[\s\S]*?\*/|//.*)'
    
    regex = re.compile(pattern)
    
    marker = "___COMMENT_REMOVED___"
    
    def _marker_replacer(match):
        s = match.group(0)
        if s.startswith('/'):
            return marker
        else:
            return s

    marked_code = regex.sub(_marker_replacer, code)
    
    lines = marked_code.splitlines()
    new_lines = []
    
    for line in lines:
        clean_line = line.replace(marker, "")
        
        if not clean_line.strip():
            # Line is now whitespace or empty.
            if marker in line:
                # It contained a comment and now it's just whitespace/empty.
                # Skip it to "remove the empty line".
                continue
            else:
                # It was already empty or whitespace. Keep it.
                new_lines.append(clean_line)
        else:
            # Line has code. Keep it, but trim trailing whitespace 
            # (which might have been before the comment).
            new_lines.append(clean_line.rstrip())

    # Add a final newline if the original file had one and we didn't end with one
    result = "\n".join(new_lines)
    if code.endswith("\n") and not result.endswith("\n"):
        result += "\n"
    return result

def process_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        new_content = strip_comments(content)
        
        with open(filepath, 'w', encoding='utf-8', newline='\n') as f:
            f.write(new_content)
        # print(f"Processed: {filepath}")
    except Exception as e:
        print(f"Error processing {filepath}: {e}")

if __name__ == "__main__":
    for arg in sys.argv[1:]:
        if os.path.isfile(arg):
            process_file(arg)
