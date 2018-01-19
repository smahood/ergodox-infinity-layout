(ns clj-ergodox.core
  (:require [clj-ergodox.kll :as kll]))

(defn make-kll-files []
  (kll/make-files "resources/ergodox-keymap.edn" "../kiibohd/"))

(make-kll-files)