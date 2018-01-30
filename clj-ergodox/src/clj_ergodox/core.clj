(ns clj-ergodox.core
  (:require [clj-ergodox.kll :as kll]
            [clj-ergodox.qmk :as qmk]
            [clj-ergodox.quick-ref :as reference]))

(defn make-kll-files []
  (kll/make-files "resources/ergodox-keymap.edn" "../kiibohd/"))

(defn make-qmk-files []
  (qmk/make-files "resources/ergodox-keymap.edn" "../qmk"))

(defn make-quick-reference []
  (reference/make-files "resources/ergodox-keymap.edn" "../reference"))


(comment (make-kll-files))

(make-qmk-files)

(comment (make-quick-reference))