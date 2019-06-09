package dominio;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.IntStream;

public final class Utilidades {

	public static final String EXP_REG_VOCALES = "[^AEIOUaeiou����������]";
	
	public static final double CIEN_POR_CIENTO = 100D;
	
	private Utilidades() {}
	
	/**
	 * Cuenta la cantidad de vocales que tiene una palabra.
	 * 
	 * @param cadena Cadena a determinar 
	 * @return Devuelve en valor entero la cantidad de vocales de una cadena
	 */
	public static int contarVocales(String cadena) {
		
	    int totalVocales = cadena.replaceAll(Utilidades.EXP_REG_VOCALES, "").length();
	    
	    return totalVocales;
	}
	
	/**
	 * Calcula la fecha final a partir de la fecha original, la cantidad de d�as dados, 
	 * los d�as a no contar y si hay d�a inhabil determina el siguiente d�a hab�l.
	 * 
	 * @param fechaOriginal Fecha original
	 * @param cantidadDias Cantidad de d�as a calcular
	 * @param diaSemanaNoContable Array con los d�as a no contar
	 * @param diaSemanaInHabiles Array con los d�as que son inh�biles, por tanto se saca el siguiente d�a hab�l
	 * @return Fecha finalmente calculada
	 * @author ELKIN
	 */
	public static Date calcularFecha(Date fechaOriginal, int cantidadDias, int diaSemanaNoContable[], int diaSemanaInHabiles[]) {

		Calendar calendarAux = Calendar.getInstance();
		
		calendarAux.setTime(fechaOriginal);
				
		int contador = BigDecimal.ZERO.intValue();
		
		int one = Integer.signum(cantidadDias); 

		cantidadDias = Math.abs(cantidadDias);

		while (contador < cantidadDias) {

			calendarAux.add(Calendar.DAY_OF_MONTH, one);

			boolean noAplicaDia = Arrays.stream(diaSemanaNoContable).filter(d -> d == calendarAux.get(Calendar.DAY_OF_WEEK)).findFirst().isPresent();
			
			if (!noAplicaDia) {
				++contador;
			}			
		}
		
		IntStream stream2 = Arrays.stream(diaSemanaInHabiles);
		
		boolean diaInabil = stream2.filter(d -> d == calendarAux.get(Calendar.DAY_OF_WEEK)).findFirst().isPresent();

		if (diaInabil) {
			return calcularFecha(calendarAux.getTime(), one, diaSemanaNoContable, diaSemanaInHabiles);
		}

		return calendarAux.getTime();
	}
	
}
