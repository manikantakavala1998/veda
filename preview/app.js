const previewParams = new URLSearchParams(location.search);
const requestedScreen = previewParams.get('screen');
const state = {
  screen: ['home','create','chart','planets','panchang','dasha','tools','settings','palmistry'].includes(requestedScreen) ? requestedScreen : 'home',
  chart: null,
  division: 'rashi',
  chartStyle: ['north','south','east'].includes(previewParams.get('style')) ? previewParams.get('style') : (localStorage.getItem('chartStyle') || 'north'),
  language: previewParams.get('lang') || localStorage.getItem('appLanguage') || 'English',
  palmStep: ['landing','camera','analysing','map','report'].includes(previewParams.get('palm')) ? previewParams.get('palm') : 'landing',
  palmHand: 'right',
  palmDominance: 'dominant',
  palmTab: 'lines',
  palmImage: null,
  palmReport: null,
  palmStream: null
};

const app = document.querySelector('#app');
const bottomNav = document.querySelector('#bottomNav');
const SIGNS = ['Aries','Taurus','Gemini','Cancer','Leo','Virgo','Libra','Scorpio','Sagittarius','Capricorn','Aquarius','Pisces'];
const SIGN_GLYPHS = ['♈','♉','♊','♋','♌','♍','♎','♏','♐','♑','♒','♓'];
const PLANET_META = {
  Sun: ['☉','#f48223'], Moon: ['☾','#70778b'], Mars: ['♂','#c92e2e'], Mercury: ['☿','#348b42'],
  Jupiter: ['♃','#d28b12'], Venus: ['♀','#ed4f86'], Saturn: ['♄','#633071'], Rahu: ['☊','#3f4c68'], Ketu: ['☋','#934735']
};
const CHART_PLANET_COLORS = {
  Sun:'#44212a', Moon:'#282b76', Mars:'#b13236', Mercury:'#44212a', Jupiter:'#3d2422',
  Venus:'#4b2130', Saturn:'#3d2422', Rahu:'#3d2422', Ketu:'#3d2422'
};
const LANGUAGES = [
  ['English','English'],['অসমীয়া','Assamese'],['বাংলা','Bengali'],['बड़ो','Bodo'],['डोगरी','Dogri'],
  ['ગુજરાતી','Gujarati'],['हिन्दी','Hindi'],['ಕನ್ನಡ','Kannada'],['کٲشُر','Kashmiri'],['कोंकणी','Konkani'],
  ['मैथिली','Maithili'],['മലയാളം','Malayalam'],['মৈতৈলোন্','Manipuri'],['मराठी','Marathi'],['नेपाली','Nepali'],
  ['ଓଡ଼ିଆ','Odia'],['ਪੰਜਾਬੀ','Punjabi'],['संस्कृतम्','Sanskrit'],['ᱥᱟᱱᱛᱟᱲᱤ','Santali'],['سنڌي','Sindhi'],
  ['தமிழ்','Tamil'],['తెలుగు','Telugu'],['اردو','Urdu']
];
const demoPayload = { name:'Arjun', date:'1990-05-15', time:'10:30', place:'New Delhi, India', offset:'+05:30', ayanamsa:'lahiri' };

function esc(value='') { return String(value).replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c])); }
function t(key) { return window.APP_TRANSLATIONS?.[state.language]?.[key] || window.APP_TRANSLATIONS?.English?.[key] || key; }
function applyLocale() {
  const rtl=['Urdu','Kashmiri','Sindhi'].includes(state.language);
  document.documentElement.lang=window.APP_LANGUAGE_CODES?.[state.language]||'en';
  document.documentElement.dir=rtl?'rtl':'ltr';
  document.body.classList.toggle('rtl',rtl);
  const labels={home:t('home'),planets:t('insights'),tools:t('tools'),settings:t('profile')};
  bottomNav.querySelectorAll('button').forEach(button=>{const label=button.querySelector('span:last-child');if(label)label.textContent=labels[button.dataset.screen];});
}
function degrees(value) { const d=Math.floor(value), m=Math.floor((value-d)*60); return `${String(d).padStart(2,'0')}° ${String(m).padStart(2,'0')}′`; }
function toast(message) { const node=document.querySelector('#toast'); node.textContent=message; node.classList.add('show'); setTimeout(()=>node.classList.remove('show'),2200); }
function setScreen(screen) {
  stopPalmCamera();
  state.screen = screen;
  if (screen === 'palmistry' && !state.palmStep) state.palmStep = 'landing';
  document.querySelectorAll('.bottom-nav button').forEach(b => b.classList.toggle('active', b.dataset.screen === screen || (screen==='chart' && b.dataset.screen==='planets') || (screen==='palmistry' && b.dataset.screen==='tools')));
  render(); app.scrollTop=0;
}
function screenTop(title, action='') {
  return `<header class="screen-top"><button class="icon-button back" data-back aria-label="Back">‹</button><h1>${title}</h1>${action}</header>`;
}
function render() {
  applyLocale();
  bottomNav.hidden = state.screen === 'palmistry' && ['camera','analysing'].includes(state.palmStep);
  document.querySelectorAll('.bottom-nav button').forEach(b => b.classList.toggle('active', b.dataset.screen === state.screen || (state.screen==='chart' && b.dataset.screen==='planets') || (state.screen==='palmistry' && b.dataset.screen==='tools')));
  if (state.screen==='home') renderHome();
  else if (state.screen==='create') renderForm();
  else if (state.screen==='chart') renderChart();
  else if (state.screen==='planets') renderPlanets();
  else if (state.screen==='panchang') renderPanchang();
  else if (state.screen==='dasha') renderDasha();
  else if (state.screen==='tools') renderTools();
  else if (state.screen==='palmistry') renderPalmistry();
  else renderSettings();
  app.querySelectorAll('[data-back]').forEach(b => b.onclick=()=>setScreen('home'));
}

function feature(icon,title,subtitle,screen) {
  return `<article class="feature-card" data-go="${screen}"><div class="feature-top"><span class="round-icon">${icon}</span><span class="mini-arrow">›</span></div><h3>${title}</h3><p>${subtitle}</p></article>`;
}
function renderHome() {
  const name = state.chart?.name || 'Arjun';
  const moon = state.chart?.planets?.find(p=>p.name==='Moon');
  app.innerHTML = `<div class="home-screen">
    <section class="hero"><div class="hero-brand"><span class="sun-mark">☼</span>Jyotish Sutra<span class="bell">♧</span></div><div class="hero-copy"><h1>${t('welcome')},<br>${esc(name)}</h1><p>${t('cosmicBlueprint')}</p></div></section>
    <section class="home-content">
      <article class="moon-card" data-go="chart"><div class="moon-orb">◐</div><div><h2>${t('moon')} · ${esc(moon?.nakshatra || 'Rohini')}</h2><p>${t('moonDescription')}</p></div><span class="chevron">›</span></article>
      <div class="feature-grid">${feature('◎',t('birthChart'),t('exploreKundali'),'create')}${feature('▣',t('panchang'),t('panchangSubtitle'),'panchang')}${feature('♄',t('dasha'),t('dashaSubtitle'),'dasha')}${feature('♡',t('compatibility'),t('harmony'),'tools')}${feature('✋',t('palmistry'),t('palmistrySubtitle'),'palmistry')}</div>
    </section></div>`;
  app.querySelectorAll('[data-go]').forEach(el=>el.onclick=()=>setScreen(el.dataset.go));
}

function field(icon,label,name,value,type='text',end='') {
  return `<div class="field"><span class="field-icon">${icon}</span><div class="field-content"><label for="${name}">${label}</label><div class="input-wrap"><input id="${name}" name="${name}" value="${esc(value)}" type="${type}" required>${end?`<span class="input-end">${end}</span>`:''}</div></div></div>`;
}
function renderForm() {
  app.innerHTML = `<div class="inner-screen form-screen"><span class="form-sun">☼</span>
    <header class="screen-top"><button class="icon-button back" data-back>‹</button><h1>${t('createKundali')}</h1></header>
    <form class="form-body" id="birthForm"><p class="form-intro">${t('formIntro')}</p>
      ${field('♙',t('name'),'name',state.chart?.name||demoPayload.name)}
      ${field('□',t('birthDate'),'date',state.chart?.date||demoPayload.date,'date','▣')}
      ${field('◷',t('birthTime'),'time',state.chart?.time||demoPayload.time,'time','◷')}
      <div class="field"><span class="field-icon">⌖</span><div class="field-content"><label for="place">${t('birthPlace')}</label><div class="input-wrap"><input id="place" name="place" value="${esc(state.chart?.place||demoPayload.place)}" list="cities" autocomplete="address-level2" required><span class="input-end">⌕</span></div></div></div>
      <datalist id="cities"><option>New Delhi, India</option><option>Chennai, India</option><option>Bengaluru, India</option><option>Hyderabad, India</option><option>Mumbai, India</option><option>Kochi, India</option><option>Madurai, India</option><option>Coimbatore, India</option><option>Thiruvananthapuram, India</option></datalist>
      <div class="field"><span class="field-icon">◎</span><div class="field-content"><label for="offset">${t('utcOffset')}</label><div class="input-wrap"><select id="offset" name="offset"><option value="+05:30">+5:30 (IST)</option><option value="+05:45">+5:45 (NPT)</option><option value="+06:00">+6:00</option><option value="+04:00">+4:00</option><option value="+00:00">UTC</option></select><span class="input-end">⌄</span></div></div></div>
      <input type="hidden" name="ayanamsa" value="lahiri"><div class="error" id="formError"></div>
      <button class="primary" type="submit">✦ &nbsp; ${t('calculateChart')}</button><div class="lotus-divider">— ♧ —</div><p class="form-note">${t('coordinatesAuto')}</p>
    </form></div>`;
  document.querySelector('#birthForm').onsubmit = calculate;
}
async function calculate(event) {
  event.preventDefault();
  const payload=Object.fromEntries(new FormData(event.currentTarget).entries());
  const button=event.currentTarget.querySelector('.primary'); button.disabled=true; button.textContent=t('calculating');
  try {
    const response=await fetch('/api/chart',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload)});
    const data=await response.json(); if(!response.ok) throw new Error(data.error||'Calculation failed');
    state.chart=data; state.division='rashi'; setScreen('chart'); toast(t('chartCalculated'));
  } catch(error) {
    document.querySelector('#formError').textContent=error.message; button.disabled=false; button.textContent=`✦  ${t('calculateChart')}`;
  }
}

function navamsaSign(longitude) {
  const sign=Math.floor(longitude/30), part=Math.min(8,Math.floor((longitude%30)/(30/9)));
  return ([0,9,6,3][sign%4]+part)%12;
}
function divisionData(chart) {
  if(state.division!=='navamsha') return {asc:chart.ascendant.signIndex, planets:chart.planets.map(p=>({...p, renderSign:p.signIndex, renderHouse:p.house}))};
  const asc=navamsaSign(chart.ascendant.longitude);
  return {asc, planets:chart.planets.map(p=>{const s=navamsaSign(p.longitude);return {...p,renderSign:s,renderHouse:((s-asc+12)%12)+1};})};
}
function occupants(data,key,index) { return data.planets.filter(p=>p[key]===index).map(p=>p.short+(p.retrograde?'℞':'')).join(' '); }
function chartLabel(data,key,index,x,y,signLabel) {
  const bodies=data.planets.filter(p=>p[key]===index);
  const lineStep=bodies.length>3?11:15;
  const startY=y-(Math.max(0,bodies.length-1)*lineStep/2);
  const planetLines=bodies.map((p,i)=>{const meta=PLANET_META[p.name]||['●','#5f435f'];return `<tspan class="planet-label" style="fill:${CHART_PLANET_COLORS[p.name]||'#44212a'};font-size:${bodies.length>3?11:15}px" x="${x}" ${i===0?`y="${startY}"`:`dy="${lineStep}"`}>${meta[0]} ${p.short}${p.retrograde?'℞':''}</tspan>`;}).join('');
  return `<text x="${x}" y="${startY}">${planetLines}<tspan class="sign-label" x="${x}" ${bodies.length?'dy="15"':`y="${y}"`}>${signLabel}</tspan></text>`;
}

function chartDefs() {
  return `<defs><radialGradient id="parchment" cx="50%" cy="47%" r="72%"><stop offset="0" stop-color="#fff9e9"/><stop offset=".58" stop-color="#fff6e4"/><stop offset="1" stop-color="#f9ecd3"/></radialGradient><radialGradient id="omGlow"><stop offset="0" stop-color="#fffaf0"/><stop offset="1" stop-color="#f7dda7"/></radialGradient></defs>`;
}
function chartWatermark() {
  const rays=Array.from({length:16},(_,i)=>{const a=i*Math.PI/8,x1=342+Math.cos(a)*23,y1=51+Math.sin(a)*23,x2=342+Math.cos(a)*34,y2=51+Math.sin(a)*34;return `<line x1="${x1.toFixed(1)}" y1="${y1.toFixed(1)}" x2="${x2.toFixed(1)}" y2="${y2.toFixed(1)}"/>`;}).join('');
  return `<g class="chart-watermark"><circle cx="342" cy="51" r="18"/><circle cx="342" cy="51" r="8"/>${rays}</g>`;
}
function omSeal() {
  return `<g class="om-seal"><circle cx="200" cy="200" r="27"/><circle cx="200" cy="200" r="21"/><text class="om-center" x="200" y="211">ॐ</text></g>`;
}

function northChart(data) {
  const coords=[[200,66],[104,45],[46,108],[87,200],[46,296],[104,357],[200,326],[296,357],[354,296],[313,200],[354,108],[296,45]];
  const labels=coords.map((xy,i)=>{const sign=(data.asc+i)%12;return chartLabel(data,'renderHouse',i+1,xy[0],xy[1],`${SIGN_GLYPHS[sign]} ${sign+1}`);}).join('');
  return `<svg class="chart-svg" viewBox="0 0 400 400" aria-label="North Indian chart">${chartDefs()}<rect class="paper-bg" x="3" y="3" width="394" height="394"/>${chartWatermark()}<g class="chart-geometry"><rect x="3" y="3" width="394" height="394"/><line x1="3" y1="3" x2="397" y2="397"/><line x1="397" y1="3" x2="3" y2="397"/><line x1="200" y1="3" x2="397" y2="200"/><line x1="397" y1="200" x2="200" y2="397"/><line x1="200" y1="397" x2="3" y2="200"/><line x1="3" y1="200" x2="200" y2="3"/></g>${labels}${omSeal()}</svg>`;
}
function southChart(data) {
  const cells=[{s:11,x:50,y:50},{s:0,x:150,y:50},{s:1,x:250,y:50},{s:2,x:350,y:50},{s:10,x:50,y:150},{s:3,x:350,y:150},{s:9,x:50,y:250},{s:4,x:350,y:250},{s:8,x:50,y:350},{s:7,x:150,y:350},{s:6,x:250,y:350},{s:5,x:350,y:350}];
  const grid=[100,200,300].map(v=>`<line x1="${v}" y1="2" x2="${v}" y2="398"/><line x1="2" y1="${v}" x2="398" y2="${v}"/>`).join('');
  const labels=cells.map(c=>chartLabel(data,'renderSign',c.s,c.x,c.y-7,`${SIGN_GLYPHS[c.s]} ${SIGNS[c.s].slice(0,3)}${c.s===data.asc?' • Asc':''}`)).join('');
  return `<svg class="chart-svg" viewBox="0 0 400 400" aria-label="South Indian chart">${chartDefs()}<rect class="paper-bg" x="2" y="2" width="396" height="396"/>${chartWatermark()}<g class="chart-geometry"><rect x="2" y="2" width="396" height="396"/>${grid}<rect x="100" y="100" width="200" height="200"/></g>${omSeal()}${labels}</svg>`;
}
function eastChart(data) {
  const pos=[[200,44],[95,44],[45,110],[45,200],[45,300],[95,356],[200,356],[305,356],[355,300],[355,200],[355,110],[305,44]];
  const labels=pos.map((p,i)=>{const sign=(data.asc+i)%12;return chartLabel(data,'renderHouse',i+1,p[0],p[1],`${SIGN_GLYPHS[sign]} ${sign+1}`);}).join('');
  return `<svg class="chart-svg" viewBox="0 0 400 400" aria-label="East Indian chart">${chartDefs()}<rect class="paper-bg" x="3" y="3" width="394" height="394"/>${chartWatermark()}<g class="chart-geometry"><rect x="3" y="3" width="394" height="394"/><path d="M3 100h394M3 300h394M100 3v394M300 3v394M100 100l100 100-100 100M300 100L200 200l100 100"/></g>${omSeal()}${labels}</svg>`;
}
function renderChartGraphic(data) { return state.chartStyle==='south'?southChart(data):state.chartStyle==='east'?eastChart(data):northChart(data); }
function renderChart() {
  if(!state.chart) { renderForm(); return; }
  const c=state.chart, moon=c.planets.find(p=>p.name==='Moon'), data=divisionData(c);
  app.innerHTML=`<div class="inner-screen">${screenTop(t('rashiKundali'))}<div class="segmented">${['rashi','navamsha','bhava'].map(d=>`<button data-division="${d}" class="${state.division===d?'active':''}">${t(d)}</button>`).join('')}</div>
    <div class="chart-style-picker">${[['north','north'],['south','south'],['east','east']].map(s=>`<button data-style="${s[0]}" class="${state.chartStyle===s[0]?'active':''}">${t(s[1])}</button>`).join('')}</div>
    <section class="chart-content"><div class="chart-box">${renderChartGraphic(data)}</div>
      <div class="summary-row"><div class="summary-chip"><span class="summary-icon moon-icon">☾</span><div><b>${moon.sign}</b><span>${t('moon')}</span></div></div><div class="summary-chip"><span class="summary-icon">${SIGN_GLYPHS[c.ascendant.signIndex]}</span><div><b>${c.ascendant.sign}</b><span>${t('lagna')}</span></div></div><div class="summary-chip"><span class="summary-icon star-icon">✺</span><div><b>${moon.nakshatra}</b><span>${t('pada')} ${moon.pada}</span></div></div></div>
      <article class="insight-card" id="openInsights"><h3><span>☼</span>${t('chartInsights')}</h3><p>${t('insightBody').replace('{nakshatra}',moon.nakshatra)} &nbsp;›</p></article>
    </section></div>`;
  app.querySelectorAll('[data-division]').forEach(b=>b.onclick=()=>{state.division=b.dataset.division;renderChart();});
  app.querySelectorAll('[data-style]').forEach(b=>b.onclick=()=>{state.chartStyle=b.dataset.style;localStorage.setItem('chartStyle',state.chartStyle);renderChart();});
  document.querySelector('#openInsights').onclick=()=>setScreen('planets');
}

function renderPlanets() {
  if(!state.chart) { renderForm(); return; }
  const planets=state.chart.planets;
  app.innerHTML=`<div class="inner-screen">${screenTop(t('planetaryPositions'),'<button class="icon-button share" id="share">⌯</button>')}<section class="planet-content"><div class="planet-table"><div class="planet-row header"><span>${t('planet')}</span><span>${t('sign')}</span><span>${t('degree')}</span></div>${planets.map(p=>{const meta=PLANET_META[p.name]||['●','#6a536c'],glyph=SIGN_GLYPHS[p.signIndex];return `<div class="planet-row"><span class="planet-name"><i class="planet-symbol" style="background:${meta[1]}" aria-label="${p.name} symbol">${meta[0]}</i><span>${p.name}${p.retrograde?' <small class="retrograde">℞</small>':''}</span></span><span class="planet-sign"><i class="zodiac-glyph" style="color:${meta[1]}" aria-label="${p.sign} sign">${glyph}</i><span>${p.sign}</span></span><span>${degrees(p.degree)}</span></div>`}).join('')}</div><article class="key-card"><h3><span>✧</span>${t('keyTakeaway')}</h3><p>${t('takeaway')}</p></article></section></div>`;
  document.querySelector('#share').onclick=async()=>{try{await navigator.share({title:'Jyotish Sutra',text:`${state.chart.name}'s Vedic planetary positions`});}catch{toast('Chart summary ready to share');}};
}

function renderPanchang() {
  if(!state.chart) { renderForm(); return; }
  app.innerHTML=`<div class="inner-screen">${screenTop(t('dailyPanchang'))}<h2 class="page-title" style="font-size:22px">${esc(state.chart.date)}</h2><section class="list-content">${Object.entries(state.chart.panchang).map(([k,v])=>`<article class="list-card"><span>${k[0].toUpperCase()+k.slice(1)}</span><b>${esc(v)}</b></article>`).join('')}</section></div>`;
}
function renderDasha() {
  if(!state.chart) { renderForm(); return; }
  const today=new Date().toISOString().slice(0,10);
  app.innerHTML=`<div class="inner-screen">${screenTop(t('vimshottariDasha'))}<section class="list-content">${state.chart.dasha.map(d=>`<article class="list-card ${d.start<=today&&today<d.end?'current':''}"><div class="dasha-lord">${d.lord.slice(0,2)}</div><div class="dasha-copy"><b>${d.lord} Mahadasha</b><small>${d.start} — ${d.end}</small></div><b>${d.years}y</b></article>`).join('')}</section></div>`;
}
function renderTools() {
  const tools=[['✋',t('palmistry'),t('palmistrySubtitle'),'palmistry'],['◎','Vedic Kundali','Rashi, Navamsha and Bhava charts','chart'],['✦','Vimshottari Dasha','Major and sub-period timeline','dasha'],['♡','Marriage Matching','Ashtakoota compatibility',''],['▣','Daily Panchang','Tithi, Nakshatra and Yoga','panchang'],['▤','Lal Kitab','Fixed-house work chart',''],['⌘','KP Astrology','Star and sub-lords',''],['♢','Remedies','Traditional mindful practices',''],['✺','Gemini Guide','Multilingual explanations','']];
  app.innerHTML=`<div class="inner-screen"><h1 class="page-title">${t('astrologyTools')}</h1><section class="tools-grid">${tools.map(tool=>`<article class="tool-card" data-go="${tool[3]}"><span class="round-icon">${tool[0]}</span><h3>${tool[1]}</h3><p>${tool[2]}</p>${tool[3]?'':`<div class="badge">${t('comingNext')}</div>`}</article>`).join('')}</section></div>`;
  app.querySelectorAll('.tool-card').forEach(el=>el.onclick=()=>el.dataset.go?setScreen(el.dataset.go):toast('This specialist module is in the next milestone'));
}

const PALM_LINES = [
  {key:'heart', name:'Heart Line', color:'#e33c56', path:'M72 155 C125 132 183 140 238 164'},
  {key:'head', name:'Head Line', color:'#2678d8', path:'M70 184 C122 174 179 193 235 207'},
  {key:'life', name:'Life Line', color:'#17955b', path:'M83 163 C48 203 59 271 112 314'},
  {key:'fate', name:'Fate Line', color:'#b96822', path:'M157 319 C154 266 163 216 158 166'},
  {key:'sun', name:'Sun Line', color:'#ef8b2f', path:'M199 284 C202 236 205 193 207 149'},
  {key:'mercury', name:'Mercury Line', color:'#9a36bd', path:'M226 278 C230 232 235 196 247 164'}
];
const PALM_MOUNTS = [
  ['Jupiter',112,95,'#f3b64f'],['Saturn',153,82,'#dfc375'],['Apollo',195,91,'#f7cd48'],['Mercury',232,118,'#d99abf'],
  ['Upper Mars',72,190,'#d67d5e'],['Lower Mars',77,253,'#c97c66'],['Venus',94,258,'#e7a091'],['Moon',222,267,'#a595d7'],['Plain of Mars',156,207,'#8fc3bd']
];
const PALM_MARKS = [['⑂','Fork'],['✚','Cross'],['□','Square'],['△','Triangle'],['◇','Island'],['◒','Break'],['✣','Star']];

function palmDrawing(mode='lines') {
  const outline='<path class="palm-outline" d="M88 337 C67 302 52 259 57 217 C60 187 68 163 79 148 L75 90 C74 72 84 61 96 64 C105 66 109 75 109 91 L110 132 L116 53 C118 35 128 25 141 29 C151 32 154 42 153 58 L153 128 L162 43 C164 25 176 17 188 22 C198 26 201 36 199 53 L194 132 L205 62 C208 45 219 38 231 43 C241 47 243 58 240 73 L229 151 C251 164 262 185 257 219 C251 262 226 303 201 339 C175 366 113 363 88 337 Z"/>';
  const creases='<path class="palm-crease" d="M95 322 C132 334 173 333 207 316 M94 287 C120 273 139 264 161 259 M106 114 L108 78 M148 111 L150 62 M190 115 L194 61 M226 128 L235 83"/>';
  let layer='';
  if (mode==='mounts') layer=PALM_MOUNTS.map(m=>`<g><circle class="mount-zone" cx="${m[1]}" cy="${m[2]}" r="25" fill="${m[3]}"/><circle class="mount-dot" cx="${m[1]}" cy="${m[2]}" r="4" fill="#a96818"/></g>`).join('');
  else layer=PALM_LINES.map(l=>`<path class="major-line" d="${l.path}" stroke="${l.color}"/>`).join('');
  const marks=mode==='markings'?'<g class="palm-marks"><circle cx="120" cy="181" r="10"/><text x="120" y="186">✚</text><circle cx="177" cy="221" r="10"/><text x="177" y="226">△</text><circle cx="207" cy="249" r="10"/><text x="207" y="254">✣</text></g>':'';
  return `<svg class="palm-svg" viewBox="0 0 300 380" role="img" aria-label="Annotated palm diagram"><defs><radialGradient id="palmPaper"><stop offset="0" stop-color="#fffdf6"/><stop offset="1" stop-color="#f7e6c8"/></radialGradient></defs><circle class="palm-halo" cx="150" cy="190" r="143"/>${outline}${layer}${creases}${marks}</svg>`;
}

function renderPalmistry() {
  if (state.palmStep==='camera') return renderPalmCamera();
  if (state.palmStep==='analysing') return renderPalmAnalysing();
  if (state.palmStep==='map') return renderPalmMap();
  if (state.palmStep==='report') return renderPalmReport();
  renderPalmLanding();
}

function palmBackButton(target='landing') { return `<button class="icon-button" data-palm-back="${target}" aria-label="Back">‹</button>`; }
function bindPalmBack() { app.querySelectorAll('[data-palm-back]').forEach(b=>b.onclick=()=>{stopPalmCamera();if(b.dataset.palmBack==='tools'){setScreen('tools');return;}state.palmStep=b.dataset.palmBack;render();}); }
function palmHeader(title,target='landing',action='') { return `<header class="screen-top palm-top">${palmBackButton(target)}<h1>${title}</h1>${action}</header>`; }

function renderPalmLanding() {
  app.innerHTML=`<div class="inner-screen palm-screen palm-landing">${palmHeader(t('palmistry'),'tools')}
    <section class="palm-intro-card"><div class="palm-zodiac">♈　♉　♊　♋　♌　♍　♎</div>${palmDrawing('lines')}<h2>${t('discoverHands')}</h2><p>${t('palmIntro')}</p></section>
    <section class="palm-actions"><div class="hand-switch"><button data-hand="left" class="${state.palmHand==='left'?'active':''}">${t('leftHand')}</button><button data-hand="right" class="${state.palmHand==='right'?'active':''}">${t('rightHand')}</button></div>
      <div class="dominance-row"><span>${t('handRole')}</span><button data-dominance="dominant" class="${state.palmDominance==='dominant'?'active':''}">${t('dominant')}</button><button data-dominance="nonDominant" class="${state.palmDominance==='nonDominant'?'active':''}">${t('nonDominant')}</button></div>
      <button class="palm-primary" id="scanPalm"><span>▣</span>${t('scanPalm')}<b>›</b></button>
      <button class="palm-secondary" id="uploadPalm"><span>▧</span>${t('uploadImage')}<b>›</b></button><input id="palmUpload" type="file" accept="image/*" hidden>
      <article class="privacy-card"><span>▣</span><div><b>${t('palmPrivate')}</b><small>${t('palmPrivateBody')}</small></div></article>
      <p class="palm-disclaimer">${t('palmDisclaimer')}</p></section></div>`;
  bindPalmBack();
  app.querySelectorAll('[data-hand]').forEach(b=>b.onclick=()=>{state.palmHand=b.dataset.hand;renderPalmLanding();});
  app.querySelectorAll('[data-dominance]').forEach(b=>b.onclick=()=>{state.palmDominance=b.dataset.dominance;renderPalmLanding();});
  document.querySelector('#scanPalm').onclick=()=>{state.palmStep='camera';render();};
  document.querySelector('#uploadPalm').onclick=()=>document.querySelector('#palmUpload').click();
  document.querySelector('#palmUpload').onchange=e=>loadPalmFile(e.target.files?.[0]);
}

function renderPalmCamera() {
  app.innerHTML=`<div class="palm-camera-screen"><header class="palm-camera-header">${palmBackButton('landing')}<h1>${t('scanYourPalm')}</h1><span></span></header>
    <div class="camera-stage"><video id="palmVideo" autoplay muted playsinline></video><div class="camera-stars"></div><div class="guide-corners"></div><div class="camera-hand-guide">${palmDrawing('lines')}</div><span class="hand-label">${state.palmHand==='right'?t('rightHand'):t('leftHand')}</span></div>
    <div class="camera-instruction">ⓘ　${t('fitHand')}</div><div class="quality-chips"><span id="lightChip">✓ ${t('goodLight')}</span><span id="alignChip">✓ ${t('handAligned')}</span></div>
    <div class="camera-controls"><button id="cameraGallery" class="camera-side">▧<small>${t('gallery')}</small></button><button id="capturePalm" class="shutter" aria-label="Capture palm"></button><button id="cameraFlash" class="camera-side">ϟ<small>${t('flash')}</small></button></div><input id="cameraUpload" type="file" accept="image/*" hidden>
    <button class="demo-scan" id="demoPalm">${t('tryDemo')}</button></div>`;
  bindPalmBack();
  document.querySelector('#cameraGallery').onclick=()=>document.querySelector('#cameraUpload').click();
  document.querySelector('#cameraUpload').onchange=e=>loadPalmFile(e.target.files?.[0]);
  document.querySelector('#capturePalm').onclick=capturePalmFrame;
  document.querySelector('#demoPalm').onclick=()=>beginPalmAnalysis(null);
  document.querySelector('#cameraFlash').onclick=e=>{e.currentTarget.classList.toggle('active');toast(t('flashHint'));};
  startPalmCamera();
}

async function startPalmCamera() {
  const video=document.querySelector('#palmVideo'); if(!video) return;
  try { state.palmStream=await navigator.mediaDevices.getUserMedia({video:{facingMode:'environment',width:{ideal:1280},height:{ideal:1280}},audio:false}); video.srcObject=state.palmStream; }
  catch { document.querySelector('.camera-stage')?.classList.add('camera-fallback'); document.querySelector('#lightChip').textContent=`ⓘ ${t('cameraUnavailable')}`; }
}
function stopPalmCamera() { if(state.palmStream){state.palmStream.getTracks().forEach(track=>track.stop());state.palmStream=null;} }
function loadPalmFile(file) { if(!file)return; const reader=new FileReader();reader.onload=()=>beginPalmAnalysis(String(reader.result));reader.readAsDataURL(file); }
function capturePalmFrame() {
  const video=document.querySelector('#palmVideo');
  if(!video?.videoWidth){beginPalmAnalysis(null);return;}
  const canvas=document.createElement('canvas'),size=Math.min(video.videoWidth,video.videoHeight);canvas.width=size;canvas.height=size;
  canvas.getContext('2d').drawImage(video,(video.videoWidth-size)/2,(video.videoHeight-size)/2,size,size,0,0,size,size);beginPalmAnalysis(canvas.toDataURL('image/jpeg',.9));
}
function beginPalmAnalysis(dataUrl) { stopPalmCamera();state.palmImage=dataUrl;state.palmStep='analysing';render();analyzePalmImage(dataUrl).then(report=>{state.palmReport=report;state.palmStep='map';render();}); }

function renderPalmAnalysing() {
  app.innerHTML=`<div class="palm-analysing"><div class="analysis-orbit"><span>✋</span></div><h1>${t('analysingPalm')}</h1><p>${t('analysisSteps')}</p><div class="analysis-bar"><i></i></div><small>${t('analysisPrivacy')}</small></div>`;
}

function demoPalmReport() {
  return {quality:92,qualityLabel:'Excellent',handType:'Air–Fire blend',areas:9,marks:7,
    lines:{heart:{score:82,label:'Curved and clear'},head:{score:88,label:'Long with gentle slope'},life:{score:86,label:'Deep and continuous'},fate:{score:66,label:'Moderate'},sun:{score:61,label:'Developing'},mercury:{score:73,label:'Clear'}},
    mounts:{Jupiter:78,Saturn:67,Apollo:76,Mercury:70,'Upper Mars':74,'Lower Mars':71,Venus:82,Moon:77,'Plain of Mars':69}};
}
function analyzePalmImage(dataUrl) {
  if(!dataUrl) return new Promise(resolve=>setTimeout(()=>resolve(demoPalmReport()),1050));
  return new Promise(resolve=>{const img=new Image();img.onload=()=>{const canvas=document.createElement('canvas');canvas.width=160;canvas.height=160;const ctx=canvas.getContext('2d',{willReadFrequently:true});ctx.drawImage(img,0,0,160,160);const px=ctx.getImageData(0,0,160,160).data;let lum=0,lumSq=0,skin=0,edge=0,samples=0;const gray=new Float32Array(25600);for(let i=0;i<25600;i++){const r=px[i*4],g=px[i*4+1],b=px[i*4+2],y=.299*r+.587*g+.114*b;gray[i]=y;lum+=y;lumSq+=y*y;if(r>75&&g>30&&b>15&&r>g&&r>b&&Math.max(r,g,b)-Math.min(r,g,b)>12)skin++;}for(let y=1;y<159;y+=2)for(let x=1;x<159;x+=2){const i=y*160+x;edge+=Math.abs(gray[i]-gray[i-1])+Math.abs(gray[i]-gray[i-160]);samples++;}const mean=lum/25600,contrast=Math.sqrt(Math.max(0,lumSq/25600-mean*mean)),coverage=skin/25600,sharp=edge/samples;const quality=Math.round(Math.max(38,Math.min(98,55+contrast*.32+sharp*.45-Math.abs(142-mean)*.12+Math.min(.35,coverage)*35)));const metric=(offset)=>Math.round(Math.max(48,Math.min(94,58+(contrast%19)+(sharp%13)+offset-(mean%7))));const report=demoPalmReport();report.quality=quality;report.qualityLabel=quality>=85?'Excellent':quality>=70?'Good':'Usable';report.handType=coverage>.55?'Earth–Fire blend':contrast>62?'Air–Fire blend':'Water–Air blend';Object.entries(report.lines).forEach(([k,v],i)=>{v.score=metric(i*4-7);v.label=v.score>82?(k==='head'?'Long and clear':'Deep and clear'):v.score>68?'Balanced and visible':'Fine and developing';});Object.keys(report.mounts).forEach((k,i)=>report.mounts[k]=metric((i%4)*3-5));setTimeout(()=>resolve(report),700);};img.onerror=()=>setTimeout(()=>resolve(demoPalmReport()),700);img.src=dataUrl;});
}

function renderPalmMap() {
  const report=state.palmReport||demoPalmReport(),mode=state.palmTab;
  const labels=mode==='lines'?PALM_LINES.map(l=>`<span class="diagram-label" style="color:${l.color}">${l.name}</span>`).join(''):mode==='mounts'?PALM_MOUNTS.map(m=>`<span class="diagram-label">${m[0]}</span>`).join(''):PALM_MARKS.map(m=>`<span class="diagram-label">${m[0]} ${m[1]}</span>`).join('');
  app.innerHTML=`<div class="inner-screen palm-screen">${palmHeader(t('cheiroPalmMap'),'landing')}<div class="analysis-complete">● ${t('analysisComplete')} <b>${report.quality}%</b></div>
    <div class="palm-tabs">${['lines','mounts','markings'].map(tab=>`<button data-palm-tab="${tab}" class="${mode===tab?'active':''}">${t(tab)}</button>`).join('')}</div>
    <section class="palm-map-card"><div class="map-art">${palmDrawing(mode)}</div><div class="diagram-labels ${mode}">${labels}</div></section>
    ${mode==='markings'?`<div class="marking-legend">${PALM_MARKS.map(m=>`<span><b>${m[0]}</b>${m[1]}</span>`).join('')}</div>`:`<article class="palm-insight"><span>☼</span><div><b>${mode==='mounts'?t('mountsIdentified').replace('{count}',report.areas):t('majorLines')}</b><small>${t('tapFeature')}</small></div></article>`}
    <button class="palm-primary map-cta" id="viewPalmReport">${t('viewFullReading')}<b>›</b></button></div>`;
  bindPalmBack();app.querySelectorAll('[data-palm-tab]').forEach(b=>b.onclick=()=>{state.palmTab=b.dataset.palmTab;renderPalmMap();});document.querySelector('#viewPalmReport').onclick=()=>{state.palmStep='report';render();};
}

function lineCard(icon,title,value,copy){return `<article class="reading-card"><span>${icon}</span><div><b>${title}</b><strong>${value}</strong><small>${copy}</small></div><i>›</i></article>`;}
function renderPalmReport() {
  const r=state.palmReport||demoPalmReport();
  app.innerHTML=`<div class="inner-screen palm-screen">${palmHeader(t('yourPalmReading'),'map','<button class="icon-button share" id="sharePalm">⌯</button>')}<section class="profile-card"><span class="palm-seal">✋</span><div><h2>${t('cheiroAnalysis')}</h2><p>${state.palmHand==='right'?t('rightHand'):t('leftHand')} • ${state.palmDominance==='dominant'?t('activeTendencies'):t('inheritedTendencies')}</p></div><em>✓ ${t('scanQuality')}<b>${r.qualityLabel}</b></em></section>
    <div class="report-tabs"><button class="active">${t('overview')}</button><button data-open-tab="lines">${t('lines')}</button><button data-open-tab="mounts">${t('mounts')}</button><button data-open-tab="markings">${t('markings')}</button></div>
    <section class="reading-grid">${lineCard('≈',t('handType'),r.handType,t('handTypeCopy'))}${lineCard('♡','Heart Line',r.lines.heart.label,t('heartCopy'))}${lineCard('⌁','Head Line',r.lines.head.label,t('headCopy'))}${lineCard('⌒','Life Line',r.lines.life.label,t('lifeCopy'))}${lineCard('▥','Fate Line',r.lines.fate.label,t('fateCopy'))}${lineCard('✋',t('thumbFingers'),t('balancedWill'),t('thumbCopy'))}</section>
    <article class="life-phases"><h3>☼ ${t('lifePhases')}</h3><div><span>♧<b>${t('earlyYears')}</b><small>${t('learningDiscovery')}</small></span><span>☼<b>${t('midYears')}</b><small>${t('growthRelationships')}</small></span><span>♧<b>${t('laterYears')}</b><small>${t('wisdomContribution')}</small></span></div></article>
    <div class="report-actions"><button id="compareHands">↻<span>${t('compareHands')}</span></button><button id="savePalm">▯<span>${t('saveReading')}</span></button><button id="sharePalm2">⇧<span>${t('shareReport')}</span></button></div>
    <button class="delete-palm" id="deletePalm">▣　${t('deletePalm')}</button><p class="report-disclaimer">ⓘ　${t('palmDisclaimer')}</p></div>`;
  bindPalmBack();app.querySelectorAll('[data-open-tab]').forEach(b=>b.onclick=()=>{state.palmTab=b.dataset.openTab;state.palmStep='map';render();});
  const share=async()=>{const text=`Jyotish Sutra Cheiro reading: ${r.handType}; Heart ${r.lines.heart.label}; Head ${r.lines.head.label}; Life ${r.lines.life.label}. Cultural reflection only.`;try{await navigator.share({title:'My Palm Reading',text});}catch{navigator.clipboard?.writeText(text);toast(t('reportReady'));}};
  document.querySelector('#sharePalm').onclick=share;document.querySelector('#sharePalm2').onclick=share;document.querySelector('#savePalm').onclick=()=>{localStorage.setItem('savedPalmReport',JSON.stringify(r));toast(t('readingSaved'));};
  document.querySelector('#compareHands').onclick=()=>{state.palmHand=state.palmHand==='right'?'left':'right';state.palmStep='landing';toast(t('scanOtherHand'));render();};
  document.querySelector('#deletePalm').onclick=()=>{state.palmImage=null;state.palmReport=null;localStorage.removeItem('savedPalmReport');state.palmStep='landing';toast(t('palmDeleted'));render();};
}
function renderSettings() {
  app.innerHTML=`<div class="inner-screen"><h1 class="page-title">${t('profileSettings')}</h1><section class="settings"><h2>${t('chartStyle')}</h2><select class="select-control" id="chartStyle"><option value="south" ${state.chartStyle==='south'?'selected':''}>${t('south')}</option><option value="north" ${state.chartStyle==='north'?'selected':''}>${t('north')}</option><option value="east" ${state.chartStyle==='east'?'selected':''}>${t('east')}</option></select><h2>${t('calculationStandard')}</h2><select class="select-control"><option>Lahiri / Chitrapaksha</option><option>B. V. Raman</option><option>Krishnamurti</option></select><h2>${t('language')}</h2><div class="language-list">${LANGUAGES.map(l=>`<div class="language ${state.language===l[1]?'active':''}" data-language="${l[1]}"><div><b>${l[0]}</b><br><small>${l[1]}</small></div><span>${state.language===l[1]?'✓':''}</span></div>`).join('')}</div><p class="disclaimer">${t('disclaimer')}</p></section></div>`;
  document.querySelector('#chartStyle').onchange=e=>{state.chartStyle=e.target.value;localStorage.setItem('chartStyle',state.chartStyle);toast(`${e.target.options[e.target.selectedIndex].text} chart selected`);};
  app.querySelectorAll('.language').forEach(el=>el.onclick=()=>{state.language=el.dataset.language;localStorage.setItem('appLanguage',state.language);render();toast(`${state.language} · ${t('selected')}`);});
}

bottomNav.querySelectorAll('button').forEach(button=>button.onclick=()=>setScreen(button.dataset.screen));
render();
fetch('/api/chart',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(demoPayload)}).then(r=>r.json()).then(data=>{state.chart=data;render();}).catch(()=>{});
