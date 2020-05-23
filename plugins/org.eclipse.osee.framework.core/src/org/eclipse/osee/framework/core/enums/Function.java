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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Megumi Telles
 */
public enum Function {

   BRANCH_COMMIT,
   CHANGE_REPORT,
   CREATE_BRANCH,
   PURGE_BRANCH,
   UPDATE_BRANCH_TYPE,
   UPDATE_BRANCH_STATE,
   UPDATE_ARCHIVE_STATE;

   public static Function fromString(String toMatch) {
      for (Function function : Function.values()) {
         if (function.name().equalsIgnoreCase(toMatch)) {
            return function;
         }
      }
      throw new OseeCoreException("Invalid name - Function [%s] was not found ", toMatch);
   }
}
