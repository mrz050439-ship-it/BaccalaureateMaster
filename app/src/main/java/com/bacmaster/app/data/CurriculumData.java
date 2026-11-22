package com.bacmaster.app.data;

import android.graphics.Color;

import com.bacmaster.app.model.Models.GradeYear;
import com.bacmaster.app.model.Models.Lesson;
import com.bacmaster.app.model.Models.Subject;
import com.bacmaster.app.model.Models.Track;
import com.bacmaster.app.model.Models.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * The Egyptian Baccalaureate structure, from the official ministerial decisions
 * (213 and 234 of 2025 and the August 2026 decision). Unit/lesson content is the
 * official book structure for the prep year (ministry books, 2026/2027) and the
 * official term-1 distributions for grade 2 Arabic and English; the remaining
 * grade 2 subjects carry a clearly-labeled provisional split until the detailed
 * official distribution is published.
 */
public class CurriculumData {

    public static final String PREP = "prep";
    public static final String G2 = "grade2";
    public static final String G3 = "grade3";

    public static List<Track> getTracks() {
        List<Track> tracks = new ArrayList<>();
        tracks.add(new Track("medicine", "الطب وعلوم الحياة", "💊", Color.parseColor("#0EA5E9")));
        tracks.add(new Track("engineering", "الهندسة وعلوم الحاسب", "⚙️", Color.parseColor("#6366F1")));
        tracks.add(new Track("business", "الأعمال", "💼", Color.parseColor("#F59E0B")));
        tracks.add(new Track("arts", "الآداب والفنون", "🎨", Color.parseColor("#EC4899")));
        return tracks;
    }

    public static Track getTrack(String id) {
        for (Track t : getTracks()) if (t.id.equals(id)) return t;
        return null;
    }

    public static List<GradeYear> getYears() {
        List<GradeYear> years = new ArrayList<>();
        years.add(new GradeYear(PREP, "الصف الأول الثانوي",
                "السنة التمهيدية — مواد عامة لكل الطلاب", true));
        years.add(new GradeYear(G2, "الصف الثاني الثانوي",
                "بداية مرحلة الشهادة — 400 درجة", true));
        years.add(new GradeYear(G3, "الصف الثالث الثانوي",
                "سنة الشهادة — 200 درجة — المنهج لم يصدر بعد", false));
        return years;
    }

    public static GradeYear getYear(String id) {
        for (GradeYear y : getYears()) if (y.id.equals(id)) return y;
        return null;
    }

    public static List<Subject> getSubjects(String trackId, String yearId) {
        List<Subject> list = new ArrayList<>();
        if (PREP.equals(yearId)) {
            list.add(new Subject("arabic", "اللغة العربية", Subject.SECTION_MAIN, true, "100 درجة"));
            list.add(new Subject("lang1", "اللغة الأجنبية الأولى", Subject.SECTION_MAIN, true, "100 درجة"));
            list.add(new Subject("history", "التاريخ", Subject.SECTION_MAIN, true, "100 درجة"));
            list.add(new Subject("math", "الرياضيات", Subject.SECTION_MAIN, true, "100 درجة"));
            list.add(new Subject("science", "العلوم المتكاملة", Subject.SECTION_MAIN, true, "100 درجة"));
            list.add(new Subject("philosophy", "الفلسفة والمنطق", Subject.SECTION_MAIN, true, "100 درجة"));
            list.add(new Subject("religion", "التربية الدينية", Subject.SECTION_NON_COUNTED, false, "نجاح ورسوب"));
            list.add(new Subject("lang2", "اللغة الأجنبية الثانية", Subject.SECTION_NON_COUNTED, false, "نجاح ورسوب"));
            list.add(new Subject("pe", "التربية الرياضية", Subject.SECTION_NON_COUNTED, false, "نجاح ورسوب"));
            list.add(new Subject("programming", "البرمجة والذكاء الاصطناعي", Subject.SECTION_NON_COUNTED, false, "نجاح ورسوب"));
        } else if (G2.equals(yearId)) {
            list.add(new Subject("arabic", "اللغة العربية", Subject.SECTION_MAIN, true, "100 درجة"));
            list.add(new Subject("lang1", "اللغة الأجنبية الأولى", Subject.SECTION_MAIN, true, "100 درجة"));
            list.add(new Subject("history", "التاريخ", Subject.SECTION_MAIN, true, "100 درجة"));
            if ("medicine".equals(trackId)) {
                list.add(new Subject("physics", "الفيزياء", Subject.SECTION_SPECIALIZATION, true, "مادة تخصص — 100 درجة"));
                list.add(new Subject("math", "الرياضيات", Subject.SECTION_SPECIALIZATION, true, "مادة تخصص — 100 درجة"));
            } else if ("engineering".equals(trackId)) {
                list.add(new Subject("chemistry", "الكيمياء", Subject.SECTION_SPECIALIZATION, true, "مادة تخصص — 100 درجة"));
                list.add(new Subject("programming", "البرمجة والذكاء الاصطناعي", Subject.SECTION_SPECIALIZATION, true, "مادة تخصص — 100 درجة"));
            } else if ("business".equals(trackId)) {
                list.add(new Subject("accounting", "المحاسبة", Subject.SECTION_SPECIALIZATION, true, "مادة تخصص — 100 درجة"));
                list.add(new Subject("management", "إدارة الأعمال", Subject.SECTION_SPECIALIZATION, true, "مادة تخصص — 100 درجة"));
            } else if ("arts".equals(trackId)) {
                list.add(new Subject("psychology", "علم النفس", Subject.SECTION_SPECIALIZATION, true, "مادة تخصص — 100 درجة"));
                list.add(new Subject("lang2", "اللغة الأجنبية الثانية", Subject.SECTION_SPECIALIZATION, true, "مادة تخصص — 100 درجة"));
            }
            list.add(new Subject("financial", "الثقافة المالية", Subject.SECTION_NON_COUNTED, false, "لا تضاف للمجموع"));
            list.add(new Subject("pe", "التربية الرياضية", Subject.SECTION_NON_COUNTED, false, "امتحان عملي"));
        } else if (G3.equals(yearId)) {
            if ("medicine".equals(trackId)) {
                list.add(new Subject("biology_adv", "الأحياء (مستوى متقدم)", Subject.SECTION_MAIN, true, "100 درجة"));
                list.add(new Subject("chemistry_adv", "الكيمياء (مستوى متقدم)", Subject.SECTION_MAIN, true, "100 درجة"));
            } else if ("engineering".equals(trackId)) {
                list.add(new Subject("math_adv", "الرياضيات (مستوى متقدم)", Subject.SECTION_MAIN, true, "100 درجة"));
                list.add(new Subject("physics_adv", "الفيزياء (مستوى متقدم)", Subject.SECTION_MAIN, true, "100 درجة"));
            } else if ("business".equals(trackId)) {
                list.add(new Subject("economics_adv", "الاقتصاد (مستوى متقدم)", Subject.SECTION_MAIN, true, "100 درجة"));
                list.add(new Subject("math", "الرياضيات", Subject.SECTION_MAIN, true, "100 درجة"));
            } else if ("arts".equals(trackId)) {
                list.add(new Subject("geography_adv", "الجغرافيا (مستوى متقدم)", Subject.SECTION_MAIN, true, "100 درجة"));
                list.add(new Subject("statistics", "الإحصاء", Subject.SECTION_MAIN, true, "100 درجة"));
            }
            list.add(new Subject("religion", "التربية الدينية", Subject.SECTION_NON_COUNTED, false, "نجاح ورسوب — 70% على الأقل"));
        }
        for (Subject s : list) decorate(s);
        return list;
    }

    /** Per-subject identity: emoji and brand color used across the app. */
    private static void decorate(Subject s) {
        if ("arabic".equals(s.id)) { s.emoji = "📕"; s.color = Color.parseColor("#DC2626"); }
        else if ("lang1".equals(s.id)) { s.emoji = "🔤"; s.color = Color.parseColor("#2563EB"); }
        else if ("lang2".equals(s.id)) { s.emoji = "🌐"; s.color = Color.parseColor("#7C3AED"); }
        else if ("history".equals(s.id)) { s.emoji = "🏛️"; s.color = Color.parseColor("#B45309"); }
        else if ("physics".equals(s.id)) { s.emoji = "⚛️"; s.color = Color.parseColor("#7C3AED"); }
        else if ("math".equals(s.id)) { s.emoji = "➗"; s.color = Color.parseColor("#0891B2"); }
        else if ("chemistry".equals(s.id)) { s.emoji = "⚗️"; s.color = Color.parseColor("#059669"); }
        else if ("programming".equals(s.id)) { s.emoji = "💻"; s.color = Color.parseColor("#4F46E5"); }
        else if ("accounting".equals(s.id)) { s.emoji = "📊"; s.color = Color.parseColor("#D97706"); }
        else if ("management".equals(s.id)) { s.emoji = "📁"; s.color = Color.parseColor("#C2410C"); }
        else if ("psychology".equals(s.id)) { s.emoji = "🤔"; s.color = Color.parseColor("#DB2777"); }
        else if ("religion".equals(s.id)) { s.emoji = "🕌"; s.color = Color.parseColor("#047857"); }
        else if ("pe".equals(s.id)) { s.emoji = "⚽"; s.color = Color.parseColor("#65A30D"); }
        else if ("financial".equals(s.id)) { s.emoji = "💰"; s.color = Color.parseColor("#CA8A04"); }
        else if ("science".equals(s.id)) { s.emoji = "🔬"; s.color = Color.parseColor("#0D9488"); }
        else if ("philosophy".equals(s.id)) { s.emoji = "💭"; s.color = Color.parseColor("#9333EA"); }
    }

    public static String sectionTitle(int section, String yearId) {
        if (section == Subject.SECTION_MAIN) {
            return G3.equals(yearId) ? "مواد التخصص (تضاف للمجموع)" : "المواد الأساسية (تضاف للمجموع)";
        }
        if (section == Subject.SECTION_SPECIALIZATION) {
            return "مادة التخصص — تختار مادة واحدة حسب مسارك";
        }
        return "مواد لا تضاف للمجموع";
    }

    /** Label shown in the course screen telling the student what this content is based on. */
    public static String sourceLabel(String subjectId, String yearId) {
        if (G2.equals(yearId) && ("arabic".equals(subjectId)
                || "history".equals(subjectId) || "physics".equals(subjectId)
                || "chemistry".equals(subjectId) || "psychology".equals(subjectId))) {
            return "الترم الأول كاملًا — محتوى كتاب الوزارة الرسمي 📖";
        }
        if (G2.equals(yearId) && ("lang1".equals(subjectId) || "lang2".equals(subjectId))) {
            return "الكتاب التفاعلي الرسمي للغات غير متاح للتضمين دون اتصال — يُضاف فور توفره رسميًا";
        }
        if (PREP.equals(yearId)) {
            return "هيكل كتاب الوزارة الرسمي — المحتوى يُضاف تباعًا";
        }
        return "تقسيم مبدئي — بانتظار التوزيع الرسمي التفصيلي";
    }

    public static List<Unit> getUnits(String subjectId, String yearId) {
        List<Unit> units = new ArrayList<>();
        if (PREP.equals(yearId)) {
            if ("arabic".equals(subjectId)) {
                units.add(unit(subjectId, 1, "الوحدة الأولى", 3));
                units.add(unit(subjectId, 2, "الوحدة الثانية", 3));
                units.add(unit(subjectId, 3, "الوحدة الثالثة", 3));
                units.add(specialUnit(subjectId, "قصة أبو الفوارس عنترة",
                        new String[]{"الفصل الأول", "الفصل الثاني", "الفصل الثالث"}));
            } else if ("lang1".equals(subjectId)) {
                for (int u = 1; u <= 6; u++) units.add(unit(subjectId, u, "الوحدة " + ordinal(u), 5));
            } else if ("math".equals(subjectId)) {
                int[] counts = {4, 4, 4, 5};
                for (int u = 1; u <= 4; u++) units.add(unit(subjectId, u, "الوحدة " + ordinal(u), counts[u - 1]));
            } else if ("science".equals(subjectId)) {
                units.add(unit(subjectId, 1, "الوحدة الأولى", 7));
                units.add(unit(subjectId, 2, "الوحدة الثانية", 7));
            } else if ("history".equals(subjectId)) {
                int[] counts = {4, 4, 4, 3};
                for (int u = 1; u <= 4; u++) units.add(unit(subjectId, u, "الوحدة " + ordinal(u), counts[u - 1]));
            } else if ("philosophy".equals(subjectId)) {
                units.add(unit(subjectId, 1, "الوحدة الأولى", 12));
                units.add(unit(subjectId, 2, "الوحدة الثانية", 11));
            } else if ("religion".equals(subjectId)) {
                for (int u = 1; u <= 6; u++) units.add(unit(subjectId, u, "الوحدة " + ordinal(u), 3));
            } else if ("lang2".equals(subjectId)) {
                units.add(unit(subjectId, 1, "الوحدة الأولى", 7));
                units.add(unit(subjectId, 2, "الوحدة الثانية", 6));
            } else if ("programming".equals(subjectId)) {
                int[] counts = {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 4, 4};
                for (int u = 1; u <= 13; u++) units.add(unit(subjectId, u, "الوحدة " + ordinal(u), counts[u - 1]));
            } else if ("religion".equals(subjectId)) {
                for (int u = 1; u <= 6; u++) units.add(unit(subjectId, u, "الوحدة " + ordinal(u), 3));
            } else {
                units.add(specialUnit(subjectId, "المحتوى العملي",
                        new String[]{"يُحدد حسب الجدول المدرسي"}));
            }
        } else if (G2.equals(yearId)) {
            if ("arabic".equals(subjectId)) {
                units.add(bookUnit(subjectId, 1, "الوحدة الأولى",
                        new String[]{"العلم بين القانون والضمير (نص معلوماتي)",
                                "الخلق جوهر الفضيلة — المنفلوطي (نص نثري)",
                                "العلم نور — الرصافي (نص شعري)"}));
                units.add(bookUnit(subjectId, 2, "الوحدة الثانية",
                        new String[]{"التقنية وصناعة الرأي العام (نص معلوماتي)",
                                "بين الذوق والعقل — أحمد أمين (نص نثري)",
                                "أثر الكلمة وحفظ اللسان — صالح بن عبد القدوس (نص شعري)"}));
                units.add(bookUnit(subjectId, 3, "الوحدة الثالثة",
                        new String[]{"حقوق الإنسان بين العالمية والخصوصية (نص معلوماتي)",
                                "الحرية الفكرية — العقاد (نص نثري)",
                                "يا أخي الإنسان — إيليا أبو ماضي (نص شعري)"}));
            } else if ("lang1".equals(subjectId)) {
                units.add(specialUnit(subjectId, "الوحدة الأولى — Unit 1",
                        new String[]{"A - Social Reflection",
                                "B - Shared Responsibility",
                                "القصة المقررة: Chapter 1"}));
                units.add(specialUnit(subjectId, "الوحدة الثانية — Unit 2",
                        new String[]{"A - Leisure in a Digital World",
                                "B - Lifelong Activity",
                                "القصة المقررة: Chapter 2"}));
                units.add(specialUnit(subjectId, "الوحدة الثالثة — Unit 3",
                        new String[]{"A - Historical Landscape",
                                "مراجعة عامة"}));
            } else if ("history".equals(subjectId)) {
                units.add(bookUnit(subjectId, 1, "الوحدة الأولى — مصر بين الماضي والحاضر",
                        new String[]{"بناء الدولة المصرية واستمرارها عبر التاريخ",
                                "الدولة المصرية القديمة ومقاومة الاحتلال",
                                "التحولات الكبرى في مصر (العصر الوسيط)",
                                "التحولات الكبرى في مصر (العصر الحديث)"}));
                units.add(bookUnit(subjectId, 2, "الوحدة الثانية — تحولات القوة وبناء الوعي بمصر في العصر الحديث والمعاصر",
                        new String[]{"سياسات الاحتلال بمصر في العصر الحديث",
                                "صمود الشعب المصري ومقاومة الاحتلال",
                                "ثورة 23 يوليو والتحولات الكبرى في مصر",
                                "الهوية الوطنية في مواجهة الاستعمار الجديد"}));
                units.add(bookUnit(subjectId, 3, "الوحدة الثالثة — مصر وقضايا التحرر الوطني في الوطن العربي وإفريقيا",
                        new String[]{"حركات التحرر الوطني ضد الاحتلال الفرنسي والإيطالي في العالم العربي",
                                "حركات التحرر الوطني ضد الاحتلال البريطاني والإسباني في العالم العربي",
                                "مرتكزات الدور المصري إقليميًا ودوليًا",
                                "دور مصر في دعم حركات التحرر الوطني"}));
            } else if ("physics".equals(subjectId)) {
                units.add(bookUnit(subjectId, 1, "الفصل الأول — الميكانيكا",
                        new String[]{"متجهات السرعة والسرعة النسبية",
                                "حركة المقذوفات الأفقية",
                                "حركة المقذوفات بزاوية",
                                "عزم القوة",
                                "اتزان القوى",
                                "القدرة والكفاءة",
                                "كمية التحرك والدفع",
                                "حفظ كمية الحركة",
                                "كمية التحرك والطاقة الميكانيكية",
                                "الحركة الدائرية المنتظمة والقوة المركزية",
                                "الحركة الدائرية: الأفقية والرأسية",
                                "قوانين كبلر والجاذبية الكونية"}));
                units.add(bookUnit(subjectId, 2, "الفصل الثاني — الحركة الاهتزازية والموجات",
                        new String[]{"الحركة التوافقية البسيطة",
                                "البندول الزنبركي",
                                "البندول البسيط",
                                "الموجات الجيبية",
                                "تداخل الموجات والموجات الموقوفة",
                                "انكسار الموجات",
                                "تداخل موجات الصوت",
                                "تأثير دوبلر",
                                "انكسار الضوء والانعكاس الكلي الداخلي",
                                "العدسات والصور",
                                "تجربة الشق المزدوج ليونج"}));
            } else if ("chemistry".equals(subjectId)) {
                units.add(bookUnit(subjectId, 1, "الفصل الأول — حالات المادة",
                        new String[]{"تغيرات الحالة",
                                "اتزان السائل والبخار وضغط البخار",
                                "مخطط الطور",
                                "قوانين الغازات",
                                "معادلة الغاز المثالي",
                                "الضغط الكلي والضغط الجزئي",
                                "الغاز المثالي والغاز الحقيقي",
                                "خصائص المحاليل",
                                "ارتفاع درجة الغليان وانخفاض درجة التجمد",
                                "الضغط الأسموزي",
                                "الغرويات (محاليل الغرويات)",
                                "بنية البلورات الفلزية",
                                "بنية البلورات الأيونية"}));
                units.add(specialUnit(subjectId, "الفصل الثاني — التغير والاتزان في المادة",
                        new String[]{"التفاعلات الكيميائية وتغيرات الإنثالبي",
                                "قانون هس",
                                "الخلية الكهروكيميائية (1) خلية دانيال",
                                "الخلية الكهروكيميائية (2) خلايا متنوعة",
                                "التفاعلات عند الأقطاب الكهربائية (1)",
                                "التحليل الكهربائي (2) كمية الكهرباء، خليتان تحليليتان",
                                "سرعة التفاعل",
                                "العوامل المؤثرة في سرعة التفاعل",
                                "الاتزان الكيميائي",
                                "مبدأ لوشاتيليه",
                                "الاتزان الأيوني",
                                "الأملاح والمحاليل المائية",
                                "حاصل الإذابة"}));
            } else if ("psychology".equals(subjectId)) {
                units.add(bookUnit(subjectId, 1, "الوحدة الأولى — علم النفس؛ مفتاح نفسك والآخرين",
                        new String[]{"من الفلسفة إلى المعمل: كيف نشأ علم النفس؟",
                                "أنت لست مجرد رد فعل: سر المثير والاستجابة",
                                "علم النفس في حياتك: أكثر مما تتخيل",
                                "كيف يفكر عالم النفس؟"}));
                units.add(bookUnit(subjectId, 2, "الوحدة الثانية — عقلي المذهل؛ كيف يعمل ولماذا يخطئ أحيانًا؟",
                        new String[]{"كيف يبدأ السلوك؟ (الأسس البيولوجية والمعرفية)",
                                "لماذا أرى الأشياء بطريقتي؟ (أسرار الانتباه والإدراك)",
                                "كيف نتعلم ونحتفظ بالمعلومات؟",
                                "كيف نفكر ونتخذ قراراتنا؟"}));
                units.add(bookUnit(subjectId, 3, "الوحدة الثالثة — أنا في طور التكوين: رحلة النمو والهوية",
                        new String[]{"من الطفولة إلى المراهقة: كيف أتغير؟",
                                "المراهقة وتكوين الهوية (من أنا؟)",
                                "الانفعالات وتنظيمها (كيف أفهم مشاعري؟)",
                                "مهارات الحياة والتكيف النفسي"}));
            } else {
                for (int u = 1; u <= 3; u++) {
                    units.add(unit(subjectId, u, "الوحدة " + ordinal(u) + " — الترم الأول", 3));
                }
            }
        }
        return units;
    }

    private static Unit unit(String subjectId, int number, String title, int lessonCount) {
        Unit unit = new Unit(subjectId + "_u" + number, title);
        for (int l = 1; l <= lessonCount; l++) {
            unit.lessons.add(new Lesson(subjectId + "_u" + number + "_l" + l,
                    "الدرس " + ordinalM(l), null));
        }
        return unit;
    }

    private static Unit specialUnit(String subjectId, String title, String[] lessonTitles) {
        Unit unit = new Unit(subjectId + "_" + Math.abs(title.hashCode()), title);
        int i = 1;
        for (String t : lessonTitles) {
            unit.lessons.add(new Lesson(unit.id + "_l" + i, t, null));
            i++;
        }
        return unit;
    }

    /** Unit whose lessons carry the full official ministry-book pages as bundled images. */
    private static Unit bookUnit(String subjectId, int number, String title, String[] lessonTitles) {
        Unit unit = new Unit(subjectId + "_u" + number, title);
        int i = 1;
        for (String t : lessonTitles) {
            unit.lessons.add(new Lesson(unit.id + "_l" + i, t,
                    "lessons/g2_" + subjectId + "/u" + number + "l" + i));
            i++;
        }
        return unit;
    }

    private static String ordinal(int n) {
        String[] fem = {"الأولى", "الثانية", "الثالثة", "الرابعة", "الخامسة", "السادسة",
                "السابعة", "الثامنة", "التاسعة", "العاشرة", "الحادية عشر", "الثانية عشر", "الثالثة عشر"};
        if (n >= 1 && n <= fem.length) return fem[n - 1];
        return String.valueOf(n);
    }

    private static String ordinalM(int n) {
        String[] masc = {"الأول", "الثاني", "الثالث", "الرابع", "الخامس", "السادس",
                "السابع", "الثامن", "التاسع", "العاشر", "الحادي عشر", "الثاني عشر"};
        if (n >= 1 && n <= masc.length) return masc[n - 1];
        return String.valueOf(n);
    }

    public static String courseKey(String yearId, String subjectId) {
        return yearId + "_" + subjectId;
    }
}
