# chestnut1 notes

See base-README.txt for template notes.

## Development

terminal: `lein repl`, or from Emacs: `M-x cider-jack-in`.

In the REPL do

```clojure
(run)
(browser-repl)
```

```
(ns chestnut1.core)
(swap! app-state assoc :text "Interactivity FTW")
```

Heroku:
``` sh
git push heroku master:master
heroku open
```

Foreman

``` sh
lein with-profile -dev,+production uberjar && foreman start
```

Now your app is running at
[http://localhost:5000](http://localhost:5000) in production mode.

## Notes

http://blog.markwatson.com/2014/10/experimenting-with-clojure-emberjs-and.html Look at Reagent
(http://holmsand.github.io/reagent/) instead of Om. Claims to be simpler and effective.

## License

Copyright © 2014 Degel Sofware Ltd.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
