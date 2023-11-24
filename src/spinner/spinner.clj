(ns spinner.spinner
  (:gen-class))

(def charsets {:clock ["🕛" "🕐" "🕑" "🕒" "🕓" "🕔" "🕕" "🕖" "🕗" "🕘" "🕙" "🕚"]
               :dots ["⠋" "⠙" "⠹" "⠸" "⠼" "⠴" "⠦" "⠧" "⠇" "⠏"]
               :many-dots ["⣾" "⣽" "⣻" "⢿" "⡿" "⣟" "⣯" "⣷"]})

(defn spinner-step [interval charset]
  ;; Clear the current line and print the next character
  (doseq [char (cycle charset)]
    (print (str "\r" char))
    (flush)
    ;; Wait for the specified interval before continuing
    (Thread/sleep interval)))

(defn stop-spinner [spinner-thread]
  (println "\nStopping spinner...")
  (.interrupt spinner-thread))

(defn start-spinner [style]
  (let [spinner-thread (Thread. #(spinner-step 100 (style charsets)))]
    (.start spinner-thread)
    ;; Add a shutdown hook to handle Ctrl+C and ensure that the spinner stops cleanly.
    (.addShutdownHook (Runtime/getRuntime) (Thread. #(stop-spinner spinner-thread)))
    spinner-thread))

(comment
  ;; To start the spinner, just call the `start-spinner` function.
  ;; The spinner will keep running until the program is interrupted.
  (def spinning (start-spinner :clock))
  ;; To later stop the spinner programmatically:
  (stop-spinner spinning))

(defn -main [& args]
  ;; The main method which can be used to start the spinner from the command line
  (println "Starting spinner. Press Ctrl+C to stop...")
  (let [spinner-thread (start-spinner :dots)]
    (try
      ;; Keep the main thread alive indefinitely while the spinner is running
      (while true
        (Thread/sleep 1000))
      (catch InterruptedException e
        ;; InterruptedException caught when Ctrl+C is pressed or the thread is interrupted
        (println "Interrupted!"))
      (finally
        (stop-spinner spinner-thread)))))
