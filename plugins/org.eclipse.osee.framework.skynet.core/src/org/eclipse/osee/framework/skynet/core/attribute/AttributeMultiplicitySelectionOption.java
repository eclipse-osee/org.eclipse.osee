/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute;

import java.util.HashMap;

/**
 * @author Jeremy A. Midvidy
 */
public enum AttributeMultiplicitySelectionOption {

   AddSelection,
   ReplaceAll,
   DeleteSelected,
   RemoveAll;

   public static HashMap<AttributeMultiplicitySelectionOption, Boolean> getOptionMap() {
      HashMap<AttributeMultiplicitySelectionOption, Boolean> map =
         new HashMap<AttributeMultiplicitySelectionOption, Boolean>();
      for (AttributeMultiplicitySelectionOption opt : values()) {
         map.put(opt, false);
      }
      return map;
   }

}
