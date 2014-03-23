import java.io.File;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XMLHelper {
	public static String serializeDocumentToString(Document doc, boolean formatted) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			if(formatted) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			}
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Ustvari prazen DOM Document z novo instanco DocumentBuilderFactory
	 * @return - nov Document oziroma null, če ne prišlo do napake
	 */
	public static Document newEmptyDocument() {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
		}
		return doc;
	}

	/**
	 * Ustvari DOM Document in parsaj podano datoteko
	 * @return - nov Document oziroma null, če ne prišlo do napake
	 */	
	public static Document newParsedDocument(File xmlFile){
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
		} catch (Exception e) {
		}
		return doc;
	}
}
