# Quick Reference (Racket)

**Contents**



## Basic Template

````
(require sinbad)

(define ds
  (sail-to "<URL>"
           ...          ; options, params, etc... (see below)
           (load)       ; to immediately load data as well
           (manifest))  ; to view data schema upon load
````

