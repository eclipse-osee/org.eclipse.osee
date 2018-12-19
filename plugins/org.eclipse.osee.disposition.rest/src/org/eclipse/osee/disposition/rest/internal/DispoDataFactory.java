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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

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
      newSet.setTime(new Date());
      newSet.setImportPath(descriptor.getImportPath());
      newSet.setImportState("NONE");
      newSet.setNotesList(new ArrayList<Note>());
      newSet.setDispoType(descriptor.getDispoType());

      return newSet;
   }

   public void setStatus(DispoItemData item) {
      item.setStatus(dispoConnector.getItemStatus(item));
   }

   public void initDispoItem(DispoItemData itemToInit) {
      if (itemToInit.getAnnotationsList() == null) {
         itemToInit.setAnnotationsList(new ArrayList<DispoAnnotationData>());
      }
      if (itemToInit.getAborted() == null) {
         itemToInit.setAborted(false);
      }
      if (itemToInit.getAssignee() == null) {
         itemToInit.setAssignee("UnAssigned");
      }
      if (itemToInit.getCategory() == null) {
         itemToInit.setCategory("none");
      }
      if (itemToInit.getCreationDate() == null) {
         itemToInit.setCreationDate(new Date());
      }
      if (itemToInit.getDiscrepanciesAsRanges() == null) {
         itemToInit.setDiscrepanciesAsRanges("none");
      }
      if (itemToInit.getAborted() == null) {
         itemToInit.setAborted(false);
      }
      if (itemToInit.getElapsedTime() == null) {
         itemToInit.setElapsedTime("none");
      }
      if (itemToInit.getItemNotes() == null) {
         itemToInit.setItemNotes("none");
      }
      if (itemToInit.getMachine() == null) {
         itemToInit.setMachine("none");
      }
      if (itemToInit.getStatus() == null) {
         itemToInit.setStatus("none");
      }
      if (itemToInit.getTeam() == null) {
         itemToInit.setTeam("none");
      }
      if (itemToInit.getVersion() == null) {
         itemToInit.setVersion("none");
      }
      if (itemToInit.getTotalPoints() == null) {
         itemToInit.setTotalPoints("none");
      }

      if (itemToInit.getIsIncludeDetails() == null) {
         itemToInit.setIsIncludeDetails(false);
      }

      if (itemToInit.getIsIncludeDetails()) {
         if (itemToInit.getAnnotationsList() == null) {
            itemToInit.setAnnotationsList(new ArrayList<DispoAnnotationData>());
         }
      }
      if (itemToInit.getDiscrepanciesList() == null) {
         itemToInit.setDiscrepanciesList(new HashMap<String, Discrepancy>());
      }
      if (itemToInit.getDiscrepanciesList().size() == 0) {
         itemToInit.setStatus(DispoStrings.Item_Pass);
      } else {
         itemToInit.setStatus(dispoConnector.getItemStatus(itemToInit));
      }
      if (!Strings.isValid(itemToInit.getAssignee())) {
         itemToInit.setAssignee("UnAssigned");
      }

      itemToInit.setNeedsRerun(false);
      itemToInit.setNeedsReview(false);

   }

   public void initAnnotation(DispoAnnotationData annotationToInit) {
      annotationToInit.setIdsOfCoveredDiscrepancies(new ArrayList<String>());
      annotationToInit.setCustomerNotes("");
      annotationToInit.setDeveloperNotes("");
      annotationToInit.setResolution("");
      annotationToInit.setResolutionType("None");
      annotationToInit.setIsDefault(false);
   }

   public DispoItem createUpdatedItem(List<DispoAnnotationData> annotationsList, Map<String, Discrepancy> discrepanciesList) {
      DispoItemData newItem = new DispoItemData();
      newItem.setAnnotationsList(annotationsList);
      newItem.setDiscrepanciesList(discrepanciesList);
      newItem.setStatus(dispoConnector.getItemStatus(newItem));

      return newItem;
   }

   public String getNewId() {
      return GUID.create();
   }
}
