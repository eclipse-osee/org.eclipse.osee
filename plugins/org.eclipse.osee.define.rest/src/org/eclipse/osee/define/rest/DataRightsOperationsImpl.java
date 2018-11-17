/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GeneralData;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.define.api.DataRightsOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.model.datarights.DataRight;
import org.eclipse.osee.framework.core.model.datarights.DataRightAnchor;
import org.eclipse.osee.framework.core.model.datarights.DataRightEntry;
import org.eclipse.osee.framework.core.model.datarights.DataRightInput;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Ryan D. Brooks
 */
public class DataRightsOperationsImpl implements DataRightsOperations {

   private static final ArtifactToken MAPPING_ARTIFACT =
      ArtifactToken.valueOf(5443258, "DataRightsFooters", COMMON, GeneralData);

   private final OrcsApi orcsApi;

   public DataRightsOperationsImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public DataRightResult getDataRights(List<ArtifactId> artifacts, BranchId branch) {
      return getDataRights(artifacts, branch, "");
   }

   @Override
   public DataRightResult getDataRights(List<ArtifactId> artifacts, BranchId branch, String overrideClassification) {
      DataRightInput request = new DataRightInput();
      populateRequest(artifacts, branch, request, overrideClassification);
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);

      DataRightResult mapping = new DataRightResult();

      Map<String, DataRight> classificationsToDataRights = getClassificationToDataRights(query);
      mapping.getDataRights().addAll(classificationsToDataRights.values());
      List<DataRightEntry> orderedData = getOrderedList(request);
      findMatchForAll(orderedData.iterator(), mapping.getDataRightAnchors(), classificationsToDataRights);

      return mapping;
   }

   private void populateRequest(List<ArtifactId> artifacts, BranchId branch, DataRightInput request, String overrideClassification) {
      int index = 0;

      for (ArtifactId artifact : artifacts) {
         ArtifactReadable art =
            orcsApi.getQueryFactory().fromBranch(branch).andId(artifact).getResults().getOneOrDefault(
               ArtifactReadable.SENTINEL);

         String classification = null;
         String orientation = "Portrait";
         if (DataRightsClassification.isValid(overrideClassification)) {
            classification = overrideClassification;
         } else if (art.isValid()) {
            classification = art.getSoleAttributeAsString(CoreAttributeTypes.DataRightsClassification, "");
            orientation = art.getSoleAttributeValue(CoreAttributeTypes.PageType, "Portrait");
         }

         request.addData(artifact, classification, PageOrientation.fromString(orientation), index);
         index++;
      }
   }

   private DataRightAnchor getAnchor(ArtifactId id, Collection<DataRightAnchor> anchors) {
      for (DataRightAnchor anchor : anchors) {
         if (anchor.getId().equals(id)) {
            return anchor;
         }
      }
      return null;
   }

   private void findMatchForAll(Iterator<DataRightEntry> iterator, Collection<DataRightAnchor> anchors, Map<String, DataRight> classificationsToDataRight) {
      DataRightEntry previousArtifact = null;
      while (iterator.hasNext()) {
         DataRightEntry currentArtifact = iterator.next();
         String classification = currentArtifact.getClassification();
         PageOrientation orientation = currentArtifact.getOrientation();
         boolean isSetDataRightFooter = false;

         if (previousArtifact == null) {
            isSetDataRightFooter = true;
         } else {
            String previousArtClassification = previousArtifact.getClassification();
            if (!classification.equals(previousArtClassification)) {
               isSetDataRightFooter = true;
            } else {
               DataRightAnchor previousArtAnchor = getAnchor(previousArtifact.getId(), anchors);
               PageOrientation prevOrientation = previousArtifact.getOrientation();
               if (previousArtAnchor != null && orientation.equals(prevOrientation)) {
                  previousArtAnchor.setContinuous(true);
               }
            }
         }

         if (!Strings.isValid(classification)) {
            classification = "DEFAULT";
         }

         DataRight dataRight = classificationsToDataRight.get(classification);
         if (dataRight == null) {
            classification = "Unspecified";
            dataRight = classificationsToDataRight.get(classification);
         }

         DataRightAnchor anchor = new DataRightAnchor();
         anchor.setSetDataRightFooter(isSetDataRightFooter);
         anchor.setId(currentArtifact.getId());
         anchor.setDataRight(dataRight);
         anchors.add(anchor);

         previousArtifact = currentArtifact;
      }
   }

   private List<DataRightEntry> getOrderedList(DataRightInput request) {
      List<DataRightEntry> orderedData = new ArrayList<>();
      orderedData.addAll(request.getData());
      Collections.sort(orderedData, new Comparator<DataRightEntry>() {

         @Override
         public int compare(DataRightEntry arg0, DataRightEntry arg1) {
            return arg0.getIndex() - arg1.getIndex();
         }
      });
      return orderedData;
   }

   private Map<String, DataRight> getClassificationToDataRights(QueryBuilder query) {
      Map<String, DataRight> toReturn = new HashMap<>();

      ArtifactReadable footerMappingArt =
         query.andId(MAPPING_ARTIFACT).getResults().getOneOrDefault(ArtifactReadable.SENTINEL);

      if (footerMappingArt.isValid()) {
         List<String> footers = new ArrayList<>();
         footers = footerMappingArt.getAttributeValues(CoreAttributeTypes.GeneralStringData);
         for (String footer : footers) {
            String[] enumToFooter = footer.split("\\n", 2);
            if (enumToFooter.length == 2) {
               DataRight dataRight = new DataRight();
               dataRight.setContent(enumToFooter[1].trim());
               toReturn.put(enumToFooter[0].trim(), dataRight);
            }
         }
      } else {
         DataRight dataRight = new DataRight();
         dataRight.setContent(DataRightResult.UNSPECIFIED);
         toReturn.put("Unspecified", dataRight);
      }

      return toReturn;
   }

}
