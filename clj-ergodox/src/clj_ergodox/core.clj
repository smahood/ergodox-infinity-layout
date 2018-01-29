(ns clj-ergodox.core
  (:require [clj-ergodox.kll-hands :as kll]
            [clj-ergodox.quick-ref :as reference]))

(defn make-kll-files []
  (kll/make-files "resources/ergodox-keymap.edn" "../kiibohd/"))

(defn make-quick-reference []
  (reference/make-files "resources/ergodox-keymap.edn" "../reference"))


(make-kll-files)

(make-quick-reference)