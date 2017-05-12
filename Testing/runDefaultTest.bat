@echo off
rem @Author: Pontus Laestadius
rem @Since: 06-05-2017

cd c:\EyeAutomate
java -Xmx512m -jar EyeAutomate.jar "sensitivity=70" scripts\generated.txt
cd C:\Users\pontu\IdeaProjects\group-7\Testing
start compare.py
start report.py
timeout 3
taskkill /IM python.exe
taskkill /IM cmd.exe