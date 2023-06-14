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

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
   private String resolutionMethodType;
   private String possiblePairs; //Used for MCDC pair coverage, gets all of the possible pairs
   private String pairs; //Used for MCDC pair coverage, gets all of the satisfied pairs for a Pair Px
   private Map<Integer, DispoPairAnnotation> pairAnnotations; //Used for MCDC pair coverage

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
      return isDefault;
   }

   public boolean getIsAnalyze() {
      return isAnalyze;
   }

   public String getResolutionMethodType() {
      return resolutionMethodType;
   }

   public String getPossiblePairs() {
      return possiblePairs;
   }

   public String getPairs() {
      return pairs;
   }

   public Map<Integer, DispoPairAnnotation> getPairAnnotations() {
      return pairAnnotations;
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

   public void setPairs(String pairs) {
      this.pairs = pairs;
   }

   public void setPairAnnotations(Map<Integer, DispoPairAnnotation> pairAnnotation) {
      this.pairAnnotations = pairAnnotation;
   }
}