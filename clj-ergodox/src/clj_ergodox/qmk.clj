(ns clj-ergodox.qmk
  (:require [clojure.edn :as edn]
            [clojure.string :as string]))

(defn qmk-constants [] (edn/read-string (slurp "resources/qmk-constants.edn")))


(defn qmk-translation [] (edn/read-string (slurp "resources/qmk-translation.edn")))


(defn custom-keymap [] (edn/read-string (slurp "resources/custom-keymap.edn")))


(defn shortcuts [] (edn/read-string (slurp "resources/shortcuts.edn")))


(defn sort-for-qmk [keymap]
  (let [ordered [(->> (get-in keymap [:left :fingers])
                      (sort-by #(vector (second (key %))
                                        (first (key %))))
                      (into []))
                 (->> (get-in keymap [:left :thumb])
                      (sort-by #(vector (- (second (key %)))
                                        (first (key %))))
                      (into []))
                 (->> (get-in keymap [:right :fingers])
                      (sort-by #(vector (- (second (key %)))
                                        (- (first (key %)))))
                      (into []))
                 (->> (get-in keymap [:right :thumb])
                      (sort-by #(vector (- (second (key %)))
                                        (- (first (key %)))))
                      (into []))]]
    (mapcat conj ordered)))


(defn layers [keymap]
  (->> (sort-for-qmk keymap)
       (map second)
       (map keys)
       (apply concat)
       (into #{})))


(defn translate-key [x]
  (let [m {:x           x
           :shortcut    (get (shortcuts) x)
           :translation (get (qmk-translation) x)}]
    (or (:translation m) "KC_NO"))

  #_(let [shortcut (get shortcuts x)]
      (cond
        (nil? x) "KC_NO"

        (nil? shortcut) (get (qmk-translation) x)
        :else {:x           x
               :shortcut    shortcut
               :translation (get (qmk-translation) x)})))






(defn make-file-contents []
  (let [constants (qmk-constants)
        translation (qmk-translation)
        keymap (custom-keymap)]

    (string/join
      "\n"
      [(get constants :includes)
       (get constants :defines)
       ""
       (get constants :enums)
       (get constants :start-layouts)
       (let [ordered {:left
                      {:fingers (->>
                                  (-> keymap :left :fingers)
                                  (sort-by #(vector (second (key %))
                                                    (first (key %))))
                                  (into []))
                       :thumb   (->>
                                  (-> keymap :left :thumb)
                                  (sort-by #(vector (- (second (key %)))
                                                    (first (key %))))
                                  (into []))}
                      :right
                      {:fingers (->>
                                  (-> keymap :right :fingers)
                                  (sort-by #(vector (- (second (key %)))
                                                    (- (first (key %)))))
                                  (into []))
                       :thumb   (->>
                                  (-> keymap
                                      :right
                                      :thumb)
                                  (sort-by #(vector (- (second (key %)))
                                                    (- (first (key %)))))
                                  (into []))}}]
         #_(concat (-> ordered :left :fingers)
                   (-> ordered :left :thumb)
                   (-> ordered :right :fingers)
                   (-> ordered :right :thumb))

         (conj (get-in ordered [:left :fingers])
               (get-in ordered [:left :thumb])
               (get-in ordered [:right :fingers])
               (get-in ordered [:right :thumb])
               ))
       (get constants :stop-layouts)
       (get constants :last)])))


(let [constants (qmk-constants)
      translation (qmk-translation)
      keymap (custom-keymap)]
  (make-file-contents)



  #_(spit (str "../../qmk_firmware/keyboards/ergodox_infinity/keymaps/shaun/" "keymap.c")
          (make-file-contents)))

; Step 1 - get base layer working with no shortcuts or translation





; string - type these characters in order
; map -
; set - type these characters at the same time
; vector -
; list -
; keyword - translate to string


;
;    MO(layer) - momentary switch to layer. As soon as you let
;                go of the key, the layer is deactivated and you
;                pop back out to the previous layer.
;    LT(layer, kc) - momentary switch to layer when held,
;                    and kc when tapped.
;    TG(layer) - toggles a layer on or off.
;    TO(layer) - Goes to a layer. This code is special,
;                because it lets you go either up or down the stack --
;                just goes directly to the layer you want. So while
;               other codes only let you go up the stack (from layer 0 to
;               layer 3, for example), TO(2) is going to get you to layer 2,
;               no matter where you activate it from -- even if you're
;               currently on layer 5. This gets activated on keydown
;               (as soon as the key is pressed).
;    TT(layer) - Layer Tap-Toggle. If you hold the key down,
;               the layer becomes active, and then deactivates when
;               you let go. And if you tap it, the layer simply becomes
;               active (toggles on). It needs 5 taps by default,
;               but you can set it by defining TAPPING_TOGGLE,
;               for example, #define TAPPING_TOGGLE 2 for just two taps.


;    LSFT(kc) or S(kc) - applies left Shift to kc (keycode)
;    RSFT(kc) - applies right Shift to kc
;    LCTL(kc) - applies left Control to kc
;    RCTL(kc) - applies right Control to kc
;    LALT(kc) - applies left Alt to kc
;    RALT(kc) - applies right Alt to kc
;    LGUI(kc) - applies left GUI (command/win) to kc
;    RGUI(kc) - applies right GUI (command/win) to kc
;    HYPR(kc) - applies Hyper (all modifiers) to kc
;    MEH(kc) - applies Meh (all modifiers except Win/Cmd) to kc
;    LCAG(kc) - applies CtrlAltGui to kc
;    LALT(LCTL(KC_DEL)) -- this makes a key that sends Alt, Control,
;                          and Delete in a single keypress.

; ignore - KC_NO
; transparent - KC_TRANSPARENT


;CTL_T(kc) - is LCTL when held and kc when tapped
;SFT_T(kc) - is LSFT when held and kc when tapped
;ALT_T(kc) - is LALT when held and kc when tapped
;ALGR_T(kc) - is AltGr when held and kc when tapped
;GUI_T(kc) - is LGUI when held and kc when tapped
;ALL_T(kc) - is Hyper (all mods) when held and kc when tapped.
;            To read more about what you can do with a Hyper key,
;            see this blog post by Brett Terpstra
;LCAG_T(kc) - is CtrlAltGui when held and kc when tapped
;MEH_T(kc) - is like Hyper, but not as cool -- does not include the Cmd/Win
;            key, so just sends Alt+Ctrl+Shift.


;#define PERMISSIVE_HOLD


