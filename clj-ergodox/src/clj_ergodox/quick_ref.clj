(ns clj-ergodox.quick-ref
  (:require [clojure.edn :as edn]
            [clojure.string]))


(defn make-files [layout-file target-folder]
  (let [layout (edn/read-string (slurp layout-file))]
    (str )



    (:custom-keymap layout))




  #_(let [kll-header "resources/kll-header.txt"
          kll-bodies (make-kll-file-bodies kll-header layout-file)]
      (spit (str target-folder "ergodox.bash")
            (make-bash-file-body kll-bodies))
      (doseq [body kll-bodies]
        (spit
          (str target-folder "ergodox-" (key (first body)) ".kll")
          (val (first body)))))

  )