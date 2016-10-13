/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.intergration;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.OseeTypeDefinition;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Active;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UriGeneralStringData;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Parent;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.integrationRule;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.sort;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.verifyData;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Roberto E. Escobar
 */
public class LoaderTest {

   @Rule
   public TestRule db = integrationRule(this);

   @OsgiService
   public OrcsApi orcsApi;
   @OsgiService
   public JdbcService jdbcService;

   // @formatter:off
   @OsgiService private OrcsDataStore dataStore;
   @Mock private LoadDataHandler builder;
   @Captor private ArgumentCaptor<LoadDescription> descriptorCaptor;
   @Captor private ArgumentCaptor<ArtifactData> artifactCaptor;
   @Captor private ArgumentCaptor<AttributeData> attributeCaptor;
   @Captor private ArgumentCaptor<RelationData> relationCaptor;
   @Mock private OrcsTypes types;
   @Mock private ArtifactTypes artTypes;
   @Mock private OrcsSession session;
   @Mock private AttributeTypes attrTypes;
   // @formatter:on

   private HasCancellation cancellation;
   private DataLoaderFactory loaderFactory;
   private ArtifactReadable OseeTypesFrameworkArt;
   private int OseeTypesFrameworkId;
   private String OseeTypesFrameworkGuid;
   private ArtifactReadable OseeTypesClientDemoArt;
   private int OseeTypesClientDemoId;
   private String OseeTypesClientDemoGuid;
   private final int UserGroupsId = CoreArtifactTokens.UserGroups.getUuid().intValue();
   private final String UserGroupsGuid = CoreArtifactTokens.UserGroups.getGuid();
   private final TransactionId tx5 = TransactionId.valueOf(5);
   private final TransactionId tx7 = TransactionId.valueOf(7);
   private final TransactionId tx10 = TransactionId.valueOf(10);

   @Before
   public void setUp() throws OseeCoreException {
      JdbcClient jdbcClient = jdbcService.getClient();

      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
         CoreArtifactTypes.OseeTypeDefinition).getResults()) {
         if (art.getName().contains("Framework")) {
            OseeTypesFrameworkId = art.getId().intValue();
            OseeTypesFrameworkGuid = art.getGuid();
            OseeTypesFrameworkArt = art;
         } else if (art.getName().contains("OseeTypes_ClientDemo")) {
            OseeTypesClientDemoId = art.getId().intValue();
            OseeTypesClientDemoGuid = art.getGuid();
            OseeTypesClientDemoArt = art;
         }
      }

      if (jdbcClient.getConfig().isProduction()) {
         throw new OseeStateException("Test should not be run against a Production Database");
      }

      MockitoAnnotations.initMocks(this);

      when(types.getArtifactTypes()).thenReturn(artTypes);
      when(types.getAttributeTypes()).thenReturn(attrTypes);

      loaderFactory = dataStore.createDataModule(types).getDataLoaderFactory();

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      when(artTypes.get(OseeTypeDefinition.getId())).thenReturn(OseeTypeDefinition);
      when(artTypes.get(Folder.getId())).thenReturn(Folder);

      when(attrTypes.get(Name.getId())).thenReturn(Name);
      when(attrTypes.get(UriGeneralStringData.getId())).thenReturn(UriGeneralStringData);
      when(attrTypes.get(Active.getId())).thenReturn(Active);

      when(attrTypes.getAttributeProviderId(Name)).thenReturn("DefaultAttributeDataProvider");
      when(attrTypes.getAttributeProviderId(UriGeneralStringData)).thenReturn("DefaultAttributeDataProvider");
      when(attrTypes.getAttributeProviderId(Active)).thenReturn("DefaultAttributeDataProvider");

   }

   @org.junit.Test
   public void testLoad() throws OseeCoreException {
      DataLoader loader =
         loaderFactory.newDataLoaderFromIds(session, COMMON, OseeTypesFrameworkId, OseeTypesClientDemoId, UserGroupsId);
      loader.withLoadLevel(LoadLevel.ALL);
      verifyArtsAttrAndRelData(loader);
   }

   private void verifyArtsAttrAndRelData(DataLoader loader) {

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(7)).onData(attributeCaptor.capture());
      verify(builder, times(6)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      verifyArts(arts);

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      verifyData(attrs.next(), 4, OseeTypesFrameworkId, NEW, Active.getId(), COMMON, tx5, 13L, "true", "");
      verifyData(attrs.next(), 5, OseeTypesFrameworkId, NEW, Name.getId(), COMMON, tx5, 14L,
         "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "");
      verifyData(attrs.next(), 6, OseeTypesFrameworkId, NEW, UriGeneralStringData.getId(), COMMON, tx5, 15L, "",
         "attr://15/" + OseeTypesFrameworkGuid + ".zip");

      verifyData(attrs.next(), 7, OseeTypesClientDemoId, NEW, Active.getId(), COMMON, tx5, 16L, "true", "");
      verifyData(attrs.next(), 8, OseeTypesClientDemoId, NEW, Name.getId(), COMMON, tx5, 17L,
         "org.eclipse.osee.client.demo.OseeTypes_ClientDemo", "");
      verifyData(attrs.next(), 9, OseeTypesClientDemoId, NEW, UriGeneralStringData.getId(), COMMON, tx5, 18L, "",
         "attr://18/" + OseeTypesClientDemoGuid + ".zip");

      verifyData(attrs.next(), 20, UserGroupsId, NEW, Name.getId(), COMMON, tx7, 49L, "User Groups", "");

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      verifyRels(rels);
   }

   private void verifyRels(Iterator<RelationData> rels) {
      verifyData(rels.next(), 1, UserGroupsId, 48656, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         41L);
      verifyData(rels.next(), 2, 197818, UserGroupsId, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         40L);
      verifyData(rels.next(), 3, UserGroupsId, 52247, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         39L);
      verifyData(rels.next(), 9, UserGroupsId, 8033605, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx10,
         110L);
      verifyData(rels.next(), 10, UserGroupsId, 136750, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx10,
         108L);
      verifyData(rels.next(), 11, UserGroupsId, 5367074, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx10,
         107L);
   }

   private void verifyArts(Iterator<ArtifactData> arts) {
      verifyData(arts.next(), UserGroupsId, UserGroupsGuid, NEW, Folder.getId(), COMMON, tx7, 42L);
      verifyData(arts.next(), OseeTypesClientDemoId, OseeTypesClientDemoGuid, NEW, OseeTypeDefinition.getId(), COMMON,
         tx5, 9L);
      verifyData(arts.next(), OseeTypesFrameworkId, OseeTypesFrameworkGuid, NEW, OseeTypeDefinition.getId(), COMMON,
         tx5, 8L);
   }

   @org.junit.Test
   public void testLoadByTypes() throws OseeCoreException {
      DataLoader loader =
         loaderFactory.newDataLoaderFromIds(session, COMMON, OseeTypesFrameworkId, OseeTypesClientDemoId, UserGroupsId);
      loader.withLoadLevel(LoadLevel.ALL);

      loader.withAttributeTypes(Name);
      loader.withRelationTypes(Default_Hierarchical__Parent);

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(3)).onData(attributeCaptor.capture());
      verify(builder, times(6)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      verifyArts(arts);

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      verifyData(attrs.next(), 5, OseeTypesFrameworkId, NEW, Name.getId(), COMMON, tx5, 14L,
         "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "");
      verifyData(attrs.next(), 8, OseeTypesClientDemoId, NEW, Name.getId(), COMMON, tx5, 17L,
         "org.eclipse.osee.client.demo.OseeTypes_ClientDemo", "");
      verifyData(attrs.next(), 20, UserGroupsId, NEW, Name.getId(), COMMON, tx7, 49L, "User Groups", "");

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      verifyRels(rels);
   }

   @org.junit.Test
   public void testLoadByIds() throws OseeCoreException {
      DataLoader loader =
         loaderFactory.newDataLoaderFromIds(session, COMMON, OseeTypesFrameworkId, OseeTypesClientDemoId, UserGroupsId);
      loader.withLoadLevel(LoadLevel.ALL);

      List<Integer> activeAttrIds = new LinkedList<>();
      AttributeReadable<Object> frameworkActiveAttr = getActiveAttr(OseeTypesFrameworkArt);
      Integer frameworkActiveAttrId = frameworkActiveAttr.getId().intValue();
      activeAttrIds.add(frameworkActiveAttrId);
      AttributeReadable<Object> clientDemoActiveAttr = getActiveAttr(OseeTypesClientDemoArt);
      Integer clientDemoActiveAttrId = clientDemoActiveAttr.getId().intValue();
      activeAttrIds.add(clientDemoActiveAttrId);
      loader.withAttributeIds(activeAttrIds);

      loader.withRelationIds(2, 3);

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(2)).onData(attributeCaptor.capture());
      verify(builder, times(2)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      verifyArts(arts);

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      verifyData(attrs.next(), frameworkActiveAttrId, OseeTypesFrameworkId, NEW, Active.getId(), COMMON, tx5,
         frameworkActiveAttr.getGammaId(), "true", "");
      verifyData(attrs.next(), clientDemoActiveAttrId, OseeTypesClientDemoId, NEW, Active.getId(), COMMON, tx5,
         clientDemoActiveAttr.getGammaId(), "true", "");

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      verifyData(rels.next(), 2, 197818, UserGroupsId, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         40L);
      verifyData(rels.next(), 3, UserGroupsId, 52247, "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         39L);
   }

   private AttributeReadable<Object> getActiveAttr(ArtifactReadable artifact) {
      for (AttributeReadable<Object> attr : artifact.getAttributes()) {
         if (attr.getAttributeType().equals(Active)) {
            return attr;
         }
      }
      return null;
   }

   @org.junit.Test
   public void testLoadByGuids() throws OseeCoreException {
      String[] ids = new String[] {OseeTypesFrameworkGuid, OseeTypesClientDemoGuid, UserGroupsGuid};
      DataLoader loader = loaderFactory.newDataLoaderFromGuids(session, COMMON, ids);
      loader.withLoadLevel(LoadLevel.ALL);
      verifyArtsAttrAndRelData(loader);
   }
}
