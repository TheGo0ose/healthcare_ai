# HealthCare Assistant

**HealthCare** is a full-stack project that includes a Python-based chatbot and a Java-based web interface. It helps users find medical facilities and get basic medical support using an AI-powered chatbot.

---

## 🧠 Project Structure

### 1. `bot_api/` — Python Chatbot

- Built with **PyTorch** and **NLTK**
- Uses a pre-trained RNN model (`data_rnn.pth`)
- Retrieves nearby medical centers from `medical_centers.json`

**Key Files:**
- `chat.py` – main chatbot interface
- `model_chat.py` – defines the RNN model
- `nltk_utils.py` – tokenization and bag-of-words processing
- `train.py` – training script for the model
- `requirements.txt` – lists required Python packages

**How to Run the Chatbot:**

```bash
cd bot_api
pip install -r requirements.txt
python chat.py
```

> Make sure you have Python 3.7+, and install required libraries: `torch`, `nltk`, `geocoder`, etc.

---

### 2. `web_site/` — Java Web Interface

- Java Maven project
- Includes a simple HTML frontend (`index.html`)
- Can be used to connect to and interact with the bot

**How to Run the Web App:**

```bash
cd web_site
./mvnw spring-boot:run
```

> On Windows use:
```bash
mvnw.cmd spring-boot:run
```

---

## 🧰 Tools & AI Assistants Used

- 🧠 **ChatGPT** — used for designing chatbot logic and Python coding assistance
- 📘 **Grok** — used for learning, code suggestions, and AI model setup
- 🌐 **Galileo.ai** — helped with frontend interface design (HTML/CSS layout for the website)

> The AI model training and Python bot development were supported by **Grok** and **ChatGPT**.  
> The frontend interface in the Java web project was assisted by **Galileo.ai**.

---

## 📁 Directory Overview

```
HealthCare/
├── bot_api/         # Python-based AI chatbot
└── web_site/        # Java-based web application
```

---

## 📦 Requirements

- Python 3.7+
- Java 11+
- Maven
- Python libraries:
  - torch
  - nltk
  - geocoder

---

## 📄 License

MIT License © 2025 YourName
