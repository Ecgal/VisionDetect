# VisionDetect: Local and Server AI Object Detection Android App

**VisionDetect** is a full-stack Android application that performs real-time object detection locally using MLKit or TensorFlow Lite (EfficientDet),  
and also supports server side AI analysis through a Torch + FastAPI model pipeline.  

The app features a futuristic HUD-inspired interface, inspired by 80s sci-fi movies and TV shows.

---

## Overview

VisionDetect bridges local AI inference** and server-based model analysis in one cohesive Android experience.

- On-device models (MLKit / TFLite EfficientDet) detect and label real-world objects in real time.
- A secure backend authenticates users via LDAP and manages photo uploads.
- Uploaded photos are analyzed by a DINOv2 model served via FastAPI, generating heatmap overlays that highlight what the AI considers the most "interesting" regions.
- Both the notes server and model server are Dockerized, ensuring easy deployment and reproducibility.

---

## Architecture





---

## Core Features

###  **Local AI Detection**
- Uses CameraX for real-time preview and capture.
- Supports two local AI pipelines:
  - MLKit – lightweight, fast, but lower classification accuracy.
  - EfficientDet (TFLite) – higher accuracy and more detailed object labeling.
- Animated overlay shows bounding boxes and confidence scores.

###  **Server-Side Analysis**
- Once captured, images can be uploaded to a Ktor based backend.
- The backend securely stores images and forwards them to a FastAPI + PyTorch model server.
- Model server uses Meta’s DINOv2 to produce heatmap overlays, visualizing areas of interest or importance within the image.

###  **Authentication & Infrastructure**
- LDAP-backed authentication for secure user validation.
- JWT tokens for API communication between Android and server.
- Both Notes Server and Model Server are Dockerized for modular, portable deployment.

### **UI & Design**
- Built with Jetpack Compose and a custom PredatorVision inspired theme.
- Futuristic, minimal HUD design with glowing cyan highlights.
- Includes:
  - Camera interface with real-time detection overlay.
  - Secure login/register screens.
  - Gallery view with uploaded & analyzed photos.
  - Full screen image preview with rotation and scaling support.

---

##  Tech Stack

### **Android (Client)**
- Kotlin • Jetpack Compose • CameraX
- MLKit • TensorFlow Lite (EfficientDet)
- Coil • Material3
- JWT Auth • LDAP Integration

### **Backend (Ktor Server)**
- Kotlin • Ktor Framework
- JWT • LDAP Authentication
- SQLite • Image Storage
- Dockerized microservice

### **Model Server**
- FastAPI • PyTorch • DINOv2
- Image processing with TorchVision
- Generates heatmap overlays for visualization
- Dockerized and network-linked to the main Ktor server

---

##  Setup & Run

### 🖥️ 1. Clone the repository
```bash
git clone https://github.com/<your-username>/VisionDetect.git
cd VisionDetect
```




VisionDetect/
├── app/                    # Android app (Jetpack Compose)
│   ├── uiElements/         # Camera, Gallery, Login/Register screens
│   ├── repository/         # API + model interaction
│   ├── viewmodel/          # Business logic
│   └── model/              # Data classes
│
├── server/                 # Ktor backend (Dockerized)
│   ├── routes/             # API endpoints
│   ├── auth/               # LDAP integration
│   └── storage/            # Image persistence
│
└── model-server/           # FastAPI + Torch DINOv2 model server
    ├── app.py
    ├── model/weights/
    └── Dockerfile


