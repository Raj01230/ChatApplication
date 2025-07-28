# 💬 ChatApplication

A real-time chat application built using **Spring Boot** (Backend) and **Angular** (Frontend) with **Socket.IO** integration for instant messaging. Supports features like 1-1 chat, group chat, message reply, delete-for-self/everyone, typing indicators, message status, and more.

---

## 🚀 Features

### ✅ Real-Time Chat
- One-to-one personal chat
- Group chat with multiple users
- Real-time message delivery using **Socket.IO**

### ✅ Message Features
- **Send / Receive** messages instantly
- **Reply** to a specific message (like WhatsApp)
- **Delete for self / Delete for everyone**
- **Seen / Delivered / Sent** message status tracking

### ✅ UI/UX (Angular)
- Clean, modern chat UI inspired by WhatsApp
- Hover options on messages (reply/delete)
- Chat pagination on scroll
- Dynamic chat list with online/offline indicators

### ✅ Admin & Group Management
- Create groups
- Add/remove users from group
- Show typing indicators per room

---

## 🛠️ Tech Stack

| Layer        | Technology           |
|--------------|----------------------|
| **Frontend** | Angular (TypeScript, HTML, CSS) |
| **Backend**  | Spring Boot, WebSocket / Netty-SocketIO |
| **Database** | MySQL / PostgreSQL |
| **Real-Time**| Socket.IO (client + server) |
| **Auth**     | Spring Security / JWT |

---

## 🧱 Modules Overview

### 📦 Backend (Spring Boot)
- User Module (Registration, Login, Profile)
- Chat Module (Rooms, Messages, Replies)
- Group Module (Add/Remove members, roles)
- Message Status Module (Seen, Delivered, Sent)
- DeleteMessage Module (track per-user message visibility)

### 🎨 Frontend (Angular)
- Login / Register
- Chat Dashboard
- Message UI Component (with actions)
- Modal system for group and member management

---

## 🧪 How to Run the Project

### 🔧 Backend (Spring Boot)
```bash
cd spring-chat-backend
mvn clean install
mvn spring-boot:run
