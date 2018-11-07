/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.event;

/**
 * @author Donald G. Dunne
 */
public enum RelationOrderModType {
   Default("AFRkIhe6hTMJL8pL4IAA"),
   Absolute("AFRkIhftz3PrR0yVYqwA");

   private final String guid;

   private RelationOrderModType(String guid) {
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public static RelationOrderModType getType(String guid) {
      for (RelationOrderModType type : values()) {
         if (type.guid.equals(guid)) {
            return type;
         }
      }
      return null;
   }

};
