/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch.recording.xform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Andrew M. Finkbeiner
 */
public class ElementVsTimeCSV extends AbstractSaxHandler {
   private static String UPDATE = "Update";
   private static String PUBSUBHEADERINFO = "PubSubHeaderInfo";
   private final String[] pubSubHeaderElementsToStore = new String[] {"timeTag", "sequenceNum"};
   private String message;
   private String time;
   private final List<Storage> storage = new ArrayList<Storage>();
   private final List<String> columnsToPrint = new ArrayList<String>();
   private final StringBuilder builder = new StringBuilder();

   /**
    * @param elementColumns
    */
   public ElementVsTimeCSV(String[] elementColumns) {
      for (String str : elementColumns) {
         columnsToPrint.add(str);
      }
   }

   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
      if ("WatchRecord".equals(localName)) {
         Collections.sort(storage);
         HashMap<String, String> timeSlice = new HashMap<String, String>();

         builder.append("time");
         for (String str : columnsToPrint) {
            builder.append(",");
            builder.append(str);
         }
         builder.append("\n");

         for (int i = 0; i < storage.size(); i++) {
            Storage o = storage.get(i);
            long lastTime = o.time;
            timeSlice.put(o.name, o.value);
            for (; i < storage.size(); i++) {
               o = storage.get(i);
               if (o.time == lastTime) {
                  timeSlice.put(o.name, o.value);
               } else {
                  i--;
                  break;
               }
            }

            builder.append(lastTime);
            for (String str : columnsToPrint) {
               builder.append(",");
               String value = timeSlice.get(str);
               builder.append((value == null ? "" : value));
            }
            builder.append("\n");
            timeSlice.clear();
         }
      }
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {

      if (UPDATE.equals(localName)) {
         time = attributes.getValue("time");
         message = attributes.getValue("message");
      } else if (PUBSUBHEADERINFO.equals(localName)) {
         for (String str : pubSubHeaderElementsToStore) {
            storage.add(new Storage(time, String.format("%s.PubSubHeader.%s", message, str), attributes.getValue(str)));
         }
      } else if ("Element".equals(localName)) {
         storage.add(new Storage(time, String.format("%s.%s", message, attributes.getValue("name")),
            attributes.getValue("value")));
      }
   }

   private static final class Storage implements Comparable<Storage> {
      public long time;
      public String name;
      public String value;

      public Storage(String time, String name, String value) {
         this.time = Long.parseLong(time);
         this.name = name;
         this.value = value;
      }

      @Override
      public int compareTo(Storage o) {
         if (time > o.time) {
            return 1;
         } else if (time == o.time) {
            return 0;
         } else {
            return -1;
         }
      }
   }

   /**
    * @return the builder
    */
   public StringBuilder getBuilder() {
      return builder;
   }
}