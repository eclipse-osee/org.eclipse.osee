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
public class StringAttribute extends Attribute {
   public static final String TYPE_NAME = "String";

   public StringAttribute(String name) {
      this(name, "");
   }

   public StringAttribute(String name, String value) {
      super(new VarcharWithOverflowProtectionMediaResolver(), name);
      setStringData(value);
   }

   @Override
   public String getTypeName() {
      return TYPE_NAME;
   }

   @Override
   public void setValidityXml(String validityXml) throws SAXException {
   }
}
