(ns lucy.core
  (:gen-class))

(require '[clojure.core.reducers :as r])

(import 'org.apache.commons.math3.complex.Complex
        'java.io.File
        'java.awt.Color
        'java.awt.image.BufferedImage
        'javax.imageio.ImageIO)

(defn exp-cos-x-c
  "Returns the iterative function generating the next number in the
  sequence a_n = exp(cos(c a_{n-1})), for some constant c."
  [c]
  (fn [a]
    (.exp (.cos (.multiply a c)))))

(defn iterations-to-unbounded
  "Checks if the orbit iterator(iterator( ... iterator(0))) of 0 under
  iteration of map-fun *looks* bounded - i.e. does it reach Infinity
  after iteration-limit iterations."
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
    0x00FF00))

(defn draw-fractal
  "Returns a cavas-width by canvas-height BufferedImage of fractal set
  described by iterator-map in real-range and imaginary-range.
  boundedness determined for iteration-limit iterations."
  [iterator-map
   canvas-width
   canvas-height
   real-range
   imaginary-range
   iteration-limit]
  (let [image (BufferedImage. canvas-width canvas-height
                              BufferedImage/TYPE_3BYTE_BGR)

        canvas-indices (vec (for [x (range canvas-width)
                                  y (range canvas-height)]
                              [x y]))

        pixel-to-complex (fn [[x y]]
                           (Complex. (pixel-coordinate-to-point
                                      x canvas-width real-range)
                                     (pixel-coordinate-to-point
                                      y canvas-height imaginary-range)))

        iterations-for-pixel (fn [pixel]
                               (iterations-to-unbounded
                                (iterator-map (pixel-to-complex pixel))
                                iteration-limit))

        canvas-colors (r/fold concat conj
                              (r/map (juxt identity
                                           (comp iterations-to-color
                                                 iterations-for-pixel))
                                     canvas-indices))]

    (doseq [[[x y] color] canvas-colors]
      (.setRGB image x y color))
    image))

(def output-file "fractal")
(def canvas-width 100)
(def canvas-height 100)
(def real-range [-1.0 1.0])
(def imaginary-range [-1.0 1.0])
(def iteration-limit 15)
(def output-file "fractal")

(defn -main [& args]
  (ImageIO/write (draw-fractal exp-cos-x-c
                               canvas-width
                               canvas-height
                               real-range
                               imaginary-range
                               iteration-limit)
                 "png"
                 (File. (str output-file ".png"))))
