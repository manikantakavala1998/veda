from __future__ import annotations

import json
import math
from datetime import date, datetime, timedelta, timezone
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import urlparse

import swisseph as swe


ROOT = Path(__file__).resolve().parent
HOST = "127.0.0.1"
PORT = 8765

# Offline preview geocoding. Production will use the selected Maps/Places provider.
CITY_COORDINATES = {
    "new delhi": (28.6139, 77.2090), "delhi": (28.6139, 77.2090),
    "mumbai": (19.0760, 72.8777), "kolkata": (22.5726, 88.3639),
    "chennai": (13.0827, 80.2707), "bengaluru": (12.9716, 77.5946),
    "bangalore": (12.9716, 77.5946), "hyderabad": (17.3850, 78.4867),
    "ahmedabad": (23.0225, 72.5714), "pune": (18.5204, 73.8567),
    "jaipur": (26.9124, 75.7873), "lucknow": (26.8467, 80.9462),
    "kochi": (9.9312, 76.2673), "cochin": (9.9312, 76.2673),
    "thiruvananthapuram": (8.5241, 76.9366), "trivandrum": (8.5241, 76.9366),
    "madurai": (9.9252, 78.1198), "coimbatore": (11.0168, 76.9558),
    "mysuru": (12.2958, 76.6394), "mysore": (12.2958, 76.6394),
    "mangaluru": (12.9141, 74.8560), "mangalore": (12.9141, 74.8560),
    "vijayawada": (16.5062, 80.6480), "visakhapatnam": (17.6868, 83.2185),
    "tirupati": (13.6288, 79.4192), "warangal": (17.9689, 79.5941),
    "bhubaneswar": (20.2961, 85.8245), "patna": (25.5941, 85.1376),
    "chandigarh": (30.7333, 76.7794), "guwahati": (26.1445, 91.7362),
    "srinagar": (34.0837, 74.7973), "varanasi": (25.3176, 82.9739),
}

SIGNS = [
    "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
    "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces",
]
NAKSHATRAS = [
    "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra",
    "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni",
    "Uttara Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha",
    "Jyeshtha", "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana",
    "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati",
]
PLANETS = [
    ("Sun", "Su", swe.SUN), ("Moon", "Mo", swe.MOON), ("Mars", "Ma", swe.MARS),
    ("Mercury", "Me", swe.MERCURY), ("Jupiter", "Ju", swe.JUPITER),
    ("Venus", "Ve", swe.VENUS), ("Saturn", "Sa", swe.SATURN),
    ("Rahu", "Ra", swe.MEAN_NODE),
]
TITHIS = [
    "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami", "Shashthi", "Saptami",
    "Ashtami", "Navami", "Dashami", "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima",
    "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami", "Shashthi", "Saptami",
    "Ashtami", "Navami", "Dashami", "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Amavasya",
]
YOGAS = [
    "Vishkambha", "Priti", "Ayushman", "Saubhagya", "Shobhana", "Atiganda", "Sukarma",
    "Dhriti", "Shula", "Ganda", "Vriddhi", "Dhruva", "Vyaghata", "Harshana", "Vajra",
    "Siddhi", "Vyatipata", "Variyana", "Parigha", "Shiva", "Siddha", "Sadhya", "Shubha",
    "Shukla", "Brahma", "Indra", "Vaidhriti",
]
DASHA = [
    ("Ketu", 7), ("Venus", 20), ("Sun", 6), ("Moon", 10), ("Mars", 7),
    ("Rahu", 18), ("Jupiter", 16), ("Saturn", 19), ("Mercury", 17),
]


def normalize(value: float) -> float:
    return value % 360.0


def parse_offset(value: str) -> int:
    sign = -1 if value.startswith("-") else 1
    hours, minutes = value.lstrip("+-").split(":")
    return sign * (int(hours) * 60 + int(minutes))


def coordinates_for_place(place: str) -> tuple[float, float]:
    normalized = " ".join(place.lower().replace(",", " ").split())
    for city, coordinates in CITY_COORDINATES.items():
        if city in normalized:
            return coordinates
    # A safe preview fallback keeps the form clean; production will geocode every place selection.
    return CITY_COORDINATES["new delhi"]


def panchang(local_date: date, sun: float, moon: float) -> dict:
    span = 360.0 / 27.0
    elongation = normalize(moon - sun)
    tithi_index = int(elongation // 12.0)
    half_tithi = int(elongation // 6.0)
    repeating = ["Bava", "Balava", "Kaulava", "Taitila", "Garaja", "Vanija", "Vishti"]
    if half_tithi == 0:
        karana = "Kimstughna"
    elif half_tithi == 57:
        karana = "Shakuni"
    elif half_tithi == 58:
        karana = "Chatushpada"
    elif half_tithi == 59:
        karana = "Naga"
    else:
        karana = repeating[(half_tithi - 1) % 7]
    vara = ["Somavara", "Mangalavara", "Budhavara", "Guruvara", "Shukravara", "Shanivara", "Ravivara"]
    return {
        "vara": vara[local_date.weekday()],
        "tithi": f"{'Shukla' if tithi_index < 15 else 'Krishna'} {TITHIS[tithi_index]}",
        "nakshatra": NAKSHATRAS[int(moon // span)],
        "yoga": YOGAS[int(normalize(sun + moon) // span)],
        "karana": karana,
    }


def vimshottari(birth_date: date, moon: float) -> list[dict]:
    span = 360.0 / 27.0
    nakshatra = int(moon // span)
    lord_index = nakshatra % 9
    traversed = (moon % span) / span
    cursor = birth_date - timedelta(days=round(DASHA[lord_index][1] * traversed * 365.2425))
    periods = []
    for offset in range(10):
        lord, years = DASHA[(lord_index + offset) % 9]
        end = cursor + timedelta(days=round(years * 365.2425))
        periods.append({"lord": lord, "years": years, "start": cursor.isoformat(), "end": end.isoformat()})
        cursor = end
    return periods


def calculate(payload: dict) -> dict:
    name = str(payload.get("name") or "Guest")[:80]
    local_date = date.fromisoformat(payload["date"])
    local_time = datetime.strptime(payload["time"], "%H:%M").time()
    offset_minutes = parse_offset(payload.get("offset", "+05:30"))
    place = str(payload.get("place") or "")[:120]
    latitude, longitude = coordinates_for_place(place)
    mode_name = payload.get("ayanamsa", "lahiri")
    modes = {"lahiri": swe.SIDM_LAHIRI, "raman": swe.SIDM_RAMAN, "kp": swe.SIDM_KRISHNAMURTI}
    swe.set_sid_mode(modes.get(mode_name, swe.SIDM_LAHIRI))

    local_dt = datetime.combine(local_date, local_time, tzinfo=timezone(timedelta(minutes=offset_minutes)))
    utc = local_dt.astimezone(timezone.utc)
    hour = utc.hour + utc.minute / 60.0 + utc.second / 3600.0
    jd = swe.julday(utc.year, utc.month, utc.day, hour)
    flags = swe.FLG_SWIEPH | swe.FLG_SIDEREAL | swe.FLG_SPEED
    _, ascmc = swe.houses_ex(jd, latitude, longitude, b"P", swe.FLG_SIDEREAL)
    ascendant = normalize(ascmc[0])
    asc_sign = int(ascendant // 30.0)
    positions = []
    for planet_name, short, planet_id in PLANETS:
        values = swe.calc_ut(jd, planet_id, flags)[0]
        lon = normalize(values[0])
        sign = int(lon // 30.0)
        nak = int(lon // (360.0 / 27.0))
        positions.append({
            "name": planet_name,
            "short": short,
            "longitude": round(lon, 6),
            "sign": SIGNS[sign],
            "signIndex": sign,
            "degree": round(lon % 30.0, 4),
            "nakshatra": NAKSHATRAS[nak],
            "pada": int((lon % (360.0 / 27.0)) // (360.0 / 108.0)) + 1,
            "house": ((sign - asc_sign + 12) % 12) + 1,
            "retrograde": values[3] < 0 or planet_name == "Rahu",
        })
    rahu = next(p for p in positions if p["name"] == "Rahu")
    ketu_lon = normalize(rahu["longitude"] + 180.0)
    ketu_sign = int(ketu_lon // 30.0)
    positions.append({
        "name": "Ketu", "short": "Ke", "longitude": ketu_lon,
        "sign": SIGNS[ketu_sign], "signIndex": ketu_sign,
        "degree": round(ketu_lon % 30.0, 4),
        "nakshatra": NAKSHATRAS[int(ketu_lon // (360.0 / 27.0))],
        "pada": int((ketu_lon % (360.0 / 27.0)) // (360.0 / 108.0)) + 1,
        "house": ((ketu_sign - asc_sign + 12) % 12) + 1, "retrograde": True,
    })
    sun = next(p["longitude"] for p in positions if p["name"] == "Sun")
    moon = next(p["longitude"] for p in positions if p["name"] == "Moon")
    return {
        "name": name,
        "date": local_date.isoformat(),
        "time": local_time.strftime("%H:%M"),
        "place": place,
        "julianDay": jd,
        "ascendant": {"longitude": ascendant, "sign": SIGNS[asc_sign], "signIndex": asc_sign, "degree": ascendant % 30.0},
        "planets": positions,
        "panchang": panchang(local_date, sun, moon),
        "dasha": vimshottari(local_date, moon),
    }


class Handler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(ROOT), **kwargs)

    def do_POST(self):
        if urlparse(self.path).path != "/api/chart":
            self.send_error(404)
            return
        try:
            length = min(int(self.headers.get("Content-Length", "0")), 16_384)
            payload = json.loads(self.rfile.read(length))
            response = json.dumps(calculate(payload), ensure_ascii=False).encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.send_header("Content-Length", str(len(response)))
            self.end_headers()
            self.wfile.write(response)
        except Exception as exc:
            response = json.dumps({"error": str(exc)}).encode("utf-8")
            self.send_response(400)
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.send_header("Content-Length", str(len(response)))
            self.end_headers()
            self.wfile.write(response)

    def log_message(self, fmt, *args):
        print(fmt % args, flush=True)


if __name__ == "__main__":
    print(f"Jyotish Sutra preview: http://{HOST}:{PORT}", flush=True)
    ThreadingHTTPServer((HOST, PORT), Handler).serve_forever()
