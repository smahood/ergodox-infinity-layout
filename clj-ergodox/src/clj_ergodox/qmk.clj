(ns clj-ergodox.qmk
  (:require [clojure.edn :as edn]
            [clojure.string]))

(def qmk-edn (edn/read-string (slurp "resources/qmk.edn")))

(def start-c-layout-declaration
  "\nconst uint16_t PROGMEM keymaps[][MATRIX_ROWS][MATRIX_COLS] = {")

(def end-c-layout-declaration
  "};")

(defn num-layers [keymap]
  (let [position-layers (for [hand keymap
                              section (val hand)
                              position (val section)
                              layer-map (val position)]
                          (key layer-map))]
    (apply max
           (filter int? position-layers))))

(defn make-defines [layers]
  (let [layer-defines (clojure.string/join "\n"
                                           (map #(str "#define _" % " " %)
                                                layers))]
    (clojure.string/join "\n"
                         [layer-defines])))

(defn make-layer [layer]
  (str "/* Keymap " layer " */\n"
       "[_" layer "] = LAYOUT_ergodox("


       "), \n"
       ))


(defn make-layers [layers]
  (clojure.string/join "\n"
                       (map make-layer layers)))



(defn make-body [layout]
  (let [keymap (get layout :custom-keymap)
        max-layer (num-layers keymap)
        integer-layers (range max-layer)]
    (clojure.string/join "\n"
                         [(make-defines integer-layers)
                          start-c-layout-declaration
                          (make-layers integer-layers)
                          end-c-layout-declaration])

    ))


(defn make-files [layout-file target-folder]
  (let [qmk-header "resources/qmk-header.txt"
        layout (edn/read-string (slurp layout-file))
        qmk-body (make-body layout)]
    (clojure.string/join "\n"
                         [(slurp qmk-header)
                          qmk-body])))

(make-files "resources/ergodox-keymap.edn" "../qmk")





(defn ergodox-layout [path]
  (edn/read-string (slurp path)))


(defn ordered-layout [keymap]
  {:left  [:equals
           :1 :2 :3 :4 :5 :esc
           :backslash :q :w :e :r :t :function1
           :tab :a :s :d :f :g
           :lshift :z :x :c :v :b :function2
           :lgui :backtick :function3 :function4 :function5
           :lctrl :lalt :home :backspace :delete :end]
   :right [:function6 :6 :7 :8 :9 :0 :minus
           :lbrace :y :u :i :o :p :rbrace
           :h :j :k :l :semicolon :quote
           :function7 :n :m :comma :period :slash :rshift
           :left :down :up :right :rgui
           :ralt :rctrl :pageup :pagedown :enter :space]})


(let [el (ergodox-layout "resources/ergodox-keymap.edn")
      km (:keymap el)
      ol (ordered-layout km)]
  {:left  (map #(get km %)
               (:left ol))
   :right (map #(get km %)
               (:right ol))})