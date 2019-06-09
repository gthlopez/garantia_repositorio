package dominio.integracion;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Vendedor;
import dominio.GarantiaExtendida;
import dominio.Producto;
import dominio.Utilidades;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
	
	private static final String CLIENTE_1 = "Marco Antonio López Gómez";
	
	private static final String COMPUTADOR_ASUS = "Asus Viv0";
	
	private static final double PRECIO_MENOR_500MIL = 499999.37D;
	
	private static final double PRECIO_MAYOR_500MIL = 505000.87D;
	
	private static final Date FECHA_ACTUAL = Calendar.getInstance().getTime();
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), CLIENTE_1, Calendar.getInstance().getTime());

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));

	}

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), CLIENTE_1, FECHA_ACTUAL);
		try {
			vendedor.generarGarantia(producto.getCodigo(), CLIENTE_1, FECHA_ACTUAL);
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}
	
	@Test
	public void productoNoCuentaConGarantiaExtendida() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_ASUS).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		try {
			vendedor.generarGarantia(producto.getCodigo(), CLIENTE_1, FECHA_ACTUAL);
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_NO_CUENTA_CON_GARANTIA, e.getMessage());
		}
	}
	
	@Test
	public void productoConGarantiaExtendidaDatos() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO_MENOR_500MIL).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		vendedor.generarGarantia(producto.getCodigo(), CLIENTE_1, FECHA_ACTUAL);
		
		GarantiaExtendida garantiaExtendida = repositorioGarantia.obtener(producto.getCodigo());
		
		Assert.assertEquals(producto.getCodigo(), garantiaExtendida.getProducto().getCodigo());
		Assert.assertEquals(CLIENTE_1, garantiaExtendida.getNombreCliente());
		Assert.assertEquals(FECHA_ACTUAL, garantiaExtendida.getFechaSolicitudGarantia());
	}
	
	@Test
	public void productoConGarantiaExtendidaMenor500Mil() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO_MENOR_500MIL).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		vendedor.generarGarantia(producto.getCodigo(), CLIENTE_1, FECHA_ACTUAL);
		
		GarantiaExtendida garantiaExtendida = repositorioGarantia.obtener(producto.getCodigo());
		
		Assert.assertEquals(PRECIO_MENOR_500MIL * .1, garantiaExtendida.getPrecioGarantia(), .99);
		
		Assert.assertEquals(Utilidades.calcularFecha(FECHA_ACTUAL, 100, new int[] {}, new int[] {}), garantiaExtendida.getFechaFinGarantia());
	}
	
	@Test
	public void productoConGarantiaExtendidaMayor500Mil() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO_MAYOR_500MIL).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		vendedor.generarGarantia(producto.getCodigo(), CLIENTE_1, FECHA_ACTUAL);
		
		GarantiaExtendida garantiaExtendida = repositorioGarantia.obtener(producto.getCodigo());
		
		Assert.assertEquals(PRECIO_MAYOR_500MIL * .2, garantiaExtendida.getPrecioGarantia(), .99);
		
		Assert.assertEquals(Utilidades.calcularFecha(FECHA_ACTUAL, 200, new int[] {Calendar.MONDAY}, new int[] {Calendar.SUNDAY}), garantiaExtendida.getFechaFinGarantia());
	}
}
