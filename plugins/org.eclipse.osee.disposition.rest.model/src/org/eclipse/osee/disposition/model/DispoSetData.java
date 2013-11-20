/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.disposition.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.json.JSONArray;

/**
 * @author Angel Avila
 */

@XmlRootElement(name = "DispoSetData")
public class DispoSetData extends DispoSetDescriptorData implements DispoSet {

   public enum DispositionOperationsEnum {
      NEW_IMPORT,
      RE_IMPORT
   }

   private String guid;
   private DispositionOperationsEnum operation;
   private JSONArray notesList;
   private String importState;
   private String statusCount;

   public DispoSetData() {

   }

   @Override
   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public DispositionOperationsEnum getOperation() {
      return operation;
   }

   public void setOperation(DispositionOperationsEnum operation) {
      this.operation = operation;
   }

   public void setNotesList(JSONArray notesList) {
      this.notesList = notesList;
   }

   @Override
   public JSONArray getNotesList() {
      return notesList;
   }

   public String getImportState() {
      return importState;
   }

   public void setImportState(String importState) {
      this.importState = importState;
   }

   @Override
   public String getStatusCount() {
      return statusCount;
   }

   public void setStatusCount(String statusCount) {
      this.statusCount = statusCount;
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      for (Identity<?> identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }
}
