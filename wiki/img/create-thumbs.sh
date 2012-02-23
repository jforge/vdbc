#!/bin/sh

rm ./thumbs/*.png

for i in *.png
do
  echo "processing $i"
  convert -thumbnail 200 $i ./thumbs/$i
done

