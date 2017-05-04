(ns QSort)

(import '[java.util.concurrent Executors])
(use 'clojure.java.io)
;(require '[clojure.core.reducers :as r])

(defn get-lines [fname]
  (doall
    (map read-string
      (clojure.string/split-lines
         (slurp fname)))))
(def numberList (get-lines "numbers_txt.txt"))

(defn qSort [list]
    (let [pivot (first list)]
      (when pivot
        (lazy-cat (qSort (filter #(< % pivot) list))
                  (filter #(= % pivot) list)
                  (qSort (filter #(> % pivot) list))))))


(defn qSort-concur1 [list]
  (let [pivot (first list)]
    (when pivot
        (lazy-cat @(future(qSort-concur1 (filter #(< % pivot) list)))
                  (filter #(= % pivot) list)
                  @(future(qSort-concur1 (filter #(> % pivot) list)))))))


(defn qSort-concur [list]
  (when (first list)
    (let [pivot (first list)
          lSort (future (qSort-concur (doall (filter #(< % pivot) list))))
          rSort (future (qSort-concur (doall (filter #(> % pivot) list))))
          equal (doall (filter #(= % pivot) list))]
      (lazy-cat @lSort equal @rSort))))




;; Does it run?
;;(qSort-concur numberList)
;; Is it sorted?
;(println (apply <= (qSort numberList)))
;;(println (apply <= (qSort-concur numberList)))
;; Is it really sorted?
;;(println (take 30 (qSort-concur numberList)))
;;(time (apply <= (qSort-concur numberList)))

(defn timing [threads]
  ; limit threads agents/futures can create
  ;https://github.com/clojure/clojure/blob/clojure-1.9.0-alpha14/src/clj/clojure/core.clj#L2085
  ;https://github.com/clojure/clojure/blob/clojure-1.9.0-alpha14/src/clj/clojure/core.clj#L6838
  (set-agent-send-off-executor! (Executors/newFixedThreadPool threads))
  (print "Threads: ")
  (println threads)
  (time (apply <= (qSort-concur numberList)))
  (time (apply <= (qSort-concur numberList)))
  (time (apply <= (qSort-concur numberList)))
  (time (apply <= (qSort-concur numberList)))
  (time (apply <= (qSort-concur numberList))))

; Benchmark
(println "Benchmark")
(time (apply <= (sort numberList)))
(time (apply <= (sort numberList)))
(time (apply <= (sort numberList)))
(time (apply <= (sort numberList)))
(time (apply <= (sort numberList)))
;;Run sort without future overhead
(println "Threads: 1 (no future overhead)")
(time (apply <=(qSort numberList)))
(time (apply <=(qSort numberList)))
(time (apply <=(qSort numberList)))
(time (apply <=(qSort numberList)))
(time (apply <=(qSort numberList)))

(timing 1)
(timing 2)
(timing 4)
(timing 8)
(timing 16)
(timing 32)


;;Kill threadpools
(shutdown-agents)