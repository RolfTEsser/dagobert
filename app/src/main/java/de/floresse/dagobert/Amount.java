package de.floresse.dagobert;

import java.math.BigDecimal;

public class Amount {

	private static Boolean blankzero = true;
    private BigDecimal bd;
    
	public Amount(String val) {
		bd = new BigDecimal(val); 
		}
	
	public Amount(double val) {
		bd = new BigDecimal (val);
		}
	
	public double doubleValue() {
		return bd.doubleValue();
	}
	
	public String toString() {
		bd = new BigDecimal(bd.toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
		String s = bd.toString();
		s = s.replace(".00", "");
		if (blankzero) {
			if (s.compareTo("0")==0) {
				s = new String("");
			}
		}	
		return s;
	}
	
	public static Boolean isBlankzero() {
		return blankzero;
	}
	
	public static void setBlankzero(Boolean bl) {
		blankzero = bl;
	}

}
