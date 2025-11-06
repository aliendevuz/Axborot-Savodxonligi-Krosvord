//#include <jni.h>
//#include <string>
//#include <vector>
//#include <algorithm>
//#include <random>
//#include <sstream>
//#include <tuple>
//#include <ctime>
//
//using namespace std;
//
//// === Word struktura ===
//struct Word {
//    string text;
//    string clue;
//    int x, y;
//    bool horizontal;
//    int number;
//};
//
//// === CrosswordGenerator class (tozalangan, faqat kerakli qismlar) ===
//class CrosswordGenerator {
//private:
//    vector<vector<char>> grid;
//    vector<Word> placedWords;
//    vector<pair<string, string>> wordBank;
//    int gridSize;
//    mt19937 rng;
//
//    void trim(string& s) {
//        s.erase(0, s.find_first_not_of(" \t\r\n"));
//        s.erase(s.find_last_not_of(" \t\r\n") + 1);
//    }
//
//    bool canPlaceWord(const string& word, int x, int y, bool horizontal) {
//        if (horizontal) {
//            if (y + (int)word.length() > gridSize) return false;
//            for (int i = 0; i < (int)word.length(); i++) {
//                char c = grid[x][y + i];
//                if (c != '#' && c != word[i]) return false;
//            }
//            if (y > 0 && grid[x][y - 1] != '#') return false;
//            if (y + (int)word.length() < gridSize && grid[x][y + (int)word.length()] != '#') return false;
//        } else {
//            if (x + (int)word.length() > gridSize) return false;
//            for (int i = 0; i < (int)word.length(); i++) {
//                char c = grid[x + i][y];
//                if (c != '#' && c != word[i]) return false;
//            }
//            if (x > 0 && grid[x - 1][y] != '#') return false;
//            if (x + (int)word.length() < gridSize && grid[x + (int)word.length()][y] != '#') return false;
//        }
//        return true;
//    }
//
//    void placeWord(const Word& word) {
//        for (int i = 0; i < (int)word.text.length(); i++) {
//            if (word.horizontal) {
//                grid[word.x][word.y + i] = word.text[i];
//            } else {
//                grid[word.x + i][word.y] = word.text[i];
//            }
//        }
//        placedWords.push_back(word);
//    }
//
//    vector<tuple<int, int, bool, int>> findIntersections(const string& word) {
//        vector<tuple<int, int, bool, int>> positions;
//        for (const auto& placed : placedWords) {
//            for (int i = 0; i < (int)word.length(); i++) {
//                for (int j = 0; j < (int)placed.text.length(); j++) {
//                    if (word[i] == placed.text[j]) {
//                        if (placed.horizontal) {
//                            int x = placed.x - i;
//                            int y = placed.y + j;
//                            if (x >= 0 && x + (int)word.length() <= gridSize) {
//                                positions.push_back({x, y, false, i});
//                            }
//                        } else {
//                            int x = placed.x + j;
//                            int y = placed.y - i;
//                            if (y >= 0 && y + (int)word.length() <= gridSize) {
//                                positions.push_back({x, y, true, i});
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return positions;
//    }
//
//public:
//    CrosswordGenerator(int size) : gridSize(size), rng(time(0)) {
//        grid.assign(gridSize, vector<char>(gridSize, '#'));
//    }
//
//    void loadWordsFromString(const string& data) {
//        wordBank.clear();
//        stringstream ss(data);
//        string line;
//        while (getline(ss, line)) {
//            size_t pos = line.find('|');
//            if (pos == string::npos) continue;
//            string word = line.substr(0, pos);
//            string clue = line.substr(pos + 1);
//            trim(word); trim(clue);
//            transform(word.begin(), word.end(), word.begin(), ::toupper);
//            if (!word.empty() && word.length() >= 3) {
//                wordBank.push_back({word, clue});
//            }
//        }
//        // Uzunlik bo‘yicha saralash → zichroq grid
//        sort(wordBank.begin(), wordBank.end(), [](const auto& a, const auto& b) {
//            return a.first.length() > b.first.length();
//        });
//        shuffle(wordBank.begin(), wordBank.end(), rng);
//    }
//
//    bool generate(int maxWords) {
//        if (wordBank.empty()) return false;
//
//        // Birinchi so‘z o‘rtada
//        Word first;
//        first.text = wordBank[0].first;
//        first.clue = wordBank[0].second;
//        first.x = gridSize / 2;
//        first.y = (gridSize - (int)first.text.length()) / 2;
//        first.horizontal = true;
//        first.number = 1;
//        placeWord(first);
//
//        int wordNum = 2;
//        int attempts = 0;
//        const int maxAttempts = (int)wordBank.size() * 15;
//
//        for (size_t i = 1; i < wordBank.size() && (int)placedWords.size() < maxWords && attempts < maxAttempts; ++i) {
//            attempts++;
//            string word = wordBank[i].first;
//            string clue = wordBank[i].second;
//
//            auto positions = findIntersections(word);
//            shuffle(positions.begin(), positions.end(), rng);
//
//            for (const auto& [x, y, horiz, _] : positions) {
//                if (canPlaceWord(word, x, y, horiz)) {
//                    Word w{word, clue, x, y, horiz, wordNum++};
//                    placeWord(w);
//                    break;
//                }
//            }
//        }
//        return placedWords.size() >= 10; // Minimal muvaffaqiyat
//    }
//
//    string getResult() {
//        // Grid chegaralarini topish
//        int minX = gridSize, maxX = 0, minY = gridSize, maxY = 0;
//        for (int i = 0; i < gridSize; i++) {
//            for (int j = 0; j < gridSize; j++) {
//                if (grid[i][j] != '#') {
//                    minX = min(minX, i); maxX = max(maxX, i);
//                    minY = min(minY, j); maxY = max(maxY, j);
//                }
//            }
//        }
//
//        stringstream result;
//
//        // === GRID ===
//        result << "=== CROSSWORD ===\n";
//        for (int i = minX; i <= maxX; i++) {
//            for (int j = minY; j <= maxY; j++) {
//                char c = grid[i][j];
//                result << (c == '#' ? '#' : c);
//            }
//            result << '\n';
//        }
//
//        // === CLUES ===
//        vector<Word> across, down;
//        for (const auto& w : placedWords) {
//            (w.horizontal ? across : down).push_back(w);
//        }
//        sort(across.begin(), across.end(), [](const Word& a, const Word& b) { return a.number < b.number; });
//        sort(down.begin(), down.end(), [](const Word& a, const Word& b) { return a.number < b.number; });
//
//        result << "\n=== GORIZONTAL ===\n";
//        for (const auto& w : across) {
//            result << w.number << ". " << w.clue << " (" << w.text.length() << ")\n";
//        }
//
//        result << "\n=== VERTIKAL ===\n";
//        for (const auto& w : down) {
//            result << w.number << ". " << w.clue << " (" << w.text.length() << ")\n";
//        }
//
//        return result.str();
//    }
//};
//
//// === JNI FUNKSION ===
//
//extern "C" JNIEXPORT jstring JNICALL
//Java_uz_alien_crossword_Crossword_generateCrossword(
//        JNIEnv* env,
//        jobject /* this */,
//        jint gridSize,
//        jint maxWords,
//        jstring jWordList) {
//
//    const char* cWordList = env->GetStringUTFChars(jWordList, nullptr);
//    if (!cWordList) return env->NewStringUTF("Error: wordList null");
//
//    string wordListStr(cWordList);
//    env->ReleaseStringUTFChars(jWordList, cWordList);
//
//    CrosswordGenerator generator(gridSize);
//    generator.loadWordsFromString(wordListStr);
//
//    if (!generator.generate(maxWords)) {
//        return env->NewStringUTF("Error: Crossword generatsiya qilinmadi");
//    }
//
//    string result = generator.getResult();
//    return env->NewStringUTF(result.c_str());
//}

#include <jni.h>
#include <string>
#include <vector>
#include <algorithm>
#include <random>
#include <sstream>
#include <tuple>
#include <ctime>

using namespace std;

// === Word struktura ===
struct Word {
    string text;
    string clue;
    int x, y;           // original grid koordinatalari
    bool horizontal;
    int number;
};

// === CrosswordGenerator class (faqat natija qismini o'zgartirish) ===
class CrosswordGenerator {
private:
    vector<vector<char>> grid;
    vector<Word> placedWords;
    vector<pair<string, string>> wordBank;
    int gridSize;
    mt19937 rng;

    void trim(string& s) {
        s.erase(0, s.find_first_not_of(" \t\r\n"));
        s.erase(s.find_last_not_of(" \t\r\n") + 1);
    }

    bool canPlaceWord(const string& word, int x, int y, bool horizontal) {
        if (horizontal) {
            if (y + (int)word.length() > gridSize) return false;
            for (int i = 0; i < (int)word.length(); i++) {
                char c = grid[x][y + i];
                if (c != '#' && c != word[i]) return false;
            }
            if (y > 0 && grid[x][y - 1] != '#') return false;
            if (y + (int)word.length() < gridSize && grid[x][y + (int)word.length()] != '#') return false;
        } else {
            if (x + (int)word.length() > gridSize) return false;
            for (int i = 0; i < (int)word.length(); i++) {
                char c = grid[x + i][y];
                if (c != '#' && c != word[i]) return false;
            }
            if (x > 0 && grid[x - 1][y] != '#') return false;
            if (x + (int)word.length() < gridSize && grid[x + (int)word.length()][y] != '#') return false;
        }
        return true;
    }

    void placeWord(const Word& word) {
        for (int i = 0; i < (int)word.text.length(); i++) {
            if (word.horizontal) {
                grid[word.x][word.y + i] = word.text[i];
            } else {
                grid[word.x + i][word.y] = word.text[i];
            }
        }
        placedWords.push_back(word);
    }

    vector<tuple<int, int, bool, int>> findIntersections(const string& word) {
        vector<tuple<int, int, bool, int>> positions;
        for (const auto& placed : placedWords) {
            for (int i = 0; i < (int)word.length(); i++) {
                for (int j = 0; j < (int)placed.text.length(); j++) {
                    if (word[i] == placed.text[j]) {
                        if (placed.horizontal) {
                            int x = placed.x - i;
                            int y = placed.y + j;
                            if (x >= 0 && x + (int)word.length() <= gridSize) {
                                positions.push_back({x, y, false, i});
                            }
                        } else {
                            int x = placed.x + j;
                            int y = placed.y - i;
                            if (y >= 0 && y + (int)word.length() <= gridSize) {
                                positions.push_back({x, y, true, i});
                            }
                        }
                    }
                }
            }
        }
        return positions;
    }

public:
    CrosswordGenerator(int size) : gridSize(size), rng(time(0)) {
        grid.assign(gridSize, vector<char>(gridSize, '#'));
    }

    void loadWordsFromString(const string& data) {
        wordBank.clear();
        stringstream ss(data);
        string line;
        while (getline(ss, line)) {
            size_t pos = line.find('|');
            if (pos == string::npos) continue;
            string word = line.substr(0, pos);
            string clue = line.substr(pos + 1);
            trim(word); trim(clue);
            transform(word.begin(), word.end(), word.begin(), ::toupper);
            if (!word.empty() && word.length() >= 3) {
                wordBank.push_back({word, clue});
            }
        }
        sort(wordBank.begin(), wordBank.end(), [](const auto& a, const auto& b) {
            return a.first.length() > b.first.length();
        });
        shuffle(wordBank.begin(), wordBank.end(), rng);
    }

    bool generate(int maxWords) {
        if (wordBank.empty()) return false;

        Word first;
        first.text = wordBank[0].first;
        first.clue = wordBank[0].second;
        first.x = gridSize / 2;
        first.y = (gridSize - (int)first.text.length()) / 2;
        first.horizontal = true;
        first.number = 1;
        placeWord(first);

        int wordNum = 2;
        int attempts = 0;
        const int maxAttempts = (int)wordBank.size() * 15;

        for (size_t i = 1; i < wordBank.size() && (int)placedWords.size() < maxWords && attempts < maxAttempts; ++i) {
            attempts++;
            string word = wordBank[i].first;
            string clue = wordBank[i].second;

            auto positions = findIntersections(word);
            shuffle(positions.begin(), positions.end(), rng);

            for (const auto& [x, y, horiz, _] : positions) {
                if (canPlaceWord(word, x, y, horiz)) {
                    Word w{word, clue, x, y, horiz, wordNum++};
                    placeWord(w);
                    break;
                }
            }
        }
        return placedWords.size() >= 10;
    }

    string getResult() {
        // 1. Original grid chegaralarini topish
        int minX = gridSize, maxX = 0, minY = gridSize, maxY = 0;
        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                if (grid[i][j] != '#') {
                    minX = min(minX, i); maxX = max(maxX, i);
                    minY = min(minY, j); maxY = max(maxY, j);
                }
            }
        }

        // 2. Kengaytirilgan grid yaratish: 2*gridSize-1
        int newSize = 2 * gridSize - 1;
        vector<vector<char>> expanded(newSize, vector<char>(newSize, '|'));
        vector<tuple<int, int, int>> wordStarts;  // (clue_index, exp_row, exp_col)

        // 3. Harflarni joylashtirish va bosh harf koordinatalarini saqlash
        int clueIndex = 1;
        vector<Word> across, down;
        for (const auto& w : placedWords) {
            (w.horizontal ? across : down).push_back(w);
        }
        sort(across.begin(), across.end(), [](const Word& a, const Word& b) { return a.number < b.number; });
        sort(down.begin(), down.end(), [](const Word& a, const Word& b) { return a.number < b.number; });

        // Clue indexlarni to'g'ri tartibda belgilash
        vector<Word> allWords;
        allWords.reserve(across.size() + down.size());
        allWords.insert(allWords.end(), across.begin(), across.end());
        allWords.insert(allWords.end(), down.begin(), down.end());

        for (const auto& w : allWords) {
            int startRow = 2 * w.x;
            int startCol = 2 * w.y;
            expanded[startRow][startCol] = w.text[0];  // bosh harf

            // Bosh harf koordinatasini saqlash
            wordStarts.emplace_back(clueIndex++, startRow, startCol);

            // Qolgan harflarni joylashtirish
            if (w.horizontal) {
                for (int k = 1; k < (int)w.text.length(); ++k) {
                    int col = 2 * (w.y + k);
                    expanded[startRow][col] = w.text[k];
                    expanded[startRow][col - 1] = ' ';  // oldingi | ni o'chirish
                }
            } else {
                for (int k = 1; k < (int)w.text.length(); ++k) {
                    int row = 2 * (w.x + k);
                    expanded[row][startCol] = w.text[k];
                    expanded[row - 1][startCol] = ' ';  // oldingi | ni o'chirish
                }
            }
        }

        // 4. Ishlatilgan hududni topish
        int expMinX = newSize, expMaxX = 0, expMinY = newSize, expMaxY = 0;
        for (int i = 0; i < newSize; ++i) {
            for (int j = 0; j < newSize; ++j) {
                if (expanded[i][j] != '|') {
                    expMinX = min(expMinX, i); expMaxX = max(expMaxX, i);
                    expMinY = min(expMinY, j); expMaxY = max(expMaxY, j);
                }
            }
        }

        // 5. Natija chiqarish
        stringstream result;
        result << "=== CROSSWORD ===\n";
        for (int i = expMinX; i <= expMaxX; ++i) {
            for (int j = expMinY; j <= expMaxY; ++j) {
                char c = expanded[i][j];
                result << (c == ' ' ? ' ' : c);
            }
            result << '\n';
        }

        // === CLUES + KOORDINATALAR ===
        result << "\n=== GORIZONTAL ===\n";
        clueIndex = 1;
        for (const auto& w : across) {
            auto [idx, r, c] = wordStarts[clueIndex - 1];
            result << w.number << ". " << w.clue << " (" << w.text.length() << ") → ["
                   << (r - expMinX + 1) << "," << (c - expMinY + 1) << "]\n";
            clueIndex++;
        }

        result << "\n=== VERTIKAL ===\n";
        for (const auto& w : down) {
            auto [idx, r, c] = wordStarts[clueIndex - 1];
            result << w.number << ". " << w.clue << " (" << w.text.length() << ") → ["
                   << (r - expMinX + 1) << "," << (c - expMinY + 1) << "]\n";
            clueIndex++;
        }

        return result.str();
    }
};

// === JNI FUNKSION (o'zgarmagan) ===
extern "C" JNIEXPORT jstring JNICALL
Java_uz_alien_crossword_Crossword_generateCrossword(
        JNIEnv* env,
        jobject /* this */,
        jint gridSize,
        jint maxWords,
        jstring jWordList) {

    const char* cWordList = env->GetStringUTFChars(jWordList, nullptr);
    if (!cWordList) return env->NewStringUTF("Error: wordList null");

    string wordListStr(cWordList);
    env->ReleaseStringUTFChars(jWordList, cWordList);

    CrosswordGenerator generator(gridSize);
    generator.loadWordsFromString(wordListStr);

    if (!generator.generate(maxWords)) {
        return env->NewStringUTF("Error: Crossword generatsiya qilinmadi");
    }

    string result = generator.getResult();
    return env->NewStringUTF(result.c_str());
}