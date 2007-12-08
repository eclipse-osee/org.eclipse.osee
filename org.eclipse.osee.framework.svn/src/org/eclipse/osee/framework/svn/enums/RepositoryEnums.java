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
package org.eclipse.osee.framework.svn.enums;

/**
 * @author Roberto E. Escobar
 */
public class RepositoryEnums {

   public enum ControlledType {
      dir, file;

      public static boolean isDefined(String value) {
         ControlledType[] types = ControlledType.values();
         for (ControlledType type : types) {
            if (type.name().equals(value)) {
               return true;
            }
         }
         return false;
      }
   }

   public enum EntryFields {
      committedRev("committed-rev"),
      fileName("name"),
      committeDate("committed-date"),
      url("url"),
      lastAuthor("last-author"),
      kind("kind"),
      uuid("uuid"),
      repository("repos"),
      currentRevision("revision"),
      textTime("text-time"),
      dateCommitted("committed-date"),
      checksum("checksum"),
      properTime("prop-time");

      private String value;

      private EntryFields(String value) {
         this.value = value;
      }

      public String getEntryName() {
         return value;
      }
   }
}
