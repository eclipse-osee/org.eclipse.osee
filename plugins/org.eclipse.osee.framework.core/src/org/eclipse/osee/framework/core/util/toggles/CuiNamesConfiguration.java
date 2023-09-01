/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.util.toggles;

import java.util.Objects;

/**
 * An enumeration of the expected values for the "CuiNamesConfiguration" manifest unloaded configuration toggle.
 *
 * @author Loren K. Ashley
 */

public enum CuiNamesConfiguration {

   /**
    * Indicates the standard display names and descriptions should be used for the CUI related data rights attributes.
    */

   STANDARD(),

   /**
    * Indicates a special set of display names and descriptions should be used for the CUI related data rights
    * attributes.
    */

   VERSION_ONE();

   /**
    * An implementation of a converter function for use with a {@link Toggle} produced with the {@link ToggleFactory} to
    * return toggles values as {@link CuiNamesConfiguration} enumeration members.
    *
    * @param name the "CuiNamesConfiguration" toggle value to be converted.
    * @return when the <code>name</code> matches an enumeration member's token name that enumeration member; otherwise,
    * {@link CuiNamesConfiguration#STANDARD}.
    */

   public static CuiNamesConfiguration convert(String name) {

      if (Objects.isNull(name)) {
         return STANDARD;
      }

      try {
         return CuiNamesConfiguration.valueOf(name);
      } catch (Exception e) {
         return STANDARD;
      }

   }

}

/* EOF */
