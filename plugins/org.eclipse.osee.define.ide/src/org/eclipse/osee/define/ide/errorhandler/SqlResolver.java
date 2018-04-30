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

public class SqlResolver implements Resolver {

   public SqlResolver() {
      super();
   }

   @Override
   public boolean resolve(Exception ex, StateValue value) {
      if (ex.getMessage().contains("connection")) {
         value.andSaveValid(false);
         return true;
      }
      return false;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      return this.getClass().getCanonicalName().equals(obj.getClass().getCanonicalName());
   }

   @Override
   public int hashCode() {
      return this.getClass().getCanonicalName().hashCode();
   }

}
