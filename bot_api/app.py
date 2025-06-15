from flask import Flask, render_template, request, jsonify
from flask_cors import CORS
from chat import get_response
import traceback

app = Flask(__name__)
CORS(app)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/predict', methods=['POST'])
def predict():
    try:
        text = request.get_json().get('message')
        if not text:
            return jsonify({'answer': 'Please enter a valid message.'}), 400
        response = get_response(text)
        return jsonify({'answer': response})
    except Exception as e:
        error_message = f"Server error: {str(e)}\n{traceback.format_exc()}"
        print(error_message)  # Логируем полную трассировку
        return jsonify({'answer': f'Sorry, something went wrong on the server: {str(e)}'}), 500

if __name__ == '__main__':
    app.run(debug=True)