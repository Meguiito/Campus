from flask import Flask, request, jsonify
from flask_pymongo import PyMongo
import bcrypt
from pymongo.errors import PyMongoError, ServerSelectionTimeoutError

app = Flask(__name__)

# Configura la URI para conectar al cluster de MongoDB
app.config['MONGO_URI'] = 'mongodb+srv://Martin:wnL9Q2Ruwf4WJGE0@campusfit.xih68.mongodb.net/CampusFIT_DB?retryWrites=true&w=majority'

mongo = PyMongo(app)

@app.route('/users', methods=['POST'])
def create_user():
    try:
        username = request.json.get("username")
        rut = request.json.get("rut")
        password = request.json.get("password")
        email = request.json.get("email")

        # Verifica que los campos no estén vacíos
        if username and password and email:
            salt = bcrypt.gensalt()
            hashed_password = bcrypt.hashpw(password.encode('utf-8'), salt)
            # Inserta en la colección 'Usuarios' de la base de datos 'CampusFIT_DB'
            result = mongo.db.Usuarios.insert_one(
                {'rut': rut, 'username': username, 'password': hashed_password.decode('utf-8'), 'email': email}
            )
            response = {
                'id': str(result.inserted_id),
                'rut': rut,
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
    try:
        # Elimina el usuario de la colección 'Usuarios'
        result = mongo.db.Usuarios.delete_one({'username': username})

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
    try:
        email = request.json.get("email")
        password = request.json.get("password")

        user = mongo.db.Usuarios.find_one({'email': email})
        
        if user and bcrypt.checkpw(password.encode('utf-8'), user['password'].encode('utf-8')):
            return jsonify({"message": "Verificación exitosa"}), 200
        else:
            return jsonify({"error": "Usuario o contraseña incorrectos"}), 401
    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/espacios', methods=['GET'])
def get_espacios():
    try:
        canchas = mongo.db.Espacios.find({}, {"nombre": 1, "_id": 0})
        lista_canchas = [cancha['nombre'] for cancha in canchas]

        return jsonify(lista_canchas), 200
    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500


@app.route('/reservas', methods=['POST'])
def crear_reserva():
    try:
        nombre = request.json.get("nombre")
        rut = request.json.get("rut")
        carrera = request.json.get("carrera")
        cancha = request.json.get("cancha")
        duracion = request.json.get("duracion")

        if nombre and rut and carrera and cancha and duracion:
            reserva = {
                'nombre': nombre,
                'rut': rut,
                'carrera': carrera,
                'cancha': cancha,
                'duracion': duracion
            }
            result = mongo.db.Reservas.insert_one(reserva)
            return jsonify({"message": "Reserva creada exitosamente", "id": str(result.inserted_id)}), 201
        else:
            return jsonify({"error": "Todos los campos son obligatorios"}), 400

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

if __name__ == '__main__':
    try:
        app.run(debug=True)
    except ServerSelectionTimeoutError as e:
        print(f"Error de conexión a MongoDB: {e}")


