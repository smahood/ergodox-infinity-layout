(ns clj-ergodox.svg
  (:require [dali.io :as io]
            [dali.layout.stack]
            [dali.layout.align]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generate SVG Quick References
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn key-legend [text]
  [:dali/align {:relative-to :first :axis :center}
   [:rect {:stroke :black
           :fill   :none} :_ [200 100]]
   [:text {:text-family "Verdana" :font-size 12} text]]
  )


;[:dali/page
;
;   [:rect
;    {:stroke :black :stroke-width 2 :fill :none}
;    :_ [15 10]]
;   ]
;[:text {:font-family "Verdana" :font-size "15"}
;    [10 10] "Hello"]

(defn p [])

(defn i [])

(defn m [])

(defn col-5-keys [legends]
  [:dali/page
   [:dali/stack
    {:direction :down
     :anchor    :top
     :gap       5}
    (list (key-legend (nth legends 0))
      (key-legend (nth legends 1))
      (key-legend (nth legends 2))
      (key-legend (nth legends 3))
      (key-legend (nth legends 4)))]]

  )

(defn a-1 [])

(defn a-2 [])


(defn left-hand []
  [:dali/page
   [:dali/stack
    {:direction :right
     :anchor    :left
     :gap       50}
    ;[:rect {:fill :mediumslateblue} :_ [10 10]]
    ;[:rect {:fill :mediumslateblue} :_ [10 10]]
    ;[:rect {:fill :mediumslateblue} :_ [10 10]]
    (list (col-5-keys ["1" "tab" "del" "\\" "NONE"])
          (col-5-keys ["2" "L" "A" "P" "Y"]))
    ]])


;(def document
;  [:dali/page
;   [:dali/stack
;    {:position [10 10] :anchor :left :direction :right}
;    [:rect {:fill :mediumslateblue} :_ [10 10]]
;    [:rect {:fill :sandybrown} :_ [30 20]]
;    [:rect {:fill :green} :_ [40 20]]
;    [:rect {:fill :orange} :_ [20 20]]]])


(left-hand)
(io/render-png (left-hand) "left-hand.png")

;(io/render-png document "document.png")