#!/bin/sh

# @Author: Pontus Laestadius
# @Since: 06-05-2017
# This is a background process script.
# It checks for any new git material and runs the univierse.bat
# if anything new is on the branch on git. checks once every 10 minutes.

git checkout AutomatedTesting

while true; do
# This might aswell be magic. 
# http://stackoverflow.com/questions/3258243/check-if-pull-needed-in-git
[ $(git rev-parse HEAD) = $(git ls-remote $(git rev-parse --abbrev-ref @{u} | \
	sed 's/\// /g') | cut -f1) ] && echo "0" || echo "5"
	
	if [ $? -eq '5' ];
		then
			echo running test
			start Universe.bat
		fi
	if [ $? -eq '0' ];
		then
			echo up to date
		fi
	
	sleep 5s
done