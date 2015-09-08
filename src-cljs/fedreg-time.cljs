(ns fedreg-time
  (:require
      [cljs-time.core :as t]
      [cljs-time.local :as l]
      [cljs-time.format :as f]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; code to format time strings

(def federal-formatter (f/formatter "yyyy-MM-dd"))
(def us-formatter (f/formatter "MM-dd-yyyy"))

(defn my-time-string
  "output formated 'now' or 'now minus x weeks'"
  ([my-format]
    (f/unparse my-format (l/local-now)))
  ([my-format minus-weeks]
    (f/unparse my-format (t/minus (l/local-now) (t/weeks minus-weeks)))))

(defn federal-to-js
  [in-string]
  (if (clojure.string/blank? in-string)
    nil
    (f/parse federal-formatter in-string)))

(defn time-to-url 
  [my-time]

  (str "&conditions%5Bpublication_date%5D%5Bgte%5D="
       (f/unparse federal-formatter my-time))
  ) 

(defn one-week-ago
  []
  (time-to-url (t/minus (l/local-now) (t/weeks 1))))


