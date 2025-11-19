package inventariored;

import java.sql.*;

// Clase encargada de manejar la conexión a MySQL y la creación de la BD/tablas
public class conexion {

    // Datos de conexión
    private static final String URL= "jdbc:mysql://localhost:3306/"; // URL base del servidor MySQL
    private static final String db = "inventario_equipos";            // Nombre de la base de datos
    private static final String user = "root";                        // Usuario de MySQL
    private static final String pass = "";                            // Contraseña del usuario MySQL
    
    // Método que devuelve un objeto Connection ya conectado a la BD
    public static Connection getConexion() throws ClassNotFoundException{
        
        Connection con = null; // Se inicializa la conexión como nula
        
        try {
            // Registrar el driver de MySQL (obligatorio para trabajar con JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            // Nota: se requiere tener agregado el MySQL Connector al proyecto
            
            // Conexión inicial SOLO al servidor (no a una base de datos específica)
            con = DriverManager.getConnection(URL, user, pass);
            System.out.println("Conexión exitosa.");
            
            // Crear Statement para ejecutar SQL
            Statement st = con.createStatement();
            
            // Crear la base de datos si no existe
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + db);
            st.close();
            
            // Conectarse ahora sí a la base "inventario_equipos"
            con = DriverManager.getConnection(URL + db, user, pass);
            
            // Crear tabla "equipos" si no existe
            Statement st2 = con.createStatement();
            String crearTabla = " CREATE TABLE IF NOT EXISTS equipos"
                    + " (id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombre VARCHAR(50),"
                    + "tipo VARCHAR(50),"
                    + "marca VARCHAR(50),"
                    + "modelo VARCHAR(50),"
                    + "ip VARCHAR(50),"
                    + "ubicacion VARCHAR(50)"
                    + ");";
            st2.executeUpdate(crearTabla);
            
            // Crear tabla "usuarios"
            Statement st3 = con.createStatement();
            String tablaUsuarios = " CREATE TABLE IF NOT EXISTS usuarios"
                    + " (id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "usuario VARCHAR(50) UNIQUE,"           // usuario único
                    + "contrasena VARCHAR(50) UNIQUE);";      // contraseña única (NO recomendable)
            st3.executeUpdate(tablaUsuarios);
            
            // Insertar usuarios predeterminados
            // Nota: esto dará error si ya existen por la restricción UNIQUE
            Statement st4 = con.createStatement();
            String insert =" INSERT INTO usuarios(usuario,contrasena)"
                    + "VALUES "
                    + "('admin','12345'),"
                    + "('raziel','razner2736'),"
                    + "('monse','monmon'),"
                    + "('valval','valval'),"
                    +"('invitado','00000');";
            st4.executeUpdate(insert);
            
            System.out.println("Base de datos y tablas listas.");
           
        } catch (SQLException e) {
            // Si ocurre algún error en la conexión o SQL
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Retorna la conexión para que otras clases la usen
        return con;
    }

    // Método placeholder (está de adorno)
    static PreparedStatement prepareStatement(String update_equipos_red_SET_nombre_ip_ubicacio) {
        throw new UnsupportedOperationException("valio papoi");
    }
}
    

  
    


