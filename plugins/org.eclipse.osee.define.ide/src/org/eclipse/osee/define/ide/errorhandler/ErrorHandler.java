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
package org.eclipse.osee.define.ide.errorhandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ryan D. Brooks
 */
public class ErrorHandler {

   private final Set<Resolver> set;
   private final StateValue value;

   public ErrorHandler() {
      super();
      this.set = new HashSet<>();
      this.value = new StateValue();
   }

   public void processException(Exception ex) {
      boolean resolved = false;
      for (Resolver r : set) {
         if (r.resolve(ex, value)) {
            resolved = true;
         }
      }
      if (!resolved) {
         throw new RuntimeException("Exception not resolved", ex);
      }
   }

   public void addResolver(Resolver resolver) {
      set.add(resolver);
   }

   public boolean isSaveValid() {
      return value.isSaveValid();
   }
}
