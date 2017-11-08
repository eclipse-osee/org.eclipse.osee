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

import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;

/**
 * @author Donald G. Dunne
 */
public class CpaDecision extends NamedIdentity<String> {

   private String applicability, rationale, assignees, pcrSystem, origPcrLocation, decisionLocation,
      duplicatedPcrLocation, completedBy, completedDate, duplicatedPcrId;
   private boolean complete = false;
   private CpaPcr originatingPcr;

   public CpaDecision() {
      super(null, null);
   }

   public CpaDecision(String id, String name) {
      super(id, name);
   }

   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   public boolean isComplete() {
      return complete;
   }

   public void setComplete(Boolean complete) {
      this.complete = complete;
   }

   public String getPcrSystem() {
      return pcrSystem;
   }

   public void setPcrSystem(String pcrSystem) {
      this.pcrSystem = pcrSystem;
   }

   public String getAssignees() {
      return assignees;
   }

   public void setAssignees(String assignees) {
      this.assignees = assignees;
   }

   public String getOrigPcrLocation() {
      return origPcrLocation;
   }

   public void setOrigPcrLocation(String origPcrLocation) {
      this.origPcrLocation = origPcrLocation;
   }

   public String getDecisionLocation() {
      return decisionLocation;
   }

   public void setDecisionLocation(String decisionLocation) {
      this.decisionLocation = decisionLocation;
   }

   public String getDuplicatedPcrLocation() {
      return duplicatedPcrLocation;
   }

   public void setDuplicatedPcrLocation(String duplicatedPcrLocation) {
      this.duplicatedPcrLocation = duplicatedPcrLocation;
   }

   public String completedBy() {
      return null;
   }

   public String completedDate() {
      return null;
   }

   public String getCompletedBy() {
      return completedBy;
   }

   public void setCompletedBy(String completedBy) {
      this.completedBy = completedBy;
   }

   public String getCompletedDate() {
      return completedDate;
   }

   public void setCompletedDate(String completedDate) {
      this.completedDate = completedDate;
   }

   public String getApplicability() {
      return applicability;
   }

   public void setApplicability(String applicability) {
      this.applicability = applicability;
   }

   public String getDuplicatedPcrId() {
      return duplicatedPcrId;
   }

   public void setDuplicatedPcrId(String duplicatedPcrId) {
      this.duplicatedPcrId = duplicatedPcrId;
   }

   public CpaPcr getOriginatingPcr() {
      return originatingPcr;
   }

   public void setOriginatingPcr(CpaPcr originatingPcr) {
      this.originatingPcr = originatingPcr;
   }

}
