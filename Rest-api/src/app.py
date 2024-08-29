from flask import Flask, request, jsonify
from flask_pymongo import PyMongo
from werkzeug.security import generate_password_hash, check_password_hash
import bcrypt

app = Flask(__name__)
app.config['MONGO_URI'] = 'mongodb://localhost:27017/pythonmongodb'

mongo = PyMongo(app)

@app.route('/users', methods=['POST'])

def create_user():
    username = request.json.get("username")
    password = request.json.get("password")
    email = request.json.get("email")

    # Verifica que los campos no estén vacíos
    if username and password and email:
        salt = bcrypt.gensalt()
        hashed_password = bcrypt.hashpw(password.encode('utf-8'), salt)
        result = mongo.db.users.insert_one(
            {'username': username, 'password': hashed_password.decode('utf-8'), 'email': email}
        )
        response = {
            'id': str(result.inserted_id),
            'username': username,
            'email': email
        }
        return jsonify(response), 201 
    else:
        return not_found()  # Si algún campo está vacío, lanza un error 404

@app.errorhandler(404)
def not_found(error=None):
    message = {
        'message': 'Recurso no encontrado: ' + request.url,
        'status': 404
    }
    return jsonify(message), 404

if __name__ == '__main__':
    app.run(debug=True)

