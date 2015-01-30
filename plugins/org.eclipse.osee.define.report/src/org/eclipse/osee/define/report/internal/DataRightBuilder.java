/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.define.report.api.DataRight;
import org.eclipse.osee.define.report.api.DataRightAnchor;
import org.eclipse.osee.define.report.api.DataRightEntry;
import org.eclipse.osee.define.report.api.DataRightId;
import org.eclipse.osee.define.report.api.DataRightInput;
import org.eclipse.osee.define.report.api.DataRightResult;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Angel Avila
 */
public class DataRightBuilder {

   private final OrcsApi orcsApi;

   private static final IArtifactToken MAPPING_ARTIFACT = TokenFactory.createArtifactToken("AOkJ_kFNbEXCS7UjmfwA",
      "DataRightsFooters", CoreArtifactTypes.GeneralData);

   public DataRightBuilder(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public DataRightResult getDataRights(DataRightInput request) {
      QueryFactory queryFactory = orcsApi.getQueryFactory(null);
      QueryBuilder query = queryFactory.fromBranch(CoreBranches.COMMON);

      DataRightResult mapping = new DataRightResult();

      Map<String, DataRight> classificationsToDataRights = getClassificationToDataRights(query);
      mapping.getDataRights().addAll(classificationsToDataRights.values());
      List<DataRightEntry> orderedData = getOrderedList(request);
      findMatchForAll(orderedData.iterator(), mapping.getDataRightAnchors(), classificationsToDataRights);
      return mapping;
   }

   private List<DataRightEntry> getOrderedList(DataRightInput request) {
      List<DataRightEntry> orderedData = new ArrayList<DataRightEntry>();
      orderedData.addAll(request.getData());
      Collections.sort(orderedData, new Comparator<DataRightEntry>() {

         @Override
         public int compare(DataRightEntry arg0, DataRightEntry arg1) {
            return arg0.getIndex() - arg1.getIndex();
         }
      });
      return orderedData;
   }

   private void findMatchForAll(Iterator<DataRightEntry> iterator, Collection<DataRightAnchor> anchors, Map<String, DataRight> classificationsToDataRight) {
      DataRightEntry currentArtifact = iterator.next();
      while (currentArtifact != null) {
         String classification = currentArtifact.getClassification();
         if (!Strings.isValid(classification)) {
            classification = "DEFAULT";
         }

         boolean isNextDifferent = false;
         boolean needsPageBreak = true;
         DataRightEntry nextArt = null;
         if (iterator.hasNext()) {
            nextArt = iterator.next();
         }
         if (nextArt != null) {
            boolean isNextLandscape = nextArt.getOrientation().isLandscape();
            String nextClassification = nextArt.getClassification();

            if (isNextLandscape) {
               needsPageBreak = true;
            } else {
               if (!classification.equalsIgnoreCase(nextClassification)) {
                  isNextDifferent = true;
                  needsPageBreak = true;
               } else {
                  needsPageBreak = false;
               }
            }
         } else {
            // last art doesn't need a page break
            needsPageBreak = false;
            isNextDifferent = true;
         }

         DataRight dataRight = classificationsToDataRight.get(classification);
         if (dataRight == null) {
            classification = "Unspecified";
            dataRight = classificationsToDataRight.get(classification);
         }

         DataRightAnchor anchor = new DataRightAnchor();
         anchor.setNextDifferent(isNextDifferent);
         anchor.setNeedsPageBreak(needsPageBreak);
         anchor.setId(currentArtifact.getGuid());
         anchor.setDataRightId(dataRight.getId());
         anchors.add(anchor);

         currentArtifact = nextArt;
      }
   }

   private Map<String, DataRight> getClassificationToDataRights(QueryBuilder query) {
      Map<String, DataRight> toReturn = new HashMap<String, DataRight>();

      @SuppressWarnings("unchecked")
      ArtifactReadable footerMappingArt = query.andIds(MAPPING_ARTIFACT).getResults().getOneOrNull();

      if (footerMappingArt != null) {
         List<String> footers = new ArrayList<String>();
         footers = footerMappingArt.getAttributeValues(CoreAttributeTypes.GeneralStringData);
         for (String footer : footers) {
            String[] enumToFooter = footer.split("\\n", 2);
            if (enumToFooter.length == 2) {
               DataRightId id = new DataRightId();
               id.setId(GUID.create());

               DataRight dataRight = new DataRight();
               dataRight.setId(id);
               dataRight.setContent(enumToFooter[1].trim());

               toReturn.put(enumToFooter[0].trim(), dataRight);
            }
         }
      } else {
         DataRightId id = new DataRightId();
         id.setId(GUID.create());

         DataRight dataRight = new DataRight();
         dataRight.setId(id);
         dataRight.setContent("NO DATA RIGHTS ARTIFACT FOUND");
         toReturn.put("Unspecified", dataRight);
      }

      return toReturn;
   }
}
