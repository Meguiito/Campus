from flask import Flask, request, jsonify
from flask_pymongo import PyMongo
from werkzeug.security import generate_password_hash, check_password_hash
import bcrypt
from pymongo.errors import PyMongoError, ServerSelectionTimeoutError

app = Flask(__name__)

# Configura la URI para conectar al cluster de MongoDB
app.config['MONGO_URI'] = 'mongodb+srv://Martin:wnL9Q2Ruwf4WJGE0@campusfit.xih68.mongodb.net/?retryWrites=true&w=majority'

mongo = PyMongo(app)

@app.route('/users', methods=['POST'])

def create_user():
    try:
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
            return jsonify({"error": "Todos los campos son obligatorios"}), 400

    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/users/<username>', methods=['DELETE'])
def delete_user(username):
    result = mongo.db.users.delete_one({'username': username})
    try:
        result = mongo.db.users.delete_one({'username': username})

    if result.deleted_count > 0:
        return jsonify({"message": f"Usuario {username} eliminado correctamente"}), 200
    else:
        return jsonify({"message": f"Usuario {username} no encontrado"}), 404
        if result.deleted_count > 0:
            return jsonify({"message": f"Usuario {username} eliminado correctamente"}), 200
        else:
            return jsonify({"error": f"Usuario {username} no encontrado"}), 404
    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/users/verify', methods=['POST'])
def verify_user():
    username = request.json.get("username")
    password = request.json.get("password")
    try:
        username = request.json.get("username")
        password = request.json.get("password")

    user = mongo.db.users.find_one({'username': username})
    
    if user and bcrypt.checkpw(password.encode('utf-8'), user['password'].encode('utf-8')):
        return jsonify({"message": "Verificación exitosa"}), 200
    else:
        return jsonify({"message": "Usuario o contraseña incorrectos"}), 401
        user = mongo.db.users.find_one({'username': username})
        
        if user and bcrypt.checkpw(password.encode('utf-8'), user['password'].encode('utf-8')):
            return jsonify({"message": "Verificación exitosa"}), 200
        else:
            return jsonify({"error": "Usuario o contraseña incorrectos"}), 401
    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.errorhandler(404)
def not_found(error=None):
    message = {
        'message': 'Recurso no encontrado: ' + request.url,
        'status': 404
    }
    return jsonify(message), 404

if __name__ == '__main__':
    app.run(debug=True)
@app.errorhandler(500)
def server_error(error=None):
    message = {
        'message': 'Error interno del servidor',
        'status': 500
    }
    return jsonify(message), 500

# Maneja errores generales
@app.errorhandler(Exception)
def handle_exception(e):
    return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

    app.run(debug=True)if __name__ == '__main__':
    try:
        app.run(debug=True)
    except ServerSelectionTimeoutError as e:
        print(f"Error de conexión a MongoDB: {e}")
