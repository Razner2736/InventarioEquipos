package inventariored;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

// Clase principal de la ventana donde se muestra y administra el inventario
public class interfaz extends javax.swing.JFrame {
    
    private  String usuariosesion;

    // ==============================
    // CONSTRUCTOR DE LA INTERFAZ
    // ==============================
    public interfaz() throws ClassNotFoundException {

        setTitle("Inventario de Equipos de Red"); // Título de la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Terminar programa al cerrar
        initComponents(); // Carga y coloca todos los componentes (swing)
        sesion.setText("");

        try {
            mostrarDatos(); // Cargar la tabla con datos desde la BD
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public interfaz(String usuariosesion){
        initComponents();
        this.usuariosesion= usuariosesion;
        
        sesion.setText(usuariosesion);
    }

    // ===========================================================
    // MÉTODO PARA AGREGAR UN EQUIPO A LA BASE DE DATOS
    // ===========================================================
    private void agregarEquipo() throws ClassNotFoundException {

        final int longitud = 5; // Longitud mínima permitida para cada campo

        try (Connection con = conexion.getConexion()) {

            // 1. Validar campos vacíos
            if (nom.getText().isEmpty() || tipo.getText().isEmpty() ||
                marca.getText().isEmpty() || modelo.getText().isEmpty() ||
                ip.getText().isEmpty() || ubicacion.getText().isEmpty()) {

                JOptionPane.showMessageDialog(this, "Llena todos los campos antes de agregar un equipo.");
                return;
            }

            // 2. Validar longitud mínima
            if (nom.getText().length() <= longitud ||
                tipo.getText().length() <= longitud ||
                marca.getText().length() <= longitud ||
                modelo.getText().length() <= longitud ||
                ip.getText().length() <= longitud ||
                ubicacion.getText().length() <= longitud) {

                // Detectar exactamente cuál campo falla
                String campovacio = "";
                if (nom.getText().length() <= longitud) campovacio = "Nombre";
                else if (tipo.getText().length() <= longitud) campovacio = "Tipo";
                else if (marca.getText().length() <= longitud) campovacio = "Marca";
                else if (modelo.getText().length() <= longitud) campovacio = "Modelo";
                else if (ip.getText().length() <= longitud) campovacio = "IP";
                else if (ubicacion.getText().length() <= longitud) campovacio = "Área";

                JOptionPane.showMessageDialog(this,
                        "El campo '" + campovacio + "' debe tener más de " +
                        longitud + " caracteres");
                return;
            }

            // 3. Insertar registro con PreparedStatement
            String sql = "INSERT INTO equipos (nombre, tipo, marca, modelo, ip, ubicacion) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Asignar valores a cada ?
            ps.setString(1, nom.getText());
            ps.setString(2, tipo.getText());
            ps.setString(3, marca.getText());
            ps.setString(4, modelo.getText());
            ps.setString(5, ip.getText());
            ps.setString(6, ubicacion.getText());

            ps.executeUpdate(); // Ejecutar consulta

            // Obtener ID generado (autoincrement)
            ResultSet rs = ps.getGeneratedKeys();
            int idGenerado = 0;
            if (rs.next()) idGenerado = rs.getInt(1);

            rs.close();
            ps.close();

            // Mostrar resumen con todos los datos recién agregados
            String resumen = String.format(
                "Equipo agregado correctamente:\n\n" +
                "ID: %d\n" +
                "Nombre: %s\n" +
                "Tipo: %s\n" +
                "Marca: %s\n" +
                "Modelo: %s\n" +
                "IP: %s\n" +
                "Área: %s",
                idGenerado,
                nom.getText(),
                tipo.getText(),
                marca.getText(),
                modelo.getText(),
                ip.getText(),
                ubicacion.getText()
            );

            JOptionPane.showMessageDialog(this, resumen, "Resumen del nuevo equipo", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(this, "Se agregó el equipo");

            limpiar(); // Limpiar campos visuales
            mostrarDatos(); // Refrescar tabla

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar equipo: " + e.getMessage());
        }
    }

    // ===========================================================
    // MÉTODO PARA ELIMINAR EQUIPO
    // ===========================================================
    public void eliminarEquipo() throws ClassNotFoundException {

        int filaSeleccionada = tabla.getSelectedRow(); // Fila seleccionada

        if (filaSeleccionada == -1) { // Si no seleccionó nada
            JOptionPane.showMessageDialog(this, "Seleccione un equipo para eliminar.");
            return;
        }

        int id = Integer.parseInt(tabla.getValueAt(filaSeleccionada, 0).toString());

        // Preguntar confirmación antes de eliminar
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar el equipo con ID: " + id + "?",
            "Confirmación de eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            try (Connection con = conexion.getConexion()) {

                String sql = "DELETE FROM equipos WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, id);

                int filasAfectadas = ps.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "Equipo eliminado correctamente.");
                    mostrarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el equipo con ese ID.");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar equipo: " + e.getMessage());
            }

        } else {
            JOptionPane.showMessageDialog(this, "Eliminación cancelada.");
        }
    }

    // ===========================================================
    // MÉTODO PARA CARGAR Y MOSTRAR LA TABLA DE EQUIPOS
    // ===========================================================
    private void mostrarDatos() throws ClassNotFoundException {

        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "nombre", "tipo", "marca", "modelo", "ip", "ubicacion"}, 0
        );

        try (Connection con = conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM equipos")) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("tipo"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getString("ip"),
                    rs.getString("ubicacion")
                });
            }

            tabla.setModel(modelo); // Asignar modelo actualizado a la tabla

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar los datos: " + e.getMessage());
        }
    }

    // ===========================================================
    // MÉTODO PARA LIMPIAR CAMPOS
    // ===========================================================
    private void limpiar() {
        nom.setText("");
        tipo.setText("");
        marca.setText("");
        modelo.setText("");
        ip.setText("");
        ubicacion.setText("");
    }

    // ===========================================================
    // MÉTODO PARA VOLVER AL LOGIN
    // ===========================================================
    public void cerrar() {

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estas seguro de querer cerrar el gestor?",
                "Cerrar programa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            this.dispose();           // Cerrar ventana actual
            new login().setVisible(true); // Regresar al login
        }
    }


    
    
    
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tipo = new javax.swing.JTextField();
        marca = new javax.swing.JTextField();
        nom = new javax.swing.JTextField();
        modelo = new javax.swing.JTextField();
        ip = new javax.swing.JTextField();
        ubicacion = new javax.swing.JTextField();
        agregar = new javax.swing.JButton();
        mostrar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        limpiar = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        sesion = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 102));
        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel1.setFont(new java.awt.Font("Bauhaus 93", 3, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("REGISTRO DE EQUIPOS DE COMPUTO");

        tipo.setBackground(new java.awt.Color(204, 255, 204));
        tipo.setForeground(new java.awt.Color(51, 51, 51));
        tipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoActionPerformed(evt);
            }
        });

        marca.setBackground(new java.awt.Color(204, 255, 204));

        nom.setBackground(new java.awt.Color(204, 255, 204));

        modelo.setBackground(new java.awt.Color(204, 255, 204));

        ip.setBackground(new java.awt.Color(204, 255, 204));

        ubicacion.setBackground(new java.awt.Color(204, 255, 204));

        agregar.setBackground(new java.awt.Color(217, 219, 219));
        agregar.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        agregar.setText("Agregar");
        agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarActionPerformed(evt);
            }
        });

        mostrar.setBackground(new java.awt.Color(0, 248, 93));
        mostrar.setText("Mostrar equipos almacenados");
        mostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nombre:");

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Tipo:");

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Marca:");

        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Área:");

        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Modelo:");

        jLabel7.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("IP:");

        tabla.setBackground(new java.awt.Color(143, 188, 206));
        tabla.setFont(new java.awt.Font("Imprint MT Shadow", 3, 14)); // NOI18N
        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Tipo", "Marca", "Modelo", "IP", "Área"
            }
        ));
        jScrollPane1.setViewportView(tabla);

        limpiar.setBackground(new java.awt.Color(217, 219, 219));
        limpiar.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        limpiar.setText("Limpiar campos");
        limpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(217, 219, 219));
        jButton1.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jButton1.setText("Eliminar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(204, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jButton2.setText("Cerrar gestor de quipos");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Usuario en sesión:");

        sesion.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        sesion.setForeground(new java.awt.Color(255, 255, 255));
        sesion.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 27, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(mostrar)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addGap(48, 48, 48)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(ubicacion)
                                            .addComponent(ip, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel2))
                                        .addGap(48, 48, 48)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(nom, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tipo, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(marca, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(modelo, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(73, 73, 73)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(114, 114, 114))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sesion, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(agregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(limpiar)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addComponent(jLabel2))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(sesion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(nom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(marca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(modelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(ubicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mostrar)
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agregar)
                    .addComponent(jButton1)
                    .addComponent(limpiar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 4, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoActionPerformed
        
    }//GEN-LAST:event_tipoActionPerformed

    private void agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarActionPerformed
        try {
            agregarEquipo();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_agregarActionPerformed

    private void limpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_limpiarActionPerformed

    private void mostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrarActionPerformed
        try {
            mostrarDatos();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mostrarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            eliminarEquipo();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        cerrar();
    }//GEN-LAST:event_jButton2ActionPerformed

   
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregar;
    private javax.swing.JTextField ip;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton limpiar;
    private javax.swing.JTextField marca;
    private javax.swing.JTextField modelo;
    private javax.swing.JButton mostrar;
    private javax.swing.JTextField nom;
    private javax.swing.JLabel sesion;
    private javax.swing.JTable tabla;
    private javax.swing.JTextField tipo;
    private javax.swing.JTextField ubicacion;
    // End of variables declaration//GEN-END:variables
}
