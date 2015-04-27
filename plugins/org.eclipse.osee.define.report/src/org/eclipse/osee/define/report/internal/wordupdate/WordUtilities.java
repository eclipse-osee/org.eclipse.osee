/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal.wordupdate;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.w3c.dom.Element;

/**
 * @author David W. Miller
 */
public class WordUtilities {

   public static final String LISTNUM_FIELD_HEAD = "<w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr>";

   public static byte[] getFormattedContent(Element formattedItemElement) throws XMLStreamException {
      ByteArrayOutputStream data = new ByteArrayOutputStream(1024);
      XMLStreamWriter xmlWriter = null;
      try {
         xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(data, "UTF-8");
         for (Element e : Jaxp.getChildDirects(formattedItemElement)) {
            Jaxp.writeNode(xmlWriter, e, false);
         }
      } finally {
         if (xmlWriter != null) {
            xmlWriter.flush();
            xmlWriter.close();
         }
      }
      return data.toByteArray();
   }

   private static final Pattern tagKiller = Pattern.compile("<.*?>", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern paragraphPattern = Pattern.compile("<w:p( .*?)?>");
   private static final Pattern referencePattern = Pattern.compile("(_Ref[0-9]{9}|Word\\.Bookmark\\.End)");

   public static String textOnly(String str) {
      str = paragraphPattern.matcher(str).replaceAll(" ");
      str = tagKiller.matcher(str).replaceAll("").trim();
      return Xml.unescape(str).toString();
   }

   public static String referencesOnly(String content) {
      List<String> references = new ArrayList<String>();

      Matcher referenceMatcher = referencePattern.matcher(content);
      while (referenceMatcher.find()) {
         referenceMatcher.toString();
         String reference = referenceMatcher.group(1);
         references.add(reference);
      }

      StringBuilder sb = new StringBuilder();
      for (String reference : references) {
         sb.append(reference);
         sb.append("\n");
      }

      return sb.toString();
   }
}
