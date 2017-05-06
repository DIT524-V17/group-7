#!/bin/sh

git checkout AutomatedTesting

while true; do
# This might aswell be magic. 
# http://stackoverflow.com/questions/3258243/check-if-pull-needed-in-git
	[ $(git rev-parse HEAD) = $(git ls-remote $(git rev-parse --abbrev-ref @{u} | \
	sed 's/\// /g') | cut -f1) ] && echo up to date || echo start Universe.bat
	sleep 3s
done