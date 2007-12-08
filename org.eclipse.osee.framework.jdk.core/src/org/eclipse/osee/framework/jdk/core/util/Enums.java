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
package org.eclipse.osee.framework.jdk.core.util;

/**
 * @author Donald G. Dunne
 */
public class Enums {

   public static String getCommaDeliminatedString(Enum<?>[] enums) {
      StringBuffer sb = new StringBuffer();
      for (Enum<?> e : enums)
         sb.append(e.name() + ",");
      return sb.toString().replaceFirst(",$", "");
   }
}
