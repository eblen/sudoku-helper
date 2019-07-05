(ns sudoku.view
  (:require [sudoku.params :as p]
            [sudoku.input  :as i]
            [quil.core :as q]))

(def bg-color      [255 255 255])
(def grid-color    [  0   0   0])
(def cursor-color  [240 200 255])
(def def-sym-color [  0   0   0])

(defn- set-bold-lines [bold?]
  (if bold? (q/stroke-weight 2)
            (q/stroke-weight 1)))

(defn- draw-hline [start finish vpos bold?]
  (set-bold-lines bold?)
  (q/line [start vpos] [finish vpos]))

(defn- draw-vline [start finish hpos bold?]
  (set-bold-lines bold?)
  (q/line [hpos start] [hpos finish]))

; Convert logical grid position to board position (horizontal)
(defn- hline [i]
  (+ p/margin (* p/hsq-size i)))

; Convert logical grid position to board position (vertical)
(defn- vline [i]
  (+ p/margin (* p/vsq-size i)))

; Convert square index to x,y coordinates
(defn- square-coords [i]
  [(mod i p/game-size) (quot i p/game-size)])

; Convert x,y coordinate to square index
(defn- square-idx [x y]
  (+ x (* y p/game-size)))

(defn- move-square-impl [x y dir]
  (let [bound (dec p/game-size)]
    (cond
      (= dir :left)  (if (= 0     x) [x y] [(dec x) y])
      (= dir :right) (if (= bound x) [x y] [(inc x) y])
      (= dir :up)    (if (= 0     y) [x y] [x (dec y)])
      (= dir :down)  (if (= bound y) [x y] [x (inc y)]))))

(defn- move-square [sq dir]
  (let [[x y] (move-square-impl
                (mod sq p/game-size) (quot sq p/game-size) dir)]
    (+ x (* y p/game-size))))

(defn- handle-events [game]
  (let [[mevent mdetails] (i/get-mouse-event)
        [kevent kdetails] (i/get-kb-event)]
    (cond
      ; Handle mouse events
      (= mevent :sel-square)
        (assoc game :sel-square (apply square-idx mdetails))

      ; Handle keyboard events. Ignore events too close together (1/5 second)
      (and kevent (< 200 (- (q/millis) (:last-kb-click game))))
        (cond

          ; Navigate board with arrow keys
          (= kevent :nav-board)
            (assoc game :sel-square (move-square (:sel-square game) kdetails)
                        :last-kb-click (q/millis))

          ; Enter symbol
          (= kevent :enter-symbol)
            (let [sel-square (:sel-square game)
                  squares    (:squares game)]
              (assoc game :squares (assoc squares sel-square kdetails)
                          :last-kb-click (q/millis)))
          :else game)
      :else game)))

(defn draw-board [g]
  (apply q/background bg-color)
  (apply q/fill bg-color)

  ; Draw grid lines
  (apply q/stroke grid-color)
  (let [rmargin  (- p/board-width p/margin)
        box-size (int (Math/sqrt p/game-size))]
    (dorun (map #(draw-hline p/margin rmargin (hline %) (= (mod % box-size) 0)) (range (inc p/game-size))))
    (dorun (map #(draw-vline p/margin rmargin (vline %) (= (mod % box-size) 0)) (range (inc p/game-size)))))

  (let [[x y]  (square-coords (:sel-square g))
        xpos   (hline x)
        ypos   (vline y)]

    ; Draw cursor
    (apply q/fill cursor-color)
    (q/rect xpos ypos p/hsq-size p/vsq-size)

    ; Draw symbols
    (q/text-font (q/create-font "Times-Bold" (/ p/hsq-size 1.3) true))
    (q/text-align :center :center)
    (dorun (for [x (range p/game-size) y (range p/game-size)]
      ((apply q/fill (get (:square-colors g) (square-idx x y)))
       (q/with-translation [(+ (hline x) (/ p/hsq-size 2))
                            (+ (vline y) (/ p/vsq-size 2))]
         (q/text (str (get (:squares g) (square-idx x y))) 0 0)))))))

(defn setup-game [] {
  :sel-square    0
  :squares       (vec (repeat (* p/game-size p/game-size) nil))
  :square-colors (vec (repeat (* p/game-size p/game-size) def-sym-color))
  :last-kb-click 0})

(defn update-game [game]
  (handle-events game))
