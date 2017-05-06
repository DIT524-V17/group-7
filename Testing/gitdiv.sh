#!/bin/sh

git fetch
LINES=$? | grep wc -l
if [ 1 -eq 1 ]
then
	echo "if"
fi
echo "Done."
sleep 5s