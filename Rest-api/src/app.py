from flask import Flask, request, jsonify
from flask_pymongo import PyMongo
from werkzeug.security import generate_password_hash, check_password_hash
import bcrypt

app = Flask(__name__)
app.config['MONGO_URI'] = 'mongodb://localhost:27017/pythonmongodb'

app.config['MONGO_URI'] = 'mongodb+srv://Martin:wnL9Q2Ruwf4WJGE0@campusfit.xih68.mongodb.net/?retryWrites=true&w=majority'

mongo = PyMongo(app)

@app.route('/users', methods=['POST'])

def create_user():
    username = request.json.get("username")
    password = request.json.get("password")
    email = request.json.get("email")

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
        return not_found()  

@app.route('/users/<username>', methods=['DELETE'])
def delete_user(username):
    result = mongo.db.users.delete_one({'username': username})

    if result.deleted_count > 0:
        return jsonify({"message": f"Usuario {username} eliminado correctamente"}), 200
    else:
        return jsonify({"message": f"Usuario {username} no encontrado"}), 404

@app.route('/users/verify', methods=['POST'])
def verify_user():
    username = request.json.get("username")
    password = request.json.get("password")

    user = mongo.db.users.find_one({'username': username})
    
    if user and bcrypt.checkpw(password.encode('utf-8'), user['password'].encode('utf-8')):
        return jsonify({"message": "Verificación exitosa"}), 200
    else:
        return jsonify({"message": "Usuario o contraseña incorrectos"}), 401

@app.errorhandler(404)
def not_found(error=None):
    message = {
        'message': 'Recurso no encontrado: ' + request.url,
        'status': 404
    }
    return jsonify(message), 404

if __name__ == '__main__':
    app.run(debug=True)

    app.run(debug=True)