# VisionDetect: Local and Server AI Object Detection Android App

**VisionDetect** is a full-stack Android application that performs real-time object detection locally using MLKit or TensorFlow Lite (EfficientDet),  
and also supports server side AI analysis through a Torch + FastAPI model pipeline.  

The app features a futuristic HUD-inspired interface, inspired by 80s sci-fi movies and TV shows.



![Local AI Model Object Detection](images/image1.png)
![AI Interest Analysis Heatmap](images/image3.png)
![AI Interest Analysis Heatmap](images/image4.png)
![User Capture Gallery](images/image5.png)
![Login Screen](images/image2.png)





---

## Overview

VisionDetect bridges local AI inference** and server-based model analysis in one cohesive Android experience.

- On-device models (MLKit / TFLite EfficientDet) detect and label real-world objects in real time.
- A secure backend authenticates users via LDAP and manages photo uploads.
- Uploaded photos are analyzed by a DINOv2 model served via FastAPI, generating heatmap overlays that highlight what the AI considers the most "interesting" regions.
- Both the notes server and model server are Dockerized, ensuring easy deployment and reproducibility.

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

###  1. Clone the repository
```bash
git clone https://github.com/<your-username>/VisionDetect.git
cd VisionDetect
```

###  2. Launch the Android App
Open the project folder in Android Studio.
Make sure your emulator or physical device is running Android 13+ (API 33+).
Build and install the app:

```bash
./gradlew installDebug
```
Or simply run the app from android studio.

### 3. Start the Backend (Ktor Server) + Model Server (FastAPI + Torch + DINOv2) 
The Ktor backend handles:
- LDAP authentication
- JWT token issuing
- User photo storage
- Communication with the model server
- 
The model server performs the AI heatmap generation using Meta’s DINOv2.

From the /server directory:

``` bash
docker compose up --build
```

### 4. Login or Register
When you first open the app, you’ll be prompted to login or register.
Credentials are validated via LDAP on the backend.
Once logged in, you’ll be directed to the Camera screen.

You can:
- Switch between MLKit and EfficientDet models.
- Capture and upload an image for server-side AI heatmap analysis.
- View all processed images in the Photo Gallery.




