# Jyotish Sutra

An original, clean-room Android astrology application inspired by the publicly described workflows of professional Indian astrology products. It does not contain MindSutra code, assets, branding, private APIs, or copied prediction text.

## Current milestone

- High-fidelity Material 3 interface based on the approved concept image
- Birth details with coordinates, time zone, and selectable ayanamsha
- Offline tropical-to-sidereal calculation pipeline
- Navagraha positions, signs, degrees, nakshatra, pada, whole-sign houses, and retrograde state
- North Indian Rashi chart
- Panchang: vara, tithi, paksha, nakshatra, yoga, and karana
- Vimshottari Mahadasha timeline
- Ashtakoota compatibility score with an auditable eight-part breakdown
- KP star lord, sub-lord, and sub-sub-lord calculations
- Original rule-based chart insights, yoga detection, and cautious gemstone review
- Locale declaration for the 22 scheduled Indian languages plus English

## Calculation integrity

The bundled `OfflineEphemeris` is a low-precision implementation intended for product development and interface testing. It must not be represented as professional ephemeris parity. Before release, replace it through the `Ephemeris` interface with a properly licensed high-precision ephemeris and validate golden charts against independently sourced reference data.

Prediction text is generated from visible chart evidence. It is cultural/reflection content and not medical, legal, investment, or other professional advice.

## Planned modules

1. High-precision ephemeris adapter and golden-chart validation
2. Bhava cusps and six house systems
3. Shodasha Varga and Ashtakavarga
4. Antar/Pratyantar/Sookshma/Prana Dasha layers plus Ashtottari and Yogini Dasha
5. Transit and Varshaphala
6. Full KP cusp/significator workflow
7. Lal Kitab chart, verified rule corpus, and safe remedies
8. Multilingual resource translation and multilingual PDF generation
9. Secure server-side Gemini explanation service; no API secret will be stored in the APK

## Build requirements

- Android Studio with JDK 17
- Android SDK 36
- Gradle 8.13 (wrapper will be generated when the local Android toolchain is available)

Open the project root in Android Studio, allow Gradle sync, and run the `app` configuration.
