/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public class OrcsTransactionUtil {

   public static List<Integer> asIntegerList(String rawValue) {
      List<Integer> toReturn;
      if (Strings.isValid(rawValue)) {
         String[] entries = rawValue.split(",");
         toReturn = new ArrayList<>();
         for (String entry : entries) {
            Integer value = Integer.parseInt(entry.trim());
            toReturn.add(value);
         }
      } else {
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }

   public static <T> T executeCallable(Callable<T> callable) {
      try {
         return callable.call();
      } catch (Exception ex) {
         if (ex instanceof OseeCoreException) {
            throw (OseeCoreException) ex;
         } else {
            throw new OseeCoreException(ex);
         }
      }
   }
}
