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

package org.eclipse.osee.disposition.rest.internal;

import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */

public class DispoDataFactory {

   private Log logger;
   private DispoConnector dispoConnector;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setDispoConnector(DispoConnector dispoConnector) {
      this.dispoConnector = dispoConnector;
   }

   public void start() {
      logger.trace("Starting DispoDataFactory...");
   }

   public void stop() {
      logger.trace("Stopping DispoDataFactory...");
   }

   public DispoSetData creteSetDataFromDescriptor(DispoSetDescriptorData descriptor) {
      DispoSetData newSet = new DispoSetData();
      newSet.setName(descriptor.getName());
      newSet.setImportPath(descriptor.getImportPath());
      newSet.setImportState("NONE");
      newSet.setNotesList(new JSONArray());
      newSet.setDispoConfig(buildDispoConfigFromType(descriptor.getDispoType()));

      return newSet;
   }

   private DispoConfig buildDispoConfigFromType(String dispoType) {
      DispoConfig toReturn = new DispoConfig();
      if (dispoType.equalsIgnoreCase("testScript")) {
         toReturn.setName("Test Script");
         toReturn.setPeerNotes("Develop Notes");
         toReturn.setCustomerNotes("Customer Notes");
         toReturn.setResolutionTitle("PCR");
         toReturn.setResolutionType("PCR Type");
         toReturn.setType("");
      } else {
         toReturn.setName("Code Method");
         toReturn.setPeerNotes("Develop Notes");
         toReturn.setCustomerNotes("Customer Notes");
         toReturn.setResolutionTitle("Coverage Method Type");
         toReturn.setResolutionType("Coverage Method");
         toReturn.setType("");
      }
      return toReturn;
   }

   public void initDispoItem(DispoItemData itemToInit) {
      itemToInit.setAnnotationsList(new JSONArray());
      if (itemToInit.getDiscrepanciesList() == null) {
         itemToInit.setDiscrepanciesList(new JSONObject());
      }

      if (itemToInit.getDiscrepanciesList().length() == 0) {
         itemToInit.setStatus(DispoStrings.Item_Pass);
      } else {
         itemToInit.setStatus(DispoStrings.Item_InComplete);
      }
   }

   public void initAnnotation(DispoAnnotationData annotationToInit) {
      annotationToInit.setIdsOfCoveredDiscrepancies(new JSONArray());
      annotationToInit.setCustomerNotes("");
      annotationToInit.setDeveloperNotes("");
      annotationToInit.setResolution("");
      annotationToInit.setResolutionType("None");
   }

   public DispoItem createUpdatedItem(JSONArray annotationsList, JSONObject discrepanciesList) throws JSONException {
      DispoItemData newItem = new DispoItemData();
      newItem.setAnnotationsList(annotationsList);
      newItem.setDiscrepanciesList(discrepanciesList);
      newItem.setStatus(dispoConnector.allDiscrepanciesAnnotated(newItem));

      return newItem;
   }

   public JSONArray mergeJsonArrays(JSONArray currentArray, JSONArray arrayToAdd) {
      String currentJsonString = currentArray.toString().trim();
      String toAddJsonString = arrayToAdd.toString();
      StringBuilder sb = new StringBuilder();

      if (!currentJsonString.equals("[]")) {
         sb.append(currentJsonString);
         sb.replace(currentJsonString.length() - 1, currentJsonString.length(), ",");
         sb.append(toAddJsonString.replaceFirst("\\[", ""));
      } else {
         sb.append(toAddJsonString);
      }
      JSONArray toReturn;
      try {
         toReturn = new JSONArray(sb.toString());
      } catch (JSONException ex) {
         toReturn = currentArray;
         throw new OseeCoreException(ex);
      }

      return toReturn;

   }

   public String getNewId() {
      return GUID.create();
   }
}
