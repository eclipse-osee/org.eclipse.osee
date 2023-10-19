/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.ide.health;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.core.MediaType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.query.IAtsConfigQuery;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.result.IResultDataListener;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultData.Type;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class OseeProductionTestsNavItem extends XNavigateItem {

   public static String NAME = "OSEE Production Tests";
   private final AtsApi atsApi;
   private static IAtsQueryService queryService;
   private final List<StandAloneRestData> datas = new ArrayList<>();
   private static final List<OseeProductionTestProvider> providers = new ArrayList<>();

   public OseeProductionTestsNavItem() {
      super(NAME, AtsImage.SEARCH, AtsNavigateViewItems.ATS_ADMIN);
      atsApi = AtsApiService.get();
      if (atsApi != null) {
         queryService = atsApi.getQueryService();
      }
   }

   public void addOseeProductionTestProvider(OseeProductionTestProvider provider) {
      providers.add(provider);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {

      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         "Run OSEE Performance Tests against the database\n\nRun with caution to not affect production")) {
         return;
      }
      IResultDataListener listener = new IResultDataListener() {

         @Override
         public void log(Type type, String str) {
            // do nothing
         }
      };

      XResultData rd = new XResultData(true, listener);
      rd.log(AHTML.beginMultiColumnTable(95, 1));
      rd.log(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Time(ms)", "Result", "Name", "Type", "Details")));
      ElapsedTime time = new ElapsedTime(" ");
      testStandAloneRest(rd);
      testAtsConfig(rd);
      testAtsApiQueries(rd);
      testAtsTeamDefinition(rd);
      testAtsQueries(rd);
      testArtifactQueries(rd);
      for (OseeProductionTestProvider provider : providers) {
         provider.testAtsQuickSearchQueries(rd);
         provider.testPublishing_1(rd);
         provider.testPublishing_2(rd);
      }

      Long ms = time.getTimeSpent();
      rd.log(AHTML.endMultiColumnTable());
      Long sec = ms / 1000;
      rd.logf(AHTML.addRowMultiColumnTable(String.format("Total Time  MS : %s  Sec: %s ", ms.toString(), sec)));
      XResultDataUI.report(rd, getName());
   }

   /////////////////////////////////
   /////////////////////////////////

   private void testAtsConfig(XResultData rd) {
      ElapsedTime time = new ElapsedTime("Get ATS Config with Pend");
      AtsApiService.get().getConfigService().getConfigurationsWithPend();
      rd.logf(AHTML.addRowMultiColumnTable(time.getTimeSpent().toString(), "PASS", "Get ATS Config with Pend", "JSON",
         "/ats/config"));
   }

   /////////////////////////////////
   /////////////////////////////////

   private List<StandAloneRestData> getStandAloneRestCalls() {

      for (OseeProductionTestProvider provider : providers) {
         datas.addAll(provider.getStandAloneRestDatas());
      }

      // @formatter:off
      datas.add(new StandAloneRestData("ATS New Action", "/ats/ui/action/NewAction", "HTML", "Create new ATS Action"));
      datas.add(new StandAloneRestData("ATS Action Endpoint", "/ats/ui/action", "HTML", "ATS UI Endpoint"));
      datas.add(new StandAloneRestData("ATS Action Search", "/ats/ui/action/Search", "HTML", "ATS - Search"));
      datas.add(new StandAloneRestData("ATS Configure Branch", "/ats/config/ui/NewAtsBranchConfig", "HTML", "ATS - Configure Branch"));
      datas.add(new StandAloneRestData("OSEE Health", "/server/health", "HTML", "OSEE Health"));
      datas.add(new StandAloneRestData("Active MQ Event Service", "/server/health/activemq", "HTML", "Active MQ Event Service"));
      datas.add(new StandAloneRestData("Load Balancer Status", "/server/health/balancer", "HTML", "Load Blancer Status"));
      datas.add(new StandAloneRestData("OSEE Health - Server Health - Overview", "/server/health/overview", "HTML", "OSEE Health - Server Health - Overview"));
      datas.add(new StandAloneRestData("Server Status", "/server/health/overview/details", "HTML", "Server Status"));
      datas.add(new StandAloneRestData("ORCS Branch", "/orcs/branch", "HTML", "Name"));
      datas.add(new StandAloneRestData("Server Health Types", "/server/health/types", "HTML", "server Types"));
      datas.add(new StandAloneRestData("Server Health Usage", "/server/health/usage", "HTML", "Account"));

      datas.add(new StandAloneRestData("ATS Health Check", "/ats/health/check", "HTML", "ATS Health Check"));
      datas.add(new StandAloneRestData("ATS Config", "/ats/config", "JSON", "\"views\""));
      datas.add(new StandAloneRestData("ATS Program", "/ats/program", "JSON", "\"name\""));
      datas.add(new StandAloneRestData("ATS Program Details", "/ats/program/details", "JSON", "\"country\""));
      datas.add(new StandAloneRestData("ATS Team Details", "/ats/team/details", "JSON", "\"Name\""));
      datas.add(new StandAloneRestData("Dispo Program", "/dispo/program", "JSON", "\"text\""));
      datas.add(new StandAloneRestData("ORCS Branch", "/orcs/branches", "JSON", "\"inheritAccessControl\""));
      datas.add(new StandAloneRestData("APPS API", "/apps/api", "JSON", "\"description\""));
      datas.add(new StandAloneRestData("ATS AI Details", "/ats/ai/details", "JSON", "\"Description\""));
      datas.add(new StandAloneRestData("ATS Country Details", "/ats/country/details", "JSON", "\"programs\""));
      datas.add(new StandAloneRestData("Server Health Status", "/server/health/status", "JSON", "\"threadStats\""));
      datas.add(new StandAloneRestData("ATS Config Validate", "/ats/config/validate", "JSON", "\"numErrorsViaSearch\""));
      datas.add(new StandAloneRestData("ORCS Working Branches", "/orcs/branches/working", "JSON", "\"associatedArtifact\""));
      datas.add(new StandAloneRestData("ORCS Datastore Info", "/orcs/datastore/info", "JSON", "\"properties\""));

      // Example of failed due to invalid url
      datas.add(new StandAloneRestData("ATS Confg - FAILED", "/ats/confg", "JSON", "items"));

      // Example of failed due to invalid result
      datas.add(new StandAloneRestData("ATS New Action - FAILED", "/ats/ui/action/NewAction", "HTML", "Blah Blah"));

      // @formatter:on
      return datas;
   }

   private void testStandAloneRest(XResultData rd) {
      for (StandAloneRestData data : getStandAloneRestCalls()) {
         String title = data.getName();
         String url = data.getUrl();
         ElapsedTime time = new ElapsedTime(" ");
         boolean passed = true;
         if (data.getMediaType().equals("HTML")) {
            String html = getHtml(url);
            if (!html.contains(data.getResultStr())) {
               passed = false;
            }
         } else {
            String json = getJson(url);
            if (!json.contains(data.getResultStr())) {
               passed = false;
            }
         }
         Long ms = time.getTimeSpent();
         rd.logf(AHTML.addRowMultiColumnTable(ms.toString(), getPassFail(passed), title, data.getMediaType(), url));
      }
   }

   private String getPassFail(boolean passed) {
      return passed ? "PASS" : AHTML.boldColor("RED", "FAIL");
   }

   protected String getHtml(String url) {
      return getAndCheckResponseCode(url, MediaType.TEXT_HTML_TYPE);
   }

   protected String getJson(String url) {
      return getAndCheckResponseCode(url, MediaType.APPLICATION_JSON_TYPE);
   }

   private String getAndCheckResponseCode(String path, MediaType mediaType) {
      JaxRsApi jaxRsApi = AtsApiService.get().jaxRsApi();
      return jaxRsApi.newTarget(path).request(mediaType).get().readEntity(String.class);
   }

   /////////////////////////////////
   /////////////////////////////////

   private Map<String, IAtsConfigQuery> getAtsWorkItemQueries() {
      ArtifactId atsTeamDefId = ArtifactId.valueOf(112825L);

      IAtsTeamDefinition atsTeamDef =
         AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(atsTeamDefId);

      Map<String, IAtsConfigQuery> queries = new HashMap<>();
      queries.put("Query Working ATS Team Workflows by Team",
         queryService.createQuery(AtsArtifactTypes.TeamWorkflow).andAttr(AtsAttributeTypes.CurrentStateType,
            StateType.Working.name()).andAttr(AtsAttributeTypes.TeamDefinitionReference, atsTeamDefId.toString()));

      ArtifactCache.deCache(CoreBranches.COMMON);

      Collection<String> actionableItems = new ArrayList<>();
      for (Long id : AtsObjects.toIds(AtsApiService.get().getActionableItemService().getActionableItems(atsTeamDef))) {
         actionableItems.add(id.toString());
      }
      queries.put("Query Working ATS Team Workflows by AI",
         queryService.createQuery(AtsArtifactTypes.TeamWorkflow).andAttr(AtsAttributeTypes.CurrentStateType,
            StateType.Working.name()).andAttr(AtsAttributeTypes.ActionableItemReference, actionableItems));

      ArtifactCache.deCache(CoreBranches.COMMON);

      queries.put("Query Working Reviews", queryService.createQuery(AtsArtifactTypes.PeerToPeerReview).andAttr(
         AtsAttributeTypes.CurrentStateType, StateType.Working.name()));

      ArtifactCache.deCache(CoreBranches.COMMON);

      queries.put("Query By State", queryService.createQuery(AtsArtifactTypes.TeamWorkflow).andAttr(
         AtsAttributeTypes.CurrentStateName, TeamState.Authorize.getName()));

      return queries;
   }

   private void testAtsApiQueries(XResultData rd) {
      for (Entry<String, IAtsConfigQuery> entry : getAtsWorkItemQueries().entrySet()) {
         String title = entry.getKey();
         ElapsedTime time = new ElapsedTime(title);
         Collection<IAtsWorkItem> workItems = entry.getValue().getWorkItems();
         boolean passed = true;
         Long ms = time.getTimeSpent();
         rd.logf(AHTML.addRowMultiColumnTable(ms.toString(), getPassFail(passed), title, "API",
            workItems.size() + " returned"));
      }
   }

   /////////////////////////////////
   /////////////////////////////////

   private Map<String, IAtsConfigQuery> getConfigObjectQueries() {
      Map<String, IAtsConfigQuery> queries = new HashMap<>();
      queries.put("Team Definition",
         queryService.createQuery(AtsArtifactTypes.TeamDefinition).andWorkType(WorkType.Code));
      queries.put("Team Definition with Name", queryService.createQuery(AtsArtifactTypes.TeamDefinition).andAttr(
         CoreAttributeTypes.Name, DemoArtifactToken.SAW_Code.getName()));
      queries.put("Team Definition with ProgramId", queryService.createQuery(AtsArtifactTypes.TeamDefinition).andAttr(
         AtsAttributeTypes.ProgramId, "Test Program ID"));
      return queries;
   }

   private void testAtsTeamDefinition(XResultData rd) {
      for (Entry<String, IAtsConfigQuery> entry : getConfigObjectQueries().entrySet()) {
         String title = entry.getKey();
         ElapsedTime time = new ElapsedTime(title);
         ResultSet<IAtsTeamDefinition> teamDefs = entry.getValue().getConfigObjectResultSet();
         IAtsTeamDefinition teamDef = (IAtsTeamDefinition) entry.getValue().getConfigObjectResultSet().getOneOrDefault(
            IAtsTeamDefinition.SENTINEL);
         boolean passed = true;
         Long ms = time.getTimeSpent();
         rd.logf(AHTML.addRowMultiColumnTable(ms.toString(), getPassFail(passed), title, "API",
            teamDefs.size() + " returned"));
      }
   }

   /////////////////////////////////
   /////////////////////////////////

   private Map<String, IAtsQuery> getAtsQueries() {
      Map<String, IAtsQuery> queries = new HashMap<>();
      queries.put("Peer Review WorkItem", queryService.createQuery(WorkItemType.PeerReview));
      ArtifactId version = queryService.getArtifactByName(AtsArtifactTypes.Version, "0.26.0");
      if (version != null) {
         queries.put("Version TeamWorkflow",
            queryService.createQuery(WorkItemType.TeamWorkflow).andVersion(version.getId()));
      }
      return queries;
   }

   private void testAtsQueries(XResultData rd) {
      for (Entry<String, IAtsQuery> entry : getAtsQueries().entrySet()) {
         String title = entry.getKey();
         ElapsedTime time = new ElapsedTime(title);
         ResultSet<IAtsWorkItem> workItems = entry.getValue().getResults();
         boolean passed = true;
         Long ms = time.getTimeSpent();
         rd.logf(AHTML.addRowMultiColumnTable(ms.toString(), getPassFail(passed), title, "API",
            workItems.size() + " returned"));
      }
   }

   /////////////////////////////////
   /////////////////////////////////

   private Map<String, QueryBuilderArtifact> getArtifactQueries() {
      QueryBuilderArtifact query1 = ArtifactQuery.createQueryBuilder(CoreBranches.COMMON);
      List<ArtifactId> artIds = new ArrayList<ArtifactId>();
      artIds.add(ArtifactId.valueOf(5367053));
      artIds.add(ArtifactId.valueOf(10867103));
      query1.andIds(artIds);
      Map<String, QueryBuilderArtifact> queries = new HashMap<>();
      queries.put("Query Artifacts", query1);
      QueryBuilderArtifact query2 = ArtifactQuery.createQueryBuilder(CoreBranches.COMMON);
      query2.and(CoreAttributeTypes.Name, "ATS CM Branch", QueryOption.CONTAINS_MATCH_OPTIONS);
      queries.put("Artifact with Attribute", query2);
      return queries;
   }

   private void testArtifactQueries(XResultData rd) {
      for (Entry<String, QueryBuilderArtifact> entry : getArtifactQueries().entrySet()) {
         String title = entry.getKey();
         ElapsedTime time = new ElapsedTime(title);
         List<Artifact> artifacts = entry.getValue().getResults().getList();
         boolean passed = true;
         Long ms = time.getTimeSpent();
         rd.logf(AHTML.addRowMultiColumnTable(ms.toString(), getPassFail(passed), title, "API",
            artifacts.size() + " returned"));
      }
   }

   /////////////////////////////////
   /////////////////////////////////

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(AtsUserGroups.AtsAdmin);
   }

}
