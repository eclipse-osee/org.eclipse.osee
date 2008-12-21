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
package org.eclipse.osee.ats.workflow.vue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public class VueLink {

   private final String xml;
   private String name;
   private String vueId;
   private boolean multiDirectional = false;
   private String fromVueId;
   private String toVueId;

   @Override
   public String toString() {
      return "[" + vueId + " - " + name + " - " + fromVueId + " --> " + toVueId + " - " + (multiDirectional ? "multiDirectional]" : "singleDirection]");
   }

   /**
    * 
    */
   public VueLink(String xml) {
      super();
      this.xml = xml;
      Matcher m =
            Pattern.compile("<ID1>(.*?)</ID1>.*?<ID2>(.*?)</ID2>", Pattern.DOTALL | Pattern.MULTILINE).matcher(xml);
      while (m.find()) {
         if (xml.contains("arrowState=\"2\"")) {
            fromVueId = m.group(1);
            toVueId = m.group(2);
         } else if (xml.contains("arrowState=\"1\"")) {
            toVueId = m.group(1);
            fromVueId = m.group(2);
         } else if (xml.contains("arrowState=\"3\"")) {
            fromVueId = m.group(1);
            toVueId = m.group(2);
            multiDirectional = true;
         } else if (xml.contains("arrowState=\"0\"")) throw new IllegalArgumentException(
               "Non-directional links not supported. id = " + getVueId());
      }
   }

   public String getVueId() {
      if (vueId == null) {
         Matcher m = Pattern.compile("<child.*? ID=\"(.*?)\" ").matcher(xml);
         if (m.find())
            vueId = m.group(1);
         else {
            vueId = "Unknown";
         }
      }
      return vueId;
   }

   public String getName() {
      if (name == null) {
         Matcher m = Pattern.compile("<child.*? label=\"(.*?)\" ").matcher(xml);
         if (m.find())
            name = m.group(1);
         else {
            name = "Unknown";
         }
      }
      return name;
   }

   /**
    * @return Returns the fromId.
    */
   public String getFromVueId() {
      return fromVueId;
   }

   /**
    * @return Returns the toId.
    */
   public String getToVueId() {
      return toVueId;
   }

   /**
    * @return Returns the multiDirectional.
    */
   public boolean isMultiDirectional() {
      return multiDirectional;
   }

}
