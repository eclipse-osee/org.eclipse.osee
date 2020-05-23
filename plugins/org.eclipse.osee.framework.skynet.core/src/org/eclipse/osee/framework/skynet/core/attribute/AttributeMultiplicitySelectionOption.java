/*********************************************************************
 * Copyright (c) 2019 Boeing
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
