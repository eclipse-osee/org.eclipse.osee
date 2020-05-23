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

package org.eclipse.osee.framework.skynet.core.relation;

/**
 * @author Donald G. Dunne
 */
public enum RelationEventType {

   Deleted("AISIbR2MjAo7JhvDvkgA"),
   Purged("AAn_P4kbcxaUKL4bosgA"),
   Added("AISIbR69A2yjMFpbsSgA"),
   ModifiedRationale("AISIbR9Tm0dwqN1KdoAA"), // this handles modifiedRationale and UnDeleted for now
   Undeleted("AISIbRqzlF3s4TeMvzgA");

   private final String guid;

   private RelationEventType(String guid) {
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public static RelationEventType getType(String guid) {
      for (RelationEventType type : values()) {
         if (type.guid.equals(guid)) {
            return type;
         }
      }
      return null;
   }
}
