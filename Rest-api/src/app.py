from flask import Flask, request, jsonify
from flask_pymongo import PyMongo
import bcrypt
from pymongo.errors import PyMongoError, ServerSelectionTimeoutError
from bson import ObjectId
from functools import wraps

app = Flask(__name__)

app.config['MONGO_URI'] = 'mongodb+srv://Martin:wnL9Q2Ruwf4WJGE0@campusfit.xih68.mongodb.net/CampusFIT_DB?retryWrites=true&w=majority'

mongo = PyMongo(app)

# Decorador para verificar si el usuario es administrador
def admin_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        username = request.json.get("username") 
        user = mongo.db.Usuarios.find_one({'username': username})

        if user and user.get('rol') == 'admin':
            return f(*args, **kwargs)  
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
        rol = request.json.get("rol", "usuario")  

        if username and password and email:
            salt = bcrypt.gensalt()
            hashed_password = bcrypt.hashpw(password.encode('utf-8'), salt)
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

# Eliminar un usuario (admins)
@app.route('/users/<username>', methods=['DELETE'])
@admin_required
def delete_user(username):
    try:
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

# Obtener lista de canchas de la base de datos
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
        mes = request.json.get("mes")
        dia = request.json.get("dia")

        if nombre and rut and carrera and cancha and duracion and mes and dia:
            reserva = {
                'nombre': nombre,
                'rut': rut,
                'carrera': carrera,
                'cancha': cancha,
                'duracion': duracion,
                'mes': mes,
                'dia': dia

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
        reserva_id = request.json.get("_id", {}).get("$oid")

        if not reserva_id:
            return jsonify({"error": "El ID de la reserva es obligatorio o está mal formateado"}), 400

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

# Manejo errores generales
@app.errorhandler(Exception)
def handle_exception(e):
    return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/articulos/<articulo_id>', methods=['PUT'])
@admin_required
def editar_articulo(articulo_id):
    try:
        nombre = request.json.get("nombre")
        descripcion = request.json.get("descripcion")
        precio = request.json.get("precio")

        if nombre and descripcion and precio:
            result = mongo.db.Articulos.update_one(
                {'_id': ObjectId(articulo_id)},
                {'$set': {'nombre': nombre, 'descripcion': descripcion, 'precio': precio}}
            )
            if result.matched_count > 0:
                return jsonify({"message": "Artículo actualizado correctamente"}), 200
            else:
                return jsonify({"error": "Artículo no encontrado"}), 404
        else:
            return jsonify({"error": "Todos los campos son obligatorios"}), 400
    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

# Eliminar un artículo (solo administradores)
@app.route('/articulos/<articulo_id>', methods=['DELETE'])
@admin_required
def eliminar_articulo(articulo_id):
    try:
        result = mongo.db.Articulos.delete_one({'_id': ObjectId(articulo_id)})

        if result.deleted_count > 0:
            return jsonify({"message": "Artículo eliminado exitosamente"}), 200
        else:
            return jsonify({"error": "Artículo no encontrado"}), 404
    except PyMongoError as e:
        return jsonify({"error": f"Error en la base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(debug=True)
