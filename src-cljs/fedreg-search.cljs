(ns fedreg-search 
  (:import [goog.net Jsonp])
  (:require
      [google-vis :as gv]
      [fedreg-time :as ft]))

(.load js/google "visualization" "1" (clj->js {:packages ["table"]})) ;macro or function
(def my-url "https://www.federalregister.gov/api/v1/articles.json?per_page=500&order=relevance&fields%5B%5D=action&fields%5B%5D=agency_names&fields%5B%5D=dates&fields%5B%5D=docket_id&fields%5B%5D=publication_date&fields%5B%5D=title&fields%5B%5D=topics&fields%5B%5D=type&fields%5B%5D=comments_close_on&fields%5B%5D=html_url")

(defn agency-url-string
  "returns a string with agencies in url format"
  []
  (let [my-agencies '("environmental-protection-agency"
                "nuclear-regulatory-commission"
                "mine-safety-and-health-administration"
                "federal-energy-regulatory-commission"
                "engineers-corps"
                "surface-mining-reclamation-and-enforcement-office"
                "energy-department")]
    (clojure.string/join (map (fn [instr] (str "&conditions%5Bagencies%5D%5B%5D=" instr)) my-agencies))))

(defn get-agency-abbr
  "sort through all the agency names and substitute abbreviations"
  [in-vector out-str]
  (if (empty? in-vector)
    out-str
   (let
      [agency-to-abbr
       {"Environmental Protection Agency" "EPA"
       "Nuclear Regulatory Commission" "NRC"
       "Mine Safety and Health Administration" "MSHA"
       "Federal Energy Regulatory Commission" "FERC"
       "Engineers Corps" "Army Corps"
       "Surface Mining Reclamation and Enforcement Office" "OSM"
       "Energy Department" "DOE"}]
    
      (if-let [agency (get agency-to-abbr (first in-vector))]
        (recur (rest in-vector) (str out-str " " agency))
        (recur (rest in-vector) out-str)))))

;; write the initial HTML
;; elements we will set at runtime
(def data-element (.getElementById js/document "datatable"))
(def dates-element (.getElementById js/document "dates"))


(set! data-element.innerHTML "Data loading...<br>  Please wait.")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; interface with google visualization 

(defn build-chart [in-vector]
 (gv/draw-chart
  [["string" "Title"]
   ["string" "Action"]
   ["string" "Agency"]
   ["string" "Docket ID"]
   ["date" "Comments Close"]
   ["date" "Publication Date"]]
  (clj->js in-vector)
  (clj->js {:allowHtml true, :width "100%", :sortColumn 4})
  (new js/google.visualization.Table data-element)))

(defn filter-results [in-vector out-vector]
  (if (empty? in-vector)
    out-vector
    (let [my-map (first in-vector)
          comments-close (ft/federal-to-js
                           (get my-map "comments_close_on"))]
      (if (clojure.string/blank? comments-close)
        (recur (rest in-vector) out-vector)
        (recur
          (rest in-vector)
          (conj
            out-vector
            (vector
              (get my-map "title")
              (get my-map "action")
              (get-agency-abbr (get my-map "agency_names") "")
              (str
                "<a href =\""
                (get my-map "html_url")
                "\">"
                (if-let [docket (get my-map "docket_id")]
                  docket
                  "No Docket Id.")
                "</a>")
              comments-close
              (ft/federal-to-js (get my-map "publication_date")))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ajax code 

(defn handler 
  "Handle the ajax response"
  [response]
  (let 
    [clj-resp (js->clj response {:kewordize-keys true})]
    
    (.setOnLoadCallback js/google (fn []
                                    (build-chart
                                      (filter-results (get clj-resp "results") []))))))

(defn err-handler 
  "Handle the ajax errors"
  [response]
    (.log js/console (str "ERROR: " response)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(set! dates-element.innerHTML (str (ft/my-time-string ft/us-formatter 1) " to " (ft/my-time-string ft/us-formatter)))

(let
  [url (str my-url (agency-url-string)
              (ft/one-week-ago))]

;(set! data-element.innerHTML url)

(.send (goog.net.Jsonp. url nil) "" handler err-handler nil)


)

