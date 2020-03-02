/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.world;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.ide.column.PriorityColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactNameColumn;
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
      atsApi = AtsClientService.get();
      worldEp = atsApi.getServerEndpoints().getWorldEndpoint();
   }

   @Test
   public void testGetCustomizationsGlobal() {
      Collection<CustomizeData> custGlobal = worldEp.getCustomizationsGlobal();
      Assert.assertNotNull(custGlobal);
      Assert.assertEquals(3, custGlobal.size());
   }

   @Test
   public void testGetCustomizations() {
      Collection<CustomizeData> custs = worldEp.getCustomizations();
      Assert.assertNotNull(custs);
      Assert.assertEquals(4, custs.size());
   }

   @Test
   public void testGetMyWorld() {
      Collection<IAtsWorkItem> items = worldEp.getMyWorld(DemoUsers.Joe_Smith);
      Assert.assertNotNull(items);
      Assert.assertFalse(items.isEmpty());
   }

   @Test
   public void testGetMyWorldUI() {
      String html = worldEp.getMyWorldUI(DemoUsers.Joe_Smith);
      Assert.assertNotNull(html);
      Assert.assertTrue(html.contains("My World"));
   }

   @Test
   public void testGetMyWorldUICustomized() {
      String html = worldEp.getMyWorldUICustomized(ArtifactId.valueOf(DemoUsers.Joe_Smith.getId()), GUID_LOCAL);
      Assert.assertNotNull(html);
      Assert.assertTrue(html.startsWith("<h2>MY World - Joe Smith - Customization: guid local"));
   }

   @Test
   public void testGetCollectionandCollectionUIAndCollectionUICustomized() {
      ArtifactToken backlog =
         AtsClientService.get().getQueryService().getArtifactByName(AtsArtifactTypes.AgileBacklog, "SAW Backlog");
      Collection<IAtsWorkItem> items = worldEp.getCollection(backlog);
      Assert.assertNotNull(items);
      Assert.assertFalse(items.isEmpty());

      String html = worldEp.getCollectionUI(backlog);
      Assert.assertNotNull(html);
      Assert.assertTrue(html.startsWith("<h2>Collection - SAW Backlog<"));

      String html2 = worldEp.getCollectionUICustomized(backlog, GUID_LOCAL);
      Assert.assertNotNull(html2);
      Assert.assertTrue(html2.startsWith("<h2>Collector - SAW Backlog - Customization: guid local<"));
   }

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
      cd.getColumnData().getColumns().add(new ArtifactNameColumn(true));
      cd.getColumnData().getColumns().add(ChangeTypeColumnUI.instance);
      PriorityColumnUI priCol = PriorityColumnUI.instance.copy();
      priCol.setShow(false);
      cd.getColumnData().getColumns().add(priCol);
      return cd;
   }

}
