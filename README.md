# Lucy

A project for the Systems for Parallel Processing course at FMI.

The goal is to generate a png image of the fractal set of values of c
in the complex plane for which the orbit of 0 under iteration of the
quadratic
        map z_{n+1} = exp(cos(z_{n-1} c))
remains bounded.

## Installation

Download from https://github.com/angueln/lucy.

    $ git clone https://github.com/angueln/lucy.git

## Usage
### With a standalone jar.
    $ lein uberjar
    $ java -jar lucy-0.1.0-SNAPSHOT-standalone.jar [options]

### Via Leiningen
    $ lein run -r -1.0:1.0:-1.0:0.0 [options]

## Options

FIXME: listing of options this app accepts.

## Examples

   $ lein run -r -1.0:1.0:-1.0:0.0 -s "800x400" -o "a-pretty-fractal.png" -t 4

## License

Distributed under the Eclipse Public License version 1.0.
