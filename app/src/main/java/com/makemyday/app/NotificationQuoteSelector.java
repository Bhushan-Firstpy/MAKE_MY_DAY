package com.makemyday.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NotificationQuoteSelector {
    private static final String[] CATEGORIES = {
        "Motivation",
        "Confidence",
        "Productivity",
        "Study",
        "Career",
        "Business",
        "Health",
        "Relationships",
        "Self-discipline",
        "Peace and mindfulness",
        "Success",
        "Hope",
        "Personal growth"
    };

    private final List<NotificationQuote> quotes;

    public NotificationQuoteSelector() {
        this.quotes = buildDefaultCatalog();
    }

    public NotificationQuote selectQuote(NotificationPreferencesModel preferences) {
        return selectQuote(preferences, false);
    }

    public NotificationQuote selectQuote(NotificationPreferencesModel preferences, boolean allowTestMode) {
        if (preferences == null) {
            preferences = NotificationPreferencesModel.defaultPreferences();
        }

        List<String> selectedLanguages = new ArrayList<>(preferences.getSelectedLanguages());
        if (selectedLanguages.isEmpty()) {
            selectedLanguages.add("English");
            selectedLanguages.add("Hindi");
            selectedLanguages.add("Marathi");
        }

        Set<String> selectedCategories = new HashSet<>(preferences.getSelectedCategories());
        if (selectedCategories.isEmpty()) {
            selectedCategories.addAll(new HashSet<>(java.util.Arrays.asList(CATEGORIES)));
        }

        List<String> preferredCategories = new ArrayList<>();
        for (String periodLabel : preferences.getSelectedTimePeriods()) {
            preferredCategories.addAll(NotificationTimePeriod.defaultPreferredCategoriesForPeriod(periodLabel));
        }

        List<NotificationQuote> filtered = new ArrayList<>();
        for (NotificationQuote quote : quotes) {
            if (!quote.isNotificationEligible()) continue;
            if (!selectedLanguages.contains(quote.getLanguage())) continue;
            if (!selectedCategories.contains(quote.getCategory())) continue;
            filtered.add(quote);
        }

        if (filtered.isEmpty()) {
            for (NotificationQuote quote : quotes) {
                if (!quote.isNotificationEligible()) continue;
                if (!selectedLanguages.contains(quote.getLanguage())) continue;
                filtered.add(quote);
            }
        }

        if (filtered.isEmpty()) {
            filtered.addAll(quotes);
        }

        // Apply period-aware preference ordering.
        List<NotificationQuote> orderedQuotes = new ArrayList<>(filtered);
        if (!preferredCategories.isEmpty()) {
            Collections.sort(orderedQuotes, (left, right) -> {
                int leftPriority = preferredCategories.contains(left.getCategory()) ? 0 : 1;
                int rightPriority = preferredCategories.contains(right.getCategory()) ? 0 : 1;
                if (leftPriority != rightPriority) {
                    return leftPriority - rightPriority;
                }
                return 0;
            });
        }

        List<String> recentIds = preferences.getRecentlyDeliveredQuoteIds();
        List<NotificationQuote> freshCandidates = new ArrayList<>();
        for (NotificationQuote quote : orderedQuotes) {
            if (!recentIds.contains(quote.getId())) {
                freshCandidates.add(quote);
            }
        }

        if (freshCandidates.isEmpty()) {
            freshCandidates.addAll(orderedQuotes);
        }

        if (freshCandidates.isEmpty()) {
            NotificationQuote fallback = new NotificationQuote(
                UUID.randomUUID().toString(),
                "Take one useful step today.",
                "English",
                "Motivation",
                true,
                true,
                true
            );
            return fallback;
        }

        int randomIndex = (int) (Math.random() * freshCandidates.size());
        return freshCandidates.get(randomIndex);
    }

    private List<NotificationQuote> buildDefaultCatalog() {
        List<NotificationQuote> catalog = new ArrayList<>();
        catalog.add(new NotificationQuote("en-motivation-1", "Start before you feel completely ready. Progress often begins with one imperfect step.", "English", "Motivation", true, true, true));
        catalog.add(new NotificationQuote("en-confidence-1", "You do not need everyone's approval to take your next honest step.", "English", "Confidence", true, true, true));
        catalog.add(new NotificationQuote("en-productivity-1", "Choose one important task, finish it with attention, then move to the next.", "English", "Productivity", true, true, true));
        catalog.add(new NotificationQuote("en-career-1", "Build skills that remain useful even when circumstances change.", "English", "Career", true, true, true));
        catalog.add(new NotificationQuote("en-study-1", "A small, consistent study session is stronger than a dramatic burst of effort.", "English", "Study", true, true, true));
        catalog.add(new NotificationQuote("en-business-1", "Clear decisions compound faster than busy activity.", "English", "Business", true, true, true));
        catalog.add(new NotificationQuote("en-health-1", "Your next good habit is an act of care, not punishment.", "English", "Health", true, true, true));
        catalog.add(new NotificationQuote("en-relationships-1", "Kindness is often the quietest way to build trust.", "English", "Relationships", true, true, true));
        catalog.add(new NotificationQuote("en-self-discipline-1", "Make the small action so clear that you can do it even on an unmotivated day.", "English", "Self-discipline", true, true, true));
        catalog.add(new NotificationQuote("en-peace-1", "You do not need to solve everything to create a calmer day.", "English", "Peace and mindfulness", true, true, true));
        catalog.add(new NotificationQuote("en-success-1", "Success is usually built by steady actions that no one sees at first.", "English", "Success", true, true, true));
        catalog.add(new NotificationQuote("en-hope-1", "A difficult phase is a chapter, not the definition of your whole story.", "English", "Hope", true, true, true));
        catalog.add(new NotificationQuote("en-growth-1", "Compare yourself with who you were yesterday, then improve one thing today.", "English", "Personal growth", true, true, true));

        catalog.add(new NotificationQuote("hi-motivation-1", "शुरुआत छोटी हो सकती है, लेकिन लगातार उठाया गया कदम दिशा बदल सकता है।", "Hindi", "Motivation", true, true, true));
        catalog.add(new NotificationQuote("hi-confidence-1", "हर किसी की मंज़ूरी ज़रूरी नहीं है; अपने सही अगले कदम पर ध्यान दें।", "Hindi", "Confidence", true, true, true));
        catalog.add(new NotificationQuote("hi-productivity-1", "एक समय में एक जरूरी काम पूरे ध्यान से करें, फिर अगले काम पर जाएँ।", "Hindi", "Productivity", true, true, true));
        catalog.add(new NotificationQuote("hi-career-1", "ऐसी कौशलें बनाइए जो परिस्थितियाँ बदलने पर भी उपयोगी रहें।", "Hindi", "Career", true, true, true));
        catalog.add(new NotificationQuote("hi-study-1", "छोटा, नियमित अभ्यास बड़े लक्ष्यों को मजबूत बनाता है।", "Hindi", "Study", true, true, true));
        catalog.add(new NotificationQuote("hi-business-1", "स्पष्ट निर्णय व्यस्त रहने से अधिक तेज़ परिणाम देते हैं।", "Hindi", "Business", true, true, true));
        catalog.add(new NotificationQuote("hi-health-1", "अच्छी आदतें खुद पर दया करने का तरीका हैं, सज़ा नहीं।", "Hindi", "Health", true, true, true));
        catalog.add(new NotificationQuote("hi-relationships-1", "दया और सुनना रिश्ते बनाने का सबसे शांत तरीका है।", "Hindi", "Relationships", true, true, true));
        catalog.add(new NotificationQuote("hi-self-discipline-1", "छोटी सही आदत को मन न होने वाले दिन भी निभाना अनुशासन बनाता है।", "Hindi", "Self-discipline", true, true, true));
        catalog.add(new NotificationQuote("hi-peace-1", "सभी सवालों का जवाब पाना जरूरी नहीं है; शांति भी एक कदम है।", "Hindi", "Peace and mindfulness", true, true, true));
        catalog.add(new NotificationQuote("hi-success-1", "सफलता अक्सर उन छोटी, लगातार मेहनतों से बनती है जो शुरुआत में दिखती नहीं हैं।", "Hindi", "Success", true, true, true));
        catalog.add(new NotificationQuote("hi-hope-1", "मुश्किल समय आपकी पूरी कहानी नहीं है; यह कहानी का सिर्फ एक हिस्सा है।", "Hindi", "Hope", true, true, true));
        catalog.add(new NotificationQuote("hi-growth-1", "कल के आप से तुलना करें, और आज एक चीज़ थोड़ा बेहतर बनाइए।", "Hindi", "Personal growth", true, true, true));

        catalog.add(new NotificationQuote("mr-motivation-1", "सुरुवात परिपूर्ण असण्याची गरज नाही; एक छोटं पाऊलही पुढची दिशा ठरवू शकते.", "Marathi", "Motivation", true, true, true));
        catalog.add(new NotificationQuote("mr-confidence-1", "प्रत्येकाची मान्यता मिळवण्यापेक्षा स्वतःच्या योग्य निर्णयावर विश्वास ठेवा.", "Marathi", "Confidence", true, true, true));
        catalog.add(new NotificationQuote("mr-productivity-1", "एकावेळी एक महत्त्वाचं काम पूर्ण करा आणि मग पुढच्या कामाकडे वळा.", "Marathi", "Productivity", true, true, true));
        catalog.add(new NotificationQuote("mr-career-1", "परिस्थिती बदलली तरी उपयोगी पडतील अशी कौशल्ये सातत्याने विकसित करा.", "Marathi", "Career", true, true, true));
        catalog.add(new NotificationQuote("mr-study-1", "लहान, नियमित अभ्यास हा मोठ्या यशाचा आधार असतो.", "Marathi", "Study", true, true, true));
        catalog.add(new NotificationQuote("mr-business-1", "स्पष्ट निर्णय आणि नियमित काम यामुळे अधिक परिणाम मिळतात.", "Marathi", "Business", true, true, true));
        catalog.add(new NotificationQuote("mr-health-1", "तुमची पुढची चांगली सवय हा स्वतःवर प्रेम दाखवण्याचा मार्ग आहे.", "Marathi", "Health", true, true, true));
        catalog.add(new NotificationQuote("mr-relationships-1", "दया आणि आदर यामुळे नातेसंबंध अधिक मजबूत होतात.", "Marathi", "Relationships", true, true, true));
        catalog.add(new NotificationQuote("mr-self-discipline-1", "मन नसतानाही छोटी योग्य सवय पूर्ण करणे म्हणजे शिस्त मजबूत करणे.", "Marathi", "Self-discipline", true, true, true));
        catalog.add(new NotificationQuote("mr-peace-1", "सर्व समस्या एकाच वेळी सोडवणे आवश्यक नाही; शांतीही एक योग्य पाऊल आहे.", "Marathi", "Peace and mindfulness", true, true, true));
        catalog.add(new NotificationQuote("mr-success-1", "यश हे प्रामुख्याने सतत घेतलेल्या लहान, योग्य पावलांमुळे होते.", "Marathi", "Success", true, true, true));
        catalog.add(new NotificationQuote("mr-hope-1", "कठीण काळ हे संपूर्ण जीवन नाही; तो केवळ एक भाग आहे.", "Marathi", "Hope", true, true, true));
        catalog.add(new NotificationQuote("mr-growth-1", "कालच्या स्वतःशी तुलना करा आणि आज एक गोष्ट थोडी अधिक चांगली करा.", "Marathi", "Personal growth", true, true, true));

        catalog.add(new NotificationQuote("en-motivation-2", "One consistent step is worth more than a perfect plan you never begin.", "English", "Motivation", true, true, true));
        catalog.add(new NotificationQuote("en-confidence-2", "Confidence grows when you act before you feel fully ready.", "English", "Confidence", true, true, true));
        catalog.add(new NotificationQuote("en-productivity-2", "Your best work appears when you remove friction and begin simply.", "English", "Productivity", true, true, true));
        catalog.add(new NotificationQuote("en-career-2", "A career grows through trust, skill, and calm consistency.", "English", "Career", true, true, true));
        catalog.add(new NotificationQuote("en-health-2", "Your health is shaped by the small choices you repeat.", "English", "Health", true, true, true));
        catalog.add(new NotificationQuote("en-relationships-2", "You build stronger relationships by listening with patience.", "English", "Relationships", true, true, true));
        catalog.add(new NotificationQuote("en-growth-2", "Growth is not a dramatic leap; it is a series of disciplined steps.", "English", "Personal growth", true, true, true));
        catalog.add(new NotificationQuote("en-peace-2", "Rest is not laziness; it is part of good work.", "English", "Peace and mindfulness", true, true, true));
        catalog.add(new NotificationQuote("hi-motivation-2", "एक सही कदम भी सही दिशा की शुरुआत करता है।", "Hindi", "Motivation", true, true, true));
        catalog.add(new NotificationQuote("hi-confidence-2", "आत्मविश्वास तब बढ़ता है जब आप डर के बावजूद आगे बढ़ते हैं।", "Hindi", "Confidence", true, true, true));
        catalog.add(new NotificationQuote("hi-productivity-2", "सफलता का आधार सरल शुरुआत और लगातार काम है।", "Hindi", "Productivity", true, true, true));
        catalog.add(new NotificationQuote("mr-motivation-2", "लहान पाऊलही पुढील यशाची सुरुवात असते.", "Marathi", "Motivation", true, true, true));
        catalog.add(new NotificationQuote("mr-confidence-2", "तुमचा आत्मविश्वास तुमच्या कृतीतून बनतो.", "Marathi", "Confidence", true, true, true));
        catalog.add(new NotificationQuote("mr-growth-2", "विकास हा एक झटका नाही; तो सतत घेतलेले छोटे निर्णय आहेत.", "Marathi", "Personal growth", true, true, true));

        return catalog;
    }
}
