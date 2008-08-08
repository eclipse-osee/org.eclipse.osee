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
package org.eclipse.osee.framework.search.engine.tagger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;
import org.eclipse.osee.framework.search.engine.utility.XmlTextInputStream;

/**
 * @author Roberto E. Escobar
 */
public class XmlAttributeTaggerProvider extends BaseAttributeTaggerProvider {

   public boolean find(AttributeData attributeData, String value) {
      boolean toReturn = false;
      if (Strings.isValid(value)) {
         value = value.toLowerCase();
         List<String> wordToSearch = new ArrayList<String>();
         Scanner wordScanner = null;
         try {
            Scanner scanner1 = new Scanner(value);
            try {
               while (scanner1.hasNext()) {
                  String entry = scanner1.next();
                  if (Strings.isValid(entry)) {
                     wordToSearch.add(entry);
                  }
               }
            } finally {
               scanner1.close();
            }

            int index = 0;
            int totalToSearch = wordToSearch.size();
            wordScanner = new Scanner(new XmlTextInputStream(getValueAsStream(attributeData)), "UTF-8");
            while (wordScanner.hasNext()) {
               String word = wordScanner.next();
               if (Strings.isValid(word)) {
                  word = word.toLowerCase();
                  if (word.contains(wordToSearch.get(index))) {
                     index++;
                     if (index >= totalToSearch) {
                        toReturn = true;
                        break;
                     }
                  } else {
                     index = 0;
                  }
               }
            }

         } catch (Exception ex) {
            OseeLog.log(XmlAttributeTaggerProvider.class, Level.SEVERE, ex.toString(), ex);
         } finally {
            wordToSearch.clear();
            wordToSearch = null;
            if (wordScanner != null) {
               wordScanner.close();
               wordScanner = null;
            }
         }
      }
      return toReturn;
   }

   public void tagIt(AttributeData attributeData, ITagCollector collector) throws Exception {
      InputStream inputStream = null;
      try {
         inputStream = getValueAsStream(attributeData);
         TagProcessor.collectFromInputStream(new XmlTextInputStream(inputStream), collector);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }
}
