(ns clj-ergodox.kll-fingers
  (:require [clojure.edn :as edn]
            [clojure.string]
            [clj-ergodox.kll :as kll]))







;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;                       New keymap -> kll                                  ;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn num-layers [keymap]
  (apply max (for [hand keymap
                   finger (val hand)
                   position (val finger)
                   layer-map (val position)]
               (key layer-map))))


(defn explode-keymap [keymap]
  (into [] (for [hand keymap
                 finger (val hand)
                 position (val finger)
                 layer-map (val position)]
             {:hand     (key hand)
              :finger   (key finger)
              :position (key position)
              :layer    (key layer-map)
              :shortcut (val layer-map)})))


(defn ->kll-keymap [exploded-keymap layout]
  (let [kll-indexes (get layout :fingers->kll-defaults)
        shortcut-values (get layout :shortcuts)
        xf (map #(assoc %
                   :kll-index
                   (get-in kll-indexes [(:hand %)
                                        (:finger %)
                                        (:position %)])
                   :kll-value (kll/get-shortcut (:shortcut %) shortcut-values 0)
                   :line-comment (str (name (:hand %)) " " (name (:finger %)) " " (:position %))))]
    {:exploded exploded-keymap
     :shortcuts shortcut-values
     :kll-indexes kll-indexes
     :xf (transduce xf conj exploded-keymap)}
    #_(transduce xf conj exploded-keymap)))


(defn make-kll-string [m]
  (str "U\"" (clojure.string/upper-case (name (:kll-index m))) "\""
       " : " (:kll-value m)
       "   # " (:line-comment m)))


(defn finger-string [hand-keymap finger]
  (let [finger-keymap (transduce
                        (filter #(= finger (:finger %)))
                        conj
                        hand-keymap)]
    (when (< 0 (count finger-keymap))
      (str "# " (clojure.string/capitalize (name finger))
           "\n"
           (clojure.string/join "\n"
                                (transduce (map #(make-kll-string %))
                                           conj
                                           (sort-by :position finger-keymap)))
           "\n"))))


(defn hand-string [layer-keymap hand]
  (let [hand-keymap (transduce
                      (filter #(= hand (:hand %)))
                      conj
                      layer-keymap)]
    (when (< 0 (count hand-keymap))
      (clojure.string/join "\n"
                           [(str "### " (clojure.string/capitalize (name hand)) " Hand ###")
                            (finger-string hand-keymap :p)
                            (finger-string hand-keymap :i)
                            (finger-string hand-keymap :m)
                            (finger-string hand-keymap :a)]))))


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
        keymap (get layout :keymap)
        max-layer (num-layers keymap)
        exploded-keymap (explode-keymap keymap)
        kll-keymap (->kll-keymap exploded-keymap layout)]
    kll-keymap
    #_(into [] (for [layer (range 0 (+ 1 max-layer))]
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
  "Don't use this!"
  #_(let [kll-header "resources/kll-header.txt"
          kll-bodies (make-kll-file-bodies kll-header layout-file)]
      (spit (str target-folder "ergodox.bash")
            (make-bash-file-body kll-bodies))
      (doseq [body kll-bodies]
        (spit
          (str target-folder "ergodox-" (key (first body)) ".kll")
          (val (first body))))))

(let [kll-header "resources/kll-header.txt"
      layout "resources/ergodox-keymap.edn"]
  (make-kll-file-bodies kll-header layout))



;(str "# --------------------------------------------------.           ,--------------------------------------------------.")
;(str "|   =    |   1  |   2  |   3  |   4  |   5  | LEFT |           | RIGHT|   6  |   7  |   8  |   9  |   0  |   -    |")
;(str "|--------+------+------+------+------+-------------|           |------+------+------+------+------+------+--------|")
;(str "| Del    |   Q  |   W  |   E  |   R  |   T  |  L1  |           |  L1  |   Y  |   U  |   I  |   O  |   P  |   \\    |")
;(str "|--------+------+------+------+------+------|      |           |      |------+------+------+------+------+--------|")
;(str "| BkSp   |   A  |   S  |   D  |   F  |   G  |------|           |------|   H  |   J  |   K  |   L  |; / L2|' / Cmd |")
;(str "|--------+------+------+------+------+------| Hyper|           | Meh  |------+------+------+------+------+--------|")
;(str "| LShift |Z/Ctrl|   X  |   C  |   V  |   B  |      |           |      |   N  |   M  |   ,  |   .  |//Ctrl| RShift |")
;(str "`--------+------+------+------+------+-------------'           `-------------+------+------+------+------+--------'")
;(str " |Grv/L1|  '\"  |AltShf| Left | Right|                                        |  Up  | Down |   [  |   ]  | ~L1  |")
;(str " '-----------------------------------'                                        `----------------------------------'")
;(str "                       ,-------------.       ,-------------.")
;(str "                       | App  | LGui |       | Alt  |Ctrl/Esc|")
;(str "                ,------|------|------|       |------+--------+------.")
;(str "                |      |      | Home |       | PgUp |        |      |")
;(str "                | Space|Backsp|------|       |------|  Tab   |Enter |")
;(str "                 |      |ace   | End  |       | PgDn |        |      |")
;(str "                `--------------------'       `----------------------' ")




;{:hand         :right,
;   :finger       :a,
;   :position     [0 1],
;   :layer        4,
;   :shortcut     :question-mark,
;   :kll-index    :l,
;   :kll-value    "'?';",
;   :line-comment "right a [0 1]"}