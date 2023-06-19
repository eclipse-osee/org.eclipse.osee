/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Test Case for {@link JAXP}
 *
 * @author Roberto E. Escobar
 */
public class JAXPTest {
   private static final String NAMESPACE_DEFINITIONS_PREFIX =
      "<root xmlns:t=\"http://t.org/t\" xmlns:w=\"http://w.org/w\" xmlns:x=\"http://x.org/x\">";

   private static final String NAMESPACE_DEFINITIONS_POSTFIX = "</root>";

   // @formatter:off
   private static final String SIMPLE_TRAILING_SPACES_DOC =
      "<node1>" +
         "<w:a w:id=\"one\" w:id2=\"two\">A Text  </w:a>" +
         "<t:b t:id=\"three\" t:id2=\"four\">B Text </t:b>" +
         "<x:c><![CDATA[ "+
             "function match(a,b) { " +
             "   if (a < b && a < 0) { " +
             "      return 1; " +
             "   } else { " +
             "      return 0; " +
             "   }" +
             "]]>" +
         "</x:c>" +
         "<d id=\"someId\"></d>" +
      "</node1>";
   // @formatter:on

   // @formatter:off
   private static final String SIMPLE_TRIMMED_DOC =
      "<node1>" +
      "<w:a w:id=\"one\" w:id2=\"two\">A Text</w:a>" +
      "<t:b t:id=\"three\" t:id2=\"four\">B Text</t:b>" +
      "<x:c><![CDATA["+
          "function match(a,b) { " +
          "   if (a < b && a < 0) { " +
          "      return 1; " +
          "   } else { " +
          "      return 0; " +
          "   }" +
          "]]>" +
      "</x:c>" +
      "<d id=\"someId\"></d>" +
   "</node1>";
   // @formatter:on

   // @formatter:off
   private static final String LEGACY_DOC =
   "<A name='george' type='level1'>\n" +
   "  <B type='level2'>I'm at level 2</B>\n" +
   "  <B type='level2'>I'm also at level 2</B>\n" +
   "  <C type='level2'>\n" +
   "      <D>likes to be C's child</D>\n" +
   "C has some more text here\n" +
   "      <D>2nd round</D>\n" +
   "END of C\n" +
   "  </C>\n" +
   "</A>";
   // @formatter:on

   // @formatter:off
   private static final String LEGACY_PRINTTED =
   "<A name=\"george\" type=\"level1\">" +
   "<B type=\"level2\">I'm at level 2</B>" +
   "<B type=\"level2\">I'm also at level 2</B>" +
   "<C type=\"level2\">" +
   "<D>" +
   "<f>This is F's Data</f>" +
   "likes to be C's child" +
   "</D>" +
   "<D>2nd round</D>" +
   " C has some more text here END of C" +
   "</C>" +
   "   </A>";
   // @formatter:on

// @formatter:off
   private static final String ATTR_EMPTY_VALUE =
      "<node1>" +
      "<w:a w:id=\"one\" w:id2=\"two\" w:thisisempty=\"\">A Text</w:a>" +
      "</node1>";
   // @formatter:on

   @Test
   public void testReadWriteAttributeWithEmptyValue()
      throws ParserConfigurationException, SAXException, IOException, XMLStreamException {
      Document document = Jaxp.readXmlDocument(ATTR_EMPTY_VALUE);
      Element startElement = document.getDocumentElement();

      String expectedAText = Jaxp.getChildText(startElement, "w:a");
      Assert.assertEquals("A Text", expectedAText);

      Element child = Jaxp.getChild(startElement, "w:a");
      String one = child.getAttribute("w:id");
      String two = child.getAttribute("w:id2");
      String empty = child.getAttribute("w:thisisempty");

      Assert.assertEquals("one", one);
      Assert.assertEquals("two", two);
      Assert.assertEquals("", empty);

      NamedNodeMap attrs = child.getAttributes();
      int manyAttrs = attrs.getLength();
      boolean emptyAttrPresent = false;
      for (int i = 0; i < manyAttrs; i++) {
         Node attr = attrs.item(i);
         if (attr.getNodeName().equals("w:thisisempty")) {
            emptyAttrPresent = true;
         }
      }
      Assert.assertTrue(emptyAttrPresent);

      StringWriter writer = new StringWriter();
      writeToAppendable(writer, startElement, false);
      Assert.assertEquals(ATTR_EMPTY_VALUE, writer.toString());
   }

   @Test
   public void testReadWriteTrailingTextSpaces()
      throws ParserConfigurationException, SAXException, IOException, XMLStreamException {
      Document document = Jaxp.readXmlDocument(SIMPLE_TRAILING_SPACES_DOC);
      Element startElement = document.getDocumentElement();

      String expectedAText = Jaxp.getChildText(startElement, "w:a");
      Assert.assertEquals("A Text  ", expectedAText);

      String expectedBText = Jaxp.getChildText(startElement, "t:b");
      Assert.assertEquals("B Text ", expectedBText);

      StringWriter writer = new StringWriter();
      writeToAppendable(writer, startElement, false);
      Assert.assertEquals(SIMPLE_TRAILING_SPACES_DOC, writer.toString());
   }

   @Test
   public void testReadWriteTrimmed()
      throws ParserConfigurationException, SAXException, IOException, XMLStreamException {
      Document document = Jaxp.readXmlDocument(SIMPLE_TRAILING_SPACES_DOC);
      Element startElement = document.getDocumentElement();

      String expectedAText = Jaxp.getChildText(startElement, "w:a");
      Assert.assertEquals("A Text  ", expectedAText);

      String expectedBText = Jaxp.getChildText(startElement, "t:b");
      Assert.assertEquals("B Text ", expectedBText);

      StringWriter writer = new StringWriter();
      writeToAppendable(writer, startElement, true);
      Assert.assertEquals(SIMPLE_TRIMMED_DOC, writer.toString());
   }

   @Test(expected = SAXParseException.class)
   public void testParseNamespaceAwareFailsNamespacesUndefined()
      throws ParserConfigurationException, SAXException, IOException {
      Jaxp.readXmlDocumentNamespaceAware(SIMPLE_TRAILING_SPACES_DOC);
   }

   @Test
   public void testReadWriteNamespaceAware()
      throws ParserConfigurationException, SAXException, IOException, XMLStreamException {
      Document document = Jaxp.readXmlDocumentNamespaceAware(
         NAMESPACE_DEFINITIONS_PREFIX + SIMPLE_TRAILING_SPACES_DOC + NAMESPACE_DEFINITIONS_POSTFIX);

      Element startElement = document.getDocumentElement();

      String expectedAText = Jaxp.getChildText(startElement, "w:a");
      Assert.assertEquals("A Text  ", expectedAText);

      String expectedBText = Jaxp.getChildText(startElement, "t:b");
      Assert.assertEquals("B Text ", expectedBText);

      String expectedDText = Jaxp.getChildText(startElement, "d");
      Assert.assertEquals("", expectedDText);

      StringWriter writer = new StringWriter();
      writeToAppendable(writer, startElement, false);
      Assert.assertEquals(NAMESPACE_DEFINITIONS_PREFIX + SIMPLE_TRAILING_SPACES_DOC + NAMESPACE_DEFINITIONS_POSTFIX,
         writer.toString());
   }

   @Test
   public void testLegacy() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {
      Document doc = Jaxp.readXmlDocument(LEGACY_DOC);

      Element e = Jaxp.getChild(doc.getDocumentElement(), "C");

      Assert.assertEquals("C", e.getTagName());
      Assert.assertEquals(null, e.getLocalName());
      Assert.assertEquals("C", e.getNodeName());
      Assert.assertEquals(1, e.getNodeType());
      Assert.assertEquals(null, e.getNodeValue());
      Assert.assertEquals(null, e.getPrefix());
      Assert.assertEquals("\n      likes to be C's child\nC has some more text here\n      2nd round\nEND of C\n  ",
         e.getTextContent());

      Assert.assertEquals("\n      ", e.getFirstChild().getNodeValue());
      Assert.assertEquals("\n      ", e.getFirstChild().getTextContent());
      Assert.assertEquals(" C has some more text here END of C", Jaxp.getElementCharacterData(e));

      NodeList nl = doc.getElementsByTagName("A");
      Assert.assertEquals(1, nl.getLength());
      Assert.assertEquals("A", nl.item(0).getNodeName());

      Element em = Jaxp.findElement(doc, "A/C/D");
      if (em != null) {
         Assert.assertEquals("D", em.getTagName());
         Assert.assertEquals("likes to be C's child", Jaxp.getElementCharacterData(em));

         List<Element> list = Jaxp.findElements(doc.getDocumentElement(), "C/D");
         Assert.assertEquals(2, list.size());

         Iterator<Element> iterator = list.iterator();
         Element e1 = iterator.next();
         Assert.assertEquals("D", e1.getTagName());
         Assert.assertEquals("likes to be C's child", Jaxp.getElementCharacterData(e1));

         Element e2 = iterator.next();
         Assert.assertEquals("D", e2.getTagName());
         Assert.assertEquals("2nd round", Jaxp.getElementCharacterData(e2));

         Element joe = Jaxp.createElement(doc, "f", "This is F's Data");
         em.appendChild(joe);

         Assert.assertEquals("This is F's Data", Jaxp.getElementCharacterData(joe));
      }

      StringWriter writer = new StringWriter();
      writeToAppendable(writer, doc.getDocumentElement(), true);

      Assert.assertEquals(LEGACY_PRINTTED, writer.toString());

      Document doc2 = Jaxp.readXmlDocument(LEGACY_PRINTTED);

      StringWriter writer2 = new StringWriter();
      writeToAppendable(writer2, doc2.getDocumentElement(), false);
      Assert.assertEquals(LEGACY_PRINTTED, writer2.toString());
   }

   private static void writeToAppendable(Writer writer, Node root, boolean trimWhitespace) throws XMLStreamException {
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter xmlWriter = null;
      try {
         xmlWriter = factory.createXMLStreamWriter(writer);
         Jaxp.writeNode(xmlWriter, root, trimWhitespace);
      } finally {
         if (xmlWriter != null) {
            xmlWriter.close();
         }
      }
   }
}
