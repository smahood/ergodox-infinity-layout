(ns clj-ergodox.qmk
  (:require [clojure.edn :as edn]
            [clojure.string :as string]))

(def qmk-constants (edn/read-string (slurp "resources/qmk-constants.edn")))


(def qmk-translation (edn/read-string (slurp "resources/qmk-translation.edn")))


(def custom-keymap (edn/read-string (slurp "resources/custom-keymap.edn")))


(def shortcuts (edn/read-string (slurp "resources/shortcuts.edn")))


(defn sort-for-qmk [keymap]
  (let [ordered [(->> (get-in keymap [:left :fingers])
                      (sort-by #(vector (- (second (key %)))
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
                      (into []))]
        xf (comp (mapcat conj))]
    (transduce xf conj ordered)))


(defn layers [keymap]
  (let [xf (comp
             (map second)
             (map keys)
             (mapcat conj)
             (distinct))]
    (transduce xf conj (sort-for-qmk custom-keymap))))


(defn translate-key [layer-key x]
  (let [shortcut-exists? (contains? shortcuts x)
        shortcut (get shortcuts x)
        translation-exists? (contains? qmk-translation x)
        translation (get qmk-translation x)]

    {:layer               layer-key
     :x                   x
     :shortcut-exists?    shortcut-exists?
     :shortcut            shortcut
     :translation-exists? translation-exists?
     :translation         (cond
                            (and (= layer-key 0) (nil? translation))
                            "KC_NO"
                            (and (not= layer-key 0) (nil? translation))
                            "KC_TRANSPARENT"
                            :else translation)

     })

  #_(let [shortcut (get shortcuts x)]
      (cond
        (nil? x) "KC_NO"

        (nil? shortcut) (get (qmk-translation) x)
        :else {:x           x
               :shortcut    shortcut
               :translation (get (qmk-translation) x)})))


(defn layer [layer-key sorted-keymap]
  (let [xf (comp
             (map second)
             (map #(get % layer-key))
             (map #(translate-key layer-key %))
             (map :translation))]
    (transduce xf
               conj
               sorted-keymap)))



(defn make-file-contents []
  (let [sorted-keymap (sort-for-qmk custom-keymap)]
    (string/join
      "\n"
      [(get qmk-constants :includes)
       (get qmk-constants :defines)
       ""
       (get qmk-constants :enums)
       (get qmk-constants :start-layouts)
       ;;;;;;;;;;;;;;; placeholder
       (string/join ", " (layer 0 (sort-for-qmk custom-keymap)))
       ;;;;;;;;;;;;;;;
       (get qmk-constants :stop-layouts)
       (get qmk-constants :last)])))


(defn make-file [target-folder target-file]
  (spit (str target-folder target-file)
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


