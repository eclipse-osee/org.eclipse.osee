/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl;

import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;

/**
 * @author Donald G. Dunne
 */
public class BooleanDefUtil {

   /**
    * @return if BooleanDef == null return defaultValue, else true or false
    */
   public static boolean get(BooleanDef booleanDef, boolean defaultValue) {
      if (booleanDef != null && booleanDef != BooleanDef.NONE) {
         return booleanDef == BooleanDef.TRUE;
      }
      return defaultValue;
   }

}
