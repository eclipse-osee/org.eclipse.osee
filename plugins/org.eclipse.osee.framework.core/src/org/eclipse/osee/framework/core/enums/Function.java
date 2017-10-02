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
