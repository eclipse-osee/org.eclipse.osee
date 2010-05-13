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
package org.eclipse.osee.framework.skynet.core.relation;

/**
 * @author Donald G. Dunne
 */
public enum RelationEventType {

   Deleted("AISIbR2MjAo7JhvDvkgA"),
   Added("AISIbR69A2yjMFpbsSgA"),
   RationaleMod("AISIbR9Tm0dwqN1KdoAA"),
   ReOrdered("AISIbRohWReUi5aitFgA"),
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
