/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.orcs.core.internal.search;

import static org.eclipse.osee.framework.core.enums.CoreTupleTypes.ViewApplicability;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
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
   private final OrcsApi orcsApi;

   public ApplicabilityQueryImpl(ApplicabilityDsQuery applicabilityDsQuery, QueryFactory queryFactory, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
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
   public List<String> getPossibleApplicabilities(BranchId branch) {
      List<String> apps = new ArrayList<String>();
      HashMap<Long, ApplicabilityToken> appTokens = getApplicabilityTokens(branch);
      for (ApplicabilityToken app : appTokens.values()) {
         apps.add(app.getName());
      }
      for (ArtifactToken view : getConfigurationsForBranch(branch)) {
         apps.add("Config = " + view.getName());
      }
      for (FeatureDefinition feature : getFeatureDefinitionData(branch)) {
         for (String val : feature.getValues()) {
            apps.add(feature.getName() + " = " + val);
         }
      }
      for (ArtifactToken group : getConfigurationGroupsForBranch(branch)) {
         apps.add("ConfigurationGroup = " + group.getName());
      }
      List<String> appsNoDups = new ArrayList<>(new HashSet<>(apps));
      Collections.sort(appsNoDups);
      return appsNoDups;
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
      Iterable<ApplicabilityId> existingAppIds =
         tupleQuery.getTuple2(CoreTupleTypes.ArtifactReferenceApplicabilityType, branch, artifact);
      for (ApplicabilityId tuple2 : existingAppIds) {
         appIds.add(tuple2);
      }
      return appIds;
   }

   @Override
   public List<ApplicabilityToken> getApplicabilityReferenceTokens(ArtifactId artifact, BranchId branch) {
      List<ApplicabilityToken> tokens = new LinkedList<>();
      tupleQuery.getTuple2NamedId(CoreTupleTypes.ArtifactReferenceApplicabilityType, branch, artifact,
         (e2, value) -> tokens.add(ApplicabilityToken.valueOf(e2, value)));
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
      List<FeatureDefinition> featureDefinition = new ArrayList<>();

      List<ArtifactReadable> featureArts =
         queryFactory.fromBranch(branchToUse).andIsOfType(CoreArtifactTypes.Feature).getResults().getList();
      Collections.sort(featureArts, new NamedComparator(SortOrder.ASCENDING));

      for (ArtifactToken featureArt : featureArts) {
         ArtifactReadable art = (ArtifactReadable) featureArt;
         FeatureDefinition feature = new FeatureDefinition();
         feature.setId(art.getId());
         feature.setName(art.getName());
         feature.setDefaultValue(art.getSoleAttributeValue(CoreAttributeTypes.DefaultValue, ""));
         feature.setValues(art.getAttributeValues(CoreAttributeTypes.Value));
         feature.setValueType(art.getSoleAttributeAsString(CoreAttributeTypes.FeatureValueType, ""));
         feature.setMultiValued(art.getSoleAttributeValue(CoreAttributeTypes.FeatureMultivalued, false));
         feature.setDescription(art.getSoleAttributeAsString(CoreAttributeTypes.Description, ""));
         feature.setData(featureArt);
         featureDefinition.add(feature);
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
            "&") && !app.getName().toLowerCase().startsWith("Config =")) {
            String[] split = app.getName().split("=");

            if (split.length == 2) {
               String name = split[0].trim();
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
   public boolean applicabilityExistsOnBranchView(BranchId branch, ArtifactId viewId, String applicability) {
      List<GammaId> tuples = new ArrayList<>();
      queryFactory.tupleQuery().getTuple2GammaFromE1E2(CoreTupleTypes.ViewApplicability, branch, viewId, applicability,
         tuples::add);
      return !tuples.isEmpty();
   }

   @Override
   public String getExistingFeatureApplicability(BranchId branch, ArtifactId viewId, String featureName) {
      String existingAppl = "";
      for (String appl : queryFactory.tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId)) {

         if (appl.startsWith(featureName + " =") & !appl.contains("|") & !appl.contains("&")) {
            existingAppl = appl;
         }
      }
      return existingAppl;
   }

   @Override
   public List<ArtifactId> getBranchViewsForApplicability(BranchId branch, ApplicabilityId applId) {
      List<ArtifactId> arts = new ArrayList<>();

      for (Long long1 : queryFactory.tupleQuery().getTuple2E1ListRaw(CoreTupleTypes.ViewApplicability, branch,
         applId.getId())) {
         arts.add(ArtifactId.valueOf(long1));

      }
      return arts;
   }

   @Override
   public boolean featureExistsOnBranch(BranchId branch, String featureName) {
      if (ApplicabilityToken.BASE.getName().equals(featureName)) {
         return true;
      }
      boolean returnValue;
      ArtifactId feature = queryFactory.fromBranch(branch).andTypeEquals(CoreArtifactTypes.Feature).andAttributeIs(
         CoreAttributeTypes.Name, featureName).asArtifactIdOrSentinel();
      if (feature.isValid()) {
         returnValue = true;
      } else {
         returnValue = false;
      }
      return returnValue;
   }

   @Override
   public boolean featureValueIsValid(BranchId branch, String featureName, String featureValue) {
      return true;
   }

   @Override
   public List<ArtifactToken> getViewsForBranch(BranchId branch) {
      List<ArtifactToken> views =
         queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).asArtifactTokens();
      List<ArtifactToken> cfgGrps =
         queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
            CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).asArtifactTokens();
      if (!cfgGrps.isEmpty()) {
         views.addAll(cfgGrps);
      }
      return views;
   }

   @Override
   public List<ArtifactToken> getConfigurationsForBranch(BranchId branch) {
      return queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).asArtifactTokens();
   }

   @Override
   public List<ArtifactToken> getConfigurationGroupsForBranch(BranchId branch) {
      List<ArtifactToken> views =
         queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
            CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).asArtifactTokens();

      return views;
   }

   @Override
   public boolean viewExistsOnBranch(BranchId branch, ArtifactId viewId) {
      Boolean returnValue = false;
      for (ArtifactToken view : getViewsForBranch(branch)) {
         if (view.equals(viewId)) {
            returnValue = true;
            break;
         }
      }

      return returnValue;
   }

   @Override
   public ArtifactToken getViewByName(BranchId branch, String viewName) {
      ArtifactToken viewToken = ArtifactToken.SENTINEL;
      for (ArtifactToken view : getViewsForBranch(branch)) {
         if (view.getName().equals(viewName)) {
            viewToken = view;
            break;
         }
      }
      return viewToken;
   }

   /*
    * The filter is a regex - if it matches the branch view name, then that branch view will not be included in the
    * table
    */
   @Override
   public String getViewTable(BranchId branch, String filter) {
      return getConfigMatrix(branch, "all", filter);
   }

   private void printTable(StringBuilder html, List<ArtifactToken> branchViews, BranchId branch, String filter) {
      html.append("<table border=\"1\">");
      if (Strings.isValid(filter)) {
         branchViews.removeIf(art -> art.getName().matches(filter));
      }

      List<FeatureDefinition> featureDefinitionData = getFeatureDefinitionData(branch);

      Collections.sort(featureDefinitionData, new Comparator<FeatureDefinition>() {
         @Override
         public int compare(FeatureDefinition obj1, FeatureDefinition obj2) {
            return obj1.getName().compareTo(obj2.getName());
         }
      });

      printColumnHeadings(html, branchViews, branch);
      Map<ArtifactId, Map<String, List<String>>> branchViewsMap = new HashMap<>();

      for (ArtifactId view : branchViews) {
         branchViewsMap.put(view, getNamedViewApplicabilityMap(branch, view));
      }
      for (FeatureDefinition featureDefinition : featureDefinitionData) {
         html.append("<tr>");
         html.append(String.format("<td>%s</td>", featureDefinition.getName()));
         html.append(String.format("<td>%s</td>", featureDefinition.getDescription()));
         for (ArtifactId view : branchViews) {
            List<String> list = branchViewsMap.get(view).get(featureDefinition.getName());
            // every view should have a value for each feature, if incorrectly configured returns null
            if (list != null) {
               html.append("<td>" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", list) + "</td>");
            } else {
               html.append("<td> </td>");
            }
         }
         html.append("</tr>");
      }
      html.append("</table>");
   }

   @Override
   public String getConfigMatrix(BranchId branch, String matrixType, String filter) {
      StringBuilder html = getHtmlStart();

      List<ArtifactToken> branchViews = new ArrayList<ArtifactToken>();

      if (matrixType.equals("configs")) {
         html.append(String.format("<h3>Configurations Feature Matrix for branch: %s</h3>",
            branchQuery.andId(branch).getResults().getExactlyOne().getName()));
         branchViews = this.getConfigurationsForBranch(branch);
         printTable(html, branchViews, branch, filter);
      } else if (matrixType.equals("groups")) {
         html.append(String.format("<h3>Configuration Groups Feature Matrix for branch: %s</h3>",
            branchQuery.andId(branch).getResults().getExactlyOne().getName()));
         branchViews = this.getConfigurationGroupsForBranch(branch);
         printTable(html, branchViews, branch, filter);
      } else if (matrixType.equals("all")) {
         html.append(String.format("<h3>Configurations and Groups Feature Matrix for branch: %s</h3>",
            branchQuery.andId(branch).getResults().getExactlyOne().getName()));
         branchViews = this.getViewsForBranch(branch);
         printTable(html, branchViews, branch, filter);
      } else {
         html.append(String.format("<h3>Configuration Groups Feature Matrix for branch: %s</h3>",
            branchQuery.andId(branch).getResults().getExactlyOne().getName()));
         branchViews = this.getConfigurationGroupsForBranch(branch);
         printTable(html, branchViews, branch, filter);
         html.append(String.format("<h3>Configurations Feature Matrix for branch: %s</h3>",
            branchQuery.andId(branch).getResults().getExactlyOne().getName()));
         branchViews = this.getConfigurationsForBranch(branch);
         printTable(html, branchViews, branch, filter);
      }

      html.append("</body></html>");
      return html.toString();
   }

   private StringBuilder getHtmlStart() {
      StringBuilder html = new StringBuilder(
         "<!DOCTYPE html><html><head><style> table { border-spacing: 0px } th,td { padding: 3px; } </style></head><body>");
      return html;
   }

   private void printColumnHeadings(StringBuilder html, List<ArtifactToken> branchViews, BranchId branch) {
      html.append("<tr>");
      html.append("<th>Feature Name</th>");
      html.append("<th>Feature Description</th>");
      for (ArtifactToken artId : branchViews) {
         html.append(String.format("<th>%s</th>", artId.getName()));
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
      if (injectDateMs == null || removalDateMs == null || applicabilityIds == null) {
         return toReturn;
      }
      Date injection = new Date(injectDateMs);
      Date removal = new Date(removalDateMs);
      List<Branch> branchList =
         branchQuery.andIsOfType(BranchType.BASELINE).andIsChildOf(branch).getResults().getList();

      HashMap<Long, ApplicabilityId> applicabilityIdsMap = new HashMap<>();
      for (ApplicabilityId applicId : applicabilityIds) {
         applicabilityIdsMap.put(applicId.getId(), applicId);
      }

      for (Branch baseBranch : branchList) {
         Date baseDate = transactionQuery.andTxId(baseBranch.getBaselineTx()).getResults().getExactlyOne().getDate();
         if (baseDate.after(injection) && (removalDateMs == -1 || baseDate.before(removal))) {
            // now determine what views of this branch are applicable

            for (ArtifactId view : getViewsForBranch(baseBranch)) {
               // Get all applicability tokens for the view of this branch
               List<ApplicabilityToken> viewApplicabilityTokens = getViewApplicabilityTokens(view, baseBranch);
               // Cross check applicabilityTokens with valid ApplicabilityIds sent in
               for (ApplicabilityToken applicToken : viewApplicabilityTokens) {
                  // If applictoken is found, add toReturn list
                  if (applicabilityIdsMap.containsKey(applicToken.getId())) {
                     toReturn.add(BranchId.create(baseBranch.getId(), view));
                     break;
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