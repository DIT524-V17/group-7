cd c:\EyeAutomate
java -Xmx512m -jar EyeAutomate.jar scripts\generated.txt
cd C:\Users\pontu\IdeaProjects\group-7\Testing
start compare.py
start report.py
taskkill /IM python.exe
taskkill /IM cmd.exe