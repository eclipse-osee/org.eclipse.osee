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
public class GenericBinaryAttribute extends Attribute {

   public GenericBinaryAttribute(String name) {
      super(new BlobMediaResolver(), name);
   }

   public GenericBinaryAttribute(IMediaResolver resolver) {
      super(resolver);
   }

   public GenericBinaryAttribute(IMediaResolver resolver, String name) {
      super(resolver, name);
   }

   @Override
   public String getTypeName() {
      return getClass().getSimpleName();
   }

   @Override
   public void setValidityXml(String validityXml) throws SAXException {
   }
}