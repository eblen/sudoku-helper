(ns sudoku.core
  (:require [sudoku.view :as v]
            [quil.core   :as q]
            [quil.middleware :as mware]))

(defn setup-game []
  (q/frame-rate 30)
  (v/setup-game))

(q/defsketch sudoku
  :title  "Sudoku Helper"
  :size   [500 500]
  :setup  setup-game
  :update v/update-game
  :draw   v/draw-board
  :middleware [mware/fun-mode])

(defn -main [& args])
