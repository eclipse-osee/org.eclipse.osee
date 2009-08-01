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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public class EnumeratedAttribute extends StringAttribute {
   // When an enumerated attribute is required for an artifact, yet doesn't exist yet, it is created upon
   // init of the artifact and given the "Unspecified" value
   public static String UNSPECIFIED_VALUE = "Unspecified";

   @Override
   public String getDisplayableString() throws OseeCoreException {
      String toDisplay = getAttributeDataProvider().getDisplayableString();
      return Strings.isValid(toDisplay) ? toDisplay : "<Select>";
   }

   @Override
   public boolean subClassSetValue(String value) throws OseeCoreException {
      if (!AttributeTypeManager.getEnumerationValues(getAttributeType()).contains(value)) {
         //throw new OseeArgumentException(value + " is not a valid enumeration of the type " + getAttributeType());
      }
      return super.subClassSetValue(value);
   }
}