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

package org.eclipse.osee.orcs.core.ds;

/**
 * @author Roberto E. Escobar
 */
public class Criteria {

   public void checkValid(Options options) {
      // For subclasses to implement
   }

   public boolean isReferenceHandler() {
      return false;
   }

   @Override
   public String toString() {
      return getClass().getSimpleName();
   }
}
