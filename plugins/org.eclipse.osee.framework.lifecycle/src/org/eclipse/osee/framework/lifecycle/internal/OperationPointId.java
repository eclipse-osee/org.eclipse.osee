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

package org.eclipse.osee.framework.lifecycle.internal;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public enum OperationPointId {
   NOOP_ID,
   PRE_CONDITION_ID,
   POST_CONDITION_ID,
   CHECK_CONDITION_ID;

   public static OperationPointId toEnum(String sourceId) {
      OperationPointId toReturn = OperationPointId.NOOP_ID;
      for (OperationPointId pointId : OperationPointId.values()) {
         if (pointId.name().equalsIgnoreCase(sourceId)) {
            toReturn = pointId;
            break;
         }
      }
      return toReturn;
   }
}