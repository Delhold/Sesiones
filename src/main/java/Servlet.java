import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/*
 * *Nombre de los programadores: Dario Verdezoto
 * *Materia: Programacion 2
 * *Fecha: 27/05/2024
 * *Detalle: Sesiones Factura con productos
 * *Version: 2
 */

//GENEREMOS NUESTRO PATH PARA PODER LLAMARLO
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
    // Sobrescribir el método doGet para manejar solicitudes GET
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Establecer el tipo de contenido de la respuesta
        resp.setContentType("text/html;charset=UTF-8");
        // Obtener el escritor para enviar la respuesta HTML
        PrintWriter out = resp.getWriter();

        // OBTENER DE SESION DEL USUARIO
        HttpSession session = req.getSession();
        // OBTENER LA LISTA DE PRODUCTOS QUE EL URUARIO PIDIO
        List<Prod> productos = (List<Prod>) session.getAttribute("productos");

        // LA RESPUESTA SERA EN HTML EN UNA TABLA QUE MOSTRARA
        //CANTIDAD, NOMBRE, VALOR Y TOTAL
        out.print("<!DOCTYPE html>");
        out.print("<html>");
        out.print("<head>");
        out.printf("<meta charset=\"utf-8\">");
        out.printf("<title>Factura</title>");
        out.print("</head>");
        out.print("<body>");

        // EN CASO QUE NO EXISTAN PRODUCTOS LO QUE HARA ESTO ES MANDAR
        //UN MENSAJE QUE NO AH INGRESADO PRODUCOTS
        if (productos == null || productos.isEmpty()) {
            out.print("<h1>No hay productos en la factura</h1>");
        } else {
            // MOSTRAR A CONTINUACION LA TABLA CON LOS PRODUCTOS.. Y COSAS
            //QUE HAYA SOLICITADO EL USUARIO
            out.print("<h1>Factura de Computron</h1>");
            out.print("<table border='1'>");
            out.print("<tr><th>Nombre del Producto</th><th>Cantidad de Producto</th><th>Valor Unitario de Cada Producto</th><th>Valor Total</th></tr>");
            double totalFactura = 0.0;
            for (Prod producto : productos) {
                //OPERACION DE LA CANTIDAD POR EL VALOR DEL PRODUCTO
                double valorTotal = producto.getCantidad() * producto.getValorUnitario();
                totalFactura += valorTotal;
                //CADA CADENA DE VALORES QUE TENGA DE TIPO:
                //STRING = %s (String)
                //NUMERO ENTERO = %d (int)
                //NUMERO FLOTANTE = %.2f (Double o float)
                //TOTAL DEL VALOR = %.2f (Double o float)
                out.printf("<tr><td>%s</td><td>%d</td><td>%.2f</td><td>%.2f</td></tr>",
                        producto.getNombre(), producto.getCantidad(), producto.getValorUnitario(), valorTotal);
            }
            // TOTAL DE LA FACTURA REALIZADO LA OPERACION
            out.printf("<tr><td colspan='3'>Total Factura</td><td>%.2f</td></tr>", totalFactura);
            out.print("</table>");
        }

        out.print("</body>");
        out.print("</html>");
    }

    // SOBRESCRIBIMOS EL METODO POST PARA PODER UTILIZARLO
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // VOLVEMOS A OBTENER LA SESION DEL USUARIO
        HttpSession session = req.getSession();

        // OBTENER LOS DATOS DE LOS PRODUCTOS DESDE EL FORMULARIO
        String nombreProducto = req.getParameter("nombreProducto");
        int cantidadProducto = Integer.parseInt(req.getParameter("cantidadProducto"));
        double valorUnitario = Double.parseDouble(req.getParameter("valorUnitario"));

        // OBTENER LA LISTA DE LOS PRODUCTOS DE LA SESION
        List<Prod> productos = (List<Prod>) session.getAttribute("productos");
        if (productos == null) {
            // SI LA LISTA DE PRODUCTOS NO EXISTE
            //SE CREARA UNA NUEVA LISTA
            productos = new ArrayList<>();
        }

        boolean productoExistente = false;
        // VERIFICAMOS SI ESQUE YA EXISTEN PRODUCTOS
        for (Prod producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombreProducto)) {
                // SI EL PRODUCTO EXISTE ACTUALIZA LA LISTA
                producto.setCantidad(producto.getCantidad() + cantidadProducto);
                productoExistente = true;
                break;
            }
        }

        if (!productoExistente) {
            // SI EL PRODUCTO NO EXISTE
            // CREARA UN NUEVO PRODUCTO Y LO VA A AGREGAR A LA LISTA
            Prod nuevoProducto = new Prod(nombreProducto, cantidadProducto, valorUnitario);
            productos.add(nuevoProducto);
        }

        // GUARDA LA LISTA ACTUALIZADA EN LA SESION VIGENTE
        session.setAttribute("productos", productos);

        // VUELVE A REDIRIGIR AL SERVLET CON LA LISTA ACTUALIZADA
        // Y LA FACTURA YA HECHA
        resp.sendRedirect("Servlet");
    }
}

// CLASE "PRODUCT" PARA REPRESENTAR UN PRODUCTO EN LA FACTURA
class Prod {
    private String nombre;
    private int cantidad;
    private double valorUnitario;

    // CONSTRUCTOR PARA INICIALIZAR LOS PRODUCTOS
    public Prod(String nombre, int cantidad, double valorUnitario) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.valorUnitario = valorUnitario;
    }

    // METODO GETTER PARA OBTENER LOS PRODUCTOS
    //SUSTITUYE AL SIMPLIFICADOR "%s"CON EL NOMBRE DEL PRODUCTO
    public String getNombre() {
        return nombre;
    }
    //SUSTITUYE AL SIMPLIFICADOR "%s" CON LA CANTIDAD DEL PRODUCTO
    public int getCantidad() {
        return cantidad;
    }
    //SUSTITUYE AL SIMPLIFICADOR "%.2f" CON EL VALOR UNITARIO DEL PRODUCTO
    public double getValorUnitario() {
        return valorUnitario;
    }
    //SUSTITUYE AL SIMPLIFICADOR "%.2f" CON EL TOTAL DEL PRODUCTO
    // Método setter para actualizar la cantidad del producto
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    //mediante sesiones yo tengo un pequeño formulario : nombre del producto, cantidad del producto, valor unitario y valor total.
//y mediante sesiones mostrar la informacion de una factura
    //Servlet tiene que procesar toda la tipo factura
}
