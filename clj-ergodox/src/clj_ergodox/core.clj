(ns clj-ergodox.core
  (:require [clojure.edn :as edn]
            [clojure.string]))

(defn ergodox-layout [path]
  (edn/read-string (slurp path)))


(defn shortcuts [layout]
  (:shortcuts layout))

(defn keymap [layout]
  (:keymap layout))


(defn num-layers [kmap]
  (apply max (for [[_ vs] kmap]
               (count vs))))

(defn usb-code [x]
  (str "U\"" (clojure.string/upper-case (name x)) "\";"))

(defn key-sequence [x]
  (str "'" x "';"))

(defn key-combination [xs]
  (str (clojure.string/join " + "
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


(defn make-body [xs]
  (clojure.string/join
    "\n"
    (map #(str "U\"" (clojure.string/upper-case (name (key %))) "\"" " : " (val %))
         xs)))

(defn make-bash-file [layers]
  (let [header (slurp "resources/bash-header.txt")
        footer (slurp "resources/bash-footer.txt")
        c (count layers)
        body (clojure.string/join "\n"
                                  (map #(str "PartialMaps[" % "]=\"ergodox-" % " lcdFuncMap\"")
                                       (range 1 c)))]
    (str header body footer)))


(defn make-files [layers target-folder]
  (let [kll-header (slurp "resources/kll-header.txt")]
    (spit (str target-folder "ergodox.bash")
          (make-bash-file layers))
    (doseq [[k vs] layers]
      (spit
        (str target-folder "ergodox-" k ".kll")
        (str kll-header
             (make-body vs))))))


(let [tf "../kiibohd/"
      el (ergodox-layout "resources/ergodox-keymap.edn")
      km (keymap el)
      sc (shortcuts el)]
  (-> (make-layers km sc)
      (make-files tf)))
