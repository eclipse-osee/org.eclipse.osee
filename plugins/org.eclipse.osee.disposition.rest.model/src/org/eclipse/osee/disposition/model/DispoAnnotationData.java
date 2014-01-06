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

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.json.JSONArray;

/**
 * @author Angel Avila
 */

@XmlRootElement(name = "DispoAnnotationData")
public class DispoAnnotationData implements Identifiable<String> {

   private String id;
   private JSONArray notesList;
   private String locationRefs;
   private boolean isConnected;
   private JSONArray idsOfCoveredDiscrepancies;
   private String resolution;
   private boolean isResolutionValid;

   public DispoAnnotationData() {

   }

   @Override
   public String getGuid() {
      return id;
   }

   @Override
   public String getName() {
      return locationRefs;
   }

   public String getId() {
      return id;
   }

   public JSONArray getNotesList() {
      return notesList;
   }

   public String getLocationRefs() {
      return locationRefs;
   }

   public JSONArray getIdsOfCoveredDiscrepancies() {
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

   public void setLocationRefs(String locationRefs) {
      this.locationRefs = locationRefs;
   }

   public void setNotesList(JSONArray notesList) {
      this.notesList = notesList;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setIsConnected(boolean isConnected) {
      this.isConnected = isConnected;
   }

   public void setIdsOfCoveredDiscrepancies(JSONArray idsOfCoveredDiscrepancies) {
      this.idsOfCoveredDiscrepancies = idsOfCoveredDiscrepancies;
   }

   public void setResolution(String resolution) {
      this.resolution = resolution;
   }

   public void setIsResolutionValid(boolean isResolutionValid) {
      this.isResolutionValid = isResolutionValid;
   }

   public void addCoveredDiscrepancyIndex(Discrepancy discrepancy) {
      idsOfCoveredDiscrepancies.put(discrepancy.getId());
   }

   public boolean isValid() {
      return isConnected && isResolutionValid;
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      for (Identity<?> identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }
}
