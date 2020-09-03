package dclient.controllers.validator;

public class VATNumberValidator {
	/**
	 * Italian Codice Fiscale normalization, formatting and validation routines.
	 * A <u>regular CF</u> is composed by 16 among letters and digits; the last
	 * character is always a letter representing the control code.
	 * A <u>temporary CF</u> could also be assigned; a temporary CF is composed of
	 * 11 digits, the last digit being the control code.
	 * Examples: MRORSS00A00A000U, 12345678903.
	 * @author Umberto Salsi <salsi@icosaedro.it>
	 * @version 2020-01-24
	 */
	
	static String normalize(String pi)
	{
		return pi.replaceAll("[ \t\r\n]", "");
	}
	
	/**
	 * Returns the formatted PI. Currently does nothing but normalization.
	 * @param pi Raw PI, possibly with spaces.
	 * @return Formatted PI.
	 */
	static String format(String pi)
	{
		return normalize(pi);
	}
	
	/**
	 * Verifies the basic syntax, length and control code of the given PI.
	 * @param pi Raw PI, possibly with spaces.
	 * @return Null if valid, or string describing why this PI must be
	 * rejected.
	 */
	static String validate(String pi)
	{
		pi = normalize(pi);
		if( pi.length() == 0 )
			return "Empty.";
		else if( pi.length() != 11 )
			return "Invalid length.";
		if( ! pi.matches("^[0-9]{11}$") )
			return "Invalid characters.";
		int s = 0;
		for(int i = 0; i < 11; i++){
			int n = pi.charAt(i) - '0';
			if( (i & 1) == 1 ){
				n *= 2;
				if( n > 9 )
					n -= 9;
			}
			s += n;
		}
		if( s % 10 != 0 )
			return "Invalid checksum.";
		return null;
	}
}
