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

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */

@XmlRootElement(name = "DispoItemData")
public class DispoItemData implements DispoItem {
   private String guid;
   private String name;
   private String assignee;
   private Date creationDate;
   private Date lastUpdate;
   private String status;
   private String version;
   private JSONObject discrepanciesList;
   private JSONArray annotationsList;
   private String totalPoints;
   private Boolean needsRerun;
   private String machine;
   private String category;
   private String elapsedTime;
   private Boolean aborted;
   private String itemNotes;

   public DispoItemData() {

   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public String getAssignee() {
      return assignee;
   }

   @Override
   public Date getCreationDate() {
      return creationDate;
   }

   @Override
   public Date getLastUpdate() {
      return lastUpdate;
   }

   @Override
   public String getStatus() {
      return status;
   }

   @Override
   public String getVersion() {
      return version;
   }

   @Override
   public JSONObject getDiscrepanciesList() {
      return discrepanciesList;
   }

   @Override
   public JSONArray getAnnotationsList() {
      return annotationsList;
   }

   @Override
   public String getTotalPoints() {
      return totalPoints;
   }

   @Override
   public Boolean getNeedsRerun() {
      return needsRerun;
   }

   @Override
   public String getMachine() {
      return machine;
   }

   @Override
   public String getCategory() {
      return category;
   }

   @Override
   public String getElapsedTime() {
      return elapsedTime;
   }

   @Override
   public Boolean getAborted() {
      return aborted;
   }

   @Override
   public String getItemNotes() {
      return itemNotes;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDiscrepanciesList(JSONObject discrepanciesList) {
      this.discrepanciesList = discrepanciesList;
   }

   public void setAnnotationsList(JSONArray annotationsList) {
      this.annotationsList = annotationsList;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public void setLastUpdate(Date lastUpdate) {
      this.lastUpdate = lastUpdate;
   }

   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void setAssignee(String assignee) {
      this.assignee = assignee;
   }

   public void setTotalPoints(String totalPoints) {
      this.totalPoints = totalPoints;
   }

   public void setNeedsRerun(boolean needsRerun) {
      this.needsRerun = needsRerun;
   }

   public void setMachine(String machine) {
      this.machine = machine;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public void setElapsedTime(String elapsedTime) {
      this.elapsedTime = elapsedTime;
   }

   public void setAborted(Boolean aborted) {
      this.aborted = aborted;
   }

   public void setItemNotes(String itemNotes) {
      this.itemNotes = itemNotes;
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