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

package org.eclipse.osee.framework.svn.entry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.svn.enums.RepositoryEnums.EntryFields;

/**
 * @author Roberto E. Escobar
 */
public class RepositoryEntry implements IRepositoryEntry {

   private Map<EntryFields, String> entryMap;
   private String modifiedFlag;
   private String fileType;
   private String controlSystem;

   public RepositoryEntry(String fileType, String controlSystem) {
      this.entryMap = new HashMap<EntryFields, String>();
      this.modifiedFlag = "";
      this.fileType = fileType;
      this.controlSystem = controlSystem;
   }

   public String getField(EntryFields field) {
      if (entryMap.containsKey(field)) {
         return entryMap.get(field);
      }
      return "";
   }

   public boolean containsField(EntryFields field) {
      return entryMap.containsKey(field);
   }

   public void setModifiedFlag(String modifiedFlag) {
      this.modifiedFlag = modifiedFlag;
   }

   public void addField(EntryFields field, String value) {
      entryMap.put(field, value);
   }

   public String toString() {
      String toReturn = " Entry: " + getType() + "\n";
      Set<EntryFields> keys = entryMap.keySet();
      for (EntryFields field : keys) {
         toReturn += "\t" + field + ": " + entryMap.get(field) + "\n";
      }
      return toReturn;
   }

   public String getVersion() {
      return getField(EntryFields.committedRev);
   }

   public String getURL() {
      return getField(EntryFields.url);
   }

   public String getModifiedFlag() {
      return modifiedFlag;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.svn.entry.IRepositoryEntry#getLastAuthor()
    */
   public String getLastAuthor() {
      return getField(EntryFields.lastAuthor);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.svn.entry.IRepositoryEntry#getLastModificationDate()
    */
   public String getLastModificationDate() {
      return getField(EntryFields.dateCommitted);
   }

   public String getType() {
      return fileType;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.svn.entry.IRepositoryEntry#getVersionControlSystem()
    */
   public String getVersionControlSystem() {
      return controlSystem;
   }
}
