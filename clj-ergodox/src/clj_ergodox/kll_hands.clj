(ns clj-ergodox.kll-hands
  (:require [clojure.edn :as edn]
            [clojure.string]
            [clj-ergodox.kll :as kll]))





(defn num-layers [keymap]
  (let [position-layers (for [hand keymap
                              section (val hand)
                              position (val section)
                              layer-map (val position)]
                          (key layer-map))]
    (apply max
           (filter int? position-layers))))

(defn explode-keymap [keymap]
  (into [] (for [hand keymap
                 section (val hand)
                 position (val section)
                 layer-map (val position)]
             {:hand     (key hand)
              :section  (key section)
              :position (key position)
              :layer    (key layer-map)
              :shortcut (val layer-map)})))



(defn ->kll-keymap [exploded-keymap layout]
  (let [kll-indexes (get layout :matrix->kll-defaults)
        shortcut-values (get layout :shortcuts)
        xf (map #(assoc %
                   :kll-index
                   (get-in kll-indexes [(:hand %)
                                        (:section %)
                                        (:position %)])
                   :kll-value (kll/get-shortcut (:shortcut %) shortcut-values 0)
                   :line-comment (str (name (:hand %)) " " (name (:section %)) " " (:position %))))]
    (transduce xf conj exploded-keymap)))



(defn make-kll-string [m]
  (str "U\"" (clojure.string/upper-case (name (:kll-index m))) "\""
       " : " (:kll-value m)
       "   # " (:line-comment m)))




(defn section-string [hand-keymap section]
  (let [section-keymap (transduce
                         (filter #(= section (:section %)))
                         conj
                         hand-keymap)]
    (when (< 0 (count section-keymap))
      (str "# " (clojure.string/capitalize (name section))
           "\n"
           (clojure.string/join "\n"
                                (transduce (map #(make-kll-string %))
                                           conj
                                           (sort-by :position section-keymap)))
           "\n"))))




(defn hand-string [layer-keymap hand]
  (let [hand-keymap (transduce
                      (filter #(= hand (:hand %)))
                      conj
                      layer-keymap)]
    (when (< 0 (count hand-keymap))
      (clojure.string/join "\n"
                           [(str "### " (clojure.string/capitalize (name hand)) " Hand ###")
                            (section-string hand-keymap :thumb)
                            (section-string hand-keymap :fingers)]))))


(defn layer-string [kll-exploded-keymap kll-header layer]

  (let [layer-keymap (transduce
                       (filter #(= layer (:layer %)))
                       conj
                       kll-exploded-keymap)]
    (clojure.string/join "\n"
                         [kll-header
                          (hand-string layer-keymap :left)
                          (hand-string layer-keymap :right)])))


(defn make-kll-file-bodies [header-file layout-file]
  (let [kll-header (slurp header-file)
        layout (edn/read-string (slurp layout-file))
        keymap (get layout :per-hand)
        max-layer (num-layers keymap)
        exploded-keymap (explode-keymap keymap)
        kll-keymap (->kll-keymap exploded-keymap layout)]
    (into [] (for [layer (range 0 (+ 1 max-layer))]
               {layer (layer-string kll-keymap kll-header layer)}))))


(defn make-bash-file-body [kll-bodies]
  (let [header (slurp "resources/bash-header.txt")
        footer (slurp "resources/bash-footer.txt")
        c (count kll-bodies)
        body (clojure.string/join "\n"
                                  (map #(str "PartialMaps[" % "]=\"ergodox-" % " lcdFuncMap\"")
                                       (range 1 c)))]
    (str header body footer)))


(defn make-files [layout-file target-folder]
  (let [kll-header "resources/kll-header.txt"
        kll-bodies (make-kll-file-bodies kll-header layout-file)]
    (spit (str target-folder "ergodox.bash")
          (make-bash-file-body kll-bodies))
    (doseq [body kll-bodies]
      (spit
        (str target-folder "ergodox-" (key (first body)) ".kll")
        (val (first body))))))


#_(let [kll-header "resources/kll-header.txt"
      layout "resources/ergodox-keymap.edn"]
  (make-kll-file-bodies kll-header layout))