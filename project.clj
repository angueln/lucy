(defproject lucy "0.1.0-SNAPSHOT"
  :description "Parallelized fractal generation."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.climate/claypoole "1.1.2"]
                 [org.apache.commons/commons-math3 "3.6.1"]
                 [commons-cli/commons-cli "1.3.1"]]
  :main ^:skip-aot lucy.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
