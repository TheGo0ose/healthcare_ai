import random
import json
import torch
import math
import geocoder
from model import RNNModel
from nltk_utils import bag_of_words, tokenize
import traceback
import os

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
print(f"Using device: {device}")

def load_data():
    try:
        if not os.path.exists('checkbot.json'):
            raise FileNotFoundError("checkbot.json not found in project directory")
        with open('checkbot.json', 'r', encoding='utf-8') as json_data:
            intents = json.load(json_data)

        data_path = os.path.join("data_rnn.pth")
        if not os.path.exists(data_path):
            raise FileNotFoundError(f"{data_path} not found")
        data = torch.load(data_path, map_location=device)

        print(f"Loaded data: input_size={data['input_size']}, hidden_size={data['hidden_size']}, output_size={data['output_size']}, num_layers={data['num_layers']}")
        return intents, data
    except Exception as e:
        print(f"Error loading data: {str(e)}\n{traceback.format_exc()}")
        raise

try:
    intents, data = load_data()
except Exception as e:
    print(f"Fatal error: {str(e)}")
    raise SystemExit

input_size = data["input_size"]
hidden_size = data["hidden_size"]
output_size = data["output_size"]
num_layers = data["num_layers"]
all_words = data['all_words']
tags = data['tags']
model_state = data["model_state"]

try:
    model = RNNModel(input_size, hidden_size, output_size, num_layers).to(device)
    model.load_state_dict(model_state)
    model.eval()
    print("Model loaded successfully")
except Exception as e:
    print(f"Error loading model: {str(e)}\n{traceback.format_exc()}")
    raise SystemExit

bot_name = "Sam"

def get_response(msg):
    try:
        print(f"Processing message: {msg}")
        sentence = tokenize(msg.lower())
        print(f"Tokenized sentence: {sentence}")

        # Обработка специальных случаев
        if any(word in sentence for word in ["name", "this is"]):
            for word in sentence:
                if word not in ["my", "is", "name", "i", "am", "this"]:
                    user_name = word.capitalize()
                    return ["name", f"Hi {user_name}, please say your age."]
        if any(word in sentence for word in ["age", "i'm", "am"]):
            for word in sentence:
                if word.isnumeric():
                    return ["age", "What is your gender?"]
        if any(word in sentence for word in ["male", "female"]):
            return ["gender", "Tell the symptoms you have to know about potential conditions."]
        if any(word in sentence for word in ["yes", "hospital", "hospitals", "medical", "center"]):
            return centres()

        # Обработка симптомов через модель
        X = bag_of_words(sentence, all_words)
        print(f"Bag of words shape: {X.shape}")
        X = torch.tensor(X, dtype=torch.float32).unsqueeze(0).unsqueeze(0).to(device)  # [1, 1, input_size]
        print(f"Input tensor shape: {X.shape}")

        with torch.no_grad():
            output = model(X)
        print(f"Model output shape: {output.shape}")
        _, predicted = torch.max(output, dim=1)
        tag = tags[predicted.item()]
        print(f"Predicted tag: {tag}")

        probs = torch.softmax(output, dim=1)
        prob = probs[0][predicted.item()]
        print(f"Prediction probability: {prob.item()}")

        if prob.item() > 0.75:
            for intent in intents['intents']:
                if intent["tag"] == tag:
                    response = intent['responses']
                    if tag in ["greeting", "goodbye", "work", "who", "Thanks", "joke", "name", "age", "gender"]:
                        return [intent['tag'], response]
                    return [intent['tag'], response, intent.get('Precaution', '')]

        return ["not_understand", "I do not understand. Can you please rephrase the sentence?"]
    except Exception as e:
        error_message = f"Error in get_response: {str(e)}\n{traceback.format_exc()}"
        print(error_message)
        return ["error", f"Bot error: {str(e)}"]

def centres():
    try:
        if not os.path.exists('medical_centers.json'):
            raise FileNotFoundError("medical_centers.json not found in project directory")
        with open("medical_centers.json", "r", encoding='utf-8') as json_file:
            medical_centers = json.load(json_file)

        location = geocoder.ip('me')
        given_location = location.latlng
        if not given_location:
            return ["center", ["Unable to detect your location. Please try again later."]]

        def haversine(lat1, lon1, lat2, lon2):
            R = 6371.0
            lat1 = math.radians(lat1)
            lon1 = math.radians(lon1)
            lat2 = math.radians(lat2)
            lon2 = math.radians(lon2)
            dlon = lon2 - lon1
            dlat = lat2 - lat1
            a = math.sin(dlat / 2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon / 2)**2
            c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
            distance = R * c
            return distance

        distances_to_centers = []
        for center in medical_centers["intents"]:
            center_location = center["location"]
            distance = haversine(given_location[0], given_location[1], center_location[0], center_location[1])
            distances_to_centers.append((center["tag"], distance, center["Address"]))

        distances_to_centers.sort(key=lambda x: x[1])
        l = ["center"]
        for center_name, distance, address in distances_to_centers[:5]:
            l.append([center_name, f"{round(distance, 2)}km", address])
        return l
    except Exception as e:
        error_message = f"Error in centres: {str(e)}\n{traceback.format_exc()}"
        print(error_message)
        return ["center", [f"Error fetching medical centers: {str(e)}"]]