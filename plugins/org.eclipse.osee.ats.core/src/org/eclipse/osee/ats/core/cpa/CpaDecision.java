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
import org.eclipse.osee.ats.api.cpa.IAtsCpaDecision;
import org.eclipse.osee.ats.api.cpa.ICpaPcr;
import org.eclipse.osee.framework.jdk.core.type.UuidNamedIdentity;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class CpaDecision extends UuidNamedIdentity<String> implements IAtsCpaDecision {

   private String applicability, rationale, assignees, pcrSystem, origPcrLocation, decisionLocation,
      duplicatedPcrLocation, completedBy, completedDate, duplicatedPcrId;
   private boolean complete = false;
   private ICpaPcr originatingPcr;

   public CpaDecision(String id, String name) {
      super(id, name);
   }

   @Override
   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   @Override
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

   @Override
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

   @Override
   public String completedBy() {
      return null;
   }

   @Override
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

   @Override
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

   public ICpaPcr getOriginatingPcr() {
      return originatingPcr;
   }

   public void setOriginatingPcr(ICpaPcr originatingPcr) {
      this.originatingPcr = originatingPcr;
   }

}
