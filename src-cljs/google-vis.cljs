(ns google-vis)

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; interface with google visualization 

(defn draw-chart
"helper function for google visulization
  from:  https://github.com/fraburnham/google-charts"
  [columns vectors options chart
                  &{:keys [tooltip]
                    :or {tooltip false}}]
  (let [data (new js/google.visualization.DataTable)]
    (doall ;gotta keep the doall on maps. lazy sequence...
     (map (fn [[type name]]
            (.addColumn data type name)) columns))
    (if tooltip
      (.addColumn data (clj->js {:type "string" :role "tooltip"})))
    (.addRows data vectors)
    (.draw chart data options)))


