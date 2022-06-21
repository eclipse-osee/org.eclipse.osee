/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * @author Angel Avila
 */

@XmlRootElement(name = "DispoSetData")
public class DispoSetData extends DispoSetDescriptorData implements DispoSet {

   private String guid;
   private String operation;
   private List<Note> notesList;
   private String importState;
   private OperationReport operationSummary;
   private String ciSet;
   private String rerunList;
   private Date time;

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

   public void setOperation(String operation) {
      this.operation = operation;
   }

   public void setNotesList(List<Note> notesList) {
      this.notesList = notesList;
   }

   public void setOperationSummary(OperationReport operationSummary) {
      this.operationSummary = operationSummary;
   }

   @Override
   public List<Note> getNotesList() {
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
   public OperationReport getOperationSummary() {
      return operationSummary;
   }

   public void setCiSet(String ciSet) {
      this.ciSet = ciSet;
   }

   @Override
   public String getCiSet() {
      return ciSet;
   }

   @Override
   public String getRerunList() {
      return rerunList;
   }

   public void setRerunList(String rerunList) {
      this.rerunList = rerunList;
   }

   @Override
   public Date getTime() {
      return time;
   }

   public void setTime(Date time) {
      this.time = time;
   }
}
