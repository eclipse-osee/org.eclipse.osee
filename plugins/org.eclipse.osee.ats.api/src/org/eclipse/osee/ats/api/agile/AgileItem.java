/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * @author Donald G. Dunne
 */
public class AgileItem extends JaxAtsObject {

   private String name;
   private String assignees;
   private String implementers;
   private String assigneesOrImplementers;
   private String agilePoints;
   private String state;
   private int order;
   private String atsId;
   private long id;
   private String featureGroups;
   private String sprint;
   private String backlog;
   private String changeType;
   private String version;
   private String unPlannedWork;
   private String notes;
   private String createDate;
   private String compCancelDate;
   private String link;

   public AgileItem() {
      // for jax-rs
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   public String getAssignees() {
      return assignees;
   }

   public void setAssignees(String assignees) {
      this.assignees = assignees;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public int getOrder() {
      return order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

   @Override
   public Long getId() {
      return id;
   }

   @Override
   public void setId(Long id) {
      this.id = id;
   }

   public String getFeatureGroups() {
      return featureGroups;
   }

   public void setFeatureGroups(String featureGroups) {
      this.featureGroups = featureGroups;
   }

   public String getSprint() {
      return sprint;
   }

   public void setSprint(String sprint) {
      this.sprint = sprint;
   }

   public String getBacklog() {
      return backlog;
   }

   public void setBacklog(String backlog) {
      this.backlog = backlog;
   }

   public String getChangeType() {
      return changeType;
   }

   public void setChangeType(String changeType) {
      this.changeType = changeType;
   }

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getNotes() {
      return notes;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }

   public String getCreateDate() {
      return createDate;
   }

   public void setCreateDate(String createDate) {
      this.createDate = createDate;
   }

   public String getCompCancelDate() {
      return compCancelDate;
   }

   public void setCompCancelDate(String compCancelDate) {
      this.compCancelDate = compCancelDate;
   }

   public String getLink() {
      return link;
   }

   public void setLink(String link) {
      this.link = link;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getUnPlannedWork() {
      return unPlannedWork;
   }

   public void setUnPlannedWork(String unPlannedWork) {
      this.unPlannedWork = unPlannedWork;
   }

   public String getImplementers() {
      return implementers;
   }

   public void setImplementers(String implementers) {
      this.implementers = implementers;
   }

   public String getAssigneesOrImplementers() {
      return assigneesOrImplementers;
   }

   public void setAssigneesOrImplementers(String assigneesOrImplementers) {
      this.assigneesOrImplementers = assigneesOrImplementers;
   }

   public String getAgilePoints() {
      return agilePoints;
   }

   public void setAgilePoints(String agilePoints) {
      this.agilePoints = agilePoints;
   }

}
