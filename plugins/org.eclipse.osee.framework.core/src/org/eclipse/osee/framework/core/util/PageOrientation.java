/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.framework.core.util;

/**
 * @author Angel Avila
 */
public enum PageOrientation {
   LANDSCAPE,
   PORTRAIT;

   public boolean isPortrait() {
      return this == PORTRAIT;
   }

   public boolean isLandscape() {
      return this == LANDSCAPE;
   }

   public static PageOrientation fromString(String value) {
      PageOrientation toReturn = PORTRAIT;
      if (LANDSCAPE.name().equalsIgnoreCase(value)) {
         toReturn = LANDSCAPE;
      }

      return toReturn;
   }
}