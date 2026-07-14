# AmpFootballFit — App Recreation Prompt

Use this as a single build brief for an AI coding assistant (or a human developer) to recreate AmpFootballFit from scratch. It describes the finished app, not the order it was built in. Build incrementally and confirm each section works before moving to the next.

## 1. What this app is

AmpFootballFit is a return-to-play conditioning app for amputee football players. It is:

- **Single-user, offline-first, no accounts.** Everything lives in the browser's/WebView's `localStorage`, on-device only. No server, no login, no sync.
- **A framework, not a coaching authority.** The 12-week strength/conditioning program is built on general strength & conditioning principles. Sport-specific skills (ball control, crutch agility, match tactics) are explicitly left blank for the athlete to fill in from a real coach or physio. The app must never invent sport-technique instruction it isn't qualified to give.
- **Soft-gated, never hard-blocking.** Any rule that could stop someone from training (phase progression, low readiness, tough sessions) is advisory only: show a warning, let the athlete override it. Never lock a feature behind a hard requirement.
- **Delivered two ways:** a single self-contained HTML file (works directly in any browser, installable as a PWA) and a Capacitor-wrapped native Android APK (adds camera photo capture, filesystem storage, and native share).

## 2. Technical architecture

- **One file, no build step.** The entire app (HTML + inline `<style>` + inline `<script>`) lives in one `.html` file. No bundler, no npm dependencies for the web app itself, no CDN calls. Everything must work by opening the file directly or serving it as a static file.
- **JavaScript style:** ES5-compatible function declarations (`function foo() {}`), not arrow functions or classes, no async/await (use `.then()` chains) — this was a deliberate choice for broad WebView compatibility, not a hard requirement, but keep it consistent if you follow it.
- **No CSS/JS frameworks.** Hand-written CSS with custom properties for theming (see palette below), dark theme only (no light mode). Hand-written inline SVG for all charts and icons.
- **Persistence:** every piece of state lives under a single localStorage prefix (e.g. `ampfootballfit_`), one key per data type, always JSON. Wrap every read/write in try/catch and fail soft (return null/empty array, never throw).
- **Native wrapper:** Capacitor (latest v8.x) targeting Android only (iOS stays out of scope / PWA-only). Plugins used: `@capacitor/camera`, `@capacitor/filesystem`, `@capacitor/share`. Access plugins via the auto-injected `window.Capacitor.Plugins.*` globals directly in the same HTML file — do not add a bundler or import statements, since the web app must stay a single static file that's also copied verbatim into the Capacitor `www/` folder.
- **Native camera/photo flow:** capture via `Camera.getPhoto({ source: "PROMPT", resultType: "uri" })` → read the file as base64 → write it to `Filesystem` under `Directory.Data` → store only the **relative filename** in localStorage, never the base64 blob itself. When displaying, resolve the URI via `Filesystem.getUri` + `Capacitor.convertFileSrc`, and append a cache-busting `?v=timestamp` query param, since the WebView caches images by URL and the filename never changes on replace.
- **Detect native vs. browser** with a single helper: `typeof Capacitor !== "undefined" && Capacitor.isNativePlatform && Capacitor.isNativePlatform()`. Every native-only feature (camera, native share) must check this and gracefully hide/fallback in the browser build (Web Share API as a share fallback, hidden buttons for camera).
- **PWA support:** generate the manifest and icons at runtime via canvas (no static icon files needed), set `theme-color`, `apple-mobile-web-app-*` meta tags.

## 3. Visual design

CSS custom properties (dark theme, define once in `:root`):

```
--bg: #0f1115;
--surface: #1a1d24;
--surface-alt: #22262f;
--border: #2c313c;
--text: #eef0f3;
--text-dim: #9aa1ad;
--accent: #3ea6ff;
--accent-dim: #1f5c85;
--good: #4caf7d;
--warn: #e0a94c;
--danger: #e05c5c;
```

- Base font: system UI stack. Headings for onboarding "info card" steps use a serif font (Georgia / Iowan Old Style) for a warmer, less clinical feel; everything else is the default sans system font.
- Cards: `background: var(--surface)`, `border: 1px solid var(--border)`, `border-radius: 10px`, `padding: 16px`.
- Primary button: solid `var(--accent)` background, dark text. Secondary button: `var(--surface-alt)` background with a border. Danger button: `var(--danger)` background.
- **Never use em dashes (—) in any user-facing text.** Use a period to split into two sentences, a colon when introducing something, a comma for a soft pause, parentheses for an aside, or a middle dot (`·`) to join short label fragments. This applies to every label, placeholder, prescription string, button, and generated message the athlete can see. Code comments are exempt.
- Bottom tab bar order matters: **Dashboard, Program, Log, Progress, Skills, Settings** — Skills is deliberately placed after Progress since it's a bonus framework section, not a fitness-tracking one.

## 4. Exercise illustrations

Every exercise is tagged with a `pose` id, and all illustration surfaces (Program cards, Log checklist rows, the workout player, Benchmarks, the Progress exercise picker) render the *same* image for a given pose id, via one shared function, so the whole app feels visually consistent.

- Style: hand-drawn/sketch-style single-color (blue) line art on a dark card background, athletic figures performing the movement, not abstract stick figures or flat geometric icons. This specific art style was produced externally (an AI image generation tool) as a single reference grid image containing all pose variations, which was then cropped into 23 individual PNGs (one per pose id) and shipped as static image assets referenced by relative path (`exercise-images/<poseId>.png`), *not* generated as inline SVG.
- **If you can't source equivalent illustrations**, the fallback approach that was tried and rejected along the way (in order of what was tried and why it failed): (1) thin inline-SVG stick figures — rejected as "stick figures, ridiculous"; (2) thicker-stroke stick figures — still read as stick figures; (3) solid joint-blended silhouette icons — technically fine but still not "illustrated" enough; (4) a clothed illustrated figure (shirt/shorts/skin-tone/hair) built from per-pose joint coordinates via inline SVG — better, but still not the reference bar. What actually satisfied the bar was real illustrated artwork, not a geometric approximation. Plan for sourcing real illustration assets (or commissioning/generating a matching set) rather than re-attempting a vector approximation.
- The 23 poses needed: squat, step-up, glute bridge, calf raise, push-up, row, press, plank, dead bug, side plank, anti-rotation hold, cardio machine, mobility (lunge), mobility (hamstring), mobility (shoulder), single-leg RDL/balance, lateral lunge, box jump, loaded carry, rotational core, single-leg hop, deceleration/landing, sprint on crutches.
- **Get the crutches detail right.** The "sprint on crutches" illustration must actually show crutches, not a runner with a prosthetic leg and no crutches. If sourcing from a generated grid with duplicate/near-duplicate cells, visually verify each candidate crop before committing to it. This was a shipped bug once, caught only by careful side-by-side comparison of the two candidate crops.
- Illustration container sizing: 64px in exercise cards, 96px in the workout player, 32px in the compact Log checklist rows, all using the same base CSS class with a size-override modifier.

## 5. Data model (localStorage keys)

All keys share one prefix (e.g. `ampfootballfit_`):

| Key suffix | Shape | Purpose |
|---|---|---|
| `profile` | object | Onboarding answers (see §7), editable later from Settings |
| `program_state` | `{ currentPhase, currentWeek, unlockedPhases[], phaseGateAcknowledgments{}, preferredTierByGroup{} }` | Where the athlete is in the 12-week program |
| `sessions` | array of session objects | Every logged workout (see §9) |
| `skills_drills` | object | User-editable Skills framework content |
| `milestones` | array of `{ id, date, label, note, createdAt }` | User-triggered achievement log |
| `settings` | `{ language, units }` | App-level preferences (units: kg/lb) |
| `profile_photo_path` | string (filename only) | Native-only; never store the image itself in localStorage |
| `onboarding_complete` | `"true"` | Set once, never re-triggers onboarding even if the profile is later cleared |
| `readiness_logs` | array of `{ date, sleep, energy, soreness, limbComfort, note, createdAt }` | Daily readiness check-ins, 1 to 5 scale, 5 always = best |
| `benchmarks` | array of `{ id, testId, date, value, note, createdAt }` | Periodic objective retest results |
| `custom_exercises` | array of `{ id, phaseNumber, category, name, prescription, pose, createdAt }` | Athlete-added exercises not in the built-in program |

Also keep a `last_view` key (tab name) so the app reopens on the last tab used, and a settings-export function that dumps **every** one of these keys as one JSON file, plus a "reset all data" flow that clears every key and the profile photo file together.

## 6. Program structure

- 3 phases, 4 weeks each, 12 weeks total: **Phase 1 "Base"** (weeks 1 to 4), **Phase 2 "Load Progression"** (weeks 5 to 8), **Phase 3 "Sport Transfer"** (weeks 9 to 12).
- 5 exercise categories per phase, in this fixed display order: **Single-Leg Strength, Upper Body, Core, Cardio, Mobility.**
- **Soft phase gating:** unlocking phase N requires at least `4 weeks x 2 sessions/week = 8` logged sessions in phase N-1. If under that, show a dismissible warning ("You've logged X of 8 recommended sessions... Continue anyway?") but never block the transition.
- **Tiered exercise progression:** exercises in the same movement family (e.g. squat, row, calf raise, side plank, dead bug, anti-rotation, box jump, single-leg hop, deceleration/landing, lateral lunge, loaded carry) exist in up to 3 tiers (easier/standard/harder variants of the same movement), each tagged with a `progressionGroup` id, a `tier` number (1 to 3), and `targetSetsMin`/`targetRepsMin`. Only one tier per group is shown at a time (whichever the athlete's current preferred tier is, default tier 2); the other tiers stay hidden but exist in the data.
- **Tier-up/down suggestion engine:** after logging a session, compare against the exercise's target sets/reps and the session's overall RPE (there is no per-exercise RPE, only per-session). Suggest tier-up after 2 consecutive clean sessions (RPE ≤ 4 and prescription met), or 3 consecutive for "elevated risk" movement families (box jump, single-leg hop, deceleration/landing) since those carry more fall/injury risk. Suggest tier-down after a single session with RPE ≥ 8 or an incomplete set/rep count. Suggestions are dismissible prompts the athlete accepts or ignores, never automatic.
- **Suggested load progression:** for any exercise where a load value has been logged before, show "Last: X kg, try Y kg this week" (roughly +5%, or +2.5kg/+5lb for light loads, rounded to a sensible increment) based on the most recent logged load, unless that session's RPE was ≥ 8, in which case suggest repeating the same load. If an exercise looks loadable (its name/prescription mentions load, weight, resistance, a band, cable, carry, dumbbell, kettlebell, or barbell) but has no load history yet, show a lighter "log a load next time to get a suggestion" nudge instead of nothing, so the feature isn't invisible before first use.
- **Estimated/unverified content must be labeled.** Any prescription based on inference rather than sourced data (e.g. match-demand-based interval structures) needs an "Estimate" badge and a plain-language note explaining what's estimated and why, so the athlete knows to adjust it against real observation.
- **Cardio prescriptions need an explicit RPE explanation and a broad equipment list**, since athletes won't all have the same equipment. Don't gate a prescription on one specific machine (e.g. "rowing machine only"): list several accessible alternatives (rowing, arm/upper-body ergometer, stationary bike, brisk crutch-walk, swimming, wheelchair/handcycle) and explain RPE in plain language ("conversational effort, you should be able to speak in full sentences").
- **Custom exercises:** let the athlete add an exercise their physio/coach prescribed that isn't built in. Form fields: phase, category, name, free-text prescription, and a pose picker (reusing the same 23 illustration ids, required so nothing renders blank). Store separately from the built-in program data and merge them in at render time (tag them `isCustom: true`, show a small "Custom" badge), so every existing view (Program cards, Log checklist, the exercise picker, the workout player) picks them up automatically without needing per-view changes. Provide a remove button in a compact management list.
- **Prosthetic-use adaptation:** capture whether the athlete trains with their prosthetic (always / never / varies) during onboarding. If not "always," show a standing, non-blocking reminder on the Program tab that prescriptions assume typical prosthetic use unless noted, and to adapt with a physio/coach where it doesn't match. Do not attempt to auto-rewrite specific exercises per prosthetic status. That judgment call belongs to a clinician, not the app.

## 7. Onboarding (10 steps, runs once)

Gate on a single `onboarding_complete` flag that, once set, never re-triggers, even if the profile is later cleared. Steps, in order:

1. **About your amputation** — amputation level (transtibial/transfemoral/bilateral), side (left/right/both), years since amputation, and "Do you train with your prosthetic?" (yes for most exercises / no, train without it / varies by exercise). All required.
2. **Activity background** — current activity level as a K-level select (K0 to K4) with a "not sure, describe in plain language" fallback (low/moderate/high day-to-day mobility), crutch experience (none/recreational/previously competitive), time since last competitive amp football play.
3. **Fitness & health** — current fitness baseline (sedentary/lightly active/regularly training), optional free-text pain points/injury history (explicitly labeled as "for your own awareness only, not a diagnosis").
4. **Goals & availability** — target (general fitness/return to club/return to competition), training days per week, typical session length in minutes.
5. **Review** — a read-only summary table of everything just entered, with a note that it can all be changed later from Settings. Every empty/unanswered field must show something explicit like "Not set", never a blank or a lone punctuation mark.
6. **Add a profile photo** — optional, native-only (hidden entirely in the browser build with a note explaining why), skippable.
7 to 10: four short info cards, serif headings, explaining respectively: the 12-week/3-phase structure, how exercises adapt as you go (tier suggestions), what gets tracked and that it's all local-only, and what the app is/isn't (a conditioning framework, not a sport-skills coach).

After the final step: save the profile, set the completion flag, and land on the Dashboard.

## 8. Tab-by-tab feature list

**Dashboard**
- Header avatar: the athlete's profile photo (or a placeholder person icon) shown small, next to the "AmpFootballFit" title, in the sticky header that's shared across every tab, not just Dashboard.
- Daily readiness check-in card at the top: 4 dimensions, each a 1 to 5 select (Sleep quality, Energy level, Muscle soreness, Residual limb/socket comfort), all scored so **5 always means best** (so soreness is framed "5 = none, 1 = very sore", not the usual direction), plus an optional note. Once submitted for today, collapse to a one-line summary with an Edit option. A low overall score or specifically low limb/socket comfort should surface a caution note on the Program tab (soft, informational, never blocking).
- Quick milestone logging: 4 preset one-tap buttons plus a custom label + note field. **Every click needs visible feedback**: on click, disable the button and change its label/color briefly (about 1.5 seconds) before reverting, and highlight the newly added entry in the list below with a brief background pulse and a scroll-into-view. Without this, users click repeatedly assuming nothing happened and log duplicates.
- Recent milestones list, newest first, each deletable.

**Program**
- Phase pills (switch phase), week stepper (prev/next, 1 to 12), a progress bar.
- Gate warning banner (see §6), readiness caution banner, prosthetic-use reminder banner: all the same soft warning-card style, all independently shown/hidden.
- "Start Workout" button launching the guided player (see §10).
- Exercise cards per category: illustration, name, a small "Custom" or "Estimate" badge where relevant, prescription text, suggested-load line where relevant, a YouTube search link (`https://www.youtube.com/results?search_query=<name>+exercise+tutorial`, generated, not a hand-picked video), and a "+ Log this" button that jumps to the Log tab with that exercise pre-checked and highlighted.
- "Add a custom exercise" form and management list at the bottom (see §6).

**Log**
- Date, phase, session duration (minutes), overall session RPE (1 to 10).
- A checklist of the current phase's exercises (same illustration as Program, prescription text always visible underneath so the athlete doesn't have to hold it in their head while filling in numbers). Checking a box reveals sets/reps/load number inputs, **except for cardio-category exercises**, which instead show a note pointing back to the session-level duration/RPE fields, since sets/reps/load don't apply there.
- Save button, tier-suggestion prompts appear after saving where applicable, recent sessions list below.

**Progress**
- Session adherence heatmap (12 weeks, calendar-style, color intensity by session count per day).
- Benchmarks & retests: a picker over a fixed set of objective tests (see §11), each with a short "how to perform this test" instruction and its matching illustration shown together, a baseline-vs-latest summary with a colored up/down delta, a trend line chart, a log-a-new-result form, and a history list.
- Readiness trend: a line chart of the daily overall readiness score over time.
- Strength progression: a picker over every exercise ever defined, showing that exercise's matching illustration (not emoji, a native `<select>` can't embed images per option, so show the selection's illustration in a preview box beside the dropdown instead) plus load-over-time and reps-over-time charts.
- Cardio progression: duration and RPE trend for cardio-tagged sessions.
- Milestone timeline.
- "Share progress" button: builds a plain-text summary (current phase/week, last 8 sessions with sets/reps/load, latest benchmark results with baseline deltas, last 5 milestones) and hands it to the native share sheet (`@capacitor/share`) so it can go straight to a coach/physio via whatever messaging app is already on the phone. Fall back to the Web Share API (`navigator.share`) in a browser, and to a plain `.txt` file download if neither is available. This is the only way data leaves the app; there is no accounts/server layer.
- "Export sessions to CSV" as a secondary, lower-emphasis action below Share.

**Skills**
- An explicitly empty, user-editable framework (not pre-filled content) for sport-specific skills, with an upfront note that the app does not provide sport-technique instruction and that video links/sources should be noted by the user. Placed after Progress in the tab order since it's a bonus section, not core fitness tracking.

**Settings**
- Profile photo (native-only, add/change/remove).
- **Editable profile card**: every onboarding field, pre-filled from the current profile, editable and re-savable any time (e.g. activity level moving from K4 down to K3 after a setback). This is essential: onboarding only runs once, so without this the athlete would be stuck with stale answers forever.
- Language (English now; Dutch/French structurally present but not translated, labeled as such), units (kg/lb).
- Data: export all data as one JSON backup (must include every storage key from §5, not just the original ones), reset all data (double-confirmed, also deletes the profile photo file).

## 9. Session log shape

```
{
  id, date, phase, durationMinutes, rpe, notes, createdAt,
  exercises: [
    { id, name, category, sets, reps, load }   // sets/reps/load may be null, esp. for cardio
  ]
}
```

## 10. Guided workout player

A full-screen overlay ("Start Workout" from the Program tab) that walks through today's preferred-tier exercises for the current phase, one at a time: exercise illustration (96px), name, prescription, a running timer, Back/Skip/Done-and-next controls, ending in a finish screen that auto-computes duration and lets the athlete confirm RPE and notes. On save, it must go through the exact same save function as the manual Log tab, so both paths produce identical session records and both feed the same tier-suggestion engine. Confirm before discarding an in-progress session if anything's been entered.

## 11. Benchmarks & readiness reference data

Benchmark tests (id, unit, direction, matching illustration pose):

| Test | Unit | Direction | Pose |
|---|---|---|---|
| Single-leg squat, max reps in 30s | reps | higher is better | squat |
| Single-leg balance hold | seconds | higher is better | single-leg RDL/balance |
| Box jump height | cm | higher is better | box jump |
| Broad jump distance | cm | higher is better | deceleration/landing |
| 10m sprint time | seconds | lower is better | sprint on crutches |
| Plank hold | seconds | higher is better | plank |

Each needs a short, concrete instruction (how to perform and what counts, e.g. "stand on your sound leg, time until first touch-down").

Readiness dimensions (all 1 to 5, 5 always best): Sleep quality, Energy level, Muscle soreness (5 = none), Residual limb/socket comfort (5 = totally comfortable, 1 = irritated/painful). Flag low readiness at an overall average under 3, or limb/socket comfort specifically at 2 or below (that one gets its own lower threshold since skin/socket issues are a harder stop signal than general tiredness).

## 12. Splash screen

A brief personal welcome overlay shown on every app launch (not just once): a circular photo, a title text, auto-dismissing after roughly 2.8 seconds or on tap. Keep this entirely separate from the athlete's own editable profile photo (different storage, different purpose: one is a fixed launch-time greeting, the other is the athlete's own profile picture shown in the header and Settings). If a circular crop needs repositioning, move the *image content* within the fixed circle (scale the image up slightly first so there's overflow to shift, then translate it), never move or resize the circle itself.

## 13. Android packaging specifics

- Capacitor Android project, `minSdkVersion`/`targetSdkVersion` per current Capacitor defaults.
- `MainActivity.onCreate` should call `getBridge().getWebView().clearCache(true)` defensively, so a reinstalled build can't silently keep serving a stale cached version of the bundled web assets.
- Manifest permissions: `INTERNET`, `CAMERA`, `READ_MEDIA_IMAGES` (API 33+) with `READ_EXTERNAL_STORAGE` scoped via `maxSdkVersion="32"` for older versions.
- Bump `versionCode`/`versionName` on every rebuild that changes bundled web content, so installing the new APK over the old one (no uninstall needed) reliably picks up the change. Make clear to the user that uninstalling before reinstalling **deletes all local data** (it's tied to the app's private data directory); in-place install over the existing app is the safe update path, and periodic JSON export is the recommended safety net regardless.
- After every change to the single HTML file: copy it into the Capacitor `www/` folder, run `npx cap sync android`, bump the version, rebuild, and verify the new code is genuinely present in the built APK (e.g. `unzip -p app.apk assets/public/index.html | grep <a marker unique to the change>`) before considering it shipped.

## 14. Working process notes (why some of the above rules exist)

- Confirm the visual/creative direction (illustration style, splash content, etc.) before building it out fully. Guessing wrong on a subjective visual call and having to redo it cost real time and trust in this project; when a request is genuinely ambiguous (especially involving images or a specific "feel"), a quick clarifying question is cheaper than a wrong build.
- Treat any AI-competitor-app inspection as read-only research: permissions/manifest and public marketing material are fair game, decompiling code or extracting/reusing another app's proprietary assets is not.
- Verify claims about what's "shipped" by inspecting the actual built artifact (grep the bundled HTML inside the APK), not just by re-reading your own source file. Trusting "I edited the file" without confirming the built binary contains that edit is how a real caching bug went undiagnosed for multiple rounds in this project's history.
- When a user reports something isn't working after you've verified it does, don't assume they're wrong: ask for or generate independent proof (a screenshot from their device settled one such dispute here), since either your verification or your assumption about what "should" be visible could be the actual gap.
