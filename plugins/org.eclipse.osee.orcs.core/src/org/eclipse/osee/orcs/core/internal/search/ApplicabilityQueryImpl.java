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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewData;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Ryan D. Brooks
 */
public class ApplicabilityQueryImpl implements ApplicabilityQuery {
   private final TupleQuery tupleQuery;
   private final ApplicabilityDsQuery applicabilityDsQuery;
   private final TransactionQuery transactionQuery;
   private final BranchQuery branchQuery;
   private final QueryFactory queryFactory;

   public ApplicabilityQueryImpl(ApplicabilityDsQuery applicabilityDsQuery, QueryFactory queryFactory) {
      this.tupleQuery = queryFactory.tupleQuery();
      this.applicabilityDsQuery = applicabilityDsQuery;
      this.transactionQuery = queryFactory.transactionQuery();
      this.branchQuery = queryFactory.branchQuery();
      this.queryFactory = queryFactory;
   }

   @Override
   public ApplicabilityToken getApplicabilityToken(ArtifactId artId, BranchId branch) {
      return applicabilityDsQuery.getApplicabilityToken(artId, branch);
   }

   @Override
   public List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokens(List<? extends ArtifactId> artIds, BranchId branch) {
      return applicabilityDsQuery.getApplicabilityTokens(artIds, branch);
   }

   @Override
   public HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch) {
      HashMap<Long, ApplicabilityToken> tokens = new HashMap<>();
      BiConsumer<Long, String> consumer = (id, name) -> tokens.put(id, new ApplicabilityToken(id, name));
      tupleQuery.getTuple2UniqueE2Pair(ViewApplicability, branch, consumer);
      if (tokens.isEmpty()) {
         tokens.put(ApplicabilityToken.BASE.getId(), ApplicabilityToken.BASE);
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
   public List<ApplicabilityId> getApplicabilitiesReferenced(ArtifactId artifact, BranchId branch) {
      List<ApplicabilityId> appIds = new LinkedList<>();
      for (ApplicabilityId tuple2 : tupleQuery.getTuple2(CoreTupleTypes.ArtifactReferenceApplicabilityType, branch,
         artifact)) {
         appIds.add(tuple2);
      }
      return appIds;
   }

   @Override
   public List<ApplicabilityToken> getApplicabilityReferenceTokens(ArtifactId artifact, BranchId branch) {
      List<ApplicabilityToken> tokens = new LinkedList<>();
      tupleQuery.getTuple2NamedId(CoreTupleTypes.ArtifactReferenceApplicabilityType, branch, artifact,
         (e2, value) -> tokens.add(ApplicabilityToken.create(e2, value)));
      return tokens;
   }

   @Override
   public List<ApplicabilityToken> getViewApplicabilityTokens(ArtifactId artId, BranchId branch) {
      List<ApplicabilityToken> result = new ArrayList<>();
      BiConsumer<Long, String> consumer = (id, name) -> result.add(new ApplicabilityToken(id, name));
      tupleQuery.getTuple2KeyValuePair(ViewApplicability, artId, branch, consumer);
      return result;
   }

   @Override
   public List<FeatureDefinition> getFeatureDefinitionData(BranchId branch) {
      BranchId branchToUse = branch;
      Branch br = branchQuery.andId(branch).getResults().getExactlyOne();
      if (br.getBranchType().equals(BranchType.MERGE)) {
         branchToUse = br.getParentBranch();
      }

      List<ArtifactReadable> featureDefinitionArts =
         queryFactory.fromBranch(branchToUse).andTypeEquals(CoreArtifactTypes.FeatureDefinition).getResults().getList();

      List<FeatureDefinition> featureDefinition = new ArrayList<>();

      for (ArtifactReadable art : featureDefinitionArts) {
         String json = art.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);
         // convert legacy field name to new
         json = json.replaceAll("\"type\"", "\"valueType\"");
         FeatureDefinition[] readValue = JsonUtil.readValue(json, FeatureDefinition[].class);
         featureDefinition.addAll(Arrays.asList(readValue));
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
   public List<BranchViewData> getViews() {
      HashCollection<BranchId, ArtifactId> branchAndViewIds = new HashCollection<>();
      BiConsumer<Long, Long> consumer =
         (branchId, artifactId) -> branchAndViewIds.put(BranchId.valueOf(branchId), ArtifactId.valueOf(artifactId));
      tupleQuery.getTuple2E1E2Pair(BranchView, COMMON, consumer);

      List<BranchViewData> branchViews = new ArrayList<>();
      for (BranchId branchId : branchAndViewIds.keySet()) {
         List<ArtifactId> values = branchAndViewIds.getValues(branchId);
         branchViews.add(new BranchViewData(branchId, values));
      }
      return branchViews;
   }

   @Override
   public String getViewTable(BranchId branch) {
      StringBuilder html = new StringBuilder(
         "<!DOCTYPE html><html><head><style> table { border-spacing: 0px } th,td { padding: 3px; } </style></head><body>");
      List<BranchViewData> views = this.getViews();
      for (BranchViewData branchView : views) {
         if (branchView.getBranch().equals(branch)) {
            html.append(String.format("<h1>Features for branch [%s]</h1>",
               branchQuery.andId(branch).getResults().getExactlyOne().getName()));
            html.append("<table border=\"1\">");
            List<ArtifactId> branchViews = branchView.getBranchViews();

            List<FeatureDefinition> featureDefinitionData = getFeatureDefinitionData(branch);

            Collections.sort(featureDefinitionData, new Comparator<FeatureDefinition>() {
               @Override
               public int compare(FeatureDefinition obj1, FeatureDefinition obj2) {
                  return obj1.getName().compareTo(obj2.getName());
               }
            });

            printColumnHeadings(html, branchViews, branch);
            Map<ArtifactId, Map<String, List<String>>> branchViewsMap = new HashMap<>();

            for (ArtifactId artId : branchViews) {
               branchViewsMap.put(artId, getNamedViewApplicabilityMap(branch, artId));
            }
            for (FeatureDefinition featureDefinition : featureDefinitionData) {
               html.append("<tr>");
               html.append(String.format("<td>%s</td>", featureDefinition.getName()));
               html.append(String.format("<td>%s</td>", featureDefinition.getDescription()));
               for (ArtifactId view : branchViews) {
                  List<String> list = branchViewsMap.get(view).get(featureDefinition.getName());
                  // every view should have a value for each feature, if incorrectly configured returns null
                  if (list != null) {
                     html.append(
                        "<td>" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", list) + "</td>");
                  } else {
                     html.append("<td> </td>");
                  }
               }
               html.append("</tr>");
            }
         }
      }
      html.append("</table></body></html>");
      return html.toString();
   }

   private void printColumnHeadings(StringBuilder html, List<ArtifactId> branchViews, BranchId branch) {
      html.append("<tr>");
      html.append("<th>Feature Name</th>");
      html.append("<th>Feature Description</th>");
      for (ArtifactId artId : branchViews) {
         html.append(String.format("<th>%s</th>",
            queryFactory.fromBranch(branch).andId(artId).getResults().getExactlyOne().getName()));
      }
      html.append("</tr>");
   }

   @Override
   public ArtifactId getVersionConfig(ArtifactId art, BranchId branch) {
      Iterable<Long> tuple2 = tupleQuery.getTuple2Raw(CoreTupleTypes.VersionConfig, branch, art);
      if (tuple2.iterator().hasNext()) {
         return ArtifactId.valueOf(tuple2.iterator().next());
      }
      return ArtifactId.SENTINEL;
   }

   @Override
   public List<BranchId> getAffectedBranches(Long injectDateMs, Long removalDateMs, List<ApplicabilityId> applicabilityIds, BranchId branch) {
      ArrayList<BranchId> toReturn = new ArrayList<>();
      Date injection = new Date(injectDateMs);
      Date removal = new Date(removalDateMs);

      // Get all Branch Views
      List<BranchViewData> branchViews = getViews();

      HashMap<Long, Branch> childBaselineBranchIds = new HashMap<>();
      for (Branch childBranch : branchQuery.andIsOfType(BranchType.BASELINE).andIsChildOf(branch).getResults()) {
         childBaselineBranchIds.put(childBranch.getId(), childBranch);
      }

      HashMap<Long, ApplicabilityId> applicabilityIdsMap = new HashMap<>();
      for (ApplicabilityId applicId : applicabilityIds) {
         applicabilityIdsMap.put(applicId.getId(), applicId);
      }

      for (BranchViewData branchView : branchViews) {
         Branch baseBranch = childBaselineBranchIds.get(branchView.getBranch().getId());
         if (baseBranch != null) {
            // Check Dates on baseBranch
            Date baseDate = transactionQuery.andTxId(baseBranch.getBaselineTx()).getResults().getExactlyOne().getDate();
            if (baseDate.after(injection) && (removalDateMs == -1 || baseDate.before(removal))) {
               // now determine what views of this branch are applicable
               for (ArtifactId view : branchView.getBranchViews()) {
                  // Get all applicability tokens for the view of this branch
                  List<ApplicabilityToken> viewApplicabilityTokens =
                     getViewApplicabilityTokens(view, branchView.getBranch());
                  // Cross check applicabilityTokens with valid ApplicabilityIds sent in
                  for (ApplicabilityToken applicToken : viewApplicabilityTokens) {
                     // If applictoken is found, add toReturn list
                     if (applicabilityIdsMap.containsKey(applicToken.getId())) {
                        toReturn.add(BranchId.create(branchView.getBranch().getId(), view));
                        break;
                     }
                  }
               }
            }
         }
      }

      return toReturn;
   }

   @Override
   public List<BranchId> getAffectedBranches(TransactionId injectionTx, TransactionId removalTx, List<ApplicabilityId> applicabilityIds, BranchId branch) {

      long timeInjectionMs = transactionQuery.andTxId(injectionTx).getResults().getExactlyOne().getDate().getTime();
      long timeRemovalMs = removalTx.isInvalid() ? -1 : transactionQuery.andTxId(
         removalTx).getResults().getExactlyOne().getDate().getTime();

      return getAffectedBranches(timeInjectionMs, timeRemovalMs, applicabilityIds, branch);
   }

   @Override
   public Set<ArtifactId> getExcludedArtifacts(BranchId branch, ArtifactId view) {
      return applicabilityDsQuery.getExcludedArtifacts(branch, view);
   }
}