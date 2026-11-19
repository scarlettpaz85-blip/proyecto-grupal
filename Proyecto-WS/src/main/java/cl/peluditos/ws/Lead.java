
package cl.peluditos.ws;

/**
 * Interesado (lead) que consulta por un producto.
 */
public class Lead {

    public Integer id;
    public String nombre;
    public String telefono;
    public String correo;

    // Canal por el cual llegó el interesado
    public String canal;

    // Producto o servicio de interés
    public String interes;

    // Estado actual del lead
    public String estado;

    public String fechaRegistro;
    public Boolean registrado;

    // Identificador de la instancia del proceso en Flowable
    public String processInstanceId;
}
