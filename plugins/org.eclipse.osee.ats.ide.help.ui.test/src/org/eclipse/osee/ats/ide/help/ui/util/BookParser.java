/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.help.ui.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BookParser {

   private static final String TOC = "toc";
   private static final String TOPIC = "topic";
   private static final String LINK = "link";
   private static final String HREF = "href";

   public Set<String> entries = new HashSet<>();
   public String path;

   public BookParser(String path) {
      this.path = path;
   }

   public void parse() throws Exception {
      entries.clear();

      URL bookUrl = HelpTestUtil.getResource(path);

      URLConnection bookConnection = bookUrl.openConnection();

      try (InputStream bookInputStream = bookConnection.getInputStream()) {
         // Parse the main book.xml
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.parse(bookInputStream);

         Element rootElement = document.getDocumentElement();

         // Store root element topic path
         storeEntry(rootElement.getAttribute(TOPIC));

         // Extract paths from the main TOC
         extractPaths(rootElement);

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void extractPaths(Element element) throws Exception {
      NodeList topics = element.getElementsByTagName(TOPIC);

      for (int i = 0; i < topics.getLength(); i++) {
         Element topic = (Element) topics.item(i);

         // Check for links to sub TOCs
         NodeList links = topic.getElementsByTagName(LINK);
         for (int j = 0; j < links.getLength(); j++) {
            Element link = (Element) links.item(j);
            String tocPath = link.getAttribute(TOC);

            storeEntry(tocPath);

            // Parse the sub TOC to extract href paths
            URL subUrl = HelpTestUtil.getResource(tocPath);
            URLConnection subConnection = subUrl.openConnection();

            try (InputStream subInputStream = subConnection.getInputStream()) {
               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
               DocumentBuilder builder = factory.newDocumentBuilder();
               Document subDocument = builder.parse(subInputStream);

               extractHrefs(subDocument.getDocumentElement());

            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   private void extractHrefs(Element element) {
      NodeList topics = element.getElementsByTagName(TOPIC);

      for (int i = 0; i < topics.getLength(); i++) {
         Element topic = (Element) topics.item(i);
         String href = topic.getAttribute(HREF);
         if (!href.isEmpty()) {
            storeEntry(href);
         }
      }
   }

   private void storeEntry(String entry) {
      if (Strings.isValid(entry)) {
         entries.add(normalizePath(entry));
      }
   }

   private String normalizePath(String reference) {
      return reference.replaceAll("\\.html#.*", ".html");
   }

   public Set<String> getEntries() {
      return entries;
   }

}
