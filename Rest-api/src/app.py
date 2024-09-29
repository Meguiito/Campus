from flask import Flask, request, jsonify
from flask_pymongo import PyMongo
import bcrypt
from pymongo.errors import PyMongoError, ServerSelectionTimeoutError
from bson import ObjectId
from functools import wraps

app = Flask(__name__)

# Configura la URI para conectar al cluster de MongoDB
app.config['MONGO_URI'] = 'mongodb+srv://Martin:wnL9Q2Ruwf4WJGE0@campusfit.xih68.mongodb.net/CampusFIT_DB?retryWrites=true&w=majority'

mongo = PyMongo(app)

# Decorador para verificar si el usuario es administrador
def admin_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        username = request.json.get("username")  # Obtener el nombre del usuario
        user = mongo.db.Usuarios.find_one({'username': username})

        if user and user.get('rol') == 'admin':
            return f(*args, **kwargs)  # Si es admin, ejecuta la función
        else:
            return jsonify({"error": "Acceso denegado. Solo los administradores pueden realizar esta acción."}), 403
    return decorated_function

# Crear un nuevo usuario
@app.route('/users', methods=['POST'])
def create_user():
    try:
        username = request.json.get("username")
        rut = request.json.get("rut")
        password = request.json.get("password")
        email = request.json.get("email")
        rol = request.json.get("rol", "usuario")  # 'usuario' por defecto

        # Verifica que los campos no estén vacíos
        if username and password and email:
            salt = bcrypt.gensalt()
            hashed_password = bcrypt.hashpw(password.encode('utf-8'), salt)
            # Inserta en la colección 'Usuarios' de la base de datos 'CampusFIT_DB'
            result = mongo.db.Usuarios.insert_one(
                {'rut': rut, 'username': username, 'password': hashed_password.decode('utf-8'), 'email': email, 'rol': rol}
            )
            response = {
                'id': str(result.inserted_id),
                'rut': rut,
                'username': username,
                'email': email,
                'rol': rol
            }
            return jsonify(response), 201 
        else:
            return jsonify({"error": "Todos los campos son obligatorios"}), 400

    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

# Eliminar un usuario (solo administradores)
@app.route('/users/<username>', methods=['DELETE'])
@admin_required
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

# Verificar usuario
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

# Obtener lista de espacios (canchas)
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

# Crear una reserva
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
    
# Cancelar una reserva
@app.route('/reservas/cancelar', methods=['DELETE'])
def cancelar_reserva():
    try:
        # Recibe el JSON que contiene el _id dentro de "$oid"
        reserva_id = request.json.get("_id", {}).get("$oid")

        if not reserva_id:
            return jsonify({"error": "El ID de la reserva es obligatorio o está mal formateado"}), 400

        # Convertir el id a ObjectId para eliminar
        result = mongo.db.Reservas.delete_one({'_id': ObjectId(reserva_id)})

        if result.deleted_count > 0:
            return jsonify({"message": "Reserva cancelada exitosamente"}), 200
        else:
            return jsonify({"error": "Reserva no encontrada"}), 404

    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

# Cambiar el rol de un usuario (solo administradores)
@app.route('/users/<username>/rol', methods=['PUT'])
@admin_required
def update_user_role(username):
    try:
        new_role = request.json.get("rol")
        if new_role:
            result = mongo.db.Usuarios.update_one(
                {'username': username}, {'$set': {'rol': new_role}}
            )

            if result.matched_count > 0:
                return jsonify({"message": f"Rol de {username} actualizado a {new_role}"}), 200
            else:
                return jsonify({"error": "Usuario no encontrado"}), 404
        else:
            return jsonify({"error": "El rol es obligatorio"}), 400

    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

# Manejo de errores 404
@app.errorhandler(404)
def not_found(error=None):
    message = {
        'message': 'Recurso no encontrado: ' + request.url,
        'status': 404
    }
    return jsonify(message), 404

# Manejo de errores 500
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
