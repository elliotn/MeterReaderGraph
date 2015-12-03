#!/bin/bash

if [ $# -lt 1 ]; then
   echo "usage: $0 layoutFile.xml"
   exit -1
fi

SRC=$1


xpath -q -e "//*[@android:id]" $SRC | \
  sed -n -e '/android:id/s/^ *<\([.0-9a-zA-Z]*\) .*android:id="@+id\/\([a-zA-Z0-9]\)\([_a-zA-Z0-9]*\)" .*/@Bind(R.id.\2\3) \1 m\u\2\3;/p' | sort -u
