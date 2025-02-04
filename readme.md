# File Upload With Jetpack Compose And Ktor-client 📂🚀

This project is an Android application that allows users to upload files (Images, PDFs, Videos) with real-time progress tracking. It is built using Jetpack Compose, Kotlin, Coroutines, and StateFlow.

## 🛠 Features
- ✅ Upload Images, PDFs, and Videos
- ✅ Track upload progress in real-time
- ✅ Support for single and multiple file uploads
- ✅ Cancel ongoing uploads
- ✅ Uses StateFlow for UI state management

## Screenshots
<div style="display:flex">
    <img src="./screenshots/single file.jpeg" width="360" height="760" alt="Single file upload status">
    <span style="width:16"></span>
    <img src="./screenshots/Multiple files.jpeg" width="360" height="760" alt="Multiple files upload status">
</div>

## 🚀 Getting Started
1️⃣ Clone the Repository
```bash
git clone https://github.com/Srinivas1109/File-upload-jetpack-compose
cd File-upload-jetpack-compose
```

2️⃣ Open in Android Studio
- Open Android Studio
- Click on "Open an Existing Project"
- Select the cloned repository folder

3️⃣ Run the App
- Connect an Android device or start an emulator
- Click Run ▶️ in Android Studio

## 📜 Usage
### File Upload
1. Select a file type (Image, PDF, or Video)
2. Choose a file from your device
3. Upload starts automatically, with real-time progress updates
4. You can cancel an upload at any time

### Multiple File Support
- Enable "Multiple Files" mode to select and upload multiple files at once

## 📦 Technologies Used
- Jetpack Compose - Modern UI Toolkit
- StateFlow - Reactive state management
- Coroutines - Asynchronous programming
- Koin - Dependency Injection
- Ktor-Client - Client side networking
- Kotlin - Primary language
- MVVM Architecture - Clean and scalable design

## 🛠 Future Improvements
- 🔹 Implement retry on failure
- 🔹 Add background upload support
- 🔹 Enhance UI with animations