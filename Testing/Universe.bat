@echo off
rem @Author: Pontus Laestadius
rem @Since: 06-05-2017

start runDefaultTest.bat
cd ../
# git fetch
# git pull
start Testing\server.py
# start F.R.O.S.T\gradlew.bat
emulator -avd myandroid22