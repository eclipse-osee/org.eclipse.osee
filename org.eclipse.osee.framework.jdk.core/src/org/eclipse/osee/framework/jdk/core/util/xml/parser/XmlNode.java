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
package org.eclipse.osee.framework.jdk.core.util.xml.parser;

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

/**
 * @author Roberto E. Escobar
 */
public class XmlNode {

   private String name;
   private String textContent;
   private Map<String, String> attributes;

   public XmlNode(String name, Attributes attributes) {
      this.name = name;
      this.attributes = new HashMap<String, String>(attributes.getLength());
      for (int i = 0; i < attributes.getLength(); i++)
         this.attributes.put(attributes.getQName(i), attributes.getValue(i));
      this.textContent = "";
   }

   /**
    * @return Returns the textContent.
    */
   public String getTextContent() {
      return textContent;
   }

   /**
    * @param textContent The textContent to set.
    */
   public void setTextContent(String textContent) {
      if (textContent != null && textContent.length() > 0) {
         this.textContent = textContent;
      }
   }

   /**
    * @param qname
    * @return Returns the value of the attribute matching the qualified name.
    */
   public String getAttributeValue(String qname) {
      return attributes.get(qname);
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }
}
