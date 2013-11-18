/* Erzeugt am 22.03.2007 von tbayen */
package de.bayen.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hilfsmethoden zum Umgang mit Strings.
 * 
 * @author tbayen
 */
public class StringUtils {
	/**
	 * Der angegebene String wird "count" mal hintereinandergehängt.
	 * Implementation des Perl-Operators "x".
	 * 
	 * @param str
	 * @param count
	 * @return String
	 */
	static public String stringTimes(String str, int count) {
		StringBuffer erg = new StringBuffer(str.length() * count);
		for (int i = 0; i < count; i++)
			erg.append(str);
		return erg.toString();
	}

	/**
	 * Ergibt den String linksbünsig mit einer festen Länge. Ist der String
	 * länger, wird er abgeschnitten, ist er kürzer, wird mit Leerstellen
	 * aufgefüllt.
	 * 
	 * @param str
	 * @param laenge
	 * @return rechtsbündiger String
	 */
	static public String linksBuendig(String str, int laenge) {
		return (str + stringTimes(" ", laenge)).substring(0, laenge);
	}

	/**
	 * Macht den ersten Buchstaben des Strings gross.
	 * 
	 * @param str
	 * @return String
	 */
	static public String firstToUpper(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	/**
	 * Macht den ersten Buchstaben des Strings klein.
	 * 
	 * @param str
	 * @return String
	 */
	static public String firstToLower(String str) {
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}

	static private Pattern camelCasePattern = null;

	/**
	 * Gibt einen Java-Bezeichner als Text für die Ausgabe z.B. in einer GUI
	 * aus. Dazu werden bei CamelCase-Stellen Leerzeichen eingefügt sowie der
	 * Start des Bezeichners in einen Großbuchstaben gewandelt.
	 * 
	 * @param javaName
	 * @return String
	 */
	static public String javaNamePrettyPrinter(String javaName) {
		if (camelCasePattern == null)
			camelCasePattern = Pattern.compile("(\\p{Lower})(\\p{Upper})");
		Matcher matcher = camelCasePattern.matcher(javaName);
		String erg = matcher.replaceAll("$1 $2");
		// Notation mit Punkten
		// String[] split = erg.split("\\.");
		// for (int i = 0; i < split.length; i++) {
		// split[i] = StringUtils.firstToUpper(split[i]);
		// }
		// erg = StringUtils.join(".", split);
		erg = erg.substring(erg.lastIndexOf('.') + 1);
		return StringUtils.firstToUpper(erg);
	}

	/**
	 * Erzeugt aus einem String, z.B. einem Namen, der aus einem Datensatz
	 * kommt, einen String, den man als ordemntlichen Dateinamewn benutzen kann.
	 * Da es heutzutage eigentrlich keine grossen Restriktionen mehr gibt, was
	 * ein Dateiname enthalten darf, unterliegt "ordentlich" sehr stark meinen
	 * persönlichen Vorlieben.
	 * 
	 * @param string
	 * @return aufbereiteter String
	 */
	static public String stringToFilename(String string) {
		string = string.replaceAll("\\s|\"|\\.", "");
		return string;
	}

	/**
	 * Fügt alle angegebenen Strings zusammen. Der Trenner (der erste Parameter)
	 * wird jeweils dazwischen geschrieben, kann aber natürlich auch "" sein.
	 * 
	 * @param trenner
	 * @param strings
	 * @return String
	 */
	static public String join(String trenner, String... strings) {
		if (strings == null)
			return null;
		StringBuffer erg = new StringBuffer();
		for (String string : strings) {
			if (erg.length() > 0)
				erg.append(trenner);
			erg.append(string);
		}
		return new String(erg);
	}

	/**
	 * Kürzt den String auf die angegebene Länge, wenn nötig. Ist der String
	 * bereits kürzer, passiert gar nichts.
	 * 
	 * @param string
	 * @param laenge
	 * @return String, der ggf. verkürzt ist
	 */
	static public String trim(String string, int laenge) {
		if (string.length() > laenge)
			return string.substring(0, laenge);
		else
			return string;
	}

	/**
	 * liest einen Reader ein und erzeugt daraus einen String. Es ist Vorsicht
	 * angebracht, wieviele Daten aus dem Reader gelesen werden, damit wir
	 * keinen unendlich grossen String erzeugen.
	 * 
	 * Es gibt eine ähnliche Methode in Apache Commons, die ich mir aber noch
	 * nicht näher angesehen habe:
	 * <code>http://commons.apache.org/io/api-release/index.html?org/apache/commons/io/IOUtils.html</code>
	 * 
	 * @param reader
	 * @return String
	 * @throws IOException
	 */
	static public String Reader2String(Reader reader) throws IOException {
		StringBuffer output = new StringBuffer();
		final int bufsize = 500;
		char[] cbuf = new char[bufsize];
		int numread;
		do {
			numread = reader.read(cbuf);
			// System.out.print(numread + " ");
			if (numread != -1) // wurde etwas gelesen?
				output.append(cbuf, 0, numread);
		} while (numread == bufsize);
		return new String(output);
	}

	/**
	 * Liest den angegebenen InputStream ein und erzeugt daraus einen String.
	 * Als Kodierung wird die Standard-Kodierung angenommen.
	 * 
	 * @param strm
	 * @return String
	 * @throws IOException
	 */
	static public String InputStream2String(InputStream strm)
			throws IOException {
		return Reader2String(new InputStreamReader(strm));
	}

	/**
	 * Stellt fest, ob ein String leer ist, d.h. ob er <code>null</code> ist
	 * oder die Länge 0 hat.
	 * 
	 * @param string
	 * @return boolean
	 */
	static public boolean isEmpty(String string) {
		return (string == null || string.length() == 0);
	}

	/**
	 * Prüft, ob der angegebene String leer ist und gibt entweder den
	 * angegebenen String wieder zurück oder einen Default-Wert, falls dieser
	 * leer ist.
	 * 
	 * @param string
	 * @param defaultString
	 * @return String
	 */
	static public String isEmpty(String string, String defaultString) {
		if (isEmpty(string))
			return defaultString;
		else
			return string;
	}
}
