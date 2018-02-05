(ns clj-ergodox.core
  (:require [clojure.java.shell :refer [sh with-sh-dir]]
            [clj-ergodox.kll :as kll]
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





(with-sh-dir "../../qmk_firmware/" (sh "make"
                                       "ergodox_infinity:shaun"))

(with-sh-dir "../../qmk_firmware/" (sh "make"
                                       "ergodox_infinity:shaun"
                                       "MASTER=right"))


(with-sh-dir "../../qmk_firmware/" (sh "dfu-util"
                                       "-D"
                                       "ergodox_infinity_shaun.bin"
                                       "-S"
                                       "mk20dx256vlh7"))

