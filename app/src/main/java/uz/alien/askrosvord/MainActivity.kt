package uz.alien.askrosvord

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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

    val src = """Animatsiya|Tasvirlarni ketma-ket ko'rsatish
Antivirus|Viruslarni zararsizlantiruvchi
Motherboard|Barcha qurilmalarni bog'lovchi
AVERAGE|O'rtacha qiymatni hisoblash (Excelda)
Bcc|Ko'rinmas nusxa manzili
Belgi|Harf, raqam yoki simvol
Bluetooth|Qisqa masofali simsiz aloqa
BMP|Windows rasm formati (paint)
Bookmark|Sevimli sahifani saqlash
Brauzer|Internet sahifalarini ko'rsatuvchi dastur
Cloud|Internetdagi masofaviy serverlar
Center|Matnni o'rtaga joylashtirish
Clipboard|Vaqtincha xotiraga nusxa olish
Crop|Tasvirning keraksiz qismini kesish
Cut|Matnni kesib bufer xotiraga olish
Debug|Dasturdagi xatolarni tuzatish
Diapazon|Bir-biriga tutash kataklar guruhi
Display|Tasvir va matnni ko'rsatuvchi ekran
Domen|IP manzilga qulay nom beruvchi yorliq
Ethernet|Simli tarmoq standarti
FAQ|Ko'p beriladigan savollar
Firewall|Tarmoqni himoya qiluvchi dastur
Fayl|Saqlangan ma'lumotlar to'plami
Filtrlash|Kerakli ma'lumotni tanlash
Flip|Tasvirni akslantirish
Footer|Sahifa pastki qismidagi matn
Format|Matn ko'rinishi yoki disk tayyorlash
Formula|Matematik hisoblash ko'rsatmasi
GIS|Geografik axborot tizimi
GUI|Foydalanuvchi grafik interfeysi
Header|Sahifa yuqori qismidagi matn
HTML|Veb sahifa yaratish tili
HTTP|Gipermatn uzatish protokoli
HTTPS|Xavfsiz gipermatn protokoli
Ikonka|Vazifani ifodalovchi belgi
Ilova|Xatga qo'shilgan hujjat
Import|Boshqa dasturdan ko'chirish
Inbox|Kiruvchi xatlar papkasi
Interfeys|Dastur yoki qurilma tashqi ko'rinishi
Internet|Global kompyuter tarmoqlari
Interpretator|Kodni satrmasatr o'qish
Run|Dasturni ishga tushirish
Keyboard|Matn kiritish qurilmasi
Monitor|Tasvirni ko'rsatuvchi ekran
Mouse|Ko'rsatgichni boshqarish qurilmasi
Network|Birbiriga ulangan kompyuterlar
Password|Maxfiy kirish kodi
Printer|Hujjatni qog'ozga chiqaruvchi
Program|Ko'rsatmalar ketmaketligi
RAM|Tezkor xotira
ROM|Faqat o'qish uchun xotira
Scanner|Rasm yoki matnni raqamlashtirish
Server|Ma'lumot saqlovchi markaziy kompyuter
Software|Dasturiy ta'minot
Spreadsheet|Jadval shaklidagi dastur
USB|Universal seriyali ulagich
Virus|Zararli dastur
WiFi|Simsiz internet aloqasi
Windows|Microsoft operatsion tizimi""".trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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