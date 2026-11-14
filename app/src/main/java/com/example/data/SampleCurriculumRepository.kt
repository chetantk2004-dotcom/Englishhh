package com.example.data

import com.example.model.AchievementBadge
import com.example.model.DailyWord
import com.example.model.DayProgress
import com.example.model.GrammarPreview
import com.example.model.LearningLevel
import com.example.model.LearningUnit
import com.example.model.PhaseRoadmapItem
import com.example.model.SampleLessonPreview
import com.example.model.TopicStrength
import com.example.model.UnitStatus
import com.example.model.VocabCategory

object SampleCurriculumRepository {

    val initialDailyWord = DailyWord(
        id = "dw_1",
        english = "Polite",
        hindi = "विनम्र / सभ्य",
        pronunciationHi = "पो-लाइट",
        partOfSpeechHi = "विशेषण (Adjective)",
        exampleEn = "Always be polite when you talk to elders.",
        exampleHi = "बड़ों से बात करते समय हमेशा विनम्र रहें।",
        categoryHi = "रोज़मर्रा के शब्द (Daily Use)",
        isSaved = false
    )

    val levels: List<LearningLevel> = listOf(
        LearningLevel(
            levelNumber = 1,
            titleEn = "Absolute Beginner",
            titleHi = "बिल्कुल शुरुआत",
            subtitleHi = "अक्षर, अभिवादन, गिनती और रोज़मर्रा के आसान शब्द",
            badgeText = "Level 1",
            units = listOf(
                unit(
                    id = "l1_u1", level = 1, order = 1,
                    titleEn = "English Alphabet", titleHi = "अंग्रेज़ी वर्णमाला (A–Z)",
                    lessonsCount = 4, completed = 4, status = UnitStatus.COMPLETED,
                    lesson = SampleLessonPreview(
                        titleEn = "Letters A to G & Sounds",
                        titleHi = "A से G तक अक्षर और उनकी ध्वनि",
                        explanationHi = "अंग्रेज़ी में कुल 26 अक्षर (Letters) होते हैं। हर अक्षर का नाम और उसकी आवाज़ (Sound) अलग हो सकती है।",
                        englishPhrase = "A for Apple, B for Ball",
                        hindiMeaning = "A से सेब, B से गेंद",
                        pronunciationHi = "ए फ़ॉर ऐपल, बी फ़ॉर बॉल",
                        realLifeExampleEn = "My name starts with the letter A.",
                        realLifeExampleHi = "मेरा नाम A अक्षर से शुरू होता है।",
                        quizQuestionHi = "अंग्रेज़ी वर्णमाला (Alphabet) में कुल कितने अक्षर होते हैं?",
                        quizOptions = listOf("24 अक्षर", "26 अक्षर", "28 अक्षर"),
                        correctOptionIndex = 1,
                        xpReward = 20
                    )
                ),
                unit(
                    id = "l1_u2", level = 1, order = 2,
                    titleEn = "Vowels and Consonants", titleHi = "स्वर और व्यंजन",
                    lessonsCount = 3, completed = 3, status = UnitStatus.COMPLETED,
                    lesson = SampleLessonPreview(
                        titleEn = "The 5 Vowels: A, E, I, O, U",
                        titleHi = "5 मुख्य स्वर (Vowels)",
                        explanationHi = "A, E, I, O, U को Vowels (स्वर) कहते हैं। बाकी 21 अक्षर Consonants (व्यंजन) कहलाते हैं।",
                        englishPhrase = "A, E, I, O, U are vowels.",
                        hindiMeaning = "A, E, I, O, U स्वर होते हैं।",
                        pronunciationHi = "ए, ई, आई, ओ, यू आर वाउअल्स।",
                        realLifeExampleEn = "An umbrella starts with a vowel.",
                        realLifeExampleHi = "छतरी (Umbrella) एक स्वर (U) से शुरू होता है।",
                        quizQuestionHi = "इनमें से कौन-सा अक्षर Vowel (स्वर) है?",
                        quizOptions = listOf("B", "E", "K"),
                        correctOptionIndex = 1,
                        xpReward = 20
                    )
                ),
                unit(
                    id = "l1_u3", level = 1, order = 3,
                    titleEn = "Basic English Words", titleHi = "बुनियादी अंग्रेज़ी शब्द",
                    lessonsCount = 5, completed = 2, status = UnitStatus.ACTIVE,
                    lesson = SampleLessonPreview(
                        titleEn = "Polite Magic Words",
                        titleHi = "ज़रूरी शिष्टाचार शब्द (Please, Thank you)",
                        explanationHi = "किसी से मदद माँगने के लिए 'Please' और धन्यवाद देने के लिए 'Thank you' का उपयोग करें। यह हर बातचीत को आसान बनाता है।",
                        englishPhrase = "Please help me. Thank you!",
                        hindiMeaning = "कृपया मेरी मदद करें। धन्यवाद!",
                        pronunciationHi = "प्लीज़ हेल्प मी। थैंक यू!",
                        realLifeExampleEn = "Please give me a glass of water.",
                        realLifeExampleHi = "कृपया मुझे एक गिलास पानी दीजिए।",
                        quizQuestionHi = "किसी का धन्यवाद करने के लिए आप क्या कहेंगे?",
                        quizOptions = listOf("Sorry", "Thank you", "Goodbye"),
                        correctOptionIndex = 1,
                        xpReward = 25
                    )
                ),
                unit(
                    id = "l1_u4", level = 1, order = 4,
                    titleEn = "Greetings", titleHi = "अभिवादन (नमस्ते कहना)",
                    lessonsCount = 4, completed = 0, status = UnitStatus.UPCOMING,
                    lesson = SampleLessonPreview(
                        titleEn = "Morning to Evening Greetings",
                        titleHi = "सुबह से रात तक नमस्ते कैसे कहें",
                        explanationHi = "समय के अनुसार अंग्रेज़ी में अभिवादन बदलता है: सुबह Good Morning, दोपहर Good Afternoon और शाम को Good Evening।",
                        englishPhrase = "Good morning! How are you?",
                        hindiMeaning = "सुप्रभात! आप कैसे हैं?",
                        pronunciationHi = "गुड मॉर्निंग! हाउ आर यू?",
                        realLifeExampleEn = "Good morning, sir! Nice to meet you.",
                        realLifeExampleHi = "सुप्रभात सर! आपसे मिलकर अच्छा लगा।",
                        quizQuestionHi = "सुबह 9 बजे किसी से मिलने पर क्या कहेंगे?",
                        quizOptions = listOf("Good night", "Good morning", "Good afternoon"),
                        correctOptionIndex = 1,
                        xpReward = 20
                    )
                ),
                unit(
                    id = "l1_u5", level = 1, order = 5,
                    titleEn = "Numbers", titleHi = "गिनती (1 से 100)",
                    lessonsCount = 4, completed = 0, status = UnitStatus.UPCOMING,
                    lesson = simplePreview("Counting 1 to 20", "1 से 20 तक गिनती", "गिनती खरीददारी, समय और फ़ोन नंबर बताने में काम आती है।", "One, Two, Three, Four, Five", "एक, दो, तीन, चार, पाँच", "वन, टू, थ्री, फ़ोर, फ़ाइव", "I need two tickets.", "मुझे दो टिकट चाहिए।", "‘10’ को अंग्रेज़ी में क्या कहते हैं?", listOf("Nine", "Ten", "Twelve"), 1)
                ),
                unit(
                    id = "l1_u6", level = 1, order = 6,
                    titleEn = "Days and Months", titleHi = "दिन और महीने",
                    lessonsCount = 4, completed = 0, status = UnitStatus.UPCOMING,
                    lesson = simplePreview("7 Days of the Week", "सप्ताह के 7 दिन", "सोमवार से रविवार तक के नाम रोज़ की बातचीत और अपॉइंटमेंट में काम आते हैं।", "Today is Monday.", "आज सोमवार है।", "टुडे इज़ मंडे।", "My exam is on Friday.", "मेरी परीक्षा शुक्रवार को है।", "‘Sunday’ का हिंदी अर्थ क्या है?", listOf("शनिवार", "रविवार", "मंगलवार"), 1)
                ),
                unit(
                    id = "l1_u7", level = 1, order = 7,
                    titleEn = "Colours", titleHi = "रंगों के नाम",
                    lessonsCount = 3, completed = 0, status = UnitStatus.UPCOMING,
                    lesson = simplePreview("Everyday Colours", "रोज़मर्रा के रंग", "कपड़े, फल और चीज़ें पहचानने के लिए रंगों के नाम सीखें।", "Blue, Green, Yellow, Red", "नीला, हरा, पीला, लाल", "ब्लू, ग्रीन, येलो, रेड", "I like this blue shirt.", "मुझे यह नीली शर्ट पसंद है।", "‘Green’ किस रंग को कहते हैं?", listOf("हरा", "काला", "सफ़ेद"), 0)
                ),
                unit(
                    id = "l1_u8", level = 1, order = 8,
                    titleEn = "Family Members", titleHi = "परिवार के सदस्य",
                    lessonsCount = 4, completed = 0, status = UnitStatus.UPCOMING,
                    lesson = simplePreview("Talking About Family", "परिवार के बारे में बताना", "अपने माता-पिता, भाई-बहन और रिश्तेदारों का अंग्रेज़ी परिचय दें।", "Mother, Father, Brother, Sister", "माँ, पिता, भाई, बहन", "मदर, फ़ादर, ब्रदर, सिस्टर", "My sister is a teacher.", "मेरी बहन एक शिक्षिका हैं।", "‘Brother’ का हिंदी अर्थ क्या है?", listOf("मामा", "भाई", "बेटा"), 1)
                ),
                unit(
                    id = "l1_u9", level = 1, order = 9,
                    titleEn = "Common Objects", titleHi = "आम वस्तुएँ",
                    lessonsCount = 4, completed = 0, status = UnitStatus.UPCOMING,
                    lesson = simplePreview("Home & Office Things", "घर और काम की चीज़ें", "मेज़, कुर्सी, किताब, पानी और चाबी जैसे रोज़ के शब्दों को याद करें।", "Table, Chair, Book, Key", "मेज़, कुर्सी, किताब, चाबी", "टेबल, चेयर, बुक, की", "The key is on the table.", "चाबी मेज़ पर रखी है।", "‘Book’ का अर्थ क्या है?", listOf("कलम", "किताब", "दरवाज़ा"), 1)
                ),
                unit(
                    id = "l1_u10", level = 1, order = 10,
                    titleEn = "Basic Introductions", titleHi = "बुनियादी परिचय",
                    lessonsCount = 4, completed = 0, status = UnitStatus.UPCOMING,
                    lesson = simplePreview("Saying Your Name & City", "अपना नाम और शहर बताना", "पहली बार किसी से मिलने पर 2 आसान वाक्यों में अपना परिचय दें।", "My name is Aarav. I am from Jaipur.", "मेरा नाम आरव है। मैं जयपुर से हूँ।", "माय नेम इज़ आरव। आय एम फ़्रॉम जयपुर।", "Hello, my name is Priya.", "नमस्ते, मेरा नाम प्रिया है।", "अपना नाम बताने के लिए सही वाक्य चुनें:", listOf("My name is...", "My age go...", "Name you is..."), 0)
                )
            )
        ),
        LearningLevel(
            levelNumber = 2,
            titleEn = "Basic English",
            titleHi = "बेसिक अंग्रेज़ी",
            subtitleHi = "सर्वनाम, क्रियाएँ, Is/Am/Are और सरल वाक्य बनाना",
            badgeText = "Level 2",
            units = listOf(
                unit("l2_u1", 2, 1, "Pronouns", "सर्वनाम (I, You, He, She)", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("I, You, We, They, He, She", "सर्वनाम का सही उपयोग", "नाम की जगह प्रयोग होने वाले शब्दों को Pronouns कहते हैं।", "I am learning. You are smart.", "मैं सीख रहा हूँ। आप होशियार हैं।", "आय एम लर्निंग। यू आर स्मार्ट।", "She is my friend.", "वह मेरी सहेली है।", "लड़के के लिए ‘वह’ को अंग्रेज़ी में क्या कहते हैं?", listOf("She", "He", "It"), 1)),
                unit("l2_u2", 2, 2, "Nouns", "संज्ञा (नाम वाले शब्द)", 3, 0, UnitStatus.UPCOMING,
                    simplePreview("People, Places & Things", "व्यक्ति, स्थान और वस्तु", "किसी भी व्यक्ति, जगह या चीज़ के नाम को Noun कहते हैं।", "Delhi, School, Doctor, Phone", "दिल्ली, स्कूल, डॉक्टर, फ़ोन", "दिल्ली, स्कूल, डॉक्टर, फ़ोन", "The doctor is kind.", "डॉक्टर दयालु हैं।", "इनमें से कौन-सा Noun (संज्ञा) है?", listOf("Run", "Happy", "School"), 2)),
                unit("l2_u3", 2, 3, "Verbs", "क्रिया (काम वाले शब्द)", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Action Words", "काम बताने वाले शब्द", "जो शब्द किसी काम के होने को बताते हैं—जैसे खाना, जाना, बोलना—उन्हें Verbs कहते हैं।", "Eat, Drink, Go, Speak, Read", "खाना, पीना, जाना, बोलना, पढ़ना", "ईट, ड्रिंक, गो, स्पीक, रीड", "I read a book daily.", "मैं रोज़ एक किताब पढ़ता हूँ।", "‘Speak’ का हिंदी अर्थ क्या है?", listOf("सोना", "बोलना", "बैठना"), 1)),
                unit("l2_u4", 2, 4, "Is, Am, Are", "Is, Am, Are का प्रयोग", 5, 0, UnitStatus.UPCOMING,
                    simplePreview("Using Is, Am, Are", "वर्तमान में होना बताना", "I के साथ 'am', एकवचन (He/She/It) के साथ 'is', और बहुवचन (You/We/They) के साथ 'are' लगता है।", "I am ready. She is happy.", "मैं तैयार हूँ। वह खुश है।", "आय एम रेडी। शी इज़ हैप्पी।", "We are students.", "हम विद्यार्थी हैं।", "‘I ___ a student.’ में सही शब्द भरें:", listOf("is", "am", "are"), 1)),
                unit("l2_u5", 2, 5, "This, That, These, Those", "यह, वह, ये, वे", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Pointing Near & Far", "पास और दूर की चीज़ें बताना", "पास की एक चीज़ के लिए This, दूर की एक के लिए That, पास की ज़्यादा के लिए These और दूर के लिए Those।", "This is my phone. That is a tree.", "यह मेरा फ़ोन है। वह एक पेड़ है।", "दिस इज़ माय फ़ोन। दैट इज़ अ ट्री।", "These apples are fresh.", "ये सेब ताज़े हैं।", "पास की एक किताब के लिए क्या चुनेंगे?", listOf("This book", "Those book", "These book"), 0)),
                unit("l2_u6", 2, 6, "Has and Have", "Has और Have (पास होना)", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Showing Ownership", "कोई चीज़ पास होना बताना", "मेरे पास है = I have; उसके पास है = He/She has।", "I have a pen. She has a bag.", "मेरे पास एक कलम है। उसके पास एक बैग है।", "आय हैव अ पेन। शी हैज़ अ बैग।", "Do you have time?", "क्या आपके पास समय है?", "‘He ___ a bicycle.’ में क्या आएगा?", listOf("have", "has", "am"), 1)),
                unit("l2_u7", 2, 7, "Articles", "A, An, The का प्रयोग", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Using A, An and The", "A, An और The कब लगाएँ", "व्यंजन की आवाज़ से पहले 'A', स्वर (A,E,I,O,U) की आवाज़ से पहले 'An' और ख़ास चीज़ के लिए 'The'।", "A boy, An apple, The sun", "एक लड़का, एक सेब, सूरज", "अ बॉय, ऐन ऐपल, द सन", "I saw an elephant.", "मैंने एक हाथी देखा।", "‘___ orange’ में क्या लगेगा?", listOf("A", "An", "Are"), 1)),
                unit("l2_u8", 2, 8, "Simple Sentences", "सरल वाक्य बनाना", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Subject + Verb + Object", "सही क्रम में वाक्य जोड़ना", "अंग्रेज़ी वाक्य में पहले कर्ता (कौन), फिर क्रिया (काम) और फिर वस्तु आती है।", "I drink tea.", "मैं चाय पीता हूँ।", "आय ड्रिंक टी।", "She speaks English.", "वह अंग्रेज़ी बोलती है।", "‘मैं पानी पीता हूँ’ का सही अनुवाद:", listOf("I water drink", "I drink water", "Drink I water"), 1)),
                unit("l2_u9", 2, 9, "Question Formation", "सवाल पूछना सीखें", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("What, Where, When, Who", "सवाल बनाने वाले शब्द", "सवाल पूछने के लिए What (क्या), Where (कहाँ), When (कब) और Who (कौन) से शुरुआत करें।", "Where do you live?", "आप कहाँ रहते हैं?", "वेअर डू यू लिव?", "What is your job?", "आपका काम क्या है?", "‘कहाँ’ के लिए कौन-सा शब्द है?", listOf("Who", "Where", "When"), 1))
            )
        ),
        LearningLevel(
            levelNumber = 3,
            titleEn = "Grammar Foundation",
            titleHi = "व्याकरण की नींव",
            subtitleHi = "काल (Tenses), संबंध सूचक शब्द और सही वाक्य संरचना",
            badgeText = "Level 3",
            units = listOf(
                unit("l3_u1", 3, 1, "Present Tense", "वर्तमान काल", 5, 0, UnitStatus.UPCOMING,
                    simplePreview("Daily Habits & Facts", "रोज़ की आदतें और वर्तमान", "जो काम रोज़ होता है या अभी चल रहा है उसे Present Tense में बोलते हैं।", "I work in an office.", "मैं एक दफ़्तर में काम करता हूँ।", "आय वर्क इन ऐन ऑफ़िस।", "She is cooking food.", "वह खाना बना रही है।", "‘मैं रोज़ चलता हूँ’:", listOf("I walk daily.", "I walked daily.", "I will walk."), 0)),
                unit("l3_u2", 3, 2, "Past Tense", "भूतकाल (बीता समय)", 5, 0, UnitStatus.UPCOMING,
                    simplePreview("Talking About Yesterday", "कल या बीते समय की बात", "बीते हुए काम के लिए verb का दूसरा रूप (जैसे went, ate, saw) उपयोग होता है।", "I went to the market.", "मैं बाज़ार गया था।", "आय वेंट टू द मार्केट।", "We watched a movie.", "हमने एक फ़िल्म देखी।", "‘Go’ का Past Tense क्या है?", listOf("Going", "Went", "Goes"), 1)),
                unit("l3_u3", 3, 3, "Future Tense", "भविष्य काल (आने वाला समय)", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Using Will for Tomorrow", "आने वाले कल की योजना", "भविष्य की बात बताने के लिए 'will' के साथ क्रिया का पहला रूप लगाते हैं।", "I will call you tomorrow.", "मैं आपको कल फ़ोन करूँगा।", "आय विल कॉल यू टुमॉरो।", "She will start today.", "वह आज शुरू करेगी।", "भविष्य बताने के लिए किस शब्द का उपयोग होता है?", listOf("did", "will", "was"), 1)),
                unit("l3_u4", 3, 4, "All Important Tense Forms", "सभी ज़रूरी Tense रूप", 6, 0, UnitStatus.UPCOMING,
                    simplePreview("Simple, Continuous & Perfect", "सरल, चल रहा और पूरा हुआ काम", "एक ही काम को 3 तरह से बोलना सीखें—होता है, हो रहा है, और हो चुका है।", "I have finished my work.", "मैं अपना काम पूरा कर चुका हूँ।", "आय हैव फ़िनिश्ड माय वर्क।", "They are waiting outside.", "वे बाहर इंतज़ार कर रहे हैं।", "‘काम अभी चल रहा है’ किस रूप में आता है?", listOf("Continuous", "Past Simple", "Future"), 0)),
                unit("l3_u5", 3, 5, "Prepositions", "स्थान व समय के शब्द (In, On, At)", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("In, On, At Made Easy", "In, On, At का आसान फ़र्क़", "अंदर के लिए In, ऊपर सतह के लिए On, और सटीक समय या जगह के लिए At लगाएँ।", "The book is on the desk.", "किताब डेस्क पर है।", "द बुक इज़ ऑन द डेस्क।", "Meet me at 5 PM.", "मुझसे शाम 5 बजे मिलें।", "‘कमरे के अंदर’ के लिए क्या सही है?", listOf("In the room", "On the room", "To the room"), 0)),
                unit("l3_u6", 3, 6, "Adjectives", "विशेषण (गुण बताने वाले शब्द)", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Describing People & Things", "चीज़ों की ख़ूबी बताना", "Big, Small, Honest, Cold जैसे शब्द किसी नाम की विशेषता बताते हैं।", "This tea is very hot.", "यह चाय बहुत गरम है।", "दिस टी इज़ वेरी हॉट।", "He is an honest man.", "वह एक ईमानदार आदमी है।", "‘ईमानदार’ को अंग्रेज़ी में क्या कहते हैं?", listOf("Fast", "Honest", "Tall"), 1)),
                unit("l3_u7", 3, 7, "Adverbs", "क्रिया विशेषण (Slowly, Always)", 3, 0, UnitStatus.UPCOMING,
                    simplePreview("How & How Often", "काम कैसे और कितनी बार होता है", "अक्सर '-ly' वाले शब्द जैसे Slowly (धीरे-धीरे), Clearly (साफ़) काम करने का तरीका बताते हैं।", "Please speak slowly.", "कृपया धीरे बोलें।", "प्लीज़ स्पीक स्लोली।", "She always arrives on time.", "वह हमेशा समय पर आती है।", "‘धीरे से’ का सही अंग्रेज़ी शब्द:", listOf("Slowly", "Quick", "Loud"), 0)),
                unit("l3_u8", 3, 8, "Conjunctions", "वाक्य जोड़ने वाले शब्द", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("And, But, Because, So", "दो छोटे वाक्यों को जोड़ना", "And (और), But (लेकिन), Because (क्योंकि) की मदद से लंबे वाक्य आसानी से बोलें।", "I tried, but it was locked.", "मैंने कोशिश की, लेकिन वह बंद था।", "आय ट्राइड, बट इट वॉज़ लॉक्ड।", "I am happy because I passed.", "मैं खुश हूँ क्योंकि मैं पास हो गया।", "‘लेकिन’ को अंग्रेज़ी में क्या कहते हैं?", listOf("And", "But", "So"), 1)),
                unit("l3_u9", 3, 9, "Modals", "Can, Should, Must, May", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Ability, Advice & Permission", "सकता हूँ, चाहिए और अनुमति", "Can = सकता हूँ, Should = चाहिए, May I = क्या मैं कर सकता हूँ?", "Can you help me?", "क्या आप मेरी मदद कर सकते हैं?", "कैन यू हेल्प मी?", "You should rest now.", "अब आपको आराम करना चाहिए।", "विनम्र अनुमति माँगने के लिए:", listOf("May I come in?", "Must I come?", "Will I in?"), 0)),
                unit("l3_u10", 3, 10, "Sentence Structure", "मज़बूत वाक्य संरचना", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Positive, Negative & Question", "सकारात्मक, नकारात्मक व प्रश्न", "एक ही वाक्य को हाँ, नहीं और सवाल में बदलना सीखें।", "She knows / She does not know.", "वह जानती है / वह नहीं जानती।", "शी नोज़ / शी डज़ नॉट नो।", "Does she know?", "क्या वह जानती है?", "नकारात्मक वाक्य बनाने में कौन-सा शब्द ज़रूरी है?", listOf("not", "very", "also"), 0))
            )
        ),
        LearningLevel(
            levelNumber = 4,
            titleEn = "Daily Conversation",
            titleHi = "रोज़मर्रा की बातचीत",
            subtitleHi = "बाज़ार, अस्पताल, रेलवे स्टेशन, इंटरव्यू और फ़ोन कॉल",
            badgeText = "Level 4",
            units = listOf(
                unit("l4_u1", 4, 1, "Self Introduction", "आत्म-परिचय (Self Intro)", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Confident 4-Line Intro", "4 वाक्यों में शानदार परिचय", "नाम, शहर, पढ़ाई/काम और शौक को आसान अंग्रेज़ी में बोलना सीखें।", "I live in Pune and I love reading.", "मैं पुणे में रहता हूँ और मुझे पढ़ना पसंद है।", "आय लिव इन पुणे एंड आय लव रीडिंग।", "I am a graphic designer.", "मैं एक ग्राफ़िक डिज़ाइनर हूँ।", "अपना शौक बताने के लिए:", listOf("I enjoy music.", "I music enjoy.", "Enjoy is music."), 0)),
                unit("l4_u2", 4, 2, "Talking with Friends", "दोस्तों से बातचीत", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Casual Friendly Chats", "दोस्तों से सहज बात करना", "दोस्तों का हालचाल पूछना और अपनी योजना साझा करना।", "Long time no see! What's new?", "बहुत दिन बाद मिले! क्या नया चल रहा है?", "लॉन्ग टाइम नो सी! वॉट्स न्यू?", "Let's catch up this weekend.", "चलो इस सप्ताहांत मिलते हैं।", "‘सब बढ़िया है’ के लिए सही वाक्य:", listOf("Everything is great!", "All go wrong!", "No time see!"), 0)),
                unit("l4_u3", 4, 3, "Shopping Conversations", "खरीददारी की बातचीत", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Asking Price & Sizes", "दाम और साइज़ पूछना", "दुकान पर चीज़ की क़ीमत, रंग और डिस्काउंट पूछने के आसान वाक्य।", "How much does this cost?", "इसकी क़ीमत कितनी है?", "हाउ मच डज़ दिस कॉस्ट?", "Do you have a smaller size?", "क्या आपके पास छोटा साइज़ है?", "दाम पूछने के लिए क्या बोलेंगे?", listOf("How much is this?", "Where is cost?", "Who much this?"), 0)),
                unit("l4_u4", 4, 4, "At Restaurants", "रेस्टोरेंट में बातचीत", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Ordering Food & Bill", "खाना ऑर्डर करना और बिल माँगना", "मेन्यू माँगना, कम तीखा खाना कहना और बिल मँगवाना।", "Could we see the menu, please?", "क्या हम मेन्यू देख सकते हैं?", "कुड वी सी द मेन्यू, प्लीज़?", "Please bring the bill.", "कृपया बिल ले आइए।", "‘कृपया पानी लाएँ’:", listOf("Please bring water.", "Go water please.", "Bring water went."), 0)),
                unit("l4_u5", 4, 5, "At Hospitals", "अस्पताल और डॉक्टर के पास", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Explaining Symptoms", "तबीयत और लक्षण बताना", "बुख़ार, सिरदर्द या दवाई के समय के बारे में डॉक्टर से स्पष्ट बात करें।", "I have had a fever since yesterday.", "मुझे कल से बुख़ार है।", "आय हैव हैड अ फ़ीवर सिंस यस्टरडे।", "How many times should I take this?", "मुझे यह दवाई कितनी बार लेनी है?", "‘सिरदर्द’ को क्या कहते हैं?", listOf("Headache", "Footstep", "Backdoor"), 0)),
                unit("l4_u6", 4, 6, "At Railway Stations", "रेलवे स्टेशन व सफ़र", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Platform & Train Timings", "प्लेटफ़ॉर्म और ट्रेन का समय", "टिकट काउंटर और पूछताछ केंद्र पर आत्मविश्वास से पूछें।", "Which platform does the train arrive on?", "ट्रेन किस प्लेटफ़ॉर्म पर आएगी?", "विच प्लेटफ़ॉर्म डज़ द ट्रेन अराइव ऑन?", "Is the train on time?", "क्या ट्रेन समय पर है?", "‘क्या यह सीट ख़ाली है?’:", listOf("Is this seat free?", "Free seat this?", "Seat is where?"), 0)),
                unit("l4_u7", 4, 7, "Job Interviews", "नौकरी का इंटरव्यू", 5, 0, UnitStatus.UPCOMING,
                    simplePreview("Answering Common Questions", "इंटरव्यू के प्रमुख सवाल", "अपनी ताक़त (Strengths) और अनुभव विनम्र व साफ़ शब्दों में बताएँ।", "I am a quick learner and hardworking.", "मैं जल्दी सीखने वाला और मेहनती हूँ।", "आय एम अ क्विक लर्नर एंड हार्डवर्किंग।", "Thank you for this opportunity.", "इस अवसर के लिए धन्यवाद।", "इंटरव्यू शुरू होने पर कहें:", listOf("Thank you for having me.", "Give me job.", "Why am I here?"), 0)),
                unit("l4_u8", 4, 8, "Telephone Conversations", "फ़ोन पर बातचीत", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Clear Phone Etiquette", "फ़ोन उठाना और संदेश लेना", "आवाज़ न आने पर या कॉल होल्ड करने के लिए सही वाक्य बोलें।", "Could you speak a bit louder, please?", "क्या आप थोड़ा ज़ोर से बोल सकते हैं?", "कुड यू स्पीक अ बिट लाउडर, प्लीज़?", "Hold on a moment, please.", "कृपया एक पल रुकिए।", "‘आपकी आवाज़ कट रही है’:", listOf("Your voice is breaking.", "Your phone is eating.", "Voice is going away."), 0)),
                unit("l4_u9", 4, 9, "Asking Questions", "सही सवाल पूछना", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Polite Inquiries", "विनम्र तरीके से जानकारी लेना", "किसी अनजान व्यक्ति से मदद या समय पूछने का शालीन तरीका।", "Excuse me, what time is it?", "माफ़ कीजिए, क्या समय हुआ है?", "एक्सक्यूज़ मी, वॉट टाइम इज़ इट?", "Could you repeat that, please?", "क्या आप वह दोहरा सकते हैं?", "ध्यान आकर्षित करने के लिए विनम्र शुरुआत:", listOf("Excuse me", "Listen", "Hey you"), 0)),
                unit("l4_u10", 4, 10, "Giving Directions", "रास्ता पूछना और बताना", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Left, Right & Landmarks", "बाएँ, दाएँ और सीधा रास्ता", "सड़क, मोड़ और नज़दीकी पहचान बताकर रास्ता समझाना।", "Go straight and turn left.", "सीधे जाएँ और बाएँ मुड़ें।", "गो स्ट्रेट एंड टर्न लेफ़्ट।", "It is next to the bank.", "यह बैंक के ठीक बगल में है।", "‘दाएँ मुड़ें’ के लिए सही वाक्य:", listOf("Turn right", "Turn straight", "Go back"), 0))
            )
        ),
        LearningLevel(
            levelNumber = 5,
            titleEn = "Confident Communication",
            titleHi = "आत्मविश्वासी संवाद",
            subtitleHi = "बोलना, पढ़ना, लिखना, सुनना और आम ग़लतियाँ सुधारना",
            badgeText = "Level 5",
            units = listOf(
                unit("l5_u1", 5, 1, "Speaking Exercises", "बोलने का अभ्यास", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Speaking in Natural Flow", "बिना हिचकिचाहट बोलना", "छोटे वाक्यों को जोड़कर 30 सेकंड तक लगातार बोलने की तकनीक।", "I believe practice makes progress.", "मेरा मानना है कि अभ्यास से प्रगति होती है।", "आय बिलीव प्रैक्टिस मेक्स प्रोग्रेस।", "Let me think for a second.", "मुझे एक सेकंड सोचने दीजिए।", "सोचते समय बोलने का अच्छा वाक्य:", listOf("Let me think for a second.", "Stop wait brain.", "I forget words."), 0)),
                unit("l5_u2", 5, 2, "Reading Practice", "पढ़ने का अभ्यास", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Short Stories & Signs", "छोटी कहानियाँ और सूचनाएँ पढ़ना", "अख़बार की सुर्ख़ियाँ, बोर्ड और छोटे पैराग्राफ़ समझकर पढ़ना।", "Caution: Wet Floor", "सावधान: फ़र्श गीला है", "कॉशन: वेट फ़्लोर", "Always read instructions carefully.", "निर्देश हमेशा ध्यान से पढ़ें।", "‘Entrance’ का अर्थ क्या है?", listOf("प्रवेश द्वार", "निकास द्वार", "रुकिए"), 0)),
                unit("l5_u3", 5, 3, "Writing Practice", "लिखने का अभ्यास", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Messages & Short Emails", "मैसेज और छोटे ईमेल लिखना", "छुट्टी का संदेश या काम का ईमेल साफ़ व सही शब्दों में लिखें।", "I am writing to inform you...", "मैं आपको सूचित करने के लिए लिख रहा हूँ...", "आय एम राइटिंग टू इनफ़ॉर्म यू...", "Best regards, Rahul", "सादर, राहुल", "औपचारिक ईमेल के अंत में क्या लिखते हैं?", listOf("Best regards", "Bye bye", "See ya"), 0)),
                unit("l5_u4", 5, 4, "Listening Comprehension", "सुनकर समझना", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Catching Key Words", "अहम शब्द पकड़कर समझना", "जब कोई तेज़ अंग्रेज़ी बोले, तो मुख्य शब्द सुनकर पूरा मतलब समझना।", "The meeting starts at ten.", "मीटिंग दस बजे शुरू होती है।", "द मीटिंग स्टार्ट्स ऐट टेन।", "Please check your email.", "कृपया अपना ईमेल जाँचें।", "सुनते समय सबसे पहले किस पर ध्यान दें?", listOf("मुख्य क्रिया और समय", "हर अक्षर की स्पेलिंग", "बोलने वाले के कपड़े"), 0)),
                unit("l5_u5", 5, 5, "Real-Life Conversations", "असली ज़िंदगी की बातचीत", 5, 0, UnitStatus.UPCOMING,
                    simplePreview("Office & Public Discussions", "ऑफ़िस और बाहरी संवाद", "मीटिंग में अपनी राय विनम्रता से रखना और दूसरों की बात से सहमत होना।", "I agree with your point.", "मैं आपकी बात से सहमत हूँ।", "आय अग्री विद योर पॉइंट।", "In my opinion, this works well.", "मेरी राय में, यह अच्छे से काम करता है।", "अपनी राय शुरू करने का सही तरीका:", listOf("In my opinion...", "Listen my order...", "You are wrong..."), 0)),
                unit("l5_u6", 5, 6, "Common Mistakes", "आम ग़लतियाँ सुधारें", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Indian English Pitfalls", "अक्सर होने वाली ग़लतियाँ", "'Myself Rahul' की जगह 'I am Rahul' और 'Revert back' की जगह 'Reply' बोलना सीखें।", "Say: I am Rahul (Not: Myself Rahul)", "कहें: I am Rahul (न कि Myself Rahul)", "आय एम राहुल", "Please reply soon.", "कृपया जल्द उत्तर दें।", "इनमें से कौन-सा वाक्य बिल्कुल सही है?", listOf("I am Priya.", "Myself Priya.", "Me is Priya."), 0)),
                unit("l5_u7", 5, 7, "Vocabulary Improvement", "शब्दावली को बेहतर बनाएँ", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Upgrading Basic Words", "आम शब्दों के स्मार्ट विकल्प", "'Very good' की जगह 'Excellent' और 'Very tired' की जगह 'Exhausted' बोलें।", "Very good -> Excellent / Wonderful", "बहुत अच्छा -> शानदार / बेहतरीन", "एक्सिलेंट / वंडरफ़ुल", "Your presentation was excellent.", "आपकी प्रस्तुति शानदार थी।", "‘Very big’ का बेहतर विकल्प कौन-सा है?", listOf("Huge", "Tiny", "Weak"), 0)),
                unit("l5_u8", 5, 8, "Confidence Building", "आत्मविश्वास बढ़ाना", 4, 0, UnitStatus.UPCOMING,
                    simplePreview("Thinking in English", "बिना डर के अंग्रेज़ी बोलना", "ग़लती होने से न डरें—छोटे वाक्य बोलकर रोज़ 5 मिनट आत्मविश्वास बनाएँ।", "Mistakes help me grow.", "ग़लतियाँ मुझे आगे बढ़ने में मदद करती हैं।", "मिस्टेक्स हेल्प मी ग्रो।", "I can express my ideas clearly.", "मैं अपने विचार साफ़-साफ़ रख सकता हूँ।", "आत्मविश्वास बढ़ाने का सबसे अच्छा तरीका:", listOf("रोज़ थोड़ा-थोड़ा बोलना", "सिर्फ़ किताब रटना", "ग़लती के डर से चुप रहना"), 0))
            )
        )
    )

    val vocabCategories: List<VocabCategory> = listOf(
        VocabCategory("cat_daily", "Daily Use Words", "रोज़मर्रा के शब्द", 120, 18),
        VocabCategory("cat_family", "Family", "परिवार व रिश्ते", 45, 12),
        VocabCategory("cat_food", "Food", "खान-पान", 80, 9),
        VocabCategory("cat_travel", "Travel", "यात्रा व सफ़र", 65, 6),
        VocabCategory("cat_education", "Education", "शिक्षा व पढ़ाई", 70, 10),
        VocabCategory("cat_shopping", "Shopping", "खरीददारी", 55, 4),
        VocabCategory("cat_office", "Office", "ऑफ़िस व कामकाज", 75, 5),
        VocabCategory("cat_tech", "Technology", "तकनीक व फ़ोन", 60, 8),
        VocabCategory("cat_emotions", "Emotions", "भावनाएँ", 50, 7),
        VocabCategory("cat_health", "Health", "स्वास्थ्य", 60, 3),
        VocabCategory("cat_nature", "Nature", "प्रकृति व मौसम", 50, 4),
        VocabCategory("cat_verbs", "Common Verbs", "ज़रूरी क्रियाएँ", 100, 14)
    )

    val grammarPreviews: List<GrammarPreview> = listOf(
        GrammarPreview(
            id = "gram_1",
            titleEn = "Is / Am / Are",
            titleHi = "है, हूँ, हैं का सही उपयोग",
            summaryHi = "वर्तमान समय में किसी व्यक्ति या चीज़ के बारे में बताने के लिए प्रयोग होता है।",
            structurePattern = "Subject + is/am/are + Complement",
            positiveExampleEn = "I am confident.",
            positiveExampleHi = "मैं आत्मविश्वासी हूँ।",
            commonMistakeHi = "ग़लती: 'I is ready' ❌ → सही: 'I am ready' ✅"
        ),
        GrammarPreview(
            id = "gram_2",
            titleEn = "Simple Present Tense",
            titleHi = "दैनिक आदतें (ता है, ती है)",
            summaryHi = "जो काम आप हर दिन करते हैं या जो हमेशा सच होता है, उसे बताने के लिए।",
            structurePattern = "I/You/We/They + Verb  |  He/She + Verb(s/es)",
            positiveExampleEn = "She speaks clearly.",
            positiveExampleHi = "वह साफ़ बोलती है।",
            commonMistakeHi = "ग़लती: 'She speak clearly' ❌ → सही: 'She speaks clearly' ✅"
        ),
        GrammarPreview(
            id = "gram_3",
            titleEn = "Polite Questions (Can / Could)",
            titleHi = "विनम्र सवाल पूछना",
            summaryHi = "किसी से मदद माँगने या अनुमति लेने के लिए सबसे सरल तरीका।",
            structurePattern = "Can / Could + Subject + Verb + ... ?",
            positiveExampleEn = "Could you help me, please?",
            positiveExampleHi = "क्या आप कृपया मेरी मदद कर सकते हैं?",
            commonMistakeHi = "हमेशा अंत में 'please' जोड़ने से वाक्य अधिक सभ्य बनता है।"
        )
    )

    val weeklyProgress: List<DayProgress> = listOf(
        DayProgress("Mon", "सोम", 30, true),
        DayProgress("Tue", "मंगल", 45, true),
        DayProgress("Wed", "बुध", 25, true),
        DayProgress("Thu", "गुरु", 50, true),
        DayProgress("Fri", "शुक्र", 40, true),
        DayProgress("Sat", "शनि", 20, false),
        DayProgress("Sun", "रवि", 0, false)
    )

    val topicStrengths: List<TopicStrength> = listOf(
        TopicStrength("अभिवादन और शिष्टाचार", "Greetings & Politeness", 0.90f, false),
        TopicStrength("अंग्रेज़ी वर्णमाला व स्वर", "Alphabet & Vowels", 0.85f, false),
        TopicStrength("बुनियादी शब्द", "Basic Vocabulary", 0.62f, false),
        TopicStrength("Is / Am / Are वाक्य", "Sentence Basics", 0.35f, true)
    )

    val achievements: List<AchievementBadge> = listOf(
        AchievementBadge("ach_1", "पहला कदम", "First Step", "पहला पाठ सफलतापूर्वक पूरा किया", true, "पूर्ण ✓"),
        AchievementBadge("ach_2", "5-दिन की स्ट्रीक", "5-Day Streak", "लगातार 5 दिन अंग्रेज़ी सीखी", true, "5/5 दिन ✓"),
        AchievementBadge("ach_3", "शब्द संग्रहकर्ता", "Word Collector", "50 नए अंग्रेज़ी शब्द सीखे", false, "38/50 शब्द"),
        AchievementBadge("ach_4", "व्याकरण मित्र", "Grammar Buddy", "पहले 3 व्याकरण अध्याय पूरे करें", false, "1/3 पूरे")
    )

    val roadmapPhases: List<PhaseRoadmapItem> = listOf(
        PhaseRoadmapItem(1, "चरण 1: ऐप की संरचना और UI डिज़ाइन", "App Structure & UI", "सक्रिय (Active)", true),
        PhaseRoadmapItem(2, "चरण 2: होम स्क्रीन और नेविगेशन डेटा", "Home & Navigation Polish", "आगामी", false),
        PhaseRoadmapItem(3, "चरण 3: 9-फ़ॉर्मेट पाठ व क्विज़ इंजन", "Lesson & Quiz Engine", "आगामी", false),
        PhaseRoadmapItem(4, "चरण 4: संपूर्ण शब्दावली व व्याकरण मॉड्यूल", "Vocabulary & Grammar", "आगामी", false),
        PhaseRoadmapItem(5, "चरण 5: XP, स्ट्रीक और Room डेटाबेस", "XP, Streaks & Progress", "आगामी", false),
        PhaseRoadmapItem(6, "चरण 6: बोलना और सुनना (TTS / Speech)", "Speaking & Listening", "आगामी", false),
        PhaseRoadmapItem(7, "चरण 7: ऑफ़लाइन सहायता और परीक्षण", "Offline Support & Testing", "आगामी", false),
        PhaseRoadmapItem(8, "चरण 8: Android APK पैकेज तैयारी", "Android Release Package", "आगामी", false)
    )

    private fun unit(
        id: String,
        level: Int,
        order: Int,
        titleEn: String,
        titleHi: String,
        lessonsCount: Int,
        completed: Int,
        status: UnitStatus,
        lesson: SampleLessonPreview
    ) = LearningUnit(
        id = id,
        levelNumber = level,
        orderNumber = order,
        titleEn = titleEn,
        titleHi = titleHi,
        lessonsCount = lessonsCount,
        completedLessons = completed,
        estimatedMinutes = 6,
        xpReward = lessonsCount * 10,
        status = status,
        sampleLesson = lesson
    )

    private fun simplePreview(
        titleEn: String,
        titleHi: String,
        explanationHi: String,
        phrase: String,
        meaningHi: String,
        pronunciationHi: String,
        exampleEn: String,
        exampleHi: String,
        quizHi: String,
        options: List<String>,
        correctIdx: Int
    ) = SampleLessonPreview(
        titleEn = titleEn,
        titleHi = titleHi,
        explanationHi = explanationHi,
        englishPhrase = phrase,
        hindiMeaning = meaningHi,
        pronunciationHi = pronunciationHi,
        realLifeExampleEn = exampleEn,
        realLifeExampleHi = exampleHi,
        quizQuestionHi = quizHi,
        quizOptions = options,
        correctOptionIndex = correctIdx,
        xpReward = 20
    )
}
