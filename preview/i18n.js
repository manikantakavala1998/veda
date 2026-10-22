(function () {
  const en = {
    home:'Home', insights:'Insights', tools:'Tools', profile:'Profile', welcome:'Namaste',
    cosmicBlueprint:'Your cosmic blueprint', moon:'Moon', moonDescription:'Nurturing, creative and deeply intuitive',
    birthChart:'Birth Chart', exploreKundali:'Explore your kundali', panchang:'Panchang', panchangSubtitle:'Tithi, Nakshatra, Yoga',
    dasha:'Dasha', dashaSubtitle:'Planetary periods', compatibility:'Compatibility', harmony:'Check harmony',
    createKundali:'Create Kundali', formIntro:'Enter your birth details to generate your Vedic chart', name:'Name',
    birthDate:'Birth date', birthTime:'Birth time', birthPlace:'Birth place', utcOffset:'UTC offset',
    calculateChart:'Calculate Chart', coordinatesAuto:'Coordinates are selected automatically from the birth place.',
    calculating:'Calculating…', chartCalculated:'Kundali calculated', rashiKundali:'Rashi Kundali', rashi:'Rashi',
    navamsha:'Navamsha', bhava:'Bhava', north:'North Indian', south:'South Indian', east:'East Indian',
    lagna:'Lagna', pada:'Pada', chartInsights:'Chart Insights',
    insightBody:'Moon in {nakshatra} gives a nurturing, creative nature. Tap to explore planetary positions and detailed interpretation.',
    planetaryPositions:'Planetary Positions', planet:'Planet', sign:'Sign', degree:'Degree', keyTakeaway:'Key Takeaway',
    takeaway:'A balanced blend of planetary drive and elemental stability shapes your unique life path.',
    dailyPanchang:'Daily Panchang', vimshottariDasha:'Vimshottari Dasha', astrologyTools:'Astrology Tools',
    comingNext:'COMING NEXT', profileSettings:'Profile & Settings', chartStyle:'Chart style',
    calculationStandard:'Calculation standard', language:'Language',
    disclaimer:'All 22 scheduled Indian languages plus English. Astrology is provided for cultural and reflective use.', selected:'Selected',
    palmistry:'Palmistry', palmistrySubtitle:'Cheiro lines, mounts and hand reading', discoverHands:'Discover the story in your hands',
    palmIntro:'Explore traditional line, mount, finger and hand-shape interpretations with a guided palm scan.',
    leftHand:'Left Hand', rightHand:'Right Hand', handRole:'Hand role', dominant:'Dominant', nonDominant:'Non-dominant',
    scanPalm:'Scan Palm', uploadImage:'Upload Image', palmPrivate:'Your palm image stays private',
    palmPrivateBody:'You control saved images and can delete them anytime.', scanYourPalm:'Scan Your Palm', fitHand:'Fit your whole hand inside the guide',
    goodLight:'Good light', handAligned:'Hand aligned', gallery:'Gallery', flash:'Flash', tryDemo:'Try the guided demo', flashHint:'Use your device camera flash when available', cameraUnavailable:'Camera unavailable — use Gallery or demo',
    analysingPalm:'Reading palm features', analysisSteps:'Checking image quality, hand proportions, lines, mounts and visible markings.', analysisPrivacy:'The interpretation runs from detected features; the image is not uploaded by this preview.',
    cheiroPalmMap:'Cheiro Palm Map', analysisComplete:'Analysis complete', lines:'Lines', mounts:'Mounts', markings:'Markings',
    mountsIdentified:'{count} mount areas identified', majorLines:'Major lines in your palm', tapFeature:'Tap a tab to inspect each traditional feature group.', viewFullReading:'View Full Reading',
    yourPalmReading:'Your Palm Reading', cheiroAnalysis:'Cheiro Analysis', activeTendencies:'Active tendencies', inheritedTendencies:'Inherited tendencies', scanQuality:'Scan quality', overview:'Overview',
    handType:'Hand Type', handTypeCopy:'Palm proportions and texture pattern.', heartCopy:'Traditional emotional-expression pattern.', headCopy:'Traditional thinking and focus pattern.', lifeCopy:'Vitality and life approach — not lifespan.', fateCopy:'Traditional direction and responsibility pattern.',
    thumbFingers:'Thumb & Fingers', balancedWill:'Balanced will and reason', thumbCopy:'Spacing and proportion considered together.', lifePhases:'Life phases', earlyYears:'Early years', midYears:'Mid years', laterYears:'Later years', learningDiscovery:'Learning and discovery', growthRelationships:'Growth and relationships', wisdomContribution:'Wisdom and contribution',
    compareHands:'Compare Both Hands', saveReading:'Save Reading', shareReport:'Share Report', deletePalm:'Delete palm image and reading', reportReady:'Reading copied and ready to share', readingSaved:'Palm reading saved on this device', scanOtherHand:'Now scan the other hand', palmDeleted:'Palm image and saved reading deleted',
    palmDisclaimer:'A traditional cultural interpretation for personal reflection — not medical, legal or financial advice.'
  };
  const coreKeys = ['home','insights','tools','profile','welcome','cosmicBlueprint','birthChart','panchang','dasha','compatibility','createKundali','name','birthDate','birthTime','birthPlace','calculateChart','profileSettings','chartStyle','calculationStandard','language','selected'];
  const core = values => Object.fromEntries(coreKeys.map((key,index)=>[key,values[index]]));
  const translations = {
    English: en,
    Assamese: core(['ঘৰ','অন্তৰ্দৃষ্টি','সঁজুলি','প্ৰ’ফাইল','নমস্কাৰ','আপোনাৰ মহাজাগতিক নক্সা','জন্ম কুণ্ডলী','পঞ্জিকা','দশা','সামঞ্জস্য','কুণ্ডলী সৃষ্টি কৰক','নাম','জন্ম তাৰিখ','জন্ম সময়','জন্ম স্থান','কুণ্ডলী গণনা কৰক','প্ৰ’ফাইল আৰু ছেটিংছ','কুণ্ডলী শৈলী','গণনা মানদণ্ড','ভাষা','নিৰ্বাচিত']),
    Bengali: core(['হোম','অন্তর্দৃষ্টি','সরঞ্জাম','প্রোফাইল','নমস্কার','আপনার মহাজাগতিক নকশা','জন্মকুণ্ডলী','পঞ্জিকা','দশা','সামঞ্জস্য','কুণ্ডলী তৈরি করুন','নাম','জন্ম তারিখ','জন্ম সময়','জন্মস্থান','কুণ্ডলী গণনা করুন','প্রোফাইল ও সেটিংস','কুণ্ডলী ধরন','গণনার মান','ভাষা','নির্বাচিত']),
    Bodo: core(["न'खर",'नोजोर','आइजें','प्रफाइल','खुरुमजों','नोंथांनि महरगोनां नक्सा','जोनोम कुण्डली','पन्जिका','दसा','गोरोबनाय','कुण्डली बानाय','मुं','जोनोम खालार','जोनोम सम','जोनोम जायगा','कुण्डली हिसाब खालाम','प्रफाइल आरो सेटिं','कुण्डली आदब','हिसाब खान्थि','राव',"सायख'बाय"]),
    Dogri: core(['घर','जानकारी','औजार','प्रोफाइल','नमस्कार','तुंदा ब्रह्मांडीय नक्शा','जन्म कुंडली','पंचांग','दशा','मेल','कुंडली बनाओ','नां','जन्म तरीक','जन्म समां','जन्म थाह्र','कुंडली गणना करो','प्रोफाइल ते सेटिंग्स','कुंडली शैली','गणना मानक','भाशा','चुनेआ गेआ']),
    Gujarati: core(['હોમ','અંતર્દૃષ્ટિ','સાધનો','પ્રોફાઇલ','નમસ્તે','તમારી બ્રહ્માંડીય રૂપરેખા','જન્મ કુંડળી','પંચાંગ','દશા','સુસંગતતા','કુંડળી બનાવો','નામ','જન્મ તારીખ','જન્મ સમય','જન્મ સ્થળ','કુંડળી ગણો','પ્રોફાઇલ અને સેટિંગ્સ','કુંડળી શૈલી','ગણતરી ધોરણ','ભાષા','પસંદ કર્યું']),
    Hindi: {
      ...core(['घर','अंतर्दृष्टि','उपकरण','प्रोफ़ाइल','नमस्ते','आपकी ब्रह्मांडीय रूपरेखा','जन्म कुंडली','पंचांग','दशा','अनुकूलता','कुंडली बनाएँ','नाम','जन्म तिथि','जन्म समय','जन्म स्थान','कुंडली गणना करें','प्रोफ़ाइल और सेटिंग्स','कुंडली शैली','गणना मानक','भाषा','चयनित']),
      moon:'चंद्रमा', moonDescription:'पोषणकारी, रचनात्मक और गहन अंतर्ज्ञानी', exploreKundali:'अपनी कुंडली देखें', panchangSubtitle:'तिथि, नक्षत्र, योग', dashaSubtitle:'ग्रह काल', harmony:'सामंजस्य जाँचें',
      formIntro:'अपनी वैदिक कुंडली बनाने हेतु जन्म विवरण दर्ज करें', utcOffset:'UTC अंतर', coordinatesAuto:'जन्म स्थान से निर्देशांक स्वतः चुने जाते हैं।', calculating:'गणना हो रही है…', chartCalculated:'कुंडली तैयार है',
      rashiKundali:'राशि कुंडली', rashi:'राशि', navamsha:'नवांश', bhava:'भाव', north:'उत्तर भारतीय', south:'दक्षिण भारतीय', east:'पूर्व भारतीय', lagna:'लग्न', pada:'पाद', chartInsights:'कुंडली अंतर्दृष्टि',
      insightBody:'{nakshatra} में चंद्रमा पोषणकारी और रचनात्मक स्वभाव देता है। ग्रह स्थितियों का विवरण देखने के लिए टैप करें।', planetaryPositions:'ग्रह स्थितियाँ', planet:'ग्रह', sign:'राशि', degree:'अंश', keyTakeaway:'मुख्य निष्कर्ष',
      takeaway:'ग्रहों की ऊर्जा और तत्वों का संतुलन आपके विशिष्ट जीवन पथ को आकार देता है।', dailyPanchang:'दैनिक पंचांग', vimshottariDasha:'विंशोत्तरी दशा', astrologyTools:'ज्योतिष उपकरण', comingNext:'शीघ्र आ रहा है',
      disclaimer:'भारत की सभी 22 अनुसूचित भाषाएँ और अंग्रेज़ी समर्थित हैं। ज्योतिष सांस्कृतिक और आत्मचिंतन उपयोग के लिए है।',
      palmistry:'हस्तरेखा', palmistrySubtitle:'कायरो पद्धति से रेखाएँ और पर्वत', discoverHands:'अपने हाथों की कहानी जानें', leftHand:'बायाँ हाथ', rightHand:'दायाँ हाथ', scanPalm:'हथेली स्कैन करें', uploadImage:'चित्र अपलोड करें', scanYourPalm:'अपनी हथेली स्कैन करें', yourPalmReading:'आपका हस्तरेखा फल', lines:'रेखाएँ', mounts:'पर्वत', markings:'चिह्न', viewFullReading:'पूरा फल देखें'
    },
    Kannada: core(['ಮುಖಪುಟ','ಒಳನೋಟಗಳು','ಸಾಧನಗಳು','ಪ್ರೊಫೈಲ್','ನಮಸ್ತೆ','ನಿಮ್ಮ ವಿಶ್ವ ನಕ್ಷೆ','ಜನ್ಮ ಕುಂಡಲಿ','ಪಂಚಾಂಗ','ದಶಾ','ಹೊಂದಾಣಿಕೆ','ಕುಂಡಲಿ ರಚಿಸಿ','ಹೆಸರು','ಹುಟ್ಟಿದ ದಿನಾಂಕ','ಹುಟ್ಟಿದ ಸಮಯ','ಹುಟ್ಟಿದ ಸ್ಥಳ','ಜಾತಕ ಲೆಕ್ಕಿಸಿ','ಪ್ರೊಫೈಲ್ ಮತ್ತು ಸೆಟ್ಟಿಂಗ್‌ಗಳು','ಕುಂಡಲಿ ಶೈಲಿ','ಲೆಕ್ಕಾಚಾರ ಮಾನದಂಡ','ಭಾಷೆ','ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ']),
    Kashmiri: core(['گَرٕ','بصیرت','اوزار','پروفایل','نمسکار','تُہنز کائناتی خاکہ','جنم کُنڈلی','پنچانگ','دشا','مطابقت','کُنڈلی بنایِو','ناو','جنم تاریخ','جنم وقت','جنم جاے','کُنڈلی حساب کریو','پروفایل تہٕ سیٹنگز','کُنڈلی انداز','حساب معیار','زبان','منتخب']),
    Konkani: core(['घर','अंतर्दृष्टी','साधनां','प्रोफायल','नमस्कार','तुमचो वैश्विक नकाशो','जन्म कुंडली','पंचांग','दशा','सुसंगतता','कुंडली तयार करात','नांव','जन्म तारीख','जन्म वेळ','जन्म सुवात','कुंडली गणना करात','प्रोफायल आनी सेटिंग्ज','कुंडली शैली','गणना मानक','भास','निवडले']),
    Maithili: core(['घर','अंतर्दृष्टि','साधन','प्रोफाइल','प्रणाम','अहाँक ब्रह्मांडीय रूपरेखा','जन्म कुंडली','पंचांग','दशा','अनुकूलता','कुंडली बनाउ','नाम','जन्म तिथि','जन्म समय','जन्म स्थान','कुंडली गणना करू','प्रोफाइल आ सेटिंग्स','कुंडली शैली','गणना मानक','भाषा','चयनित']),
    Malayalam: core(['ഹോം','ഉൾക്കാഴ്ചകൾ','ഉപകരണങ്ങൾ','പ്രൊഫൈൽ','നമസ്തേ','നിങ്ങളുടെ പ്രപഞ്ച രൂപരേഖ','ജന്മ കുണ്ഡലി','പഞ്ചാംഗം','ദശ','പൊരുത്തം','കുണ്ഡലി സൃഷ്ടിക്കുക','പേര്','ജനന തീയതി','ജനന സമയം','ജനന സ്ഥലം','ജാതകം കണക്കാക്കുക','പ്രൊഫൈലും ക്രമീകരണങ്ങളും','കുണ്ഡലി ശൈലി','കണക്കുകൂട്ടൽ മാനദണ്ഡം','ഭാഷ','തിരഞ്ഞെടുത്തു']),
    Manipuri: core(['হোম','খঙজিনবা','খুৎলাই','প্রোফাইল','নমস্কার','নহাক্কী নোংপোক মপান','পোকপা কুন্ডলী','পঞ্জিকা','দশা','চান্নবা','কুন্ডলী শেম্মু','মমিং','পোকপা নুমিৎ','পোকপা মতম','পোকপা মফম','কুন্ডলী হিসাব তৌ','প্রোফাইল অমসুং সেটিংস','কুন্ডলী মওং','হিসাব মান','লোন','খল্লে']),
    Marathi: core(['मुख्यपृष्ठ','अंतर्दृष्टी','साधने','प्रोफाइल','नमस्कार','तुमची वैश्विक रूपरेषा','जन्म कुंडली','पंचांग','दशा','सुसंगतता','कुंडली तयार करा','नाव','जन्म तारीख','जन्म वेळ','जन्म स्थान','कुंडली मोजा','प्रोफाइल आणि सेटिंग्ज','कुंडली शैली','गणना मानक','भाषा','निवडले']),
    Nepali: core(['गृह','अन्तर्दृष्टि','उपकरण','प्रोफाइल','नमस्ते','तपाईंको ब्रह्माण्डीय रूपरेखा','जन्म कुण्डली','पञ्चाङ्ग','दशा','अनुकूलता','कुण्डली बनाउनुहोस्','नाम','जन्म मिति','जन्म समय','जन्म स्थान','कुण्डली गणना गर्नुहोस्','प्रोफाइल र सेटिङ','कुण्डली शैली','गणना मानक','भाषा','चयनित']),
    Odia: core(['ହୋମ୍','ଅନ୍ତର୍ଦୃଷ୍ଟି','ସାଧନ','ପ୍ରୋଫାଇଲ୍','ନମସ୍କାର','ଆପଣଙ୍କ ବ୍ରହ୍ମାଣ୍ଡୀୟ ରୂପରେଖା','ଜନ୍ମ କୁଣ୍ଡଳୀ','ପଞ୍ଚାଙ୍ଗ','ଦଶା','ସୁସଙ୍ଗତତା','କୁଣ୍ଡଳୀ ତିଆରି କରନ୍ତୁ','ନାମ','ଜନ୍ମ ତାରିଖ','ଜନ୍ମ ସମୟ','ଜନ୍ମ ସ୍ଥାନ','କୁଣ୍ଡଳୀ ଗଣନା କରନ୍ତୁ','ପ୍ରୋଫାଇଲ୍ ଓ ସେଟିଂସ୍','କୁଣ୍ଡଳୀ ଶୈଳୀ','ଗଣନା ମାନକ','ଭାଷା','ଚୟନିତ']),
    Punjabi: core(['ਘਰ','ਅੰਦਰੂਨੀ ਜਾਣਕਾਰੀ','ਸਾਧਨ','ਪ੍ਰੋਫਾਈਲ','ਨਮਸਤੇ','ਤੁਹਾਡਾ ਬ੍ਰਹਿਮੰਡੀ ਨਕਸ਼ਾ','ਜਨਮ ਕੁੰਡਲੀ','ਪੰਚਾਂਗ','ਦਸ਼ਾ','ਅਨੁਕੂਲਤਾ','ਕੁੰਡਲੀ ਬਣਾਓ','ਨਾਮ','ਜਨਮ ਮਿਤੀ','ਜਨਮ ਸਮਾਂ','ਜਨਮ ਸਥਾਨ','ਕੁੰਡਲੀ ਗਣਨਾ ਕਰੋ','ਪ੍ਰੋਫਾਈਲ ਅਤੇ ਸੈਟਿੰਗਾਂ','ਕੁੰਡਲੀ ਸ਼ੈਲੀ','ਗਣਨਾ ਮਿਆਰ','ਭਾਸ਼ਾ','ਚੁਣਿਆ']),
    Sanskrit: core(['गृहम्','अन्तर्दृष्टयः','साधनानि','परिचयः','नमस्ते','भवतः ब्रह्माण्डीय रूपरेखा','जन्मकुण्डली','पञ्चाङ्गम्','दशा','अनुकूलता','कुण्डलीं रचयतु','नाम','जन्मदिनाङ्कः','जन्मसमयः','जन्मस्थानम्','कुण्डलीं गणयतु','परिचयः विन्यासाश्च','कुण्डलीशैली','गणनामानकम्','भाषा','चितम्']),
    Santali: core(['ᱚᱲᱟᱜ','ᱵᱩᱡᱷᱟᱹᱣ','ᱥᱟᱯᱟᱵ','ᱯᱨᱚᱯᱷᱟᱭᱤᱞ','ᱡᱚᱦᱟᱨ','ᱟᱢᱟᱜ ᱥᱮᱨᱢᱟ ᱱᱚᱠᱥᱟ','ᱡᱟᱱᱟᱢ ᱠᱩᱱᱰᱞᱤ','ᱯᱟᱧᱡᱤ','ᱫᱚᱥᱟ','ᱢᱮᱞ','ᱠᱩᱱᱰᱞᱤ ᱵᱮᱱᱟᱣ','ᱧᱩᱛᱩᱢ','ᱡᱟᱱᱟᱢ ᱢᱟᱹᱦᱤᱛ','ᱡᱟᱱᱟᱢ ᱚᱠᱛᱚ','ᱡᱟᱱᱟᱢ ᱴᱷᱟᱶ','ᱠᱩᱱᱰᱞᱤ ᱞᱮᱠᱷᱟ','ᱯᱨᱚᱯᱷᱟᱭᱤᱞ ᱥᱮᱴᱤᱝ','ᱠᱩᱱᱰᱞᱤ ᱫᱷᱟᱹᱨᱟ','ᱞᱮᱠᱷᱟ ᱢᱟᱱ','ᱯᱟᱹᱨᱥᱤ','ᱵᱟᱪᱷᱟᱣ']),
    Sindhi: core(['گهر','بصيرت','اوزار','پروفائل','نمستي','توهان جو ڪائناتي نقشو','جنم ڪنڊلي','پنجانگ','دشا','مطابقت','ڪنڊلي ٺاهيو','نالو','جنم تاريخ','جنم وقت','جنم هنڌ','ڪنڊلي حساب ڪريو','پروفائل ۽ سيٽنگون','ڪنڊلي انداز','حساب معيار','ٻولي','چونڊيل']),
    Tamil: {
      ...core(['முகப்பு','நுண்ணறிவுகள்','கருவிகள்','சுயவிவரம்','வணக்கம்','உங்கள் பிரபஞ்ச வரைபடம்','பிறப்பு ஜாதகம்','பஞ்சாங்கம்','தசை','பொருத்தம்','ஜாதகம் உருவாக்கு','பெயர்','பிறந்த தேதி','பிறந்த நேரம்','பிறந்த இடம்','ஜாதகத்தைக் கணக்கிடு','சுயவிவரம் மற்றும் அமைப்புகள்','ஜாதக வடிவம்','கணக்கீட்டு முறை','மொழி','தேர்ந்தெடுக்கப்பட்டது']),
      moon:'சந்திரன்', moonDescription:'பரிவான, படைப்பாற்றல்மிக்க, ஆழ்ந்த உள்ளுணர்வு', exploreKundali:'உங்கள் ஜாதகத்தைப் பாருங்கள்', panchangSubtitle:'திதி, நட்சத்திரம், யோகம்', dashaSubtitle:'கிரக காலங்கள்', harmony:'பொருத்தத்தைச் சரிபார்க்கவும்',
      formIntro:'உங்கள் வேத ஜாதகத்தை உருவாக்க பிறப்பு விவரங்களை உள்ளிடவும்', utcOffset:'UTC வேறுபாடு', coordinatesAuto:'பிறந்த இடத்திலிருந்து ஆயத்தொலைவுகள் தானாகத் தேர்ந்தெடுக்கப்படும்.', calculating:'கணக்கிடுகிறது…', chartCalculated:'ஜாதகம் தயாரானது',
      rashiKundali:'ராசி ஜாதகம்', rashi:'ராசி', navamsha:'நவாம்சம்', bhava:'பாவம்', north:'வட இந்திய', south:'தென் இந்திய', east:'கிழக்கு இந்திய', lagna:'லக்னம்', pada:'பாதம்', chartInsights:'ஜாதக நுண்ணறிவுகள்',
      insightBody:'{nakshatra} நட்சத்திரத்தில் சந்திரன் பரிவான, படைப்பாற்றல்மிக்க இயல்பை வழங்குகிறது. கிரக நிலைகளை அறிய தட்டவும்.', planetaryPositions:'கிரக நிலைகள்', planet:'கிரகம்', sign:'ராசி', degree:'பாகை', keyTakeaway:'முக்கிய கருத்து',
      takeaway:'கிரக ஆற்றலும் தன்மைகளின் சமநிலையும் உங்கள் தனித்துவமான வாழ்க்கைப் பாதையை வடிவமைக்கிறது.', dailyPanchang:'தினசரி பஞ்சாங்கம்', vimshottariDasha:'விம்சோத்தரி தசை', astrologyTools:'ஜோதிடக் கருவிகள்', comingNext:'விரைவில்',
      disclaimer:'இந்தியாவின் 22 அட்டவணை மொழிகளும் ஆங்கிலமும் ஆதரிக்கப்படுகின்றன. ஜோதிடம் கலாசார மற்றும் சுயபரிசீலனை பயன்பாட்டிற்காகும்.',
      palmistry:'கைரேகை', palmistrySubtitle:'கீரோ முறை கோடுகள் மற்றும் மேடுகள்', discoverHands:'உங்கள் கைகளின் கதையை அறியுங்கள்', leftHand:'இடது கை', rightHand:'வலது கை', scanPalm:'உள்ளங்கையை ஸ்கேன் செய்', uploadImage:'படத்தை பதிவேற்று', scanYourPalm:'உங்கள் உள்ளங்கையை ஸ்கேன் செய்', yourPalmReading:'உங்கள் கைரேகை வாசிப்பு', lines:'ரேகைகள்', mounts:'மேடுகள்', markings:'குறிகள்', viewFullReading:'முழு வாசிப்பைக் காண்க'
    },
    Telugu: {
      ...core(['హోమ్','అంతర్దృష్టులు','సాధనాలు','ప్రొఫైల్','నమస్తే','మీ విశ్వ రూపరేఖ','జన్మ కుండలి','పంచాంగం','దశ','అనుకూలత','కుండలి సృష్టించండి','పేరు','పుట్టిన తేదీ','పుట్టిన సమయం','పుట్టిన ప్రదేశం','జాతకాన్ని లెక్కించండి','ప్రొఫైల్ మరియు సెట్టింగులు','చార్ట్ శైలి','గణన ప్రమాణం','భాష','ఎంపికైంది']),
      moon:'చంద్రుడు', moonDescription:'పోషణాత్మకమైన, సృజనాత్మకమైన మరియు లోతైన అంతర్దృష్టి', exploreKundali:'మీ కుండలిని చూడండి', panchangSubtitle:'తిథి, నక్షత్రం, యోగం', dashaSubtitle:'గ్రహ కాలాలు', harmony:'సామరస్యాన్ని పరిశీలించండి',
      formIntro:'మీ వేద జాతకాన్ని రూపొందించడానికి జన్మ వివరాలను నమోదు చేయండి', utcOffset:'UTC వ్యత్యాసం', coordinatesAuto:'జన్మ ప్రదేశం నుండి అక్షాంశ రేఖాంశాలు స్వయంచాలకంగా ఎంపికవుతాయి.', calculating:'లెక్కిస్తోంది…', chartCalculated:'కుండలి సిద్ధమైంది',
      rashiKundali:'రాశి కుండలి', rashi:'రాశి', navamsha:'నవాంశ', bhava:'భావ', north:'ఉత్తర భారత', south:'దక్షిణ భారత', east:'తూర్పు భారత', lagna:'లగ్నం', pada:'పాదం', chartInsights:'జాతక అంతర్దృష్టులు',
      insightBody:'{nakshatra}లో చంద్రుడు పోషణాత్మకమైన, సృజనాత్మక స్వభావాన్ని ఇస్తాడు. గ్రహ స్థానాల వివరాలకు నొక్కండి.', planetaryPositions:'గ్రహ స్థానాలు', planet:'గ్రహం', sign:'రాశి', degree:'డిగ్రీ', keyTakeaway:'ముఖ్యాంశం',
      takeaway:'గ్రహశక్తి మరియు మూలకాల సమతుల్యత మీ ప్రత్యేక జీవిత మార్గాన్ని రూపొందిస్తుంది.', dailyPanchang:'దైనిక పంచాంగం', vimshottariDasha:'వింశోత్తరి దశ', astrologyTools:'జ్యోతిష సాధనాలు', comingNext:'త్వరలో వస్తుంది',
      disclaimer:'భారతదేశంలోని 22 షెడ్యూల్డ్ భాషలు మరియు ఇంగ్లీష్ మద్దతు పొందుతాయి. జ్యోతిషం సాంస్కృతిక మరియు ఆత్మపరిశీలన కోసం.',
      palmistry:'హస్తసాముద్రికం', palmistrySubtitle:'కీరో పద్ధతిలో రేఖలు మరియు పర్వతాలు', discoverHands:'మీ చేతుల్లోని కథను తెలుసుకోండి', leftHand:'ఎడమ చేయి', rightHand:'కుడి చేయి', scanPalm:'అరచేతిని స్కాన్ చేయండి', uploadImage:'చిత్రాన్ని అప్‌లోడ్ చేయండి', scanYourPalm:'మీ అరచేతిని స్కాన్ చేయండి', yourPalmReading:'మీ హస్తరేఖా ఫలితం', lines:'రేఖలు', mounts:'పర్వతాలు', markings:'గుర్తులు', viewFullReading:'పూర్తి ఫలితం చూడండి'
    },
    Urdu: core(['گھر','بصیرت','اوزار','پروفائل','نمستے','آپ کا کائناتی خاکہ','جنم کنڈلی','پنچانگ','دشا','مطابقت','کنڈلی بنائیں','نام','تاریخ پیدائش','وقت پیدائش','جائے پیدائش','کنڈلی کا حساب کریں','پروفائل اور ترتیبات','کنڈلی انداز','حساب کا معیار','زبان','منتخب'])
  };
  Object.keys(translations).forEach(language => { translations[language] = {...en, ...translations[language]}; });
  window.APP_TRANSLATIONS = translations;
  window.APP_LANGUAGE_CODES = {English:'en',Assamese:'as',Bengali:'bn',Bodo:'brx',Dogri:'doi',Gujarati:'gu',Hindi:'hi',Kannada:'kn',Kashmiri:'ks',Konkani:'kok',Maithili:'mai',Malayalam:'ml',Manipuri:'mni',Marathi:'mr',Nepali:'ne',Odia:'or',Punjabi:'pa',Sanskrit:'sa',Santali:'sat',Sindhi:'sd',Tamil:'ta',Telugu:'te',Urdu:'ur'};
})();
