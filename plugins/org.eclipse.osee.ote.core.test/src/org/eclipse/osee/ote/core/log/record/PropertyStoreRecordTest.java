/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.log.record;

import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Donald G. Dunne
 */
public class PropertyStoreRecordTest extends TestCase {

   public void testSimpleXmlWriting() throws XMLStreamException {
      PropertyStore store = new PropertyStore();
      store.put("test1", "data1");
      store.put("test2", "data2");
      store.put("test3", "data3");
      store.put("test4", "data4");
      PropertyStoreRecord record = new PropertyStoreRecord(store);
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      Writer stringStream = new StringWriter();
      XMLStreamWriter writer = factory.createXMLStreamWriter(stringStream);
      record.toXml(writer);

      Pattern pattern = Pattern.compile("key=\"test1\"");
      Matcher matcher = pattern.matcher(stringStream.toString());
      assertTrue(matcher.find());

      pattern = Pattern.compile("key=\"test2\"");
      matcher = pattern.matcher(stringStream.toString());
      assertTrue(matcher.find());

      pattern = Pattern.compile("data2");
      matcher = pattern.matcher(stringStream.toString());
      assertTrue(matcher.find());

      System.out.println(stringStream.toString());

      pattern = Pattern.compile("data4");
      matcher = pattern.matcher(stringStream.toString());
      assertTrue(matcher.find());

      pattern = Pattern.compile("key=\"test5\"");
      matcher = pattern.matcher(stringStream.toString());
      assertTrue(!matcher.find());
   }

   public void testNullPropertyStore() throws XMLStreamException {
      PropertyStoreRecord record = new PropertyStoreRecord(null);
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      Writer stringStream = new StringWriter();
      XMLStreamWriter writer = factory.createXMLStreamWriter(stringStream);
      record.toXml(writer);
   }

   public void testEmptyPropertyStore() throws XMLStreamException {
      PropertyStoreRecord record = new PropertyStoreRecord(new PropertyStore());
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      Writer stringStream = new StringWriter();
      XMLStreamWriter writer = factory.createXMLStreamWriter(stringStream);
      record.toXml(writer);

      assertTrue(stringStream.toString().length() == 31);
   }

}
