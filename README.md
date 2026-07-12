# QR Scanner & Generator

## 📌 Project Title
**SCT_AD_4 - QR Code Scanner & Generator App**

## 📖 Description
A beginner-friendly Android application built using **Kotlin** and **XML** that lets users
scan QR codes with the device camera and generate their own QR codes from custom text or
URLs. Scanning is powered by the **ZXing Android Embedded (JourneyApps)** library, and QR
generation uses the **ZXing Core** encoder. The app also supports copying results,
sharing text, and saving generated QR images to the gallery.

This project was developed as part of an Android Development internship task.

## ✨ Features
- ✅ Scan QR codes using the device camera
- ✅ Generate QR codes from user-entered text
- ✅ Display scanned result in a readable, selectable text view
- ✅ Display the generated QR code image
- ✅ Copy result text to the clipboard
- ✅ Share result text via the system share sheet
- ✅ Save generated QR code image to the gallery
- ✅ Clear input, result, and generated QR image
- ✅ Runtime camera permission handling with graceful denial messaging
- ✅ Input validation (prevents generating a QR from empty text)
- ✅ Error handling for QR generation/scanning failures
- ✅ Clean, modern, responsive Material Design UI (scrollable layout)


## 🛠️ Technologies Used
- **Language:** Kotlin
- **UI:** XML with Material Design 3 Components
- **Architecture:** Single Activity (no MVVM, no Fragments)
- **View Binding:** Enabled
- **Min SDK:** 24
- **Target SDK:** 34
- **Compile SDK:** 34

## 📦 Dependencies
```gradle
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.activity:activity-ktx:1.8.2'

// QR Scanning
implementation 'com.journeyapps:zxing-android-embedded:4.3.0'

// QR Generation
implementation 'com.google.zxing:core:3.5.2'
```

## ⚙️ Installation
1. Clone or download this repository.
2. Open **Android Studio**.
3. Select **Open an Existing Project** and choose the `SCT_AD_4` folder.
4. Let Gradle sync and download the ZXing dependencies.

## 🚀 How to Run
1. Connect an emulator or physical device (Android 7.0 / API 24 or higher, camera recommended).
2. Click **Run ▶️** to build and launch the app.
3. Grant camera permission when prompted to enable scanning.

## 📱 How to Use
1. **To generate a QR code:** Type text or a URL in the input field and tap **Generate**.
2. **To scan a QR code:** Tap **Scan QR**, grant camera permission if asked, and point the
   camera at a QR code.
3. Tap **Copy** to copy the result to the clipboard, or **Share** to send it via another app.
4. Tap **Save to Gallery** to save a generated QR image to your device photos.
5. Tap **Clear** to reset the input, result, and QR image.

## 📂 Project Structure
```
SCT_AD_4
│
├── app
│   ├── manifests
│   │      AndroidManifest.xml
│   │
│   ├── java
│   │      MainActivity.kt
│   │
│   ├── res
│   │      layout/activity_main.xml
│   │      values/colors.xml
│   │      values/strings.xml
│   │      values/themes.xml
│   │
│   └── build.gradle
│
├── README.md
└── .gitignore
```

## 🔮 Future Improvements
- Add flashlight toggle during scanning
- Maintain an in-memory scan history list (ListView)
- Add dark mode color variants
- Support generating other barcode formats (EAN, Code128, etc.)
- Add smooth button press/ripple animations
- Add landscape-optimized layout variant

## 👤 Author
**Maharudra**
Internship Task — SCT_AD_4
