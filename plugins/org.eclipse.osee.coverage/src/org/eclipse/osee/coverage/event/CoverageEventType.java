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
package org.eclipse.osee.coverage.event;

/**
 * @author Donald G. Dunne
 */
public enum CoverageEventType {

   Deleted("ABcgW44X_jiwtDxrfAgA"),
   Added("ABcgW5K27j1dQf8cC5gA"),
   Modified("ABcgW5NCnS8CxqjhS9QA");

   private final String guid;

   private CoverageEventType(String guid) {
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public static CoverageEventType getType(String guid) {
      for (CoverageEventType type : values()) {
         if (type.guid.equals(guid)) {
            return type;
         }
      }
      return null;
   }
}
