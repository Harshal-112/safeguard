# Women's Safety App

## Introduction
The Women's Safety App is an Android-based personal safety application designed to assist users during emergency situations.  
The application provides real-time emergency response features such as location tracking, SOS alerts, emergency contact communication, and wearable device emergency triggering.

The system combines mobile device sensors, background monitoring services, and cloud-based data synchronization to provide fast and reliable emergency response support.

---

## Requirements
- Android Studio  
- Minimum SDK Version: 29  
- Firebase Project Setup  
- Android Device or Emulator with Location & SMS Permissions  

---

## Features
- Emergency Contact Notifications via SMS  
- Real-Time GPS Location Tracking  
- Fake Ringing / Fake Call Emergency Escape Feature  
- One-Tap SOS Emergency Alert  
- Wear OS Emergency Trigger Support  
- Voice-Based Emergency Activation  
- Background Safety Monitoring Services  
- Safety Reporting and Monitoring System  

---

## Technical Stack
- Android Native Development (Java + XML)  
- Firebase Realtime Database  
- Google Location Services  
- Android Background Services  
- Broadcast Receivers  
- SMS Manager APIs  
- Wear OS Integration  

---

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/Harshal-112/safeguard.git
cd safeguard
```

---

### 2. Open the Project in Android Studio
- Open Android Studio  
- Select **Open an Existing Android Studio Project**  
- Navigate to the cloned repository folder  
- Select project root folder  

---

### 3. Firebase Setup
Add your Firebase configuration file:

```
app/google-services.json
```

(Required for database and cloud integration)

---

### 4. Build the Project
- Click **Build → Make Project**
- Wait for Gradle Sync to complete

---

### 5. Run the Application
- Connect Android Device OR Start Emulator  
- Click **Run App**

---

## Project Structure
```
Women_Safeguard/
 ├ app/
 ├ watchapp/
 ├ gradle/
 ├ build.gradle
 ├ settings.gradle
```

---

## Demonstration

### Screenshots
Screenshots are available in:
```
docs/screenshots/
```

---

### Application Demo Recording
Application working demo is available in:
```
docs/demo/
```

---

## Usage
Follow the user manual included in the project for detailed usage instructions and feature workflows.

---

## Project Status
⚠ Maintenance Phase  

Core features are implemented and functional.  
Some runtime behaviour may vary depending on Android version, device configuration, and dependency compatibility.

---

## Future Improvements
- UI Upgrade to Material Design 3  
- Architecture Refactor (MVVM Migration)  
- Offline Emergency Backup Flow  
- Cloud Automation Enhancements  
- Security Improvements  

---

## Contributing
For contributions, please follow coding standards and guidelines defined in `CONTRIBUTING.md`.

---

## License
This project is licensed under the MIT License.

---

## Author
Harshal Nerkar  
GitHub: https://github.com/Harshal-112
