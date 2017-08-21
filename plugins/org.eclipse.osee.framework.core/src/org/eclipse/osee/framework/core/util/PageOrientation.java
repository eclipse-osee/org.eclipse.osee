/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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