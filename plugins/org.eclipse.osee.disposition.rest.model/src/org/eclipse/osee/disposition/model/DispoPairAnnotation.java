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

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Angel Avila
 */
@XmlRootElement(name = "DispoPairAnnotation")
public class DispoPairAnnotation {

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

   private String location; //Code Line
   private int row;
   private String text;
   private Collection<Integer> pairs;
   private boolean isRowCovered;

   public DispoPairAnnotation() {
      guid = GUID.create();

   }

   //get functions
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

   public String getText() {
      return text;
   }

   public String getLocation() {
      return location;
   }

   public boolean getIsRowCovered() {
      return isRowCovered;
   }

   public int getRow() {
      return row;
   }

   public Collection<Integer> getPairs() {
      return pairs;
   }

   //set functions
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

   public void setText(String text) {
      this.text = text;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public void setIsRowCovered(boolean isRowCovered) {
      this.isRowCovered = isRowCovered;
   }

   public void setRow(int row) {
      this.row = row;
   }

   public void setPairs(Collection<Integer> pairs) {
      this.pairs = pairs;
   }
}