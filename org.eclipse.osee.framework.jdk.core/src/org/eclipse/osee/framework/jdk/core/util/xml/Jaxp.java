/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class Jaxp {
   private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

   /**
    * Obtains a list of all direct descendants of element
    * 
    * @param element the element to find the children of
    * @return A list of elements that are direct children of element. If no children exist, an empty list is returned.
    */
   public static List<Element> getChildDirects(Element element) {
      NodeList nl = element.getChildNodes();
      List<Element> elementList = new ArrayList<Element>(nl.getLength()); // this may be oversized
      for (int i = 0; i < nl.getLength(); i++) {
         Node n = nl.item(i);
         if (n.getNodeType() == Node.ELEMENT_NODE) elementList.add((Element) n);
      }
      return elementList;
   }

   /**
    * Obtains a list of all direct descendants of element with the matching tag.
    * 
    * @param element the element to find the children of
    * @param childTagName the tag name for the children
    * @return A list of elements that are direct children of element whose tag names match childTagName. If no such
    *         children exist, an empty list is returned.
    */
   public static List<Element> getChildDirects(Element element, String childTagName) {
      List<Element> elementList = new ArrayList<Element>();
      NodeList nl = element.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
         Node n = nl.item(i);
         if (n.getNodeType() == Node.ELEMENT_NODE && ((Element) n).getTagName().equals(childTagName)) elementList.add((Element) n);
      }
      return elementList;
   }

   /**
    * Obtains the first child that is a direct descendant of element with the matching tag
    * 
    * @param element the element to find the child of
    * @param childTagName the tag name for the child
    * @return the first child with the given tag one level deep from element, null if no such child exists.
    */
   public static Element getChildDirect(Element element, String childTagName) {
      NodeList nl = element.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
         Node n = nl.item(i);
         if (n.getNodeType() == Node.ELEMENT_NODE && ((Element) n).getTagName().equals(childTagName)) return ((Element) n);
      }
      return null;
   }

   /**
    * Obtains the first child that is a descendant of element with the matching tag
    * 
    * @param element the element to find the child of
    * @param childTagName the tag name for the child
    * @return the first child with the given tag, or null if no such children exist
    */
   public static Element getChild(Element element, String childTagName) {
      NodeList elementList = element.getElementsByTagName(childTagName);
      return (Element) elementList.item(0);
   }

   public static Element createElement(Document d, String tag, String characterData) {
      Element e = d.createElement(tag);
      Text t = d.createTextNode(characterData);
      e.appendChild(t);
      // e.setTextContent(characterData);
      // the above two lines do the same thing as this, but I trust them more so we'll go that
      // route.
      return e;
   }

   /**
    * Obtains the character data for the Element. Note this differs from the Node.getTextContext(); call, which returns
    * the concatenation of the character data for all children of this Element.
    * 
    * @param e The element go get the character data for
    * @param trimWhitespace iff true, each segment will be trimmed.
    * @return All of the character data for the Element e. This means if there are elements separating the character
    *         data, it will all be concatenated together. If trimWhitespace, each segment will be trimmed of whitespace,
    *         with a single space between segments; otherwise the segments will be concatenated without any space
    *         separation. If no character data is present, returns an empty string.
    */
   public static String getElementCharacterData(Element e, boolean trimWhitespace) {
      NodeList childNodes = e.getChildNodes();
      String resultString = "";
      boolean first = true;

      for (int i = 0; i < childNodes.getLength(); i++) {
         Node n = childNodes.item(i);
         if (n.getNodeType() == Node.TEXT_NODE) {
            if (!first && trimWhitespace) resultString += " ";
            resultString += trimWhitespace ? n.getNodeValue().trim() : n.getNodeValue();
            first = false;
         }
      }
      return resultString;
   }

   /**
    * Obtains the character data for the Element. Note this differs from the Node.getTextContext(); call, which returns
    * the concatenation of the character data for all children of this Element.
    * 
    * @param e The element go get the character data for
    * @return All of the character data for the Element e. This means if there are elements separating the character
    *         data, it will all be concatenated together. Each segment will be trimmed of whitespace, with a single
    *         space between segments. If no character data is present, returns an empty string.
    */
   public static String getElementCharacterData(Element e) {
      return getElementCharacterData(e, true);
   }

   /**
    * Obtains the character data for each element in the collection, return as a List. Each entry in the list
    * corresponding to the character data for one of the elements in the collection.
    * 
    * @param elements
    * @return
    */
   public static List<String> getElementsCharacterData(Collection<Element> elements) {
      List<String> result = new ArrayList<String>(elements.size());
      for (Element e : elements) {
         result.add(Jaxp.getElementCharacterData(e));
      }
      return result;
   }

   /**
    * @param nodes The NodeList whose text we will return
    * @return An ArrayList<String> of the text for all nodes
    */
   public static ArrayList<String> getChildrenTexts(NodeList nodes) {
      ArrayList<String> retVal = new ArrayList<String>(nodes.getLength());

      for (int i = 0; i < nodes.getLength(); i++) {
         Element element = (Element) nodes.item(i);
         retVal.add(getElementCharacterData(element));
      }
      return retVal;
   }

   /**
    * Obtains a list of Strings of the character data for all elements in the document whose tag name matches.
    * 
    * @param document The document to be searched
    * @param tagName The tagName for the children whose text we will obtain
    * @return An ArrayList<String> of the text for all child nodes matching tagName
    */
   public static ArrayList<String> getChildrenTexts(Document document, String tagName) {
      return getChildrenTexts(document.getElementsByTagName(tagName));
   }

   /**
    * Obtains a list of Strings of the character data for all descendants of element whose tag name matches.
    * 
    * @param document The document to be searched
    * @param tagName The tagName for the children whose text we will obtain
    * @return An ArrayList<String> of the text for all child nodes matching tagName
    */
   public static ArrayList<String> getChildrenTexts(Element element, String tagName) {
      return getChildrenTexts(element.getElementsByTagName(tagName));
   }

   public static String getChildText(Element element, String childTagName, boolean trim) {
      Element child = getChild(element, childTagName);
      if (child != null) {
         return getElementCharacterData(child, trim);
      }
      return null;
   }

   public static String getChildText(Element element, String childTagName) {
      return getChildText(element, childTagName, false);
   }

   public static String getChildTextTrim(Element element, String childTagName) {
      return getChildText(element, childTagName, true);
   }

   private static void findElementsInternal(List<Element> source, LinkedList<String> path, List<Element> list) {
      String tag = path.poll();
      LinkedList<String> childPath = new LinkedList<String>(path);

      for (Element e : source) {
         List<Element> children = getChildDirects(e, tag);
         if (!children.isEmpty()) {
            if (path.size() == 0) // we're at the end of the path
               list.addAll(children);
            else {
               findElementsInternal(children, childPath, list);
            }
         }
      }
   }

   /**
    * Searches for all sub-elements found at the path provided.
    * 
    * @param element The element underneath which we will search
    * @param elementPath The path to follow. For example ["script","configuration","element_i_want"]
    * @param firstIsRoot If true, the first item in elementPath must match element. That is, in the above example, e's
    *           tag name must be "script". This is useful when calling from the document level, that is where element is
    *           Document.getDocumentElement(), the first item in the path would be the first root element of the xml
    *           tree.
    * @return All elements that match the specified path.
    */
   public static List<Element> findElements(Element element, List<String> elementPath, boolean firstIsRoot) {
      List<Element> result = new LinkedList<Element>();
      List<Element> source = new ArrayList<Element>(1);
      source.add(element);

      LinkedList<String> path;
      if (elementPath instanceof LinkedList)
         path = (LinkedList<String>) (elementPath);
      else
         path = new LinkedList<String>(elementPath);

      // Strip off the first item of elementPath and make sure it matches 'element'
      if (firstIsRoot) {
         String firstTagName = path.poll();
         if (element.getTagName().equals(firstTagName)) return result;
      }

      findElementsInternal(source, path, result);
      return result;
   }

   private static List<Element> findElementsSinglePath(Element e, String elementPath, boolean firstIsRoot) {
      return findElements(e, Arrays.asList(elementPath.split("/")), firstIsRoot);
   }

   private static List<Element> findElements(Element e, String elementPath, boolean firstIsRoot) {
      List<Element> result = new LinkedList<Element>();
      String[] paths = elementPath.split("\\|");

      for (String path : paths) {
         result.addAll(findElementsSinglePath(e, path, firstIsRoot));
      }
      return result;
   }

   public static List<Element> findElements(Element element, String elementPath) {
      return findElements(element, elementPath, false);
   }

   public static List<Element> findElements(Document d, String elementPath) {
      return findElements(d.getDocumentElement(), elementPath, true);
   }

   /**
    * Searches for a sub-element found at the path provided. Each list element indicates the tag name for the next
    * sub-element.
    * 
    * @param e The element underneath which we will search
    * @param elementPath The path to follow. For example ["script","configuration","element_i_want"]
    * @return The first element that matches the provided path, beneath the provided element e, or null if no such
    *         element exists.
    */
   public static Element findElement(Element element, List<String> elementPath) {

      Element e = element;
      for (String tag : elementPath) {
         NodeList a = e.getChildNodes();
         Element nextElement = null;
         for (int i = 0; i < a.getLength() && nextElement == null; i++) {
            Node n = a.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && ((Element) n).getTagName().equals(tag)) nextElement =
                  (Element) n;
         }
         if (nextElement == null)
            return null;
         else
            e = nextElement;
      }
      return e;
   }

   /**
    * Searches the Document for the Element found at the '/' delimited path provided. The path should begin with the
    * root node of the document.
    * 
    * @param d The document to search
    * @param elementPath The path to follow. For example "script/configuration/element_i_want"
    * @return The first element that matches the provided path, or null if no such element exists.
    */
   public static Element findElement(Document d, String elementPath) {
      List<String> pathList = Arrays.asList(elementPath.split("/"));
      String rootTagName = pathList.get(0);
      // Remove the first item from the list, Arrays.asList List type doesn't support .remove()
      if (pathList.size() > 1)
         pathList = pathList.subList(1, pathList.size());
      else
         pathList.clear();

      Element root = d.getDocumentElement();
      if (!root.getTagName().equals(rootTagName)) return null;
      return findElement(d.getDocumentElement(), pathList);
   }

   /**
    * Searches for a sub-element found at the '/' delimited path provided. The path should begin with the first node
    * underneath the provided element.
    * 
    * @param e The element underneath which we will search
    * @param elementPath The path to follow. For example "script/configuration/element_i_want"
    * @return The first element that matches the provided path, beneath the provided element e, or null if no such
    *         element exists.
    */
   public static Element findElement(Element e, String elementPath) {
      return findElement(e, Arrays.asList(elementPath.split("/")));
   }

   public static Document readXmlDocument(InputStream is) throws ParserConfigurationException, SAXException, IOException {
      DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.parse(is);
   }

   public static Document readXmlDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException {
      DocumentBuilder builder = factory.newDocumentBuilder();
      CharBackedInputStream charBak = new CharBackedInputStream();
      charBak.addBackingSource(xmlString);
      Document document = builder.parse(charBak);
      return document;
   }

   public static Document readXmlDocument(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(xmlFile);
      return document;
   }

   public static Document readXmlDocumentFromResource(Class<?> base, String name) throws ParserConfigurationException, SAXException, IOException {
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(base.getResourceAsStream(name));
      return document;
   }

   /**
    * Adds the XSL style sheet processing instruction to the document.
    * 
    * @param d
    * @param xslPath
    */
   public static void setXslProperty(Document d, String xslPath) {
      ProcessingInstruction xsl = d.createProcessingInstruction("xml-stylesheet", // 
            "type=\"text/xsl\" href=\"" + xslPath + "\"");
      d.appendChild(xsl);
   }

   /**
    * Writes the XML document 'document' to the 'file'.
    * 
    * @param document The XML document to output
    * @param file Where to put the output
    * @throws TransformerException
    * @throws IOException
    */
   public static void writeXmlDocument(Document document, File file) throws TransformerException, IOException {
      writeXmlDocument(document, file, getCompactFormat(document));
   }

   /**
    * Writes the XML document 'document' to the 'file'.
    * 
    * @param document The XML document to output
    * @param file Where to put the output
    * @param prettyOutput If true, turns on indention so the output is more easily readable, if False turns indention
    *           off to save space.
    * @throws TransformerException
    * @throws IOException
    */
   public static void writeXmlDocument(Document document, File file, OutputFormat format) throws TransformerException, IOException {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));
      outputXmlDocument(document, out, format);
      out.close();
   }

   /**
    * Gets the XML document 'document' as a string
    * 
    * @param document The XML document to output
    * @param file Where to put the output
    * @param prettyOutput If true, turns on indention so the output is more easily readable, if False turns indention
    *           off and is assumed to provide the XML as compactly as possible.
    * @throws TransformerException
    */
   public static String xmlToString(Document document, OutputFormat format) throws IOException {
      StringWriter stringWriter = new StringWriter();
      outputXmlDocument(document, stringWriter, format);
      return stringWriter.toString();
   }

   /**
    * Sends the XML to the output
    * 
    * @param document The source XML
    * @param output Where the XML is 'printed' to
    * @param format The format style to use
    * @throws IOException
    */
   private static void outputXmlDocument(Document document, Writer output, OutputFormat format) throws IOException {
      XMLSerializer serializer = new XMLSerializer(output, format);
      serializer.serialize(document);
   }

   /**
    * Generates an OutputFormat that is pleasing to look at (indention, newlines, etc)
    * 
    * @param document the document to be formatted
    * @return the OutputFormat object to use for XML Formatting
    * @see XMLSerializer
    */
   public static OutputFormat getPrettyFormat(Document document) {
      OutputFormat format = new OutputFormat(document);
      format.setIndenting(true);
      format.setIndent(2);
      return format;
   }

   /**
    * Generates an OutputFormat that is compact (no extra whitepsace)
    * 
    * @param document the document to be formatted
    * @return the OutputFormat object to use for XML Formatting
    * @see XMLSerializer
    */
   public static OutputFormat getCompactFormat(Document document) {
      OutputFormat format = new OutputFormat(document);
      format.setIndenting(false);
      format.setLineSeparator("");
      return format;
   }

   public static Document newDocument() throws ParserConfigurationException {
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.newDocument();
   }

   public static String getDocumentXml(Document doc) {
      try {
         Source source = new DOMSource(doc);
         StringWriter stringWriter = new StringWriter();
         Result result = new StreamResult(stringWriter);
         TransformerFactory factory = TransformerFactory.newInstance();
         Transformer transformer = factory.newTransformer();
         transformer.transform(source, result);
         return stringWriter.getBuffer().toString();
      } catch (TransformerConfigurationException e) {
         e.printStackTrace();
      } catch (TransformerException e) {
         e.printStackTrace();
      }
      return null;
   }

   public static void main(String args[]) {
      try {
         Document doc = readXmlDocument("<A name='george' type='level1'>" + "  <B type='level2'>I'm at level 2</B>" //
               + "  <B type='level2'>I'm also at level 2</B>" //
               + "  <C type='level2'>" //
               + "      <D>likes to be C's child</D>" //
               + "C has some more text here" //
               + "      <D>2nd round</D>"//
               + "END of C" //
               + "  </C>" //
               + "</A>");

         Element e = (Element) getChild(doc.getDocumentElement(), "C");
         System.out.println("e.getTagName     :" + e.getTagName());
         System.out.println("e.getLocalName   :" + e.getLocalName());
         System.out.println("e.getNodeName    :" + e.getNodeName());
         System.out.println("e.getNodeType    :" + e.getNodeType());
         System.out.println("e.getNodeValue   :" + e.getNodeValue());
         System.out.println("e.getPrefix      :" + e.getPrefix());
         System.out.println("e.getTextContent :" + e.getTextContent());
         System.out.println("");
         System.out.println("e.getFirstChild().getNodeValue   :" + e.getFirstChild().getNodeValue());
         System.out.println("e.getFirstChild().getTextContent :" + e.getFirstChild().getTextContent());
         System.out.println("");
         System.out.println(getElementCharacterData(e));

         System.out.println("");
         System.out.println("Looking for the Top level item:");
         NodeList nl = doc.getElementsByTagName("A");
         for (int i = 0; i < nl.getLength(); i++) {
            System.out.println("     Found: " + nl.item(i).getNodeName());
         }
         System.out.println("Looking for the Top level item:");
         nl = doc.getDocumentElement().getElementsByTagName("A");
         for (int i = 0; i < nl.getLength(); i++) {
            System.out.println("Found: " + nl.item(i).getNodeName());
         }

         System.out.println("");
         System.out.println("Searching For D:");
         Element em = findElement(doc, "A/C/D");
         System.out.println(em.getTagName() + ":" + getElementCharacterData(em));

         List<Element> list = findElements(doc.getDocumentElement(), "C/D");
         for (Element te : list)
            System.out.println(te.getTagName() + ":" + getElementCharacterData(te));

         Element joe = Jaxp.createElement(doc, "f", "This is F's Data");
         em.appendChild(joe);

         System.out.println(Jaxp.getElementCharacterData(joe));

         System.out.println("******");
         System.out.println(xmlToString(doc, getPrettyFormat(doc)));

      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}
