package uz.alien.askrosvord

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.alien.askrosvord.ui.theme.AxborotSavodxonligiKrosvordTheme
import uz.alien.crossword.Crossword

class MainActivity : ComponentActivity() {

    val src = """Absolyut|Formuladagi manzil o‘zgarmaydigan katak manzili
Abstraksiya|Keraksiz qismlarni olib tashlab soddalashtirish
Airbrush|Bo‘yoq sepuvchi grafika vositasi
Ajratish|Matn yoki fonni rang bilan ajratib ko‘rsatish
Aktuator|Mexanik harakatni boshqaruvchi qurilma
Albom|Varaqning gorizontal joylashuvi
Algoritm|Buyruqlar ketma-ketligi
Analog|Raqamli bo‘lmagan signal turi
Animatsiya|Harakatlanuvchi tasvirlar ketma-ketligi
Aniqlash|Obyekt yoki shaxsni tanib olish jarayoni
Ankor|Gipermurojaat uchun ishlatiluvchi matn elementi
ANPR|Avtomobil raqamini avtomatik aniqlash tizimi
Antivirus|Zararli dasturlarni topib tozalovchi dastur
Hardware|Kompyuterning jismoniy qismlari
Applet|Boshqa dastur ichida bajariladigan kichik dastur
Apps|Dasturlar atamasining qisqartmasi
Aspekt|Tasvir eni va bo‘yining nisbati
Atribut|Elementning o‘ziga xos xususiyati
Audio|Tovush ma’lumotiga oid
Audiokarta|Tovushni eshitish qurilmasiga uzatuvchi karta
Auditoriya|Kontentni o‘quvchi yoki tomosha qiluvchi shaxslar
Autosum|Jadvalda yig‘indini avtomatik hisoblash funksiyasi
Average|Tanlangan qiymatlarning o‘rtachasini hisoblovchi funksiyasi
Axborot|Qiymatga ega bo‘lgan ma’lumot
Ayirish|Bir qiymatdan boshqasini olib tashlash
Aylantirish|Tasvirni burish yoki dumalatish
Baholash|Ijobiy va salbiy jihatlarni aniqlash
Bajarish|Dastur yoki buyruqni ishga tushirish
Belgi|Harf, raqam yoki tinish belgisi
Belgilar|Matn yoki tasvirning ajratilgan qismi
Beta|Dastur yakuniy versiyasidan oldingi sinov bosqichi
Bevosita|Foydalanish maqsadiga ko‘ra to‘plangan ma’lumotlar majmuasi
Bibliografiya|Foydalanilgan manbalar ro‘yxati
Bilvosita|Turli maqsadlar uchun yig‘ilgan ikkilamchi ma’lumotlar manbai
Binar|Faqat 0 va 1 raqamlardan tashkil topgan kod tizimi
Biometrik|Insonning o‘ziga xos jismoniy belgilariga asoslangan aniqlash texnologiyasi
Biometriya|Barmoq izi, ko‘z, ovoz yoki yuz kabi biometrik belgilarni tahlil qilish texnologiyasi
Bitcoin|Kriptovalyuta turi
Blog|Shaxsiy fikr, voqea va media joylashtiriladigan veb jurnal
Blockchain|Markazlashmagan, o‘zgartirib bo‘lmaydigan ma’lumotlar almashish tizimi
Bloksxema|Jarayon bosqichlarini grafik tarzda ko‘rsatuvchi sxema
Bluetooth|Qisqa masofali simsiz aloqa standarti
BMP|Rasm faylining Windows tizimidagi keng tarqalgan formati
Bogliqlik|Ma’lumotlar bazasidagi jadvallar orasidagi aloqalar
Bo'lish|Qiymatni teng qismlarga ajratish amali
Bookmark|Veb sahifaga tez kirish uchun saqlanadigan yorliq
Boolean|Faqat 0 yoki 1 qiymatni qabul qiluvchi mantiqiy tur
Botnet|Virusli dasturlar asosida yaratilgan kompyuter tarmog‘i
Brauzer|Internetga kirish va undan foydalanish dasturi
Brush|Rasm yoki dizayn chizish uchun mo‘ljallangan chotka vositasi
Cloud|Internet orqali ishlaydigan serverlar va dasturlar majmuasi
Buyruq|Dastur bajarishi kerak bo‘lgan amaliy topshiriq
CAD|Kompyuter yordamida loyihalash tizimi
CAM|Kompyuter yordamida ishlab chiqarish tizimi
CAL|Kompyuter yordamida o‘qitish tizimi
CapsLock|Klaviaturadagi katta harf rejimini yoqish tugmasi
CC|Elektron xatning nusxasi yuboriladigan manzil
CDROM|Faqat o‘qish uchun mo‘ljallangan kompakt disk xotirasi
Center|Matnni yoki sonni o‘rtaga joylashtirish buyrug‘i
CGI|Kompyuter yordamida yaratilgan tasvirlar texnologiyasi
CLI|Buyruqlar qatori orqali kompyuter bilan ishlash usuli
Clipboard|Kompyuterning vaqtinchalik xotirasi
CMS|Veb-sayt kontentini boshqarish tizimi
Compose|Yangi elektron xat yoki kontent yaratish buyrug‘i
Cookies|Veb-saytga kirganda saqlanadigan kichik fayllar
Crop|Tasvirning bir qismini olib tashlash amali
CSS|Veb-sahifa dizayni va ko‘rinishini boshqarish tili
CSV|Matn formatida saqlangan jadval fayli
Cut|Belgilanganni kesib olish va vaqtincha xotirada saqlash
Daromad|Pul tushumi yoki foyda miqdori
Dastur|Muayyan maqsad uchun ishlab chiqilgan dastur
Dasturlash|Kod yozish va dastur yaratish jarayoni
Debug|Dasturdagi xatolarni topish va tuzatish jarayoni
Deklaratsiya|CSS da stil parametrlarini belgilash qoidasi
Dekompozitsiya|Masalani kichik qismlarga bo‘lish jarayoni
Demografiya|Aholi soni va tarkibini o‘rganadigan fan
Deskilling|Malakali ish o‘rnini soddaroq texnologiyalar egallashi jarayoni
DFD|Ma’lumotlar oqimi diagrammasi
Diagnoz|Belgilar orqali muammo yoki kasallikni aniqlash
Diagramma|Ma’lumotlarni grafik tarzda ifodalash
Diapazon|Elektron jadvaldagi tutash kataklar guruhi
Displey|Ma’lumot yoki tasvirni ko‘rsatuvchi qurilma
RSI|Uzoq yozish natijasida paydo bo‘ladigan mushak shikastlanishi
Domen|Internetdagi kompyuter yoki serverni aniqlash uchun berilgan nom
DPI|Bir dyuymdagi piksel soni
Draft|Xat yoki hujjatning qora nusxasi
Dron|Uchuvchisiz uchuvchi qurilma
Dupleks|Printerda ikki tomonlama chop etish
Duplikatsiya|Elementning aynan nusxasini yaratish
Effektlar|Slayd o‘tishlari va animatsiyalar yig‘indisi
EFT|Elektron pul o‘tkazmalari tizimi
Ekran|Kompyuterning tasvir ko‘rsatuvchi qismi
Eksport|Ma’lumotlarni boshqa dasturga ko‘chirish
Elektronjadval|Qator va ustunlardan iborat jadval ko‘rinishidagi dastur
Elektronpochta|Elektron xatlarni qabul qilish va javob qaytarish
Elektrontijorat|Elektron shaklda olib boriladigan tijorat
Element|Maydonda saqlanadigan ma’lumot elementi
Empatiya|Boshqalarning ehtiyoj va his-tuyg‘ularini tushunish qobiliyati
Enter|Kursori keyingi qatorga tushiruvchi tugma
EQUALS|Mantiqiy funksiyani ifodalovchi (=)
Ergonomika|Qurilma yoki dastur qulayligi va samaradorligi
Ethernet|Simli tarmoq va LAN signallar standarti
Etiket|Xushmuomala va yaxshi tarbiyani aks ettiruvchi qoidalar
Evristik|Bilimlarni tajriba orqali o‘rganish
Expert|Ma’lum mavzuga oid faktlarni saqlovchi tizim
Fakenews|E’tiborni jalb qilish yoki adashtirish maqsadida tuzilgan noto‘g‘ri xabar
FAQ|Ko‘p beriladigan savollar
Firewall|Tarmoq orqali ma’lumot uzatishni nazorat qiluvchi dastur yoki apparat
Fayl|Ma’lumot saqlovchi tizim turi
Feedback|Kiritilgan ma’lumotni o‘zgartirish va natija olish, fikr-mulohaza
Fill|Shakl ichini bir xil rangga bo‘yash
Filtrlar|Rangga bog‘liq va klipda qo‘llanadigan effekt
Filtrlash|Keraksiz ma’lumotni o‘chirish yoki keraklini topish
Flip|Tasvirni gorizontal yoki vertikal o‘zgartirish
Fokus|Videoda yoki suratda tiniq qism
Footer|Hujjatning quyi qismidagi matn, belgi yoki grafik element
Format|Matn yoki kontent ko‘rinishini o‘zgartirish
Formatlash|Saqlash vositasini ma’lumotni qabul qilishga tayyorlash
Formula|Elektron jadvaldagi matematik hisob-kitob usuli
Forward|Elektron xatni keyin bir yoki bir nechta insonga yuborish
Fotorezistor|Yorug‘lik bilan boshqariladigan rezistor
Foyda|Faoliyat natijasida qolgan musbat qiymat
FPS|Animatsiyadagi har soniyada paydo bo‘ladigan kadrlar soni
Funksiya|Elektron jadvalda formulalar yaratishni osonlashtiruvchi kalit so‘zlar
Footage|Yozuv tugmasi bosilgandan stop tugmasi bosilguncha yozilgan video qismi
GIS|Yer yuzasiga oid ma’lumotlarni yig‘ish, saqlash va xarita ko‘rinishida ko‘rsatish tizimi
GPS|Joriy joylashuvni aniqlash uchun sun’iy yo‘ldoshlardan foydalanuvchi global pozitsiyalash tizimi
Grafika|Kompyuter yordamida yaratilgan tasvirlar va ularni boshqarish
GUI|Sichqoncha yoki trekpaddan rangli ekrandagi ikonkalarga bosish orqali kompyuter bilan aloqa qilish usuli
Hamkorlik|Birgalikda ishlash
Hamyon|Foydalanuvchining to‘lov vositalarini saqlovchi virtual hisob raqami
Harassment|Tajovuzkorlik, zug‘um yoki bosim o‘tkazish, qo‘rqitish
HCI|Inson-kompyuter interfeysi
Header|Hujjatning yuqori qismidagi maydon; barcha sahifalarda takrorlanadigan matn, belgi, bet raqami, grafik element va h.k.
Hisoblash|Natijani matematik ishlab chiqarish usuli
Hodisa|Dasturlashda foydalanuvchi harakati yoki boshqa sabab natijasida sodir bo‘lgan harakatlar
Host|Elektron pochta saqlanadigan server; kompyuter tarmog‘iga ulangan, ma’lum IP-manzilga ega kompyuter
HTML|Veb sahifa yaratish uchun ishlatiladigan til
HTTP|Gipermatn uzatish protokoli
HTTPS|Gipermatnni xavfsiz uzatish protokoli
Hub|Kompyuterlarni bir-biriga bog‘laydigan komponent; qabul qilingan ma’lumotlarni barcha ulangan qurilmalarga yuboradi
ICS|Bitta ulanish va IP-manzildan foydalanib LAN tarmog‘idagi bir nechta kompyuterni internetga ulash usuli
Identifikator|O‘zgaruvchi yoki funksiyaga berilgan nom
Ikonka|Tasvir, so‘z yoki tasvir va so‘zdаn iborat belgi; ma’lum vazifaga mo‘ljallangan tugma yoki belgi
Import|Bir dasturda yaratilgan hujjatdan boshqa dasturda foydalanish; ma’lumotlarni boshqa dasturga ko‘chirish
Inbox|Elektron pochtadagi kiruvchi xabarlar papkasi
Includes|Ma’lumotlar bazasining query operatori bo‘lib, batafsil ma’lumot elementlari orqali qidirishni amalga oshirishda foydalaniladi
Indeks|Odatda alifbo tartibida joylashadigan kalit so‘z yoki mavzular ro‘yxati; qidirish tizimida avval qidirilgan kalit so‘zlar va veb sahifalar ro‘yxati
Influencer|Ijtimoiy tarmoqlar orqali boshqalar fikriga ta’sir etuvchi shaxs
Infraqizil|Infrared; obyektlardan chiquvchi va odatiy holatda inson ko‘ziga ko‘rinmaydigan yorug‘lik to‘lqini
Infratuzilma|Xizmat ko‘rsatish yoki ishlab chiqarish uchun zarur bo‘lgan jismoniy tuzilmalar
Interaktiv|Foydalanuvchiga ma’lumot kiritish va chiqarish imkonini beruvchi dastur; foydalanuvchi tomonidan amalga oshirilgan harakatlarga audio yoki vizual javob beruvchi dastur yoki qurilma
Interferensiya|Yuborilayotgan signalning uzilishi yoki to‘xtatilishi, natijada ikkilik qiymatni noto‘g‘ri signal orqali o‘zgartirib yuborishi mumkin
Interfeys|Dastur oynasining ikonka va linklar aks ettirilgan ko‘rinishi; qurilmadan foydalanish tugmalari joylashgan old panel; dastur ma’lumotlarni kiritish va natijani chiqarishga mo‘ljallangan qismi; kompyuter ekranidagi matn va tasvirlar
Internet|Dunyodagi barcha o‘zaro ulangan kompyuterlar tarmog‘i; axborot saqlash va almashish uchun foydalaniladigan kompyuter tarmoqlarining birlashgan tizimi
Interpreter|Murakkab dasturlash tilini mashina kodiga aylantirish, bunda bir martada bitta satr o‘giriladi
Intranet|Internet kabi ishlovchi shaxsiy WAN; ma’lumotlar, kontent va tarmoqqa kirish boshqariladi
IPS|In-Panel Switching; ko‘rish burchaklarini kengroq va ranglarni boyroq ko‘rsatuvchi ekran texnologiyasi
ISBN|International Standard Book Number; kitobning xalqaro standartdagi 13 raqamdan iborat identifikatori
Run|Ma’lum dastur yoki jarayonni boshlash""".trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setClearEdge()

        setContent {
            AxborotSavodxonligiKrosvordTheme {
                var puzzleOutput by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    puzzleOutput = withContext(Dispatchers.IO) {
                        try {
                            val result = Crossword.generateCrossword(13, 25, src)
                            Log.d("CrosswordGen", "Generated successfully")
                            Log.d("CrosswordGen", result)
                            result
                        } catch (e: Exception) {
                            Log.e("CrosswordGen", "Error: ${e.message}", e)
                            null
                        }
                    }
                }

                when {
                    puzzleOutput != null -> {
                        CrosswordApp(consoleOutput = puzzleOutput!!)
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Text("Krossvord tayyorlanmoqda...")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun ComponentActivity.isNight(): Boolean {
    return (
            resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            ) == Configuration.UI_MODE_NIGHT_YES
}

fun ComponentActivity.setClearEdge() {
    if (isNight()) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            )
        )
    } else {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                darkScrim = Color.TRANSPARENT,
                scrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                darkScrim = Color.TRANSPARENT,
                scrim = Color.TRANSPARENT
            )
        )
    }
}