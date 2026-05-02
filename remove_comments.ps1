param(
    [string[]]$Files
)

$pattern = '("(?:\\.|[^"])*"|''(\\.|[^''])*''|/\*[\s\S]*?\*/|//.*)'
$marker = "___COMMENT_REMOVED___"

foreach ($file in $Files) {
    if (Test-Path $file) {
        $content = Get-Content -Raw $file
        
        # Replace comments with marker
        $markedContent = [regex]::Replace($content, $pattern, {
            param($match)
            $val = $match.Value
            if ($val.StartsWith('/')) {
                return $marker
            }
            return $val
        })
        
        $lines = $markedContent -split "\r?\n"
        $newLines = New-Object System.Collections.Generic.List[string]
        
        foreach ($line in $lines) {
            $cleanLine = $line.Replace($marker, "")
            
            if ([string]::IsNullOrWhiteSpace($cleanLine)) {
                if ($line.Contains($marker)) {
                    # Skip line that only had comments
                    continue
                } else {
                    # Keep original empty lines
                    $newLines.Add($cleanLine)
                }
            } else {
                # Keep code and trim trailing whitespace
                $newLines.Add($cleanLine.TrimEnd())
            }
        }
        
        $result = $newLines -join "`n"
        if ($content.EndsWith("`n")) {
            $result += "`n"
        }
        
        Set-Content -Path $file -Value $result -NoNewline -Encoding UTF8
    }
}
