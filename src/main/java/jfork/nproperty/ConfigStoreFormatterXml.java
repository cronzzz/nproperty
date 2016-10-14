/*
 * Decompiled with CFR 0_115.
 */
package jfork.nproperty;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jfork.nproperty.ConfigStoreFormatterImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class ConfigStoreFormatterXml
extends ConfigStoreFormatterImpl {
    @Override
    public String generate() throws IOException {
        DocumentBuilder db;
        Transformer transformer;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException pe) {
            IOException e = new IOException();
            e.initCause(pe);
            throw e;
        }
        Document doc = db.newDocument();
        Element properties = (Element)doc.appendChild(doc.createElement("properties"));
        for (Map.Entry pair : this.pairs.entrySet()) {
            Element entry = (Element)properties.appendChild(doc.createElement("entry"));
            entry.setAttribute("key", (String)pair.getKey());
            entry.appendChild(doc.createTextNode((String)pair.getValue()));
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            transformer = tf.newTransformer();
            transformer.setOutputProperty("doctype-system", "http://java.sun.com/dtd/properties.dtd");
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("method", "xml");
            transformer.setOutputProperty("encoding", "UTF-8");
        }
        catch (TransformerConfigurationException tce) {
            IOException e = new IOException();
            e.initCause(tce);
            throw e;
        }
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult streamResult = new StreamResult(writer);
        try {
            transformer.transform(source, streamResult);
        }
        catch (TransformerException te) {
            IOException e = new IOException();
            e.initCause(te);
            throw e;
        }
        return writer.toString();
    }
}

