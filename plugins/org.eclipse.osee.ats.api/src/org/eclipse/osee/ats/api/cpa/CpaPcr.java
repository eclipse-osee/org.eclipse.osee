/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.cpa;

/**
 * @author Donald G. Dunne
 */
public class CpaPcr {
   private String programName, priority, title, responsible, responsibleTeam, subsystem, status, id;

   public String getProgramName() {
      return programName;
   }

   public void setProgramName(String programName) {
      this.programName = programName;
   }

   public String getPriority() {
      return priority;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getResponsible() {
      return responsible;
   }

   public void setResponsible(String responsible) {
      this.responsible = responsible;
   }

   public String getSubsystem() {
      return subsystem;
   }

   public void setSubsystem(String subsystem) {
      this.subsystem = subsystem;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getResponsibleTeam() {
      return responsibleTeam;
   }

   public void setResponsibleTeam(String responsibleTeam) {
      this.responsibleTeam = responsibleTeam;
   }

}
