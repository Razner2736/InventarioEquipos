package inventariored;

import javax.swing.*;
import java.sql.*;

// Clase encargada del formulario de inicio de sesión (login)
public class login extends javax.swing.JFrame {
    
    // Constructor principal del formulario
    public login() {
        setTitle("Inicio de Sesión - Inventario de Equipos de Red"); // Título de la ventana
        setSize(400, 250);                                           // Tamaño de la ventana
        setLocationRelativeTo(null);                                 // Centrar ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);                     // Salir al cerrar
        setLayout(null);                                             // Layout manual
        initComponents();                                            // Inicializar componentes visuales
    }
    
    
    // ============================================================
    // MÉTODO: validarUsuario()
    // Se ejecuta al presionar el botón "Iniciar Sesión"
    // Verifica que el usuario exista en la base de datos
    // ============================================================
    private void validarUsuario() {

        String usuario = usu.getText();                 // Obtener texto del campo "usuario"
        String contraseña = new String(pass.getPassword()); // Obtener texto del campo "contraseña"

        // Validación básica: ambos campos deben estar llenos
        if (usuario.isEmpty() || contraseña.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes llenar ambos campos.");
            return; 
        } 
        
        try (Connection con = conexion.getConexion()) { // Obtener conexión desde la clase conexion
            
            // Consulta SQL para buscar usuario y contraseña
            String sql = "SELECT * FROM usuarios WHERE usuario=? AND contrasena=?";
            PreparedStatement ps = con.prepareStatement(sql);
            
            // Reemplazar parámetros del query
            ps.setString(1, usuario);
            ps.setString(2, contraseña);

            ResultSet rs = ps.executeQuery(); // Ejecutar consulta

            // Si encuentra registro → login exitoso
            if (rs.next()) {
                String usuarioIngresado = usu.getText();
                interfaz ventana = new interfaz(usuarioIngresado);
                ventana.setVisible(true);
                this.dispose();
                JOptionPane.showMessageDialog(this, "Bienvenido " + usuario);
                
                                   // Cierra ventana login
                //new interfaz().setVisible(true);  // Abre la interfaz principal
                
            } else { // Si no encuentra usuario o contraseña incorrecta
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
                
                // Limpiar campos
                usu.setText("");
                pass.setText("");
                usu.requestFocus(); // Poner cursor en "usuario"
            }

        } catch (Exception ex) {
            // Error si falla la conexión o el SQL
            JOptionPane.showMessageDialog(this, "Error en la conexión: " + ex.getMessage());
        }
    }


    // ============================================================
    // MÉTODO: cerrar()
    // Muestra un diálogo para confirmar si el usuario desea salir
    // ============================================================
    public void cerrar() {
        
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Estas seguro de querer cerrar el gestor?", // Mensaje
            "Cerrar programa",                           // Título
            JOptionPane.YES_NO_OPTION,                   // Botones SI/NO
            JOptionPane.WARNING_MESSAGE                  // Icono de advertencia
        );
        
        // Si el usuario confirma "Sí"
        if (respuesta == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Cerrando el programa...");
            System.exit(0); // Termina la aplicación
        }
    }


   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        usu = new javax.swing.JTextField();
        pass = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        salir = new javax.swing.JButton();
        ingreso = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 204, 204));

        jPanel1.setBackground(new java.awt.Color(137, 88, 72));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Book Antiqua", 0, 12)); // NOI18N
        jLabel1.setText("Usuario:");

        usu.setBackground(new java.awt.Color(255, 255, 204));
        usu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        usu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usuActionPerformed(evt);
            }
        });

        pass.setBackground(new java.awt.Color(255, 255, 204));
        pass.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Book Antiqua", 0, 12)); // NOI18N
        jLabel2.setText("Constraseña:");

        salir.setBackground(new java.awt.Color(153, 0, 51));
        salir.setFont(new java.awt.Font("Bookman Old Style", 3, 12)); // NOI18N
        salir.setForeground(new java.awt.Color(255, 255, 255));
        salir.setText("Salir del sistema");
        salir.setBorder(new javax.swing.border.MatteBorder(null));
        salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salirActionPerformed(evt);
            }
        });

        ingreso.setBackground(new java.awt.Color(0, 51, 51));
        ingreso.setFont(new java.awt.Font("Bookman Old Style", 3, 12)); // NOI18N
        ingreso.setForeground(new java.awt.Color(255, 255, 255));
        ingreso.setText("Ingresar");
        ingreso.setBorder(new javax.swing.border.MatteBorder(null));
        ingreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ingresoActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Cambria", 3, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Inicio de sesión");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(usu, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(salir, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ingreso, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel3)
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(usu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(salir)
                    .addComponent(ingreso))
                .addGap(68, 68, 68))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ingresoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ingresoActionPerformed
       validarUsuario();
    }//GEN-LAST:event_ingresoActionPerformed

    private void salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salirActionPerformed
        cerrar();
    }//GEN-LAST:event_salirActionPerformed

    private void usuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usuActionPerformed

    
    public static void main(String args[]) {
       
        java.awt.EventQueue.invokeLater(() -> new login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ingreso;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField pass;
    private javax.swing.JButton salir;
    private javax.swing.JTextField usu;
    // End of variables declaration//GEN-END:variables
}
