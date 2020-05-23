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

package org.eclipse.osee.define.ide.errorhandler;

/**
 * @author Ryan D. Brooks
 */
public class StateValue {

   private boolean isSaveValid;

   public StateValue() {
      super();
      isSaveValid = true;
   }

   public boolean isSaveValid() {
      return isSaveValid;
   }

   public void andSaveValid(boolean isSaveValid) {
      this.isSaveValid &= isSaveValid;
   }

}
