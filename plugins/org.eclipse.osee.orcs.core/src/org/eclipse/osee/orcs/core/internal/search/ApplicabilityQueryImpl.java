/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreTupleTypes.BranchView;
import static org.eclipse.osee.framework.core.enums.CoreTupleTypes.ViewApplicability;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewData;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Ryan D. Brooks
 */
public class ApplicabilityQueryImpl implements ApplicabilityQuery {
   private final TupleQuery tupleQuery;
   private final ApplicabilityDsQuery applicabilityDsQuery;

   public ApplicabilityQueryImpl(TupleQuery tupleQuery, ApplicabilityDsQuery applicabilityDsQuery) {
      this.tupleQuery = tupleQuery;
      this.applicabilityDsQuery = applicabilityDsQuery;
   }

   @Override
   public ApplicabilityToken getApplicabilityToken(ArtifactId artId, BranchId branch) {
      return applicabilityDsQuery.getApplicabilityToken(artId, branch);
   }

   @Override
   public List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokens(Collection<? extends ArtifactId> artIds, BranchId branch) {
      return applicabilityDsQuery.getApplicabilityTokens(artIds, branch);
   }

   @Override
   public HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch) {
      HashMap<Long, ApplicabilityToken> tokens = new HashMap<>();
      BiConsumer<Long, String> consumer = (id, name) -> tokens.put(id, new ApplicabilityToken(id, name));
      tupleQuery.getTuple2UniqueE2Pair(ViewApplicability, branch, consumer);
      if (tokens.isEmpty()) {
         tokens.put(1L, ApplicabilityToken.BASE);
      }
      return tokens;
   }

   @Override
   public HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch1, BranchId branch2) {
      HashMap<Long, ApplicabilityToken> tokens = new HashMap<>();
      BiConsumer<Long, String> consumer = (id, name) -> tokens.put(id, new ApplicabilityToken(id, name));
      tupleQuery.getTuple2UniqueE2Pair(ViewApplicability, branch1, consumer);
      tupleQuery.getTuple2UniqueE2Pair(ViewApplicability, branch2, consumer);
      return tokens;
   }

   @Override
   public List<FeatureDefinitionData> getFeatureDefinitionData(List<ArtifactReadable> featureDefinitionArts) {
      List<FeatureDefinitionData> featureDefinition = new ArrayList<>();

      for (ArtifactReadable art : featureDefinitionArts) {
         String json = art.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);

         ObjectMapper mapper = new ObjectMapper();
         try {
            FeatureDefinitionData[] readValue = mapper.readValue(json, FeatureDefinitionData[].class);
            featureDefinition.addAll(Arrays.asList(readValue));
         } catch (Exception e) {
            throw new OseeCoreException(e,
               String.format("Invalid JSON in general string data attribute on artifactId [%s]", art.getId()));
         }
      }
      return featureDefinition;
   }

   @Override
   public Map<String, List<String>> getNamedViewApplicabilityMap(BranchId branch, ArtifactId viewId) {
      Map<String, List<String>> toReturn = new TreeMap<>();
      List<ApplicabilityToken> appTokens = getViewApplicabilityTokens(viewId, branch);

      for (ApplicabilityToken app : appTokens) {
         // Ignore features with |s and &s, Ignore the configuration name
         if (!app.getName().equalsIgnoreCase("Base") && !app.getName().contains("|") && !app.getName().contains(
            "&") && !app.getName().toLowerCase().contains("config")) {
            String[] split = app.getName().split("=");

            if (split.length == 2) {
               String name = split[0].trim().toUpperCase();
               String value = split[1].trim();

               if (toReturn.containsKey(name)) {
                  List<String> list = new ArrayList<>();
                  list.addAll(toReturn.get(name));
                  list.add(value);
                  toReturn.put(name, list);
               } else {
                  toReturn.put(name, Arrays.asList(value));
               }
            }
         }
      }

      return toReturn;
   }

   @Override
   public List<ApplicabilityToken> getViewApplicabilityTokens(ArtifactId artId, BranchId branch) {
      List<ApplicabilityToken> result = new ArrayList<>();
      BiConsumer<Long, String> consumer = (id, name) -> result.add(new ApplicabilityToken(id, name));
      tupleQuery.getTuple2KeyValuePair(ViewApplicability, artId, branch, consumer);
      return result;
   }

   @Override
   public List<BranchViewData> getViews() {
      HashCollection<BranchId, ArtifactId> branchAndViewIds = new HashCollection<>();
      BiConsumer<Long, Long> consumer =
         (branchId, artifactId) -> branchAndViewIds.put(BranchId.valueOf(branchId), ArtifactId.valueOf(artifactId));
      tupleQuery.getTuple2E1E2Pair(BranchView, COMMON, consumer);

      List<BranchViewData> branchViews = new ArrayList<>();
      for (BranchId branchId : branchAndViewIds.keySet()) {
         List<ArtifactId> values = (List<ArtifactId>) branchAndViewIds.getValues(branchId);
         branchViews.add(new BranchViewData(branchId, values));
      }
      return branchViews;
   }
}