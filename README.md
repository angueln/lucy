# Lucy

<p align="center">
  <img src="https://raw.githubusercontent.com/angueln/lucy/master/header.png"
  alt="A region of the fractal"/>
</p>

A project for the Systems for Parallel Processing course at FMI.

The goal is to generate a png image of the fractal set of values of c
in the complex plane for which the orbit of 0 under iteration of the
quadratic map
```
z_{n+1} = exp(cos(c * z_{n-1}))
```
remains bounded.

There is also a
[design doc](https://docs.google.com/document/d/1w_aBWSc3Ac8_EZ7S_cPLk7X6HapUGdtNHkg4S0MU0HM)
in Bulgarian.

## Installation

Download from https://github.com/angueln/lucy.

    $ git clone https://github.com/angueln/lucy.git

## Usage

### With a Standalone Jar
    $ lein uberjar
    $ java -jar lucy-0.1.0-SNAPSHOT-standalone.jar [options]

### Via Leiningen
    $ lein run -r -1.0:1.0:-1.0:0.0 [options]

## Options

The following command line options are recognized:

* `-s` or `-size` - dimensions of the canvas
   *width-in-px*x*height-in-px*, `480x480` by default;

* `-r` or `-rect` - bounds of the area of the complex plane to be
     drawn in the format
     *min-real-value*:*max-real-value*:*min-imaginary-value*:*max-imaginary-value*,
     `-2.0:2.0:-2.0:2.0` by default;

* `-t` or `-tasks` - the number of threads to be used, `1` by default;

* `-o` or `-output` - the name of the output file, `zad17.png` by default;

* `-q` or `-quiet` - flag specifying that no output should be printed to stdout;

* `-m` or `-mandelbrot` - flag signalling to draw the mandelbrot set instead;

* `-i` or `-iterations` - the maximum number iterations to perform
  when calculating for a pixel, `20` by default.

## Examples

   $ lein run -r -1.0:1.0:-1.0:0.0 -s 800x400 -o a-pretty-fractal.png -t 4

## License

Distributed under the Eclipse Public License version 1.0.
