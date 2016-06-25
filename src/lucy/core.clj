(ns lucy.core
  (:gen-class))

(import 'org.apache.commons.math3.complex.Complex
        'java.io.File
        'java.awt.Color
        'java.awt.image.BufferedImage
        'javax.imageio.ImageIO)

(defn exp-cos-x-c
  "The next number in the sequence a_n = exp(cos(c a_{n-1})), for some
  constant c."
  [c a]
  (.exp (.cos (.multiply a c))))

(defn iterations-to-bounded
  "Checks if the orbit map-fun(map-fun( ... map-fun(0))) of 0 under
  iteration of map-fun *looks* bounded - i.e. does it reach Infinity
  after iteration-limit iterations."
  [map-fun iteration-limit]
  (let [finite? #(not (or (.isInfinite %)
                          (.isNaN      %)))]
    (loop [point (Complex. 0.0 0.0)
           index 0]
      (if (= index iteration-limit)
        nil
        (if (finite? point)
          (recur (map-fun point) (inc index))
          index)))))

(defn draw-fractal
  "Returns cavas-width by canvas-height BufferedImage of fractal set
  described by iterator-map in real-range and imaginary-range.
  Boundedness determined for iteration-limit iterations."
  [iterator-map
   canvas-width
   canvas-height
   real-range
   imaginary-range
   iteration-limit]
  (let [image          (BufferedImage. canvas-width canvas-height
                                       BufferedImage/TYPE_3BYTE_BGR)
        pixel-to-point (fn [pixel canvas-size [lower-bound upper-bound]]
                         (+ lower-bound (* (/ pixel canvas-size)
                                           (- upper-bound lower-bound))))]
    (doseq [x (range canvas-width)
            y (range canvas-height)
            :let [c (Complex. (pixel-to-point x canvas-width  real-range)
                              (pixel-to-point y canvas-height imaginary-range))
                  iterator (partial iterator-map c)]]
      (println "Calculating pixel [ " x ", " y " ]")

      (if-let [iterations (iterations-to-bounded iterator iteration-limit)]
        (do
          (println "Found to be unbounded after " iterations " iterations.")
          (.setRGB image x y (+ 0xFFFF00 (* 255
                              (- 1 (/ iterations
                                      iteration-limit))))))
        (do
          (println "Bounded.")
          (.setRGB image x y 0x0000FF))))
    image))

(def output-file "fractal")
(def canvas-width 3000)
(def canvas-height 3000)
(def real-range [-2.0 2.0])
(def imaginary-range [-2.0 2.0])
(def iteration-limit 10)
(def num-threads 4)
(def output-file "fractal")

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (ImageIO/write (draw-fractal exp-cos-x-c
                               canvas-width
                               canvas-height
                               real-range
                               imaginary-range
                               iteration-limit)
                 "png"
                 (File. (str output-file ".png"))))
