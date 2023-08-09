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

import static org.eclipse.osee.disposition.model.DispoStrings.ANALYSIS;
import static org.eclipse.osee.disposition.model.DispoStrings.Exception_Handling_Resolution;
import static org.eclipse.osee.disposition.model.DispoStrings.MODIFY;
import static org.eclipse.osee.disposition.model.DispoStrings.Test_Unit_Resolution;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Angel Avila
 */

@XmlRootElement(name = "DispoAnnotationData")
public class DispoAnnotationData {

   private String guid;
   private int index;
   private String developerNotes;
   private String customerNotes;
   private String locationRefs = "";
   private boolean isConnected;
   private List<String> idsOfCoveredDiscrepancies;
   private String resolution;
   private String lastResolution;
   private boolean isResolutionValid;
   private String resolutionType;
   private boolean isDefault;
   private boolean isAnalyze;
   private boolean needsModify;
   private String resolutionMethodType;
   private boolean isPairAnnotation = false; //For MCDC to determine if it is a pair annotation

   //These are used on Annotations that are parents to Pair Annotations.
   private String possiblePairs; //Used for MCDC pair coverage, gets all of the possible pairs
   private String satisfiedPairs; //Used for MCDC pair coverage, gets all of the satisfied pairs for a Pair Px

   //These are used specifically on Pair Annotations.
   private Collection<Integer> pairedWith; //Used for MCDC coverage to show what a pair annotation pairs with
   private int row; //Used for MCDC pair coverage to show this annotations row
   private boolean isRowCovered;

   public DispoAnnotationData() {
      guid = GUID.create();
      idsOfCoveredDiscrepancies = new ArrayList<>();
   }

   public String getGuid() {
      return guid;
   }

   public String getName() {
      return locationRefs;
   }

   public int getIndex() {
      return index;
   }

   public String getDeveloperNotes() {
      return developerNotes;
   }

   public String getCustomerNotes() {
      return customerNotes;
   }

   public String getLocationRefs() {
      return locationRefs;
   }

   public List<String> getIdsOfCoveredDiscrepancies() {
      return idsOfCoveredDiscrepancies;
   }

   public boolean getIsConnected() {
      return isConnected;
   }

   public String getResolution() {
      return resolution;
   }

   public String getLastResolution() {
      return lastResolution;
   }

   public boolean getIsResolutionValid() {
      return isResolutionValid;
   }

   public String getResolutionType() {
      return resolutionType;
   }

   public boolean getIsDefault() {
      if (resolutionType.equals(Test_Unit_Resolution) || resolutionType.equals(Exception_Handling_Resolution)) {
         isDefault = true;
      } else {
         isDefault = false;
      }
      return isDefault;
   }

   public boolean getIsAnalyze() {
      if (resolutionType.contains(ANALYSIS)) {
         isAnalyze = true;
      } else {
         isAnalyze = false;
      }
      return isAnalyze;
   }

   public boolean getNeedsModify() {
      if (resolutionType.contains(MODIFY)) {
         needsModify = true;
      } else {
         needsModify = false;
      }
      return needsModify;
   }

   public String getResolutionMethodType() {
      return resolutionMethodType;
   }

   public String getPossiblePairs() {
      return possiblePairs;
   }

   public String getSatisfiedPairs() {
      return satisfiedPairs;
   }

   public boolean getIsPairAnnotation() {
      if (locationRefs.contains(").")) {
         isPairAnnotation = true;
      } else {
         isPairAnnotation = false;
      }
      return isPairAnnotation;
   }

   public Collection<Integer> getPairedWith() {
      return pairedWith;
   }

   public int getRow() {
      return row;
   }

   public boolean getIsRowCovered() {
      return isRowCovered;
   }

   public void setLocationRefs(String locationRefs) {
      this.locationRefs = locationRefs;
   }

   public void setDeveloperNotes(String developerNotes) {
      this.developerNotes = developerNotes;
   }

   public void setCustomerNotes(String customerNotes) {
      this.customerNotes = customerNotes;
   }

   public void setId(String id) {
      this.guid = id;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public void setIsConnected(boolean isConnected) {
      this.isConnected = isConnected;
   }

   public void setIdsOfCoveredDiscrepancies(List<String> idsOfCoveredDiscrepancies) {
      this.idsOfCoveredDiscrepancies = idsOfCoveredDiscrepancies;
   }

   public void setResolution(String resolution) {
      this.resolution = resolution;
   }

   public void setLastResolution(String lastResolution) {
      this.lastResolution = lastResolution;
   }

   public void setIsResolutionValid(boolean isResolutionValid) {
      this.isResolutionValid = isResolutionValid;
   }

   public void setResolutionType(String resolutionType) {
      this.resolutionType = resolutionType;
   }

   public void setIsDefault(boolean isDefault) {
      this.isDefault = isDefault;
   }

   public void setIsAnalyze(boolean isAnalyzed) {
      this.isAnalyze = isAnalyzed;
   }

   public void setNeedsModify(boolean isAnalyzed) {
      this.needsModify = needsModify;
   }

   public void setResolutionMethodType(String resolutionMethodType) {
      this.resolutionMethodType = resolutionMethodType;
   }

   @JsonIgnore
   public boolean isValid() {
      return (isConnected || isDefault) && isResolutionValid && isResolutionTypeValid();
   }

   private boolean isResolutionTypeValid() {
      return resolutionType != null && !resolutionType.isEmpty() && !resolutionType.equalsIgnoreCase(
         "None") && !resolutionType.equalsIgnoreCase("null");
   }

   public void disconnect() {
      this.isConnected = false;
      this.idsOfCoveredDiscrepancies = new ArrayList<>();
   }

   public void setPossiblePairs(String possiblePairs) {
      this.possiblePairs = possiblePairs;
   }

   public void setSatisfiedPairs(String satisfiedPairs) {
      this.satisfiedPairs = satisfiedPairs;
   }

   public void setIsPairAnnotation(boolean isPairAnnotation) {
      this.isPairAnnotation = isPairAnnotation;
   }

   public void setPairedWith(Collection<Integer> pairedWith) {
      this.pairedWith = pairedWith;
   }

   public void setRow(int row) {
      this.row = row;
   }

   public void setIsRowCovered(boolean isRowCovered) {
      this.isRowCovered = isRowCovered;
   }
}