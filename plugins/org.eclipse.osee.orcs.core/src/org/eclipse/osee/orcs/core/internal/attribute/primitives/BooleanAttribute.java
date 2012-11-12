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
package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.CharacterBackedAttribute;

/**
 * @author Ryan D. Brooks
 */
@OseeAttribute("BooleanAttribute")
public class BooleanAttribute extends CharacterBackedAttribute<Boolean> {
   private static final List<String> oldChoices = Arrays.asList("yes", "no");

   @Override
   public Boolean getValue() throws OseeCoreException {
      return convertStringToValue(getDataProxy().getValueAsString());
   }

   @Override
   public boolean subClassSetValue(Boolean value) throws OseeCoreException {
      return getDataProxy().setValue(String.valueOf(value));
   }

   @Override
   protected Boolean convertStringToValue(String value) {
      if (oldChoices.contains(value)) {
         return value.equalsIgnoreCase(oldChoices.get(0));
      }
      return Boolean.valueOf(value);
   }
}