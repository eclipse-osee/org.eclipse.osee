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

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * @author Ryan D. Brooks
 */
@OseeAttribute("EnumeratedAttribute")
public class EnumeratedAttribute extends StringAttribute {
   // When an enumerated attribute is required for an artifact, yet doesn't exist yet, it is created upon
   // init of the artifact and given the "Unspecified" value
   public static String UNSPECIFIED_VALUE = "Unspecified";

   @Override
   public String getDisplayableString() throws OseeCoreException {
      String toDisplay = getDataProxy().getDisplayableString();
      return Strings.isValid(toDisplay) ? toDisplay : "<Select>";
   }

   @Override
   public boolean subClassSetValue(String value) throws OseeCoreException {
      return super.subClassSetValue(value);
   }
}