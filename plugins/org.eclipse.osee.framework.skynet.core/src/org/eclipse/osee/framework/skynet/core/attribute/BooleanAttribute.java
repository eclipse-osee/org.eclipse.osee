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

/**
 * @author Ryan D. Brooks
 */
public class BooleanAttribute extends CharacterBackedAttribute<Boolean> {
   public static final String[] booleanChoices = {"true", "false"};

   @Override
   public Boolean getValue() throws OseeCoreException {
      return Boolean.valueOf(getAttributeDataProvider().getValueAsString());
   }

   @Override
   public boolean subClassSetValue(Boolean value) throws OseeCoreException {
      return getAttributeDataProvider().setValue(String.valueOf(value));
   }

   @Override
   protected Boolean convertStringToValue(String value) {
      return Boolean.parseBoolean(value);
   }

}