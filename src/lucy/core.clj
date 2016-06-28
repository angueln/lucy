(ns lucy.core
  (:gen-class))

(require '[com.climate.claypoole :as cp])

(import 'org.apache.commons.math3.complex.Complex
        'java.io.File
        'java.awt.Color
        'java.awt.image.BufferedImage
        'javax.imageio.ImageIO
        'org.apache.commons.cli.DefaultParser
        'org.apache.commons.cli.Options)


(defn x-squared-plus-c
  "Returns the iterative function generating the next number in the
  sequence a_n = a^2 + c, for some constant c. This is the function
  for the Mandelbrot set."
  [c]
  (fn [a]
    (.add (.multiply a a) c)))

(defn exp-cos-x-c
  "Returns the iterative function generating the next number in the
  sequence a_n = exp(cos(c a_{n-1})), for some constant c."
  [c]
  (fn [a]
    (.exp (.cos (.multiply a c)))))

(defn iterations-to-unbounded
  "Checks if the orbit iterator(iterator( ... iterator(0))) of 0 under
  iteration of map-fun *looks* bounded - i.e. does it reach Infinity
  within iteration-limit iterations. If no returns nil, otherwise
  returns the number of the iteration where it does."
  [iterator iteration-limit]
  (let [finite? #(not (or (.isInfinite %)
                          (.isNaN      %)))]
    (loop [point     (Complex. 0.0 0.0)
           iteration 0]
      (if (= iteration iteration-limit)
        nil
        (if (finite? (.abs point))
          (recur (iterator point) (inc iteration))
          iteration)))))

(defn pixel-coordinate-to-point
  "Converts a pixel coordinate to a real number in the range
  lower-bound to upper-bound."
  [pixel canvas-size [lower-bound upper-bound]]
  (+ lower-bound (* (/ pixel canvas-size)
                    (- upper-bound lower-bound))))


(defn iterations-to-color
  "Converts a number of iterations or nil value to a RGB color between
  0x000000 to 0xFFFFFF. Nils are green and numbers are a gradient of blue."
  [iterations]
  (if iterations
    (int (* 256
            (- 1 (java.lang.Math/pow 0.75 iterations))))
    0xFFFFFF))

(defn draw-fractal
  "Returns a cavas-width by canvas-height BufferedImage of fractal set
  described by iterator-map in real-range and imaginary-range.
  boundedness determined for iteration-limit iterations."
  [iterator-map
   [canvas-width canvas-height]
   real-range
   imaginary-range
   iteration-limit
   num-threads]
  (let [image (BufferedImage. canvas-width canvas-height
                              BufferedImage/TYPE_3BYTE_BGR)

        canvas-colors (cp/pfor num-threads
                               [x (range canvas-width)
                                y (range canvas-height)
                                :let [c (Complex. (pixel-coordinate-to-point
                                                  x canvas-width real-range)
                                                 (pixel-coordinate-to-point
                                                  y canvas-height
                                                  imaginary-range))]]
                               [[x y] (iterations-to-color
                                       (iterations-to-unbounded
                                        (iterator-map c) iteration-limit))])]

    (doseq [[[x y] color] canvas-colors]
      (.setRGB image x y color))
    image))

(defn -main [& args]
  (let [options (Options.)
        parser (DefaultParser.)]
    (.addOption options "s" "size" true "Canvas width and height.")
    (.addOption options "r" "rect" true
                "Region in the complex plane to be displayed.")
    (.addOption options "t" "tasks" true "Number of threads to use.")
    (.addOption options "o" "output" true "Filename for output.")
    (.addOption options "q" "quiet" false
                "Don't print messages about threads.")
    (.addOption options "m" "mandelbrot" false
                "Generate the Mandelbrot set instead.")
    (.addOption options "i" "iterations" true
                "Set max number of iterations.")

    (let
        [args (.parse parser options (into-array (conj args "")))
         arg #(.getOptionValue args %)
         canvas-dimensions (map #(Integer/parseInt %)
                                (clojure.string/split (or (arg "s") "480x480")
                                                      #"x"))
         [r1 r2 i1 i2]     (map #(Double/parseDouble %)
                                (clojure.string/split (or (arg "r")
                                                          "-2.0:2.0:-2.0:2.0")
                                                      #":"))
         real-range        [r1 r2]
         imaginary-range   [i1 i2]
         num-threads       (Integer/parseInt (or (arg "t") "1"))
         print-messages    (not (.hasOption args "q"))
         iteration-limit   (Integer/parseInt (or (arg "i") "20"))
         output-file       (or (arg "o") "zad17.png")]
      (when print-messages
        (println "Running with pool of" num-threads "threads."))

      (if print-messages
        (time (ImageIO/write (draw-fractal (if (.hasOption args "m")
                                             x-squared-plus-c
                                             exp-cos-x-c)
                                           canvas-dimensions
                                           real-range imaginary-range
                                           iteration-limit
                                           num-threads)
                             "png" (File. output-file)))
        (ImageIO/write (draw-fractal exp-cos-x-c
                                     canvas-dimensions
                                     real-range imaginary-range
                                     iteration-limit
                                     num-threads)
                       "png" (File. output-file)))))
  (shutdown-agents))
