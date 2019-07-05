(ns sudoku.params
  (:require [quil.core :as q]))

(def board-width  500)
(def board-height 500)
(def margin        20)
(def game-size      9)
(def hsq-size     (/ (- board-width  margin margin) game-size))
(def vsq-size     (/ (- board-height margin margin) game-size))
(def symbols      (map char (iterate inc (int \1))))

