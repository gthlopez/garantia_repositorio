package dominio;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;
import dominio.repositorio.RepositorioProducto;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";

    public static final String EL_PRODUCTO_NO_CUENTA_CON_GARANTIA = "Este producto no cuenta con garantía extendida";
    
    public static final String EL_PRODUCTO_NO_EXISTE = "El producto no existe";
    
    public static final int MAXIMO_VOCALES_NOMBRE_PRODUCTO = 3;
    
    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;
    }

    public void generarGarantia(String codigo, String nombreCliente, Date fechaSolicitud) {

    	if (tieneGarantia(codigo)) {
    		throw new GarantiaExtendidaException(Vendedor.EL_PRODUCTO_TIENE_GARANTIA);
    	} 

    	Producto producto = repositorioProducto.obtenerPorCodigo(codigo);

    	if (Objects.isNull(producto)) {
    		throw new GarantiaExtendidaException(Vendedor.EL_PRODUCTO_NO_EXISTE);
    	}
    	
		if (Vendedor.MAXIMO_VOCALES_NOMBRE_PRODUCTO == Utilidades.contarVocales(producto.getNombre())) {
			throw new GarantiaExtendidaException(Vendedor.EL_PRODUCTO_NO_CUENTA_CON_GARANTIA);
		}

		int porcentajeGarantia = 10;
		int cantidadDiasGarantia = 100;
		Date fechaFinGarantia;
		
		if (500000D < producto.getPrecio()) {
			porcentajeGarantia = 20;
			cantidadDiasGarantia = 200;
			fechaFinGarantia = Utilidades.calcularFecha(fechaSolicitud, cantidadDiasGarantia, new int[] {Calendar.SUNDAY}, new int[] {Calendar.MONDAY});
		} else {
			fechaFinGarantia = Utilidades.calcularFecha(fechaSolicitud, cantidadDiasGarantia, new int[] {}, new int[] {});
		}

		double precioGarantia = producto.getPrecio() * (porcentajeGarantia / Utilidades.CIEN_POR_CIENTO);
		
		GarantiaExtendida garantia = new GarantiaExtendida(producto, fechaSolicitud, fechaFinGarantia, precioGarantia, nombreCliente);
		    	
    	repositorioGarantia.agregar(garantia);
    	
    }

    public boolean tieneGarantia(String codigo) {
    	
    	Producto producto = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
    	
        return Objects.nonNull(producto);
    }

}
