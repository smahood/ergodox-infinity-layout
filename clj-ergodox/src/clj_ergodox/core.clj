(ns clj-ergodox.core
  (:require [clojure.edn :as edn]))




(defn ergodox-layout [path]
  (edn/read-string (slurp path)))

(defn shortcuts [layout]
  (:shortcuts layout))

(defn keymap [layout]
  (:keymap layout))


(defn num-layers [kmap]
  (apply max (for [[_ vs] kmap]
               (count vs))))

(def shaun-layout (ergodox-layout "ergodox-keymap.edn"))
(def shaun-keymap (keymap shaun-layout))

(def shaun-shortcuts (shortcuts shaun-layout))


(set? #{:r :alt})
(set? {:r :alt})
(set? [:r :alt])


(def shaun-layer-count (num-layers (keymap shaun-layout)))

(defn usb-code [x]
  (str "U\"" (name x) "\";"))

(string? :f)


(defn get-shortcut
  ([value shortcuts]
   (let [shortcut-value (get shortcuts value)]
     (cond
       (nil? value) nil
       (nil? shortcut-value) (usb-code value)
       (set? shortcut-value) (str "SET! " value " - " shortcut-value) ; U"Alt" + U"Shift" + U"QUOTE";
       (string? shortcut-value) (str "STRING! " value " - " shortcut-value) ; '<', '<', '-';
       :else (str value " - " shortcut-value))))
  ([value shortcuts layer]
   (let [shortcut-value (get shortcuts value)]
     (cond
       (and (= 0 layer) (nil? value)) "None;"
       :else (get-shortcut value shortcuts)))))


(defn make-layers [kmap shortcuts]
  (merge
    {0 (into {} (remove #(nil? (val %))
                        (apply merge (for [[k vs] kmap]
                                       {k (get-shortcut (first vs) shortcuts 0)}))))}
    (let [layer-count (num-layers kmap)]
      (apply merge (for [n (range 1 layer-count)]
                     {n (into {} (remove #(nil? (val %))
                                         (apply merge
                                                (for [[k vs] kmap]
                                                  {k (when (< n (count vs))
                                                       (get-shortcut (nth vs n) shortcuts))}))))})))))


(make-layers shaun-keymap shaun-shortcuts)
