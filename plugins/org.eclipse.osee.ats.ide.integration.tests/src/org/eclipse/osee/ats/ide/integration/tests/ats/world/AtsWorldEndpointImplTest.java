/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.world;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsCoreColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;
import org.eclipse.osee.ats.api.workflow.world.WorldResults;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.priority.PriorityColumnUI;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.ResultRows;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactNameColumnUI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for AtsWorldEndpointImpl
 *
 * @author Donald G. Dunne
 */
public class AtsWorldEndpointImplTest {

   private static final String GUID_LOCAL = "guid local";
   private static final String GUID_GLOBAL = "guid global";
   private AtsApi atsApi;
   private AtsWorldEndpointApi worldEp;
   private static CustomizeData localCust;
   // Web Export Cust in demo db (see WebExportCust.xml)
   private final String WEB_EXPORT_CUSTOMIZED_GUID = "4vgtrpe942a9t1hu3imv30";

   @BeforeClass
   public static void classSetup() {
      SkynetCustomizations sCust = new SkynetCustomizations(null);

      /*
       * namespaces used to be bundle names, test that this is backward compatible
       */
      for (String namespace : Arrays.asList(WorldXViewerFactory.NAMESPACE, "ats." + WorldXViewerFactory.NAMESPACE,
         "ats.ide." + WorldXViewerFactory.NAMESPACE)) {
         CustomizeData globalCust = createCustomizeData(GUID_GLOBAL + namespace, namespace);
         globalCust.setGuid(GUID_GLOBAL + namespace);
         globalCust.setPersonal(false);
         sCust.saveCustomization(globalCust);
      }

      localCust = createCustomizeData(GUID_LOCAL, WorldXViewerFactory.NAMESPACE);
      localCust.setGuid(GUID_LOCAL);
      localCust.setPersonal(true);
      sCust.saveCustomization(localCust);
   }

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
      worldEp = atsApi.getServerEndpoints().getWorldEndpoint();
   }

   // /ats/world/cust/global
   @Test
   public void testGetCustomizationsGlobal() {
      Collection<CustomizeData> custGlobal = worldEp.getCustomizationsGlobal();
      Assert.assertNotNull(custGlobal);
      Assert.assertEquals(5, custGlobal.size());
   }

   // /ats/world/cust
   @Test
   public void testGetCustomizations() {
      Collection<CustomizeData> custs = worldEp.getCustomizations();
      Assert.assertNotNull(custs);
      Assert.assertEquals(6, custs.size());
   }

   // /ats/world/my/{userArtId}
   @Test
   public void testGetMyWorld() {
      Collection<IAtsWorkItem> items = worldEp.getMyWorld(DemoUsers.Joe_Smith);
      Assert.assertNotNull(items);
      Assert.assertFalse(items.isEmpty());
   }

   // /ats/world/my/{userArtId}/ui
   @Test
   public void testGetMyWorldUI() {
      String html = worldEp.getMyWorldUI(DemoUsers.Joe_Smith);
      Assert.assertNotNull(html);
      Assert.assertTrue(html.contains("My World"));
   }

   // /ats/world/my/{userArtId}/ui/{customizeGuid}
   @Test
   public void testGetMyWorldUICustomized() {
      String html = worldEp.getMyWorldUICustomized(ArtifactId.create(DemoUsers.Joe_Smith), GUID_LOCAL);
      Assert.assertNotNull(html);
      Assert.assertTrue(html.startsWith("<h2>MY World - Joe Smith - Customization: guid local"));
   }

   // /ats/world/coll/{collectorId}/ui/{customizeGuid}
   @Test
   public void testGetCollectionandCollectionUIAndCollectionUICustomized() {
      ArtifactToken backlog =
         AtsApiService.get().getQueryService().getArtifactByName(AtsArtifactTypes.AgileBacklog, "SAW Backlog");
      Collection<IAtsWorkItem> items = worldEp.getCollection(backlog);
      Assert.assertNotNull(items);
      Assert.assertFalse(items.isEmpty());

      String html = worldEp.getCollectionUI(backlog);
      Assert.assertNotNull(html);
      Assert.assertTrue(html.startsWith("<h2>Collection: SAW Backlog<"));

      String html2 = worldEp.getCollectionUICustomized(backlog, GUID_LOCAL);
      Assert.assertNotNull(html2);
      Assert.assertTrue(html2.startsWith("<h2>Collector: SAW Backlog - Customization: guid local<"));
   }

   // /ats/world/search
   @Test
   public void testSearch() {
      AtsSearchData search = new AtsSearchData("search");
      search.setId(342333423L);
      search.setTitle("SAW Backlog");
      search.setCustomizeData(localCust);
      Assert.assertEquals(1, worldEp.search(search).getResults().size());

      search.setTitle("conflicted");
      Assert.assertEquals(1, worldEp.search(search).getResults().size());
   }

   private static CustomizeData createCustomizeData(String name, String namespace) {
      CustomizeData cd = new CustomizeData();
      cd.setName(name);
      cd.setNameSpace(namespace);
      cd.getColumnData().getColumns().add(new ArtifactNameColumnUI(true));
      cd.getColumnData().getColumns().add(ChangeTypeColumnUI.instance);
      PriorityColumnUI priCol = PriorityColumnUI.instance.copy();
      priCol.setShow(false);
      cd.getColumnData().getColumns().add(priCol);
      return cd;
   }

   // Test /ats/world/column
   @Test
   public void testGetColumn() {
      Collection<AtsCoreColumn> columns = atsApi.getServerEndpoints().getWorldEndpoint().getColumns();
      Assert.assertTrue(String.valueOf(columns.size()), columns.size() > 180);
   }

   // Test /ats/world/columnjson
   @Test
   public void testGetColumnjson() {
      String json = atsApi.getServerEndpoints().getWorldEndpoint().getColumnsJson();
      Assert.assertTrue(json.contains("\"id\" : \"ats.State\","));
   }

   // Test /ats/world/teamWfsInState
   @Test
   public void testSearchNew() {
      AtsSearchData atsSearchData = new AtsSearchData();
      atsSearchData.setTeamDefIds(Arrays.asList(DemoArtifactToken.SAW_Requirements.getId()));
      atsSearchData.setStateTypes(Arrays.asList(StateType.Working));
      Collection<CustomizeData> customizations = atsApi.getStoreService().getCustomizations("WorldXViewer");
      atsSearchData.setCustomizeData(customizations.iterator().next());
      ResultRows resultRows = atsApi.getServerEndpoints().getWorldEndpoint().searchNew(atsSearchData);
      Assert.assertTrue(resultRows.getRd().isSuccess());
      Assert.assertEquals(4, resultRows.getResults().size());
   }

   // Test /ats/world/my/{userArtId}/ui/{customizeGuid}
   @Test
   public void testAtsWorldMyUi() {
      String html = atsApi.getServerEndpoints().getWorldEndpoint().getMyWorldUICustomized(
         ArtifactId.valueOf(DemoUsers.Joe_Smith.getId()), WEB_EXPORT_CUSTOMIZED_GUID);
      Assert.assertTrue(Strings.isValid(html));
      Assert.assertTrue(html.contains("MY World - Joe Smith - Customization: Web Export Cust"));
   }

   // Test /ats/world/coll/{collectorId}/json/{customizeGuid}
   @Test
   public void testCollectionJsonCustomized() {
      WorldResults results = atsApi.getServerEndpoints().getWorldEndpoint().getCollectionJsonCustomized(
         AtsArtifactToken.WebExportGoal.getToken(), WEB_EXPORT_CUSTOMIZED_GUID);
      Assert.assertNotNull(results);
      Assert.assertTrue(results.getRd().isSuccess());
      List<String> headers = results.getOrderedHeaders();
      Assert.assertEquals(12, headers.size());
      Assert.assertEquals(AtsArtifactToken.WebExportGoal.getName(), results.getCollectorArt().getName());
   }

   // Test /ats/world/coll/{collectorId}/json/{customizeGuid}/publish
   // Test /ats/world/coll/{collectorId}/worldresults
   // Test /ats/world/coll/{collectorId}/export
   @Test
   public void testCollectionJsonCustomizedPublishAndWorldResults() {
      ArtifactToken artifact = atsApi.getQueryService().getArtifact(AtsArtifactToken.WebExportGoal);
      Assert.assertNull("", atsApi.getAttributeResolver().getSoleAttributeValueAsString(artifact,
         AtsAttributeTypes.WorldResultsJson, null));
      WorldResults results = atsApi.getServerEndpoints().getWorldEndpoint().getCollectionJsonCustomizedPublish(
         AtsArtifactToken.WebExportGoal, WEB_EXPORT_CUSTOMIZED_GUID);
      Assert.assertNotNull(results);
      Assert.assertTrue(results.getRd().isSuccess());
      // Reload to get latest
      ((Artifact) artifact).reloadAttributesAndRelations();
      String json = atsApi.getAttributeResolver().getSoleAttributeValueAsString(artifact,
         AtsAttributeTypes.WorldResultsJson, null);
      Assert.assertNotNull(json);

      WorldResults worldResults = atsApi.getServerEndpoints().getWorldEndpoint().getCollectionJsonCustomizedPublished(
         AtsArtifactToken.WebExportGoal);
      Assert.assertNotNull(worldResults);
      Assert.assertTrue(worldResults.getRd().isSuccess());

      String html = atsApi.getServerEndpoints().getWorldEndpoint().getCollectionExport(AtsArtifactToken.WebExportGoal);
      Assert.assertNotNull(html);
      Assert.assertTrue(html.contains(AtsArtifactToken.WebExportGoal.getName()));
   }

}
