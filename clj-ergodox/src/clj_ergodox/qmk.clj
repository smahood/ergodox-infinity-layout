(ns clj-ergodox.qmk
  (:require [clojure.edn :as edn]
            [clojure.string]))



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