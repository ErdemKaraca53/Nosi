# 📚 Nosi — İngilizce Kelime Öğrenme Uygulaması

> Akıllı flashcard, çeviri ve sözlük arama ile İngilizce kelime hazırlığını verimli hale getiren Android uygulaması.

![Status](https://img.shields.io/badge/Status-MVP-brightgreen)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF)
![API](https://img.shields.io/badge/Min%20SDK-26-blue)
![Target](https://img.shields.io/badge/Target%20SDK-36-blue)

---

## 🎯 Nedir?

**Nosi**, İngilizce öğrenenlerin kelime hazırlığını **etkili**, **görsel** ve **verimli** bir şekilde yapmasını sağlar. 

- 📖 Online sözlük arama (tanım, telaffuz, örnek)
- 🇹🇷 Türkçe cümleleri İngilizceye çevir, kelimeleri öğren
- 🎴 Flashcard tabanlı akıllı çalışma oturumları
- 🔄 Basit SRS (Spaced Repetition System) ile öğrenme izleme
- 💾 Tam çevrimdışı veri kayıt (ML Kit + Room)

---

## ✨ Temel Özellikler

### 1️⃣ **Sözlük Arama & Otomatik Tamamlama**
```
Kelime yazınız → Otomatik önerileri görün (Datamuse API)
          ↓
Seçtiğiniz kelimeyi arayınız
          ↓
Tanımlar, Eşanlamlılar, Zıt Anlamlılar, Telaffuz 🔊
```

- **API**: [dictionaryapi.dev](https://dictionaryapi.dev) (ücretsiz, API anahtarı yok)
- Aynı kelimenin birden fazla anlamını (noun, verb, adjective) destekler
- Fonetik yazım + MP3 telaffuz dosyası (mevcutsa)

### 2️⃣ **Çeviri & Kelime Parçalama**
```
TR: "Aklıma harika bir fikir geldi"
          ↓ (ML Kit Translator)
EN: "A great idea came to my mind"
          ↓ (Kelime Ayırma)
[idea] [great] [came] [mind]
          ↓ (Her biri EN→TR çevrilir)
WordData (word + meaningTr + pos)
```

### 3️⃣ **Koleksiyonlar (Listeler)**
- Temalarına göre kelime grupları oluştur
- Her listeye **emoji + custom renk** ata
- Cascade delete — liste silinince içindeki kelimeler de silinir
- Yeni liste oluştur veya mevcut listeye kaydet

### 4️⃣ **Flashcard Çalışması**
```
📍 Özellikler:
  ✓ Swipe: Sağa → "Biliyorum" | Sola → "Tekrar Gerek"
  ✓ Tap-to-flip: 3D kart döndürme animasyonu (Compose graphicsLayer)
  ✓ Sesli telaffuz: TextToSpeech ile İngilizce seslendirme 🔊
  ✓ İlerleme çubuğu: Gerçek zamanlı ilerleme takibi
  ✓ Kart yığını: Arka plandaki kartları görsel olarak göster
```

### 5️⃣ **Akıllı Tekrar Sistemi (Basit SRS)**
```
Mastery Level:
  0 → Yeni / Tekrar Gerek
  1 → Az Biliniyor
  2 → Biliniyor ✓
  3 → İyi Biliniyor ✓✓
```
- Sağa swipe → level + 1 (max 3)
- Sola swipe → level = 0
- **Çalışma bitince**: Bilemediklerini tekrar et veya tümünü baştan başlat

---

## 🏗️ Teknoloji Stack

### **UI & Navigation**
| Teknoloji | Versiyon | Kullanım |
|-----------|----------|---------|
| **Jetpack Compose** | Latest | Tüm UI composable'ları |
| **Material3** | Latest | Design system (tema, componentler) |
| **Compose Navigation** | 2.9.6 | Ekranlar arası gezinti |

**UI Özellikleri:**
- Swipe animasyonları (`detectHorizontalDragGestures`)
- 3D flip efektleri (`graphicsLayer + rotationY`)
- Smooth transitions (`AnimatedContent`, `tween`)
- Gradient brushes (teal & gold tema)
- Custom font (Lexend TTF)

### **Backend & Veri Yönetimi**
| Teknoloji | Versiyon | Kullanım |
|-----------|----------|---------|
| **Room Database** | Latest | Lokal veri (kelimeler, listeler, cümleler) |
| **Retrofit** | 2.11.0 | HTTP istekleri |
| **Kotlinx Serialization** | 1.6.3 | JSON parsing |
| **Coroutines** | Latest | Async işlemler |
| **Flow** | Latest | Reactive data streams |

**Veritabanı:**
```
┌─ word_lists (📚 Koleksiyonlar)
│  ├─ id, name, emoji, color, created_at
│  └─ ON DELETE CASCADE
│
├─ saved_words (💾 Kaydedilen Kelimeler)
│  ├─ id, list_id, word, part_of_speech
│  ├─ meaning_tr, definitions_json, synonyms_json, antonyms_json
│  ├─ mastery_level, saved_at
│  └─ Unique Index: (list_id, word, part_of_speech)
│
└─ saved_sentences (📝 Çevirilen Cümleler)
   ├─ id, list_id, source_text, translated_text, saved_at
   └─ ON DELETE CASCADE
```

**5 Versiyon Migration:**
- v1 → v2: Tablo şemasını düzenle
- v2 → v3: Türkçe anlam sütunu ekle
- v3 → v4: Mastery level (SRS) sütunu ekle
- v4 → v5: Saved sentences tablosu ekle

### **AI & Çeviri**
| Teknoloji | Kullanım |
|-----------|---------|
| **ML Kit Translation** | TR ↔ EN çevirisi (çevrimdışı modeller) |
| **ML Kit Generative AI** | Bağımlılıkta var ama kullanılmıyor |

**Çeviri Mimarisi:**
```
WordTranslator (EN→TR, kelime çeviri için)
    ↓ Lazy-initialized Translator
    ↓ suspendCancellableCoroutine ile async wrapper
    ↓ Başarısız olursa null (app çökmez, UI geri düşer)

SentenceTranslator (TR→EN, cümle çeviri için)
    ↓ Same pattern
```

### **Ses & Medya**
| Teknoloji | Kullanım |
|-----------|---------|
| **MediaPlayer** | Sözlük telaffuz MP3 dosyaları (dictionaryapi.dev'den) |
| **TextToSpeech** | Flashcard telaffuzu (cihaz TTS) |

### **Architecture Pattern**
```
📱 Jetpack Compose UI (Stateless/Stateful Composables)
    ↓
🎬 ViewModel + StateFlow (MVVM)
    ↓
📦 Repository Pattern (WordRepository)
    ↓
💾 Room Database + ML Kit
    ↓
🌐 Retrofit Network Layer
```

---

## 📐 Mimari Tasarım

### **Katmanlar**

```
┌─────────────────────────────────────┐
│      UI Layer (Compose)              │
│  - StudyScreen, DictionaryResultScreen
│  - TranslationScreen, CollectionDetailScreen
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│    ViewModel Layer (MVVM)            │
│  - DatabaseViewModel                 │
│  - DictionaryViewModel               │
│  - TranslationViewModel              │
│  - AutoCompleteViewModel             │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Repository Layer                   │
│  - WordRepository (veri erişim)      │
└──────────────┬──────────────────────┘
               │
       ┌───────┴───────┐
       ▼               ▼
   ┌─────────┐   ┌──────────┐
   │  Room   │   │ Network  │
   │ Database│   │ (Retrofit)
   └─────────┘   └──────────┘
        ▼               ▼
   [SQLite]    [Dictionary API]
              [AutoComplete API]
              [ML Kit Services]
```

### **State Management**
- `StateFlow` ile reactive updates
- `Flow` ile database observations (hot & cold)
- `remember` + `mutableStateOf` ile UI state (composable scope)
- Lifecycle-aware collection (`collectAsStateWithLifecycle`)

### **Hata Yönetimi**
```
✓ Çeviri başarısız → null döner, UI fallback yapıyor (sadece İngilizce tanım)
✓ Ses oynatma hatası → sessizce başarısız (app çökmez)
✓ API hatası → Error state → Retry butonu
✓ Bağlantı yok → Connection error mesajı
```

---

## 🚀 Başlangıç

### **Gereksinimler**
- Android Studio Hedgehog+
- Android SDK 26+ (minSdk)
- Java 11+
- Gradle 8.2+

### **Kurulum**

```bash
# Klonla
git clone https://github.com/yourusername/nosi.git
cd nosi

# Gradle sync et
./gradlew build

# Öykünücüde çalıştır
./gradlew installDebug

# Ya da Android Studio'da
# Build → Build & Run
```

### **İlk Kullanım**
1. Uygulamayı aç → "Main Screen"
2. "🔍 Word Lookup" → Kelime ara (örn: "run")
3. POS seç (Noun, Verb, ...) → "Save Word"
4. Yeni liste oluştur → Kaydet
5. "📚 Collection" → Listeyi seç → "Study"
6. Sağa/sola swipe yaparak çalış!

---

## 📦 Bağımlılıklar

### Core
```gradle
androidx.core:core-ktx:latest
androidx.lifecycle:lifecycle-runtime-ktx:2.8.6
androidx.activity:activity-compose:latest
androidx.compose.bom:latest
```

### UI
```gradle
androidx.compose.ui:ui, ui-graphics, ui-tooling-preview
androidx.compose.material3:material3
androidx.navigation:navigation-compose:2.9.6
androidx.compose.ui:ui-text-google-fonts:1.9.4
```

### Database & Network
```gradle
androidx.room:room-runtime, room-ktx
com.squareup.retrofit2:retrofit:2.11.0
com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0
org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3
com.google.code.gson:gson:2.10.1
```

### AI/ML
```gradle
com.google.mlkit:translate:17.0.3
com.google.mlkit:genai-prompt:1.0.0-beta2 (kullanılmıyor)
com.google.firebase:firebase-ai (kullanılmıyor)
```

---

## 🎨 Tasarım & Tema

### **Renk Paleti**
- **Primary (Teal)**: `#0D9488` — CTA'lar, "Got it" butonu
- **Secondary (Gold)**: `#D97706` — "Review" butonu, alt CTA'lar
- **Background Dark**: `#1F2937` — Ana arka plan
- **Background Medium**: `#374151` — Card arka planı
- **Border**: `#4B5563` — Divider'lar
- **Text**: `#FFFFFF`, `#D1D5DB` — Başlıklar ve açıklamalar

### **Typography**
- **Font**: Lexend (Google Fonts)
- **Headings**: 28-32sp, Bold
- **Body**: 14-16sp, Regular
- **Captions**: 12-13sp, Medium

### **Animasyonlar**
| Animasyon | Kullanım | Süre |
|-----------|---------|------|
| Expand/Shrink Vertically | Ekran geçişleri | 900ms |
| Flip (rotationY) | Flashcard döndürme | 420ms |
| Fade In/Out | POS chip geçişi | 250ms |
| Swipe Scale | Kart çekişi | 220ms, easing |

---

## 📊 Ekranlar

### 1. **Main Screen** 🏠
- Hızlı erişim: Sözlük Arama, Çeviri, Koleksiyonlar
- Koleksiyon listesi (en yeni ilk)

### 2. **Dictionary Input** 🔍
- Kelime arama boşluğu
- Otomatik tamamlama (debounce: 300ms)
- History/suggestions

### 3. **Dictionary Result** 📖
- Kelime başlığı + fonetik + 🔊 telaffuz
- POS chip'leri (Noun, Verb, Adjective...)
- Seçili POS'a göre:
  - Tanımlar (numaralı)
  - Örnekler (italik)
  - Eşanlamlılar (tag'ler)
  - Zıt anlamlılar (tag'ler)
- "Save Word" butonu → Bottom Sheet
- Duplicate kontrol

### 4. **Translation Screen** 🇹🇷
- Türkçe cümle giriş
- "Çevir" butonu
- Çevrilen cümle + kelime kelime TR anlamları
- Cümle veya kelimeyi listeye kaydet

### 5. **Collection Detail** 📚
- Koleksiyon adı + istatistikler (kelime sayısı, cümle sayısı)
- Kaydedilen kelimeler listesi
- Kelimeyi sil
- "Study Oturumunu Başlat" butonu

### 6. **Study Screen** 🎴
- Kart gösterimi (3D flip)
- Swipe kontrolü
- Ön yüz: Kelime + POS
- Arka yüz: Türkçe anlam + İngilizce tanımlar + eşanlamlılar
- İlerleme çubuğu
- "Review" / "Got it" butonları
- **Oturum sonu**: Özet + "Review Missed" / "Restart All"

---

## 🔧 Geliştirme Notları

### **Bilinen Sınırlamalar**
1. ML Kit Translator bellekte kalıyor (close() çağrılmıyor) — MVP için kabul edilebilir
2. Çok büyük listelerde (1000+ kelime) swipe performansı etkilenebilir
3. Offline çalışması için ML Kit modelleri ilk çalıştırmada indirilmesi gerekir (~30MB)

### **Gelecek Geliştirmeler**
- [ ] Spaced Repetition formülü (SM-2 algoritması)
- [ ] Kelime kategorileri (KPDS, YDS, işletme vb.)
- [ ] Multimedya desteği (resim, örnek video)
- [ ] Sosyal paylaşım (liste, kişi başına özel)
- [ ] Uygulama içi analitics (öğrenme istatistikleri)
- [ ] Ses kaydı (kendi telaffuzunu kaydet)
- [ ] İçeri aktar/Dışarı aktar (CSV, JSON)

---

## 📝 Lisans

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 👤 Hakkında

Geliştirici: **Erdem Karaca** ([erdemkaraca5328@gmail.com](mailto:erdemkaraca5328@gmail.com))

---

## 🙏 Teşekkürler

- [Free Dictionary API](https://dictionaryapi.dev) — Ücretsiz sözlük
- [Datamuse](https://www.datamuse.com) — Kelime önerileri
- [ML Kit](https://developers.google.com/ml-kit) — Çeviri modelleri
- [Jetpack Compose](https://developer.android.com/compose) — Modern UI
- Google Fonts — Lexend font ailesi

---

<p align="center">
  💡 <b>Nosi ile her gün bir adım İngilizceye!</b> 💡
</p>

<p align="center">
  Made with ❤️ in Kotlin & Compose
</p>
