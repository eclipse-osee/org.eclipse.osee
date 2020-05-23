/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * @author Ryan D. Brooks
 */
@OseeAttribute("EnumeratedAttribute")
public class EnumeratedAttribute extends StringAttribute {
   public static final String NAME = EnumeratedAttribute.class.getSimpleName();

   public EnumeratedAttribute(Long id) {
      super(id);
   }

   @Override
   public String getDisplayableString() {
      String toDisplay = getDataProxy().getDisplayableString();
      return Strings.isValid(toDisplay) ? toDisplay : "<Select>";
   }
}