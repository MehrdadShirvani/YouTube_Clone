# 🎥 YouTube Clone – Full Stack Java Application (Memoli)
[![Static Badge](https://img.shields.io/badge/%231_advanced_programming_final_project-FF0000)]() [![star](https://img.shields.io/static/v1?label=%F0%9F%8C%9F&message=If%20Useful&style=flat&color=FF0000)]() [![license](https://img.shields.io/badge/License-MIT-FF0000)]() 

<p align="center">
<img src="./src/main/resources/Client/youtube-logo-white.png" width=50%>
<br/>
</p>

This project was developed as the final project for an advanced programming course👨‍💻🥇. A full-featured **YouTube Clone** built entirely in **Java**, with a **JavaFX client**, **Socket-based communication**, **RESTful media streaming**, and **MySQL database** using **Hibernate ORM**. This application replicates the core experience of YouTube — including video playback, user interaction, and content management — with a strong emphasis on **security**, **performance**, and **modular design**.

![Screenshot](https://github.com/user-attachments/assets/0cdcff8f-25fd-4a29-b297-c00df8aa86f5)

Now ready to go!🎉🎉🎉


---


## 🧰 Technology Stack

| Layer           | Technology                                                                                                                                                          |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Client (UI)** | ![JavaFX](https://img.shields.io/badge/JavaFX-0078D7?style=for-the-badge&logo=java)                                                                                 |
| **Server**      | ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java) ![REST API](https://img.shields.io/badge/REST_API-6CC24A?style=for-the-badge)       |
| **Database**    | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)                                                                  |
| **ORM**         | ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)                                                      |
| **Networking**  | ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java) ![Socket](https://img.shields.io/badge/Sockets-0A74DA?style=for-the-badge)          |
| **Streaming**   | ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java) ![HTTP Server](https://img.shields.io/badge/HTTP_Server-4BAF4F?style=for-the-badge) |
| **Email**       | ![JavaMail](https://img.shields.io/badge/JavaMail-Jakarta-6CA0DC?style=for-the-badge)                                                                               |

---

## 🚀 Features

### ✅ Core Functionalities

- 🔐 **User Authentication**: Login, Registration, Email verification
- 🏠 **Home Page**: Personalized feed with video previews & thumbnails
- 🔎 **Search**: Search videos by title, tags, or description
- 📥 **Video Uploading**: Add videos with metadata and tags
- 🏷️ **Tags**: Categorize videos using searchable tags
- 📺 **Streaming Player**:
    - Real-time streaming without full download
    - Playback controls: speed, volume, seek
- 💬 **Comments**:
    - Comment on videos
    - Like, dislike, and reply to comments
- 📂 **Playlists**: Create and manage custom playlists
- 🔔 **Notifications**: See updates from subscriptions 
- 👤 **User Profiles & Channels**: View channel pages and video lists
- ❤️ **Like/Dislike Videos**
- 📥 **Video Downloading**: Download videos directly to local storage
- 🤖 **Simple Video Recommendations**: Based on tags and viewing history
- 📌 **Subscriptions**: Follow users and view content from subscribed channels

---

## 🧱 Architecture Overview

This application is built using a **client-server architecture** with clear separation of concerns across UI, networking, and business logic.

### 🔗 Communication

- **Java Sockets** are used for real-time communication between client and server
- Designed using a custom, structured **Socket API**
- All communication is **encrypted** to ensure privacy and integrity

### 📡 Media Transfer
- Videos are streamed via a **Java RESTful API**
- Supports **byte-range (partial content)** to enable seamless playback and seeking
- Videos can also be **downloaded** by users as needed
    
---

## 🗂️ Project Structure

```
/java
 ┣ /Client
 ┃ ┣ /ClientEncryption.java         # Client-side encryption
 ┃ ┗ /Youtube.java                  # JavaFX Application
 ┣ /Server
 ┃ ┣ /Database                 # Database Manager
 ┃ ┣ /Server.java              # Server 
 ┃ ┣ /ClientHandler.java
 ┃ ┣ /ServerEncryption.java    # Server-side encryption
 ┃ /Shared              
 ┃ ┣ /Api                      # DTOs 
 ┃ ┗ /Models                   # JPA entities
 ┣ /database
 ┃ ┗ schema.sql                # MySQL schema setup
 ┗ README.md
```

---

## 🔐 Security

- 🔒 **Encrypted socket communication** using custom RSA and AES encryption/decryption methods implemented in `ServerEncryption` and `ClientEncryption` classes to secure server-client data exchange.
- 🔑 Secure login with email-based verification
- 🛡️ Built-in checks to protect user actions and prevent data tampering
---

## 📹 Media Streaming & Download

- Videos are served via a dedicated REST API supporting:
    - **HTTP byte-range** for fast and smooth seeking
    - **Video streaming** with minimal startup delay
    - **Video download** for offline viewing
        
- The media player supports:
    - Adaptive playback speed
    - Volume control
    - Seek functionality
    - Stream without full file download

---



## 🎨User Interface (GUI)
The user interface for this project was developed using the JavaFX framework, enhanced with CSS for styling. Below are some of the key UI features:
* Responsive(all pages)
* Interactive(primary buttons|searchbar|hovering)
* Input validation before backend check(Minimums for name&username|constraints for password field)
* Shortcuts

| Key | Action |
| ---- | ----- |
| `/` | search |
| `Esc` | cancel search |
| `ctrl+H` | Home |
| `ctrl+S` | Shorts |
| `ctrl+Shift+S` | subscriptions |
| `ctrl+Y` | your channel |
| `ctrl+Shift+ H` | History |
| `↑` | volume 10% up |
| `↓` | volume 10% down |
| `→` | 10sec forward |
| `←` | 10sec backward |
| `Space` | pause/upause |

---

## 🤝Contributing
Contributions are the heart of the open-source community, fostering learning, inspiration, and creativity. Your contributions are highly valued and appreciated.

If you have suggestions for improvements, please fork the repository and submit a pull request. Alternatively, you can open an issue with the “enhancement” tag.

**Don’t forget to star🌟 the project! Thanks again!**
## 🌟Acknowledgments
I would like to express my sincere gratitude to the following individuals for their invaluable contributions and support throughout this project:
* [Mohsen Minavand](https://github.com/woxane), [Ehsan Habibagahi](https://github.com/Ehsan-Habibagahi) and [Mehrdad Shirvani](https://github.com/MehrdadShirvani) (_Contributors_)
* [Dr. Saeed R Kheradpishe](https://github.com/SRKH) (_Instructor_)
* [Farid Karimi](https://github.com/Farid-Karimi) (_Teaching Assistant(TA)_)
* [Video and Image Files](https://www.pexels.com)

## 📄 License

MIT License – See `LICENSE` file.

