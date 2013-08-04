#!/bin/sh

rm ./thumbs/*.png

for i in *.png
do
  echo "processing $i"
  convert -thumbnail 250 $i ./thumbs/$i
done

