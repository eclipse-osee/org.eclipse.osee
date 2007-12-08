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
package org.eclipse.osee.framework.skynet.core.attribute;

import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class IntegerAttribute extends Attribute {
   public static final String TYPE_NAME = "Integer";

   //   private IntegerAttribute() {
   //      super(new VarcharMediaResolver());
   //   }

   /**
    * @param name
    */
   public IntegerAttribute(String name) {
      this(name, 0);
   }

   public IntegerAttribute(String name, int value) {
      super(new VarcharMediaResolver(), name);
      setStringData(Integer.toString(value, 10));
   }

   public void setInt(int value) {
      if (getInt() != value) setStringData(Integer.toString(value, 10));
   }

   public int getInt() throws NumberFormatException {
      return Integer.parseInt(getStringData());
   }

   @Override
   public String getTypeName() {
      return TYPE_NAME;
   }

   @Override
   public void setValidityXml(String validityXml) throws SAXException {
   }
}
