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
package org.eclipse.osee.ats.core.cpa;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.ats.api.cpa.ICpaPcr;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class CpaPcr implements ICpaPcr {
   private String programName, priority, title, responsible, responsibleTeam, subsystem, status, id;

   @Override
   public String getProgramName() {
      return programName;
   }

   public void setProgramName(String programName) {
      this.programName = programName;
   }

   @Override
   public String getPriority() {
      return priority;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

   @Override
   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   public String getResponsible() {
      return responsible;
   }

   public void setResponsible(String responsible) {
      this.responsible = responsible;
   }

   @Override
   public String getSubsystem() {
      return subsystem;
   }

   public void setSubsystem(String subsystem) {
      this.subsystem = subsystem;
   }

   @Override
   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   @Override
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   @Override
   public String getResponsibleTeam() {
      return responsibleTeam;
   }

   public void setResponsibleTeam(String responsibleTeam) {
      this.responsibleTeam = responsibleTeam;
   }

}
