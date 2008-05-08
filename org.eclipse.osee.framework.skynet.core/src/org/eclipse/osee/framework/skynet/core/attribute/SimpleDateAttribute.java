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

import java.text.DateFormat;
import java.util.Date;
import org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider;

/**
 * @author Ryan D. Brooks
 */
public final class SimpleDateAttribute extends DateAttribute {

   public SimpleDateAttribute(AttributeType attributeType, ICharacterAttributeDataProvider dataProvider) {
      super(attributeType, dataProvider);
   }

   @Override
   public void setFromString(String value) throws Exception {
      Date toSet = null;
      if (value == null || value.equals("")) {
         toSet = new Date(1);
      } else {
         toSet = DateFormat.getDateInstance().parse(value);
      }
      setValue(toSet);
   }

}