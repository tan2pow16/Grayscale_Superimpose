# Grayscale Superimpose
Copyright (c) 2020, tan2pow16, All rights reserved.

## Introduction
This project is inspired by the following video:

https://www.youtube.com/watch?v=LeinMHbYbJI

The video features a couple of grayscale images that give out different apperances when overlayed on white and black backgrounds. However, the examples provided in the video show ghost images with black background, which I found could be improved using simple math manipulation.

This repo does not come with precompiled binaries. It just contains a single Java file and requires no 3rd party libraries. Jre 8 is required.

## Implementation details
Please refer to [`details.pdf`](https://github.com/tan2pow16/Grayscale_Superimpose/blob/main/details.pdf) in this repo. The RGB to grayscale implementation in use is linear luminance CIE 1931 colorspace.

## Usage
`java.exe -jar compiled.jar --in1 <input_filepath_white_bg> --in2 <input_filepath_black_bg> --out <output_filepath> --res <width>x<height>`

## Example
`java.exe -jar compiled.jar --in1 D:\Image1.png --in2 D:\Image2.png --out D:\Test.png --res 1366x768`

Combine `D:\Image1.png` (which will shows with white background) and `D:\Image2.png` (which will shows with black background) into `D:\Test.png` with resolution of 1366 x 768 pixels. (The `--res` flag is optional if two input images share identical resolution.)
