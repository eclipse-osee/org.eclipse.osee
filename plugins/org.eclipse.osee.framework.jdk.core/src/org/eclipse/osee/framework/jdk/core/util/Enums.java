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

package org.eclipse.osee.framework.jdk.core.util;

/**
 * @author Donald G. Dunne
 */
public class Enums {

   public static String getCommaDeliminatedString(Enum<?>[] enums) {
      StringBuffer sb = new StringBuffer();
      for (Enum<?> e : enums) {
         sb.append(e.name() + ",");
      }
      return sb.toString().replaceFirst(",$", "");
   }
}
