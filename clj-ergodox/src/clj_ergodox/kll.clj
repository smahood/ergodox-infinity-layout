(ns clj-ergodox.kll
  (:require [clojure.edn :as edn]
            [clojure.string]))


(defn usb-code [x]
  (str "U\"" (clojure.string/upper-case (name x)) "\";"))


(defn key-sequence [x]
  (str "'" x "';"))


(defn key-combination [xs]
  (str (clojure.string/join
         " + "
         (map #(str "U\"" (clojure.string/upper-case (name %)) "\"")
              xs))
       ";"))


(defn capability [xs]
  (str (first xs) "();"))

(defn get-shortcut
  ([value shortcuts]
   (let [shortcut-value (get shortcuts value)]
     (cond
       (nil? value) nil
       (nil? shortcut-value) (usb-code value)
       (set? shortcut-value) (key-combination shortcut-value)
       (list? shortcut-value) (capability shortcut-value)
       (string? shortcut-value) (key-sequence shortcut-value)
       :else (str value " - " shortcut-value))))
  ([value shortcuts layer]
   (let [shortcut-value (get shortcuts value)]
     (cond
       (and (= 0 layer) (nil? value)) "None;"
       :else (get-shortcut value shortcuts)))))

