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

   private String guid;
   private String operation;
   private JSONArray notesList;
   private String importState;
   private String operationStatus;

   public DispoSetData() {

   }

   @Override
   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public String getOperation() {
      return operation;
   }

   public String getOperationStatus() {
      return operationStatus;
   }

   public void setOperation(String operation) {
      this.operation = operation;
   }

   public void setNotesList(JSONArray notesList) {
      this.notesList = notesList;
   }

   public void setOperationStatus(String operationStatus) {
      this.operationStatus = operationStatus;
   }

   @Override
   public JSONArray getNotesList() {
      return notesList;
   }

   @Override
   public String getImportState() {
      return importState;
   }

   public void setImportState(String importState) {
      this.importState = importState;
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
