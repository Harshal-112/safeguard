# Women Safeguard 🛡️

**Women Safeguard** is a comprehensive Android security application designed to provide immediate assistance and deterrence tools for women's safety. It features real-time location tracking, voice-activated emergency triggers, and a companion smartwatch app.

## 🚀 Key Features

* **🗣️ Voice Emergency:** automatically detects distress commands to trigger SOS alerts without needing to touch the phone.
* **📞 Fake Ringing:** Simulates an incoming phone call to help users exit uncomfortable or unsafe situations gracefully.
* **📍 Safe Map & Location Sharing:** Real-time location tracking to keep trusted contacts informed of your whereabouts.
* **⌚ Wear OS Companion:** Includes a standalone `watchapp` module for immediate access to safety features directly from a smartwatch.
* **🔐 User Authentication:** Secure login system to protect user data.

## 🛠️ Tech Stack

* **Language:** Java
* **UI:** XML Layouts
* **Backend:** Firebase (Auth, Database)
* **Platform:** Android Mobile & Wear OS

## 📂 Project Structure

```bash
Women_Safeguard/
├── app/                  # Main Android Mobile Application
│   ├── src/main/java/    # Java Source Code (Dashboard, Maps, Services)
│   └── src/main/res/     # Layouts and Assets
├── watchapp/             # Wear OS Smartwatch Module
└── build.gradle          # Project Configuration
