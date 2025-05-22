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

   private String guid = "";
   private int index;
   private String developerNotes = "";
   private String customerNotes = "";
   private String locationRefs = "";
   private boolean isConnected;
   private List<String> idsOfCoveredDiscrepancies = new ArrayList<String>();
   private String resolutionType = "";
   private String resolution = "";
   private String lastResolutionType = "";
   private String lastResolution = "";
   private String lastManualResolutionType = "";
   private String lastManualResolution = "";
   private boolean isResolutionValid;
   private boolean isDefault;
   private boolean isAnalyze;
   private boolean needsModify;
   private String resolutionMethodType = "";
   private boolean isPairAnnotation; //For MCDC to determine if it is a pair annotation

   //These are used on Annotations that are parents to Pair Annotations.
   private String possiblePairs = ""; //Used for MCDC pair coverage, gets all of the possible pairs
   private String satisfiedPairs = ""; //Used for MCDC pair coverage, gets all of the satisfied pairs for a Pair Px

   //These are used specifically on Pair Annotations.
   private Collection<Integer> pairedWith = new ArrayList<Integer>(); //Used for MCDC coverage to show what a pair annotation pairs with
   private int row; //Used for MCDC pair coverage to show this annotations row
   private boolean isRowCovered;

   public static DispoAnnotationData SENTINEL = valueOf("-1");

   public static DispoAnnotationData valueOf(String guid) {
      final class DispoAnnotationDataImpl extends DispoAnnotationData {
         private final String guid;

         public DispoAnnotationDataImpl(String guid) {
            this.guid = guid;
         }

         @Override
         public String getGuid() {
            return guid;
         }
      }
      return new DispoAnnotationDataImpl(guid);
   }

   public DispoAnnotationData() {
      guid = GUID.create();
      idsOfCoveredDiscrepancies = new ArrayList<>();
   }

   public DispoAnnotationData(DispoAnnotationData annotationData) {
      if (annotationData.getGuid() != null) {
         this.guid = annotationData.getGuid();
      } else {
         this.guid = GUID.create();
      }

      if (annotationData.getResolutionType() != null) {
         this.resolutionType = annotationData.getResolutionType();
      }
      if (annotationData.getResolution() != null) {
         this.resolution = annotationData.getResolution();
      }
      if (annotationData.getLastResolutionType() != null) {
         this.lastResolutionType = annotationData.getLastResolutionType();
      }
      if (annotationData.getLastResolution() != null) {
         this.lastResolution = annotationData.getLastResolution();
      }
      if (annotationData.getLastManualResolutionType() != null) {
         this.lastManualResolutionType = annotationData.getLastManualResolutionType();
      }
      if (annotationData.getLastManualResolution() != null) {
         this.lastManualResolution = annotationData.getLastManualResolution();
      }
      if (annotationData.getDeveloperNotes() != null) {
         this.developerNotes = annotationData.getDeveloperNotes();
      }
      if (annotationData.getCustomerNotes() != null) {
         this.customerNotes = annotationData.getCustomerNotes();
      }
      if (annotationData.getLocationRefs() != null) {
         this.locationRefs = annotationData.getLocationRefs();
      }
      if (annotationData.getIdsOfCoveredDiscrepancies() != null) {
         this.idsOfCoveredDiscrepancies = annotationData.getIdsOfCoveredDiscrepancies();
      }
      if (annotationData.getResolutionMethodType() != null) {
         this.resolutionMethodType = annotationData.getResolutionMethodType();
      }
      if (annotationData.getPossiblePairs() != null) {
         this.possiblePairs = annotationData.getPossiblePairs();
      }
      if (annotationData.getSatisfiedPairs() != null) {
         this.satisfiedPairs = annotationData.getSatisfiedPairs();
      }
      if (annotationData.getPairedWith() != null) {
         this.pairedWith = annotationData.getPairedWith();
      }
      this.index = annotationData.getIndex();
      this.isConnected = annotationData.getIsConnected();
      this.isDefault = annotationData.getIsDefault();
      this.isPairAnnotation = annotationData.getIsPairAnnotation();
      this.isRowCovered = annotationData.getIsRowCovered();
      this.row = annotationData.getRow();
      this.isResolutionValid = annotationData.getIsResolutionValid();
      this.isAnalyze = annotationData.setAndGetIsAnalyze();
      this.needsModify = annotationData.setAndGetNeedsModify();
   }

   public DispoAnnotationData(DispoAnnotationData annotationData, String resolutionType, String resolution) {
      this(annotationData);
      if (resolutionType != null) {
         this.resolutionType = resolutionType;
      }
      if (resolution != null) {
         this.resolution = resolution;
      }
      this.isAnalyze = this.setAndGetIsAnalyze();
      this.needsModify = this.setAndGetNeedsModify();
   }

   public DispoAnnotationData(DispoAnnotationData annotationData, String lastResolutionType, String lastResolution, String lastManualResolutionType, String lastManualResolution) {
      this(annotationData);
      if (lastResolutionType != null) {
         this.lastResolutionType = lastResolutionType;
      }
      if (lastResolution != null) {
         this.lastResolution = lastResolution;
      }
      if (lastManualResolutionType != null) {
         this.lastManualResolutionType = lastManualResolutionType;
      }
      if (lastManualResolution != null) {
         this.lastManualResolution = lastManualResolution;
      }
   }

   public DispoAnnotationData(DispoAnnotationData annotationData, String resolutionType, String resolution, String lastResolutionType, String lastResolution, String lastManualResolutionType, String lastManualResolution, boolean isResolutionValid) {
      this(annotationData, lastResolutionType, lastResolution, lastManualResolutionType, lastManualResolution);
      if (resolutionType != null) {
         this.resolutionType = resolutionType;
      }
      if (resolution != null) {
         this.resolution = resolution;
      }
      this.isResolutionValid = isResolutionValid;
      this.isAnalyze = this.setAndGetIsAnalyze();
      this.needsModify = this.setAndGetNeedsModify();
   }

   public DispoAnnotationData(DispoAnnotationData annotationData, String resolutionType, String resolution, boolean isResolutionValid, boolean isDefault, boolean isConnected) {
      this(annotationData, resolutionType, resolution);
      this.isResolutionValid = isResolutionValid;
      this.isDefault = isDefault;
      this.isConnected = isConnected;
   }

   public DispoAnnotationData(DispoAnnotationData annotationData, String resolutionType, String resolution, String satisfiedPairs, boolean isResolutionValid, boolean isDefault, boolean isConnected) {
      this(annotationData, resolutionType, resolution, isResolutionValid, isDefault, isConnected);
      if (satisfiedPairs != null) {
         this.satisfiedPairs = satisfiedPairs;
      }
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
      if (resolution == null) {
         this.resolution = "";
      }
      return resolution;
   }

   public String getLastResolutionType() {
      return lastResolutionType;
   }

   public String getLastResolution() {
      return lastResolution;
   }

   public String getLastManualResolutionType() {
      return lastManualResolutionType;
   }

   public String getLastManualResolution() {
      return lastManualResolution;
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

   //We retain this as this variable is used in JS.
   public boolean setAndGetIsAnalyze() {
      if (resolutionType.contains(ANALYSIS)) {
         this.isAnalyze = true;
      } else {
         this.isAnalyze = false;
      }
      return this.isAnalyze;
   }

   //We retain this as this variable is used in JS
   public boolean setAndGetNeedsModify() {
      if (resolutionType.contains(MODIFY)) {
         this.needsModify = true;
      } else {
         this.needsModify = false;
      }
      return this.needsModify;
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

   public boolean getIsParentPair() {
      if (locationRefs.contains("(P") && !locationRefs.contains(").")) {
         return true;
      } else {
         return false;
      }
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

   public void setLastResolutionType(String lastResolutionType) {
      if (lastResolutionType == null) {
         return;
      }
      this.lastResolutionType = lastResolutionType;
   }

   public void setFilledLastResolutionType(String lastResolutionType) {
      if (lastResolutionType == null || lastResolutionType.isEmpty() || lastResolutionType.isBlank()) {
         return;
      }
      this.lastResolutionType = lastResolutionType;
   }

   public void setLastResolution(String lastResolution) {
      if (lastResolution == null) {
         return;
      }
      this.lastResolution = lastResolution;
   }

   public void setFilledLastResolution(String lastResolution) {
      if (lastResolution == null || lastResolution.isEmpty() || lastResolution.isBlank()) {
         return;
      }
      this.lastResolution = lastResolution;
   }

   public void setLastManualResolutionType(String lastManualResolutionType) {
      if (lastManualResolutionType == null) {
         return;
      }
      this.lastManualResolutionType = lastManualResolutionType;
   }

   public void setFilledLastManualResolutionType(String lastManualResolutionType) {
      if (lastManualResolutionType == null || lastManualResolutionType.isEmpty() || lastManualResolutionType.isBlank()) {
         return;
      }
      this.lastManualResolutionType = lastManualResolutionType;
   }

   public void setLastManualResolution(String lastManualResolution) {
      if (lastManualResolution == null) {
         return;
      }
      this.lastManualResolution = lastManualResolution;
   }

   public void setFilledLastManualResolution(String lastManualResolution) {
      if (lastManualResolution == null || lastManualResolution.isEmpty() || lastManualResolution.isBlank()) {
         return;
      }
      this.lastManualResolution = lastManualResolution;
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

   public void setResolutionMethodType(String resolutionMethodType) {
      this.resolutionMethodType = resolutionMethodType;
   }

   @JsonIgnore
   public boolean isResolutionValid() {
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

   public boolean isValid() {
      return !this.equals(DispoAnnotationData.SENTINEL);
   }
}