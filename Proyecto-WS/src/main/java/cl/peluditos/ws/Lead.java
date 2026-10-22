package cl.peluditos.ws;

/** Interesado (lead) que consulta por un producto. */
public class Lead {
    public Integer id;
    public String nombre;
    public String telefono;
    public String correo;
    public String canal;      // instagram, whatsapp, facebook, tiktok, web, telefono
    public String interes;    // producto consultado
    public String estado;     // NUEVO, EN_SEGUIMIENTO, CONVERTIDO, NO_CONVERTIDO
    public String fechaRegistro;
    public Boolean registrado;
}
