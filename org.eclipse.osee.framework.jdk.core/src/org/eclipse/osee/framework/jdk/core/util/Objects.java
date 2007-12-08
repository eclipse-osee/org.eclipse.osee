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
 * Utility class for methods useful to all Object's
 * 
 * @author Robert A. Fisher
 */
public final class Objects {

   /**
    * Produces a hash code that uses all of the supplied values. This method is guaranteed to return the same value
    * given the same input.
    * 
    * @param PRIME_1
    * @param PRIME_2
    * @param data
    */
   public static int hashCode(int PRIME_1, int PRIME_2, Object... data) {
      int result = PRIME_1;

      for (Object val : data) {
         result = PRIME_2 * result + val.hashCode();
      }

      return result;
   }

}
