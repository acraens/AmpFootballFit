# AmpFootballFit — Claude Code Project Brief

## 1. Purpose

A free, offline-capable, single-file web app to support return-to-play physical
conditioning for amputee football (crutch-based, single-leg-support sport),
built for a 64-year-old K4-active left transtibial amputee returning to the
sport after a layoff, with a design broad enough to be reused by any amputee
footballer or club (e.g. distributed to OH Leuven Amputee Football Team
players via the community staff contact).

**Sport context the app must respect:**
- Amputee football outfield players compete on forearm crutches, sound leg
  only, no prosthesis on the pitch.
- Physical demands are fundamentally different from prosthetic-based sport:
  massive shoulder/arm load-bearing, single-leg power and balance, trunk
  stability, and repeated-sprint cardiovascular capacity.
- This app is NOT a general lower-limb-amputee fitness app (that project —
  AmputeeActive — already exists and covers daily/prosthetic-based training).
  This app is sport-specific to amputee football return-to-play.

## 2. Honesty constraint (must be respected in all copy and content)

Do not present this app as delivering expert-level amputee football coaching.
The **Physical Conditioning module** is built on well-established strength and
conditioning principles and can be built with full confidence. The **Sport
Skills module** must be structured as a framework with placeholder/empty drill
slots the user fills in themselves from real coaching sources (a physio,
video footage, or club sessions) — it must NOT contain invented technique
instructions presented as expert guidance. Any drill content seeded into the
Sport Skills module must be clearly marked with its source, or marked
`[USER TO SUPPLY — verify with a coach before use]` if none exists.

## 3. User profile fields (onboarding)

- Amputation level and side (dropdown: transtibial, transfemoral, bilateral;
  left/right/both)
- Years since amputation
- Current activity level (K-level if known, or plain-language fallback)
- Crutch experience: none / recreational / previously competitive
- Time since last competitive amp football play (if any)
- Current fitness baseline: sedentary / lightly active / regularly training
- Any pain points or injury history (free text, flagged — not diagnosed)
- Target: general fitness / return to club training / return to competition
- Available training days per week and session length

## 4. Physical Conditioning Module

12-week progressive program, three phases, two sessions/week minimum
(scalable to 4):

**Phase 1 — Base (Weeks 1–4): Foundation**
- Single-leg strength: bodyweight squats (assisted), step-ups, glute bridges,
  calf raises on sound leg
- Upper body: seated/incline push-ups, resistance band rows, shoulder presses
  — light load, high rep, endurance focus (crutch-loading demand is
  submaximal but sustained)
- Core: planks, dead bugs, side planks, anti-rotation holds
- Cardio: low-intensity steady-state (rowing, arm ergometer, or crutch-walk
  intervals if accessible)
- Mobility: hip flexor, hamstring, shoulder/thoracic mobility daily

**Phase 2 — Load Progression (Weeks 5–8)**
- Single-leg strength: loaded step-ups, single-leg RDLs (support as needed),
  lateral lunges on sound leg, box jumps (low height, controlled landing)
- Upper body: increase load on presses/rows/pulls, add explosive
  push variations (e.g. plyo push-ups if cleared)
- Core: loaded carries (farmer's walk equivalent using crutches if safe),
  rotational core work
- Cardio: interval work — 30s/30s or 20s/40s work:rest ratios, building
  toward match-intensity bursts
- Mobility: maintained, add dynamic warm-up sequences

**Phase 3 — Sport Transfer (Weeks 9–12)**
- Single-leg strength: single-leg hops/bounds (progression to power),
  deceleration/landing mechanics
- Upper body: maximal strength blocks (lower rep, higher load) plus
  continued muscular endurance work — both qualities needed for
  crutch-based sprint mechanics
- Core: dynamic stability under fatigue (e.g. planks post-interval-cardio)
- Cardio: repeated-sprint-style intervals matching estimated match demand
  (research actual amp football match structure — see Section 6)
- Mobility: maintained

**Progression logic:** phase gates require the user to confirm completion of
a minimum session count in the prior phase before the next phase unlocks
(soft gate — warn, don't hard-block, since the user may need to repeat weeks).

## 5. Sport Skills Module (framework, not content-complete)

Structure only — each slot starts empty or marked for user input:

- **Crutch mobility drills**: weight-shift drills, controlled
  acceleration/deceleration, turning and pivoting on crutches, falling and
  recovery technique — `[USER TO SUPPLY — verify with a coach before use]`
- **Ball skills on crutches**: dribbling stance and control, striking
  technique and balance points, passing — `[USER TO SUPPLY]`
- **Agility**: cone/ladder drill adaptations for crutch use —
  `[USER TO SUPPLY]`
- **Video link field**: each drill slot should allow the user to paste a
  YouTube/video URL as a reference (opens externally — app does not embed
  or host video)
- **Source/verified-by field**: free text, e.g. "Bruges training session,
  observed [date]" or "Coach [name], [date]" — so the user can track which
  drills came from a credible source vs. self-devised

## 6. Research the app-build process should do (flag, don't fabricate)

Claude Code should NOT invent match-demand data (sprint counts, distances,
work:rest ratios for actual amp football matches). If this data is not
reliably available, the Phase 3 cardio section should use a conservative,
clearly-labeled placeholder ("estimated — adjust based on actual match
observation") rather than presenting invented statistics as fact.

## 7. Session Tracker

- Log per session: date, phase, exercises completed, sets/reps/load, RPE
  (1–10), session duration, notes
- Sport Skills drills logged separately with a completion checkbox and
  optional notes
- Milestone flags: "first full-intensity single-leg hop," "first crutch
  sprint at full pace," "first full training session with club," "first
  competitive minutes played" — user-triggered, timestamped

## 8. Progress Dashboard

- Strength progression charts per exercise (load/reps over time)
- Cardio interval progression (work duration, recovery, perceived effort
  trend)
- Session adherence heatmap (calendar view, like AmputeeActive's)
- Milestone timeline
- Export to CSV

## 9. Technical Requirements

- Single HTML file (`ampfootballfit.html`), inline CSS and JavaScript, no
  build step, no external dependencies requiring network access (charting
  can use a lightweight inline solution — no CDN dependency, to preserve
  offline capability)
- All data in `localStorage`, namespaced separately from any AmputeeActive
  data (`ampfootballfit_*` keys)
- Mobile-first responsive design (this will be used pitch-side and in gym
  settings, often on a phone)
- Language: English default, with structure ready for Dutch/French (OH
  Leuven club distribution) — full translation can be a later pass; build
  the string-table architecture now
- No account system, no server, no data leaves the device

## 10. Data Schema (localStorage keys)

```
ampfootballfit_profile        — onboarding data (JSON)
ampfootballfit_program_state  — current phase, week, unlock status
ampfootballfit_sessions       — array of logged conditioning sessions
ampfootballfit_skills_drills  — user-defined drill library + completion log
ampfootballfit_milestones     — array of {date, label, note}
ampfootballfit_settings       — language, units (kg/lb), etc.
```

## 11. Build Order for Claude Code

1. Scaffold single HTML file with tab navigation (Dashboard / Program /
   Skills / Log / Progress / Settings)
2. Build onboarding flow (Section 3) → writes `ampfootballfit_profile`
3. Build Physical Conditioning program data structure (Section 4) with
   phase-gating logic
4. Build Program tab UI — displays current week's sessions, exercise cards
5. Build Session Tracker (Section 7) — logging UI, writes to
   `ampfootballfit_sessions`
6. Build Sport Skills module (Section 5) as an empty/user-editable
   framework — do not seed invented drill content
7. Build Progress Dashboard (Section 8) — charts from logged session data
8. Build Milestone tracking UI
9. Build Settings tab (language placeholder, units, data export/reset)
10. Test phase-gate logic end-to-end with sample data
11. Test on mobile viewport sizes
12. Final review pass: confirm no invented sport-technique content exists
    anywhere in the shipped file
13. Output final file to project folder as `ampfootballfit.html`

Confirm understanding of this brief before starting, and confirm after each
numbered build step before proceeding to the next.
