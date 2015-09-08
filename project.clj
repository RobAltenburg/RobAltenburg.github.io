(defproject fedreg-search "0.2.0"
  :description "searching the federal register"
  :source-paths ["src-clj"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2411"]
                 [com.andrewmcveigh/cljs-time "0.3.13"]]

  :plugins [[lein-cljsbuild "1.1.0"]]
  :cljsbuild {
    :builds [{:source-paths ["src-cljs"]
              :compiler {:output-to "resources/public/js/fedreg-search.js"
                         :optimizations :whitespace
                         :pretty-print true}}]}
)
