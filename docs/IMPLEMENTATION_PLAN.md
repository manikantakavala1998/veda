# Implementation plan

The product combines the public functional categories associated with E-Kundali, Red Astro, KPLogy, and Astrology & Remedies in one original application. Work is staged so every displayed result can be traced to a calculation or reviewed rule.

## Milestone 1 — app foundation (implemented)

- Original Material 3 visual system matching the approved concept direction
- Home, birth form, Rashi chart, planets, Panchang, Dasha, matching, tools, and settings
- Offline chart model shared by every specialist module
- Lahiri, Raman, and Krishnamurti ayanamsha options
- Locale declaration for 22 scheduled Indian languages plus English

## Milestone 2 — verified Vedic core (in progress)

- High-precision ephemeris adapter
- Sun through Saturn, mean/true nodes, ascendant, midheaven, and bhava cusps
- Retrograde, combustion, dignity, aspects, and planetary relationships
- North, South, East, and Western chart rendering
- Shodasha Varga with rule-specific division logic
- Ashtakavarga and Sarvashtakavarga
- Golden charts across dates, time zones, latitudes, and edge cases

Release gate: professional calculations must match a licensed reference ephemeris and reviewed Vedic test charts within documented tolerances.

## Milestone 3 — timing systems

- Vimshottari through Mahadasha, Antardasha, Pratyantardasha, Sookshma, and Prana
- Ashtottari and Yogini Dasha
- Transit chart and Saturn Sade Sati
- Varshaphala with annual ascendant and Mudda Dasha

## Milestone 4 — marriage matching

- Reviewed Ashtakoota rule tables and regional exceptions
- Manglik/Kuja Dosha with cancellation rules
- Dwadashakoota and Dosha Samyam
- Individual chart factors and transparent evidence for every score

## Milestone 5 — KP astrology

- Star, sub, and sub-sub lords (foundation implemented)
- Placidus cusps using KP ayanamsha
- Cusp sub-lords, significators, ruling planets, and horary numbers
- Event judgment shown as traceable rule evidence

## Milestone 6 — Lal Kitab

- Fixed-house work chart (foundation implemented)
- Reviewed edition-specific rules for sleeping/awakened planets, artificial planets, debts, and Varshaphala
- Safe traditional practices separated from predictive rules
- No dangerous, illegal, discriminatory, or medical claims

## Milestone 7 — predictions and remedies

- Original rule engine covering personality, education, career, relationships, resources, health themes, and life periods
- Yoga detection with strength and cancellation checks
- Gemstone review based on functional beneficence, dignity, dasha, contraindications, and user confirmation
- Rudraksha, mantra, charity, and behavioral practices with cultural context and safety notices

## Milestone 8 — language and reports

- Human-reviewed terminology glossary for all 23 target languages
- Localized interface and bidirectional layout testing
- Translated prediction templates with English fallback
- Embedded Noto fonts for multilingual PDF reports
- Accessible sharing, printing, and saved-chart management

## Milestone 9 — Gemini explanation layer

- The deterministic engine remains the source of every number and rule result
- A server endpoint sends only structured, user-approved chart evidence to Gemini
- Gemini explains results in the selected language; it never invents planetary positions
- API keys remain on the server, never in the APK
- Output schema validation, prompt-injection resistance, rate limits, consent, deletion, and audit logging

If “Gemini” refers to the zodiac sign rather than Google's model, it is already handled as Mithuna/Gemini in the sign system and will receive localized predictions with the other eleven signs.
