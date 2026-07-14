# AmpFootballFit

A free, offline-first return-to-play conditioning app for amputee football (crutch-based, single-leg-support sport). Built for one athlete's own comeback, shared here so any amputee footballer or club can use it, adapt it, or build on it.

**Try it now, no install:** [acraens.github.io/AmpFootballFit](https://acraens.github.io/AmpFootballFit/)

## What it is

- A 12-week, 3-phase strength & conditioning program (Base, Load Progression, Sport Transfer), with exercises that adapt tier by tier based on how sessions actually go.
- A daily readiness check-in, periodic objective benchmarks/retests, session logging, and a guided "Start Workout" mode.
- A place to add your own exercises (from a physio or coach) alongside the built-in ones.
- A "Share progress" button that hands a plain-text summary to your phone's share sheet, e.g. to send to a coach or physio.
- **Not** a source of sport-technique instruction. The Skills tab is an intentionally empty framework: fill it in from a real coach, video footage, or club sessions. This app does not invent technique advice it isn't qualified to give.

## What it isn't

Not a general lower-limb-amputee fitness app, and not a substitute for guidance from a physio, prosthetist, or coach. Every rule the app enforces (phase gating, low-readiness warnings) is advisory only: it will tell you, never block you.

## Running it

The entire app is one self-contained file: **`ampfootballfit.html`** (also published as `index.html` for GitHub Pages, kept in sync with it). No build step, no server, no account.

- **Browser / PWA:** open `ampfootballfit.html` directly, or serve the folder with any static file server and install it as a PWA. Camera-based profile photo capture is native-app-only and hides itself gracefully in a browser.
- **Android app:** the `android-app/` folder is a Capacitor project wrapping the same HTML file, adding native camera capture, filesystem storage, and native share. To rebuild after editing `ampfootballfit.html`:

  ```
  cp ampfootballfit.html android-app/www/index.html
  cd android-app
  npx cap sync android
  cd android/app && ../gradlew assembleDebug   # outputs app-debug.apk
  ```

  You'll need a JDK (21+) and the Android SDK command-line tools on your `PATH` (`JAVA_HOME`, `ANDROID_HOME`).

All data (sessions, benchmarks, readiness logs, milestones, profile) stays in local storage on the device. There is no server and nothing leaves the device except through the explicit Share/export buttons.

## Contributing

Issues and pull requests are welcome, especially from anyone actually playing or coaching amputee football. If you fork this for your own club or region, keeping the license notice intact is all that's asked.

## License

[MIT](LICENSE) &copy; 2026 [A. Craens](https://github.com/acraens)
