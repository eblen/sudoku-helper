(ns sudoku.input
  (:require [sudoku.params :as p]
            [quil.core :as q]))

(def keys-to-colors {
  \d p/def-sym-color ; default color
  \r [255   0   0]   ; red
  \g [  0 255   0]   ; green
  \b [  0   0 255]   ; blue
  \y [255 255   0]   ; yellow
  \o [255 128   0]   ; orange
  \p [255   0 255]}) ; purple

; Return square at the given board coordinates (nil if none)
(defn- square-at-pos [x y]
  (let [xtrans (- x p/margin)
        ytrans (- y p/margin)
        xpos (quot xtrans p/hsq-size)
        ypos (quot ytrans p/vsq-size)]
    (if (and (< 0 xtrans) (< 0 ytrans)
             (> p/game-size xpos) (> p/game-size ypos))
      [xpos ypos]
      nil)))
  
(defn get-mouse-event []
  (if (and (q/mouse-pressed?) (= (q/mouse-button) :left))
    (if-let [coords (square-at-pos (q/mouse-x) (q/mouse-y))]
      [:sel-square coords]
      nil)
    nil))

(defn get-kb-event []
  (if (q/key-pressed?)
    (let [key     (q/raw-key)
          keyword (q/key-as-keyword)
          color   (get keys-to-colors key)]
      (cond
        (some #(= keyword %) [:up :down :left :right])     [:nav-board     keyword]
        (some #(= key %)     (take p/game-size p/symbols)) [:enter-symbol  key]
        (= keyword :space)                                 [:delete-symbol nil]
        color                                              [:set-color     color]
        :else nil))
     nil))

