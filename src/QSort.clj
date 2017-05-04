(ns QSort
  (:import (java.io BufferedReader)))
(import '[java.util.concurrent Executors ExecutorService Callable])
(use 'clojure.java.io)


(defn get-lines [fname]
  (doall
    (map read-string
      (clojure.string/split-lines
         (slurp fname)))))


(defn qSort [list]
    (let [pivot (first list)]
      (when pivot
        (lazy-cat (qSort (filter #(< % pivot) list))
                  (filter #(= % pivot) list)
                  (qSort (filter #(> % pivot) list))))))


(def threadCount 32)
(set-agent-send-executor! (Executors/newFixedThreadPool threadCount))
(defn qSort-concur [list]
  (let [pivot (first list)
        lesser (agent ())
        greater (agent ())]
    (when pivot
      (lazy-cat @(send lesser (qSort-concur (filter #(< % pivot) list)))
                (filter #(= % pivot) list)
                @(send greater(qSort-concur (filter #(> % pivot) list)))))))

(def numberList (get-lines "numbers_txt.txt"))
;; Check if sorted
;;(println (apply <= (qSort numberList)))
;;(println (apply <= (qSort-concur numberList)))
(print (qSort-concur numberList))
;(println "Threads: 1")
;(time (qSort numberList))
;(time (qSort numberList))
;(time (qSort numberList))
;(time (qSort numberList))
;(time (qSort numberList))

(defn timing [threads]
  (set-agent-send-executor! (Executors/newFixedThreadPool threads))
  (print "Threads: ")
  (println threads)
  (time (qSort-concur numberList))
  (time (qSort-concur numberList))
  (time (qSort-concur numberList))
  (time (qSort-concur numberList))
  (time (qSort-concur numberList)))

;(timing 2)
;(timing 4)
;(timing 8)
;(timing 16)
;(timing 32)
