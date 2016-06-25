(ns lucy.core
  (:gen-class))

(import 'org.apache.commons.math3.complex.Complex)

(defn iterator-map
  "The next number in the sequence a_n = exp(cos(c a_{n-1})), for some
  constant c."
  [c a]
  (.exp (.cos (.multiply a c))))

(defn bounded-sequence?
  "Checks if the orbit map-fun(map-fun( ... map-fun(0))) of 0 under
  iteration of map-fun *looks* bounded - i.e. does it reach Infinity
  after iteration-limit iterations."
  [map-fun iteration-limit]
  (let [orbit-prefix (take iteration-limit
                           (iterate map-fun (Complex. 0.0 0.0)))
        finite? #(not (or (.isInfinite %)
                          (.isNaN      %)))]
    (every? finite?
            (map #(.abs %)
                 orbit-prefix))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
