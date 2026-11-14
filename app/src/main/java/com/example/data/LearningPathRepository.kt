package com.example.data

import com.example.model.CefrStage
import com.example.model.CourseSection
import com.example.model.CourseSectionOutline
import com.example.model.GuidebookPattern
import com.example.model.LessonNode
import com.example.model.LessonNodeStatus
import com.example.model.LessonNodeType
import com.example.model.PathUnit
import com.example.model.PathUnitTheme
import com.example.model.SampleLessonPreview

object LearningPathRepository {

    val sectionOutlines: List<CourseSectionOutline> = listOf(
        CourseSectionOutline(
            sectionNumber = 1,
            titleEn = "Beginner English",
            titleHi = "शुरुआती अंग्रेज़ी",
            cefrStage = CefrStage.PRE_A1,
            unitRangeLabel = "Units 1–5",
            isActive = true
        ),
        CourseSectionOutline(
            sectionNumber = 2,
            titleEn = "Elementary English",
            titleHi = "प्राथमिक अंग्रेज़ी",
            cefrStage = CefrStage.A1,
            unitRangeLabel = "Units 6–12",
            isActive = false
        ),
        CourseSectionOutline(
            sectionNumber = 3,
            titleEn = "Pre-Intermediate English",
            titleHi = "मध्यम-पूर्व अंग्रेज़ी",
            cefrStage = CefrStage.A2,
            unitRangeLabel = "Units 13–19",
            isActive = false
        ),
        CourseSectionOutline(
            sectionNumber = 4,
            titleEn = "Intermediate English",
            titleHi = "मध्यम अंग्रेज़ी",
            cefrStage = CefrStage.B1,
            unitRangeLabel = "Units 20–25",
            isActive = false
        )
    )

    val phase1Section: CourseSection = CourseSection(
        id = "sec_1",
        orderNumber = 1,
        titleEn = "Beginner English",
        titleHi = "खंड 1: शुरुआती अंग्रेज़ी",
        subtitleHi = "अभिवादन, परिचय, रोज़मर्रा के वाक्य और परिवार के बारे में बात करना",
        cefrStage = CefrStage.PRE_A1,
        isAvailableInPhase1 = true,
        units = listOf(
            // UNIT 1: THE BASICS
            PathUnit(
                id = "s1_u1",
                sectionId = "sec_1",
                orderNumber = 1,
                titleEn = "The Basics",
                titleHi = "बुनियादी शुरुआत",
                descriptionHi = "नमस्ते कहना, अपना नाम बताना और सरल वाक्य बनाना सीखें",
                learningObjectivesHi = listOf(
                    "अंग्रेज़ी में अभिवादन करना (Hello, Good morning)",
                    "अपना नाम और शहर बताना",
                    "I, You, We जैसे मुख्य सर्वनाम समझना"
                ),
                guidebookSummaryHi = "किसी से मिलते समय 'Hello' या समय के अनुसार 'Good morning / Good evening' कहें। अपना नाम बताने के लिए 'My name is ...' या 'I am ...' बोलें।",
                guidebookPatterns = listOf(
                    GuidebookPattern("Hello! How are you?", "नमस्ते! आप कैसे हैं?", "हैलो! हाउ आर यू?"),
                    GuidebookPattern("My name is Aarav.", "मेरा नाम आरव है।", "माय नेम इज़ आरव।"),
                    GuidebookPattern("I am fine, thank you.", "मैं ठीक हूँ, धन्यवाद।", "आय एम फ़ाइन, थैंक यू।")
                ),
                theme = PathUnitTheme.EMERALD,
                nodes = listOf(
                    LessonNode(
                        id = "s1_u1_n1",
                        unitId = "s1_u1",
                        orderInUnit = 1,
                        titleEn = "Greetings",
                        titleHi = "अभिवादन",
                        type = LessonNodeType.NEW_LESSON,
                        status = LessonNodeStatus.AVAILABLE,
                        xpReward = 20,
                        estimatedMinutes = 3,
                        unlockRequirementId = null,
                        learningObjectiveHi = "सुबह से रात तक अंग्रेज़ी में सही तरीके से नमस्ते और हालचाल पूछना सीखें।",
                        samplePreview = SampleLessonPreview(
                            titleEn = "Greetings: Hello & Good Morning",
                            titleHi = "अभिवादन: नमस्ते और हालचाल पूछना",
                            explanationHi = "अंग्रेज़ी में किसी से भी मिलने पर 'Hello' कह सकते हैं। सुबह 12 बजे से पहले 'Good morning' कहना विनम्र माना जाता है।",
                            englishPhrase = "Good morning! How are you?",
                            hindiMeaning = "सुप्रभात! आप कैसे हैं?",
                            pronunciationHi = "गुड मॉर्निंग! हाउ आर यू?",
                            realLifeExampleEn = "Hello! I am fine, thank you.",
                            realLifeExampleHi = "नमस्ते! मैं ठीक हूँ, धन्यवाद।",
                            quizQuestionHi = "सुबह किसी से मिलने पर सबसे सही अभिवादन कौन-सा है?",
                            quizOptions = listOf("Good night", "Good morning", "Goodbye"),
                            correctOptionIndex = 1,
                            xpReward = 20
                        )
                    ),
                    LessonNode(
                        id = "s1_u1_n2",
                        unitId = "s1_u1",
                        orderInUnit = 2,
                        titleEn = "Introductions",
                        titleHi = "अपना परिचय",
                        type = LessonNodeType.SPEAKING,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u1_n1",
                        learningObjectiveHi = "अपना नाम, शहर और किसी से मिलकर खुशी जताने के वाक्य बोलना सीखें।",
                        samplePreview = SampleLessonPreview(
                            titleEn = "Introducing Yourself",
                            titleHi = "आसान वाक्यों में अपना परिचय दें",
                            explanationHi = "अपना नाम बताने के लिए 'My name is ...' और शहर बताने के लिए 'I am from ...' का उपयोग करें।",
                            englishPhrase = "My name is Priya. Nice to meet you!",
                            hindiMeaning = "मेरा नाम प्रिया है। आपसे मिलकर अच्छा लगा!",
                            pronunciationHi = "माय नेम इज़ प्रिया। नाइस टू मीट यू!",
                            realLifeExampleEn = "Hi, I am Rahul from Jaipur.",
                            realLifeExampleHi = "नमस्ते, मैं जयपुर से राहुल हूँ।",
                            quizQuestionHi = "अपना शहर बताने के लिए सही वाक्य चुनें:",
                            quizOptions = listOf("I am from Delhi.", "I name Delhi.", "From Delhi am."),
                            correctOptionIndex = 0,
                            xpReward = 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u1_n3",
                        unitId = "s1_u1",
                        orderInUnit = 3,
                        titleEn = "Basic Words",
                        titleHi = "बुनियादी शब्द",
                        type = LessonNodeType.VOCABULARY,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 20,
                        estimatedMinutes = 3,
                        unlockRequirementId = "s1_u1_n2",
                        learningObjectiveHi = "पानी, चाय, किताब, घर जैसे रोज़मर्रा के शुरुआती अंग्रेज़ी शब्द याद करें।",
                        samplePreview = SampleLessonPreview(
                            titleEn = "Essential Daily Words",
                            titleHi = "रोज़ काम आने वाले शुरुआती शब्द",
                            explanationHi = "शुरुआत में रोज़ दिखने वाली चीज़ों के अंग्रेज़ी नाम याद करने से वाक्य बनाना आसान होता है।",
                            englishPhrase = "Water, Tea, Book, Home",
                            hindiMeaning = "पानी, चाय, किताब, घर",
                            pronunciationHi = "वॉटर, टी, बुक, होम",
                            realLifeExampleEn = "Please give me water.",
                            realLifeExampleHi = "कृपया मुझे पानी दीजिए।",
                            quizQuestionHi = "‘Water’ का हिंदी अर्थ क्या है?",
                            quizOptions = listOf("चाय", "पानी", "किताब"),
                            correctOptionIndex = 1,
                            xpReward = 20
                        )
                    ),
                    LessonNode(
                        id = "s1_u1_n4",
                        unitId = "s1_u1",
                        orderInUnit = 4,
                        titleEn = "Pronouns",
                        titleHi = "सर्वनाम (I, You)",
                        type = LessonNodeType.GRAMMAR,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u1_n3",
                        learningObjectiveHi = "मैं (I), आप/तुम (You), हम (We) और वे (They) का सही इस्तेमाल समझें।",
                        samplePreview = SampleLessonPreview(
                            titleEn = "Subject Pronouns: I, You, We",
                            titleHi = "मैं, आप और हम कैसे कहें",
                            explanationHi = "खुद के लिए हमेशा बड़े 'I' का उपयोग होता है। सामने वाले के लिए 'You' बोलते हैं।",
                            englishPhrase = "I am a student. You are my friend.",
                            hindiMeaning = "मैं एक विद्यार्थी हूँ। आप मेरे मित्र हैं।",
                            pronunciationHi = "आय एम अ स्टूडेंट। यू आर माय फ़्रेंड।",
                            realLifeExampleEn = "We learn English daily.",
                            realLifeExampleHi = "हम रोज़ अंग्रेज़ी सीखते हैं।",
                            quizQuestionHi = "‘आप’ या ‘तुम’ के लिए अंग्रेज़ी में क्या शब्द है?",
                            quizOptions = listOf("I", "You", "We"),
                            correctOptionIndex = 1,
                            xpReward = 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u1_n5",
                        unitId = "s1_u1",
                        orderInUnit = 5,
                        titleEn = "Simple Sentences",
                        titleHi = "सरल वाक्य",
                        type = LessonNodeType.LISTENING,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u1_n4",
                        learningObjectiveHi = "छोटे-छोटे अंग्रेज़ी वाक्यों को सुनकर उनका सही मतलब समझना सीखें।",
                        samplePreview = SampleLessonPreview(
                            titleEn = "Building Short Sentences",
                            titleHi = "3 शब्दों के आसान वाक्य",
                            explanationHi = "छोटे वाक्यों में पहले कर्ता (I/You), फिर क्रिया (drink/read) और फिर चीज़ (tea/book) आती है।",
                            englishPhrase = "I drink tea.",
                            hindiMeaning = "मैं चाय पीता हूँ।",
                            pronunciationHi = "आय ड्रिंक टी।",
                            realLifeExampleEn = "You read a good book.",
                            realLifeExampleHi = "आप एक अच्छी किताब पढ़ते हैं।",
                            quizQuestionHi = "‘मैं पानी पीता हूँ’ का सही अंग्रेज़ी अनुवाद चुनें:",
                            quizOptions = listOf("I water drink.", "I drink water.", "Drink water I."),
                            correctOptionIndex = 1,
                            xpReward = 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u1_n6",
                        unitId = "s1_u1",
                        orderInUnit = 6,
                        titleEn = "Story: First Hello",
                        titleHi = "कहानी: पहली मुलाक़ात",
                        type = LessonNodeType.STORY,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 30,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u1_n5",
                        learningObjectiveHi = "दो नए सहपाठियों की छोटी बातचीत पढ़कर और समझकर आत्मविश्वास बढ़ाएँ।",
                        samplePreview = SampleLessonPreview(
                            titleEn = "Mini Story: A Friendly Chat",
                            titleHi = "छोटी कहानी: दो दोस्तों की पहली बात",
                            explanationHi = "अमित और नेहा पहली बार क्लास में मिलते हैं—देखिए वे कैसे अंग्रेज़ी में बात शुरू करते हैं।",
                            englishPhrase = "Amit: Hello! I am Amit. Neha: Hi Amit, nice to meet you!",
                            hindiMeaning = "अमित: नमस्ते! मैं अमित हूँ। नेहा: हाय अमित, आपसे मिलकर अच्छा लगा!",
                            pronunciationHi = "हैलो! आय एम अमित। हाय अमित, नाइस टू मीट यू!",
                            realLifeExampleEn = "Are you from Lucknow? Yes, I am!",
                            realLifeExampleHi = "क्या आप लखनऊ से हैं? हाँ, मैं वहीं से हूँ!",
                            quizQuestionHi = "नेहा ने मिलने की खुशी जताने के लिए क्या कहा?",
                            quizOptions = listOf("Goodbye!", "Nice to meet you!", "Good night!"),
                            correctOptionIndex = 1,
                            xpReward = 30
                        )
                    ),
                    LessonNode(
                        id = "s1_u1_n7",
                        unitId = "s1_u1",
                        orderInUnit = 7,
                        titleEn = "Practice & Review",
                        titleHi = "अभ्यास दोहराव",
                        type = LessonNodeType.REVIEW,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 3,
                        unlockRequirementId = "s1_u1_n6",
                        learningObjectiveHi = "यूनिट परीक्षा से पहले अभिवादन, शब्द और सरल वाक्यों का तेज़ दोहराव करें।",
                        samplePreview = SampleLessonPreview(
                            titleEn = "Unit 1 Quick Revision",
                            titleHi = "इकाई 1 का तेज़ दोहराव",
                            explanationHi = "अब तक सीखे गए सभी आसान वाक्यों और शब्दों को एक बार फिर पक्का करें।",
                            englishPhrase = "Hello, my name is Ravi. I drink water.",
                            hindiMeaning = "नमस्ते, मेरा नाम रवि है। मैं पानी पीता हूँ।",
                            pronunciationHi = "हैलो, माय नेम इज़ रवि। आय ड्रिंक वॉटर।",
                            realLifeExampleEn = "We are friends.",
                            realLifeExampleHi = "हम दोस्त हैं।",
                            quizQuestionHi = "इनमें से कौन-सा वाक्य सही है?",
                            quizOptions = listOf("My name is Ravi.", "Name my Ravi.", "I Ravi name."),
                            correctOptionIndex = 0,
                            xpReward = 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u1_n8",
                        unitId = "s1_u1",
                        orderInUnit = 8,
                        titleEn = "Unit 1 Test",
                        titleHi = "इकाई 1 परीक्षा",
                        type = LessonNodeType.UNIT_TEST,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 50,
                        estimatedMinutes = 5,
                        unlockRequirementId = "s1_u1_n7",
                        learningObjectiveHi = "70% या अधिक अंक लाकर इकाई 1 पूरी करें और इकाई 2 अनलॉक करें!",
                        samplePreview = SampleLessonPreview(
                            titleEn = "Unit 1 Checkpoint Test",
                            titleHi = "इकाई 1 चेकपॉइंट परीक्षा",
                            explanationHi = "यह चेकपॉइंट परीक्षा आपकी Unit 1 की तैयारी जाँचेगी। पास होते ही Unit 2 खुल जाएगी!",
                            englishPhrase = "Good evening! I am happy to meet you.",
                            hindiMeaning = "शुभ संध्या! आपसे मिलकर खुशी हुई।",
                            pronunciationHi = "गुड ईवनिंग! आय एम हैप्पी टू मीट यू।",
                            realLifeExampleEn = "Hello! My name is Suman and I am from Pune.",
                            realLifeExampleHi = "नमस्ते! मेरा नाम सुमन है और मैं पुणे से हूँ।",
                            quizQuestionHi = "शाम के समय किसी से मिलने पर क्या कहेंगे?",
                            quizOptions = listOf("Good morning", "Good evening", "Good night"),
                            correctOptionIndex = 1,
                            xpReward = 50
                        )
                    )
                )
            ),

            // UNIT 2: COMMON PHRASES
            PathUnit(
                id = "s1_u2",
                sectionId = "sec_1",
                orderNumber = 2,
                titleEn = "Common Phrases",
                titleHi = "रोज़मर्रा के वाक्य",
                descriptionHi = "हाँ-ना, कृपया, धन्यवाद और छोटे सवाल पूछने के विनम्र तरीके",
                learningObjectivesHi = listOf(
                    "Yes, No, Please और Thank you का सहज उपयोग",
                    "माफ़ी माँगना (Excuse me / Sorry)",
                    "छोटे सवाल पूछना और जवाब देना"
                ),
                guidebookSummaryHi = "अंग्रेज़ी बातचीत में विनम्रता बहुत ज़रूरी है। किसी को रोकने के लिए 'Excuse me', माफ़ी के लिए 'Sorry' और मदद के लिए 'Please' बोलें।",
                guidebookPatterns = listOf(
                    GuidebookPattern("Please help me.", "कृपया मेरी मदद करें।", "प्लीज़ हेल्प मी।"),
                    GuidebookPattern("Thank you very much!", "बहुत-बहुत धन्यवाद!", "थैंक यू वेरी मच!"),
                    GuidebookPattern("Excuse me, where is the bus stop?", "माफ़ कीजिए, बस स्टॉप कहाँ है?", "एक्सक्यूज़ मी, वेअर इज़ द बस स्टॉप?")
                ),
                theme = PathUnitTheme.INDIGO,
                nodes = listOf(
                    LessonNode(
                        id = "s1_u2_n1",
                        unitId = "s1_u2",
                        orderInUnit = 1,
                        titleEn = "Everyday Expressions",
                        titleHi = "आम अभिव्यक्तियाँ",
                        type = LessonNodeType.NEW_LESSON,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 20,
                        estimatedMinutes = 3,
                        unlockRequirementId = "s1_u1_n8",
                        learningObjectiveHi = "No problem, Excuse me और See you soon जैसे उपयोगी वाक्य सीखें।",
                        samplePreview = simplePreview(
                            "Everyday Expressions", "आम अभिव्यक्तियाँ",
                            "भीड़ में रास्ता माँगते समय या बात शुरू करते समय 'Excuse me' बहुत काम आता है।",
                            "Excuse me, one minute please.", "माफ़ कीजिए, कृपया एक मिनट।",
                            "एक्सक्यूज़ मी, वन मिनट प्लीज़।",
                            "See you tomorrow!", "कल मिलते हैं!",
                            "किसी का ध्यान विनम्रता से खींचने के लिए क्या बोलेंगे?",
                            listOf("Excuse me", "Go away", "Stop"), 0, 20
                        )
                    ),
                    LessonNode(
                        id = "s1_u2_n2",
                        unitId = "s1_u2",
                        orderInUnit = 2,
                        titleEn = "Yes and No",
                        titleHi = "हाँ और नहीं",
                        type = LessonNodeType.VOCABULARY,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 20,
                        estimatedMinutes = 3,
                        unlockRequirementId = "s1_u2_n1",
                        learningObjectiveHi = "विनम्र तरीके से हाँ (Yes, sure) और न (No, thank you) कहना सीखें।",
                        samplePreview = simplePreview(
                            "Polite Yes & No", "विनम्र हाँ और नहीं",
                            "सिर्फ़ 'No' की जगह 'No, thank you' कहना ज़्यादा अच्छा लगता है।",
                            "Yes, please. / No, thank you.", "हाँ, कृपया। / नहीं, धन्यवाद।",
                            "येस, प्लीज़। / नो, थैंक यू।",
                            "Would you like tea? Yes, please.", "क्या आप चाय लेंगे? हाँ, कृपया।",
                            "विनम्रता से मना करने का सही तरीका:",
                            listOf("No, never!", "No, thank you.", "Not want."), 1, 20
                        )
                    ),
                    LessonNode(
                        id = "s1_u2_n3",
                        unitId = "s1_u2",
                        orderInUnit = 3,
                        titleEn = "Please & Thank You",
                        titleHi = "कृपया और धन्यवाद",
                        type = LessonNodeType.LISTENING,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u2_n2",
                        learningObjectiveHi = "आभार व्यक्त करने और जवाब में 'You are welcome' बोलने का अभ्यास करें।",
                        samplePreview = simplePreview(
                            "Gratitude Phrases", "धन्यवाद और उत्तर देना",
                            "जब कोई आपको 'Thank you' कहे, तो मुस्कुराकर 'You are welcome' कहें।",
                            "Thank you! — You're welcome.", "धन्यवाद! — आपका स्वागत है।",
                            "थैंक यू! — योर वेलकम।",
                            "Thank you for your help.", "आपकी मदद के लिए धन्यवाद।",
                            "'Thank you' के जवाब में क्या कहा जाता है?",
                            listOf("You're welcome", "Sorry", "Hello"), 0, 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u2_n4",
                        unitId = "s1_u2",
                        orderInUnit = 4,
                        titleEn = "Simple Questions",
                        titleHi = "आसान सवाल",
                        type = LessonNodeType.GRAMMAR,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u2_n3",
                        learningObjectiveHi = "What, Where और How से छोटे दैनिक सवाल बनाना सीखें।",
                        samplePreview = simplePreview(
                            "Asking Simple Questions", "What और Where से सवाल",
                            "किसी चीज़ का नाम पूछने के लिए 'What is this?' और जगह के लिए 'Where is...?' पूछें।",
                            "What is this? Where is my bag?", "यह क्या है? मेरा बैग कहाँ है?",
                            "वॉट इज़ दिस? वेअर इज़ माय बैग?",
                            "Where is the station?", "स्टेशन कहाँ है?",
                            "'यह क्या है?' को अंग्रेज़ी में क्या कहेंगे?",
                            listOf("Where is this?", "What is this?", "Who is this?"), 1, 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u2_n5",
                        unitId = "s1_u2",
                        orderInUnit = 5,
                        titleEn = "Responding",
                        titleHi = "सवालों का जवाब",
                        type = LessonNodeType.SPEAKING,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u2_n4",
                        learningObjectiveHi = "रोज़मर्रा के छोटे सवालों का स्पष्ट और आत्मविश्वास से जवाब देना सीखें।",
                        samplePreview = simplePreview(
                            "Quick Polite Replies", "छोटे सवालों के सही जवाब",
                            "अगर कोई पूछे 'How are you?', तो 'I am good, and you?' बोलकर बातचीत आगे बढ़ाएँ।",
                            "I am good, thank you. And you?", "मैं अच्छा हूँ, धन्यवाद। और आप?",
                            "आय एम गुड, थैंक यू। एंड यू?",
                            "Yes, I understand.", "हाँ, मैं समझ गया।",
                            "अगर किसी की बात समझ न आए तो क्या कहेंगे?",
                            listOf("Please repeat that.", "I am tea.", "Thank you bye."), 0, 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u2_n6",
                        unitId = "s1_u2",
                        orderInUnit = 6,
                        titleEn = "Unit 2 Practice",
                        titleHi = "इकाई 2 अभ्यास",
                        type = LessonNodeType.REVIEW,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 3,
                        unlockRequirementId = "s1_u2_n5",
                        learningObjectiveHi = "विनम्र वाक्यों और सवाल-जवाब का मिश्रित अभ्यास करें।",
                        samplePreview = simplePreview(
                            "Phrases Booster", "वाक्यांश अभ्यास",
                            "Please, Sorry, Thank you और छोटे सवालों को सही स्थिति में चुनें।",
                            "Sorry, I am late.", "माफ़ कीजिए, मुझे देर हो गई।",
                            "सॉरी, आय एम लेट।",
                            "No problem at all!", "कोई बात नहीं!",
                            "गलती होने पर क्या कहेंगे?",
                            listOf("Sorry", "Welcome", "Morning"), 0, 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u2_n7",
                        unitId = "s1_u2",
                        orderInUnit = 7,
                        titleEn = "Unit 2 Test",
                        titleHi = "इकाई 2 परीक्षा",
                        type = LessonNodeType.UNIT_TEST,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 50,
                        estimatedMinutes = 5,
                        unlockRequirementId = "s1_u2_n6",
                        learningObjectiveHi = "इकाई 2 चेकपॉइंट पास करके परिवार वाली इकाई 3 अनलॉक करें!",
                        samplePreview = simplePreview(
                            "Unit 2 Checkpoint Test", "इकाई 2 चेकपॉइंट परीक्षा",
                            "रोज़मर्रा के वाक्यों पर अपनी पकड़ पक्की करें।",
                            "Excuse me, please help me.", "माफ़ कीजिए, कृपया मेरी मदद करें।",
                            "एक्सक्यूज़ मी, प्लीज़ हेल्प मी।",
                            "Thank you so much!", "आपका बहुत-बहुत धन्यवाद!",
                            "'कृपया' को अंग्रेज़ी में क्या कहते हैं?",
                            listOf("Please", "Sorry", "Thanks"), 0, 50
                        )
                    )
                )
            ),

            // UNIT 3: PEOPLE AND FAMILY
            PathUnit(
                id = "s1_u3",
                sectionId = "sec_1",
                orderNumber = 3,
                titleEn = "People and Family",
                titleHi = "लोग और परिवार",
                descriptionHi = "रिश्तों के नाम, He/She, और My/Your/His/Her का प्रयोग",
                learningObjectivesHi = listOf(
                    "Mother, Father, Sister, Brother जैसे रिश्ते बोलना",
                    "लड़के के लिए He और लड़की के लिए She का उपयोग",
                    "My, Your, His, Her से संबंध बताना"
                ),
                guidebookSummaryHi = "परिवार के सदस्यों का परिचय देते समय पुरुष के लिए 'He / His' और महिला के लिए 'She / Her' का उपयोग करें।",
                guidebookPatterns = listOf(
                    GuidebookPattern("This is my mother.", "ये मेरी माँ हैं।", "दिस इज़ माय मदर।"),
                    GuidebookPattern("He is my brother.", "वह मेरा भाई है।", "ही इज़ माय ब्रदर।"),
                    GuidebookPattern("Her name is Ananya.", "उसका नाम अनन्या है।", "हर नेम इज़ अनन्या।")
                ),
                theme = PathUnitTheme.SAFFRON,
                nodes = listOf(
                    LessonNode(
                        id = "s1_u3_n1",
                        unitId = "s1_u3",
                        orderInUnit = 1,
                        titleEn = "Family Members",
                        titleHi = "परिवार के सदस्य",
                        type = LessonNodeType.VOCABULARY,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 20,
                        estimatedMinutes = 3,
                        unlockRequirementId = "s1_u2_n7",
                        learningObjectiveHi = "माता-पिता, भाई-बहन और दादा-दादी के अंग्रेज़ी शब्द सीखें।",
                        samplePreview = simplePreview(
                            "Family Words", "परिवार से जुड़े शब्द",
                            "Mother (माँ), Father (पिता), Brother (भाई), Sister (बहन)—ये शब्द हर परिचय में काम आते हैं।",
                            "I love my family.", "मैं अपने परिवार से प्यार करता हूँ।",
                            "आय लव माय फ़ैमिली।",
                            "My brother lives in Bhopal.", "मेरा भाई भोपाल में रहता है।",
                            "'Sister' का अर्थ क्या है?",
                            listOf("माँ", "बहन", "चाची"), 1, 20
                        )
                    ),
                    LessonNode(
                        id = "s1_u3_n2",
                        unitId = "s1_u3",
                        orderInUnit = 2,
                        titleEn = "He and She",
                        titleHi = "He और She",
                        type = LessonNodeType.GRAMMAR,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u3_n1",
                        learningObjectiveHi = "किसी लड़के/पुरुष के लिए He और लड़की/महिला के लिए She का सही उपयोग।",
                        samplePreview = simplePreview(
                            "Using He and She", "He और She में अंतर",
                            "He = वह (लड़का/पुरुष), She = वह (लड़की/महिला)। दोनों के साथ 'is' लगता है।",
                            "He is a doctor. She is a teacher.", "वह एक डॉक्टर है। वह एक शिक्षिका है।",
                            "ही इज़ अ डॉक्टर। शी इज़ अ टीचर।",
                            "She is my elder sister.", "वह मेरी बड़ी बहन है।",
                            "बहन के बारे में बात करते समय क्या उपयोग करेंगे?",
                            listOf("He", "She", "It"), 1, 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u3_n3",
                        unitId = "s1_u3",
                        orderInUnit = 3,
                        titleEn = "Possessives",
                        titleHi = "My, His, Her",
                        type = LessonNodeType.CHALLENGE,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 30,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u3_n2",
                        learningObjectiveHi = "'मेरा', 'आपका', 'उसका' बताकर चीज़ों और रिश्तों से संबंध जोड़ें।",
                        samplePreview = simplePreview(
                            "My, Your, His, Her", "संबंध बताने वाले शब्द",
                            "My = मेरा, Your = आपका, His = उसका (लड़का), Her = उसका (लड़की)।",
                            "His name is Kabir. Her bag is blue.", "उसका नाम कबीर है। उसका बैग नीला है।",
                            "हिज़ नेम इज़ कबीर। हर बैग इज़ ब्लू।",
                            "This is your phone.", "यह आपका फ़ोन है।",
                            "‘मेरा घर’ को अंग्रेज़ी में क्या कहेंगे?",
                            listOf("Your home", "My home", "His home"), 1, 30
                        )
                    ),
                    LessonNode(
                        id = "s1_u3_n4",
                        unitId = "s1_u3",
                        orderInUnit = 4,
                        titleEn = "Describing People",
                        titleHi = "लोगों का वर्णन",
                        type = LessonNodeType.LISTENING,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 25,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u3_n3",
                        learningObjectiveHi = "Kind, Tall, Happy, Honest जैसे गुणवाचक शब्दों से लोगों के बारे में बताएँ।",
                        samplePreview = simplePreview(
                            "Describing Family & Friends", "स्वभाव और गुण बताना",
                            "किसी की तारीफ़ या पहचान बताने के लिए Kind (दयालु), Smart (होशियार), Friendly (मिलनसार) बोलें।",
                            "My father is very kind.", "मेरे पिता बहुत दयालु हैं।",
                            "माय फ़ादर इज़ वेरी काइंड।",
                            "She is friendly and smart.", "वह मिलनसार और होशियार है।",
                            "'Kind' का हिंदी अर्थ क्या है?",
                            listOf("दयालु", "नाराज़", "लंबा"), 0, 25
                        )
                    ),
                    LessonNode(
                        id = "s1_u3_n5",
                        unitId = "s1_u3",
                        orderInUnit = 5,
                        titleEn = "Story: Meet My Family",
                        titleHi = "कहानी: मेरे परिवार से मिलें",
                        type = LessonNodeType.STORY,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 35,
                        estimatedMinutes = 4,
                        unlockRequirementId = "s1_u3_n4",
                        learningObjectiveHi = "तस्वीर दिखाते हुए अपने पूरे परिवार का परिचय देने वाली कहानी समझें।",
                        samplePreview = simplePreview(
                            "Story: Family Photo", "कहानी: परिवार की तस्वीर",
                            "अर्जुन अपने दोस्त को फ़ोन पर अपने परिवार की तस्वीर दिखाता है।",
                            "Look! This is my mother. She loves cooking.", "देखो! ये मेरी माँ हैं। उन्हें खाना बनाना पसंद है।",
                            "लुक! दिस इज़ माय मदर। शी लव्स कुकिंग।",
                            "And he is my little brother, Rohan.", "और यह मेरा छोटा भाई रोहन है।",
                            "रोहन कौन है?",
                            listOf("अर्जुन के पिता", "अर्जुन का छोटा भाई", "अर्जुन का शिक्षक"), 1, 35
                        )
                    ),
                    LessonNode(
                        id = "s1_u3_n6",
                        unitId = "s1_u3",
                        orderInUnit = 6,
                        titleEn = "Unit 3 Test",
                        titleHi = "इकाई 3 परीक्षा",
                        type = LessonNodeType.UNIT_TEST,
                        status = LessonNodeStatus.LOCKED,
                        xpReward = 50,
                        estimatedMinutes = 5,
                        unlockRequirementId = "s1_u3_n5",
                        learningObjectiveHi = "इकाई 3 परीक्षा पास करके अगली इकाइयों की ओर बढ़ें!",
                        samplePreview = simplePreview(
                            "Unit 3 Checkpoint Test", "इकाई 3 चेकपॉइंट परीक्षा",
                            "परिवार, सर्वनाम और संबंधवाचक शब्दों का अंतिम टेस्ट।",
                            "She is my sister and her name is Meera.", "वह मेरी बहन है और उसका नाम मीरा है।",
                            "शी इज़ माय सिस्टर एंड हर नेम इज़ मीरा।",
                            "He is our teacher.", "वे हमारे शिक्षक हैं।",
                            "'उसकी किताब' (लड़के के लिए) का सही अनुवाद:",
                            listOf("Her book", "His book", "You book"), 1, 50
                        )
                    )
                )
            )
        )
    )

    private fun simplePreview(
        titleEn: String,
        titleHi: String,
        explanationHi: String,
        englishPhrase: String,
        hindiMeaning: String,
        pronunciationHi: String,
        realLifeExampleEn: String,
        realLifeExampleHi: String,
        quizQuestionHi: String,
        quizOptions: List<String>,
        correctIndex: Int,
        xpReward: Int
    ) = SampleLessonPreview(
        titleEn = titleEn,
        titleHi = titleHi,
        explanationHi = explanationHi,
        englishPhrase = englishPhrase,
        hindiMeaning = hindiMeaning,
        pronunciationHi = pronunciationHi,
        realLifeExampleEn = realLifeExampleEn,
        realLifeExampleHi = realLifeExampleHi,
        quizQuestionHi = quizQuestionHi,
        quizOptions = quizOptions,
        correctOptionIndex = correctIndex,
        xpReward = xpReward
    )
}
