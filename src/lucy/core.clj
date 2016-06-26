(require '[clojure.core.reducers :as r])

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

(defn iterations-to-unbounded
  "Checks if the orbit map-fun(map-fun( ... map-fun(0))) of 0 under
  iteration of map-fun *looks* bounded - i.e. does it reach Infinity
  after iteration-limit iterations."
  [map-fun iteration-limit]
  (let [finite? #(not (or (.isInfinite %)
                          (.isNaN      %)))]
    (loop [point     (Complex. 0.0 0.0)
           iteration 0]
      (if (= iteration iteration-limit)
        nil
        (if (finite? (.abs point))
          (recur (map-fun point) (inc iteration))
          iteration)))))

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

        pixel-index-to-point (fn [pixel canvas-size [lower-bound upper-bound]]
                               (+ lower-bound (* (/ pixel canvas-size)
                                                 (- upper-bound lower-bound))))

        pixel-to-complex (fn [[x y]]
                           (Complex. (pixel-index-to-point x
                                                           canvas-width
                                                           real-range)
                                     (pixel-index-to-point y
                                                           canvas-height
                                                           imaginary-range)))

        iterations-for-pixel (fn [pixel]
                               (println "Calculating pixel " pixel)
                               (iterations-to-unbounded
                                (partial iterator-map (pixel-to-complex pixel))
                                iteration-limit))

        color-for-pixel (fn [pixel]
                          (if-let [iterations (iterations-for-pixel pixel)]
                            (int (* 256
                                    (- 1 (/ iterations
                                            iteration-limit))))
                            0x00FF00))

        canvas-colors (map #(vector % (color-for-pixel %)) canvas-indices)]

    (doseq [[[x y] color] canvas-colors]
      (.setRGB image x y color))
    image))

(def output-file "fractal")
(def canvas-width 600)
(def canvas-height 600)
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
