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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;

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
   private boolean isResolutionValid;
   private String resolutionType;
   private boolean isDefault;
   private boolean isAnalyze;
   private boolean resolutionTypeValid;
   private String resolutionMethodType;

   public DispoAnnotationData() {
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