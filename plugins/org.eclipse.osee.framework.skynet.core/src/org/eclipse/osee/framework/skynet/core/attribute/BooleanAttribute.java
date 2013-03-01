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
import org.eclipse.osee.framework.database.core.OseeInfo;

/**
 * @author Ryan D. Brooks
 */
public class BooleanAttribute extends CharacterBackedAttribute<Boolean> {
   public static final String[] booleanChoices = {"true", "false"};
   private static final String[] oldChoices = {"yes", "no"};

   // if true, then database is using yes/no values
   private static final String YES_NO_KEY = "yes.no.values";

   @Override
   public Boolean getValue() throws OseeCoreException {
      if (OseeInfo.isCacheEnabled(YES_NO_KEY)) {
         return getAttributeDataProvider().getValueAsString().equals(oldChoices[0]);
      } else {
         return Boolean.valueOf(getAttributeDataProvider().getValueAsString());
      }
   }

   @Override
   public boolean subClassSetValue(Boolean value) throws OseeCoreException {
      if (OseeInfo.isCacheEnabled(YES_NO_KEY)) {
         return getAttributeDataProvider().setValue(value ? oldChoices[0] : oldChoices[1]);
      } else {
         return getAttributeDataProvider().setValue(String.valueOf(value));
      }
   }

   @Override
   protected Boolean convertStringToValue(String value) throws OseeCoreException {
      if (OseeInfo.isCacheEnabled(YES_NO_KEY)) {
         return value != null && value.equalsIgnoreCase(oldChoices[0]);
      } else {
         return Boolean.parseBoolean(value);
      }
   }

}