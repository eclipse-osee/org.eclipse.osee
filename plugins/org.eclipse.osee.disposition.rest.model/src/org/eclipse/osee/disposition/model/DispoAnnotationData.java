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
   private boolean resolutionTypeValid;
   private String resolutionMethodType;

   public DispoAnnotationData() {
      guid = GUID.create();
      idsOfCoveredDiscrepancies = new ArrayList<>();
   }

   public void setName(String name) {
      this.locationRefs = name;
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

   public boolean getResolutionTypeValid() {
      return resolutionTypeValid;
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

   public void setResolutionTypeValid(boolean resolutionTypeValid) {
      this.resolutionTypeValid = resolutionTypeValid;
   }
}