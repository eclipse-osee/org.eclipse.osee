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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.RelationReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Assert;
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
   @Mock private OrcsSession session;
   // @formatter:on

   private static final ArtifactToken AtsAdminToken =
      TokenFactory.createArtifactToken(136750, "asdf", "AtsAdmin", CoreArtifactTypes.UserGroup);
   private static final ArtifactToken AtsTempAdminToken =
      TokenFactory.createArtifactToken(5367074, "qwerty", "AtsTempAdmin", CoreArtifactTypes.UserGroup);
   private HasCancellation cancellation;
   private DataLoaderFactory loaderFactory;
   private static ArtifactReadable OseeTypesFrameworkArt;
   private static int OseeTypesFrameworkId, OseeTypesFrameworkActiveAttrId, OseeTypesFrameworkNameAttrId;
   private static long OseeTypesFrameworkActiveGammaId, OseeTypesFrameworkNameGammaId;
   private static String OseeTypesFrameworkGuid;
   private static ArtifactReadable OseeTypesClientDemoArt;
   private static int OseeTypesClientDemoId, OseeTypesClientDemoActiveAttrId, OseeTypesClientDemoNameAttrId;
   private static long OseeTypesClientDemoActiveGammaId, OseeTypesClientDemoNameGammaId;
   private static String OseeTypesClientDemoGuid;
   private static final long UserGroupsArtifactGammaId = 43L, OseeTypesClientDemoGammaId = 11L,
      OseeTypesFrameworkGammaId = 8L;
   private static final int UserGroupsId = CoreArtifactTokens.UserGroups.getUuid().intValue();
   private static int UserGroupsNameAttrId;
   private static long UserGroupsNameGammaId;
   private static final Map<ArtifactToken, Integer> artTokenToRelationId = new HashMap<>();
   private static final Map<ArtifactToken, Long> artTokenToRelationGammaId = new HashMap<>();
   private final String UserGroupsGuid = CoreArtifactTokens.UserGroups.getGuid();
   private static final List<ArtifactToken> relationsArts =
      Arrays.asList(CoreArtifactTokens.Everyone, CoreArtifactTokens.DefaultHierarchyRoot, CoreArtifactTokens.OseeAdmin,
         CoreArtifactTokens.OseeAccessAdmin, AtsTempAdminToken, AtsAdminToken);
   private static long defaultHierRootToUserGroupsRelationGammaId;
   private static long userGroupsToOseeAdminRelationGammaId;
   // Transaction that OseeTypes_ClientDemo and OseeTypes_Framework were created in
   private final TransactionId tx5 = TransactionId.valueOf(5);
   // Transaction that User Groups was created in
   private final TransactionId tx7 = TransactionId.valueOf(7);
   // Transaction that AtsAdmin, AtsTempAdmin and OseeAccessAdmin were created in and related to User Groups
   private final TransactionId tx10 = TransactionId.valueOf(10);

   public void setUp() throws OseeCoreException {
      JdbcClient jdbcClient = jdbcService.getClient();
      if (jdbcClient.getConfig().isProduction()) {
         throw new OseeStateException("Test should not be run against a Production Database");
      }

      if (OseeTypesFrameworkArt == null) {
         for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            CoreArtifactTypes.OseeTypeDefinition).getResults()) {
            if (art.getName().contains("Framework")) {
               OseeTypesFrameworkId = art.getId().intValue();
               OseeTypesFrameworkGuid = art.getGuid();
               OseeTypesFrameworkArt = art;
               for (AttributeReadable<Object> attr : art.getAttributes()) {
                  if (attr.isOfType(CoreAttributeTypes.Active)) {
                     OseeTypesFrameworkActiveAttrId = attr.getId().intValue();
                     OseeTypesFrameworkActiveGammaId = Long.valueOf(attr.getGammaId());
                  } else if (attr.isOfType(CoreAttributeTypes.Name)) {
                     OseeTypesFrameworkNameAttrId = attr.getId().intValue();
                     OseeTypesFrameworkNameGammaId = Long.valueOf(attr.getGammaId());
                  }
               }
            } else if (art.getName().contains("OseeTypes_ClientDemo")) {
               OseeTypesClientDemoId = art.getId().intValue();
               OseeTypesClientDemoGuid = art.getGuid();
               OseeTypesClientDemoArt = art;
               for (AttributeReadable<Object> attr : art.getAttributes()) {
                  if (attr.isOfType(CoreAttributeTypes.Active)) {
                     OseeTypesClientDemoActiveAttrId = attr.getId().intValue();
                     OseeTypesClientDemoActiveGammaId = Long.valueOf(attr.getGammaId());
                  } else if (attr.isOfType(CoreAttributeTypes.Name)) {
                     OseeTypesClientDemoNameAttrId = attr.getId().intValue();
                     OseeTypesClientDemoNameGammaId = Long.valueOf(attr.getGammaId());
                  }
               }
            }
         }

         ArtifactReadable userGroupFolder = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            CoreArtifactTokens.UserGroups).getResults().getExactlyOne();
         for (AttributeReadable<Object> attr : userGroupFolder.getAttributes()) {
            if (attr.isOfType(CoreAttributeTypes.Name)) {
               UserGroupsNameAttrId = attr.getId().intValue();
               UserGroupsNameGammaId = Long.valueOf(attr.getGammaId());
            }
         }
         ArtifactReadable defaultHierRoot = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
            CoreArtifactTokens.DefaultHierarchyRoot).getResults().getExactlyOne();
         for (RelationReadable relation : defaultHierRoot.getRelations(CoreRelationTypes.Default_Hierarchical__Child)) {
            if (relation.getArtIdB() == CoreArtifactTokens.UserGroups.getId().intValue()) {
               defaultHierRootToUserGroupsRelationGammaId = relation.getGammaId();
               break;
            }
         }
         for (RelationReadable rel : userGroupFolder.getRelations(CoreRelationTypes.Default_Hierarchical__Child)) {
            for (ArtifactToken token : relationsArts) {
               if (rel.getArtIdB() == token.getId().intValue() || rel.getArtIdA() == token.getId().intValue()) {
                  artTokenToRelationId.put(token, rel.getId().intValue());
                  artTokenToRelationGammaId.put(token, rel.getGammaId());
               }
            }
            if (rel.getArtIdB() == CoreArtifactTokens.OseeAdmin.getId().intValue()) {
               userGroupsToOseeAdminRelationGammaId = rel.getGammaId();
            }
         }
         Assert.assertEquals(6, relationsArts.size());
      }

      MockitoAnnotations.initMocks(this);
      loaderFactory = dataStore.createDataModule(orcsApi.getOrcsTypes()).getDataLoaderFactory();

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);
   }

   /**
    * Only need one copy of the database for all 4 tests.
    */
   @org.junit.Test
   public void testAll() throws OseeCoreException {
      setUp();
      testLoad();
      setUp();
      testLoadByTypes();
      setUp();
      testLoadByIds();
      setUp();
      testLoadByGuids();
   }

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

      verifyData(attrs.next(), OseeTypesFrameworkActiveAttrId, OseeTypesFrameworkId, NEW, Active.getId(), COMMON, tx5,
         OseeTypesFrameworkActiveGammaId, true, "");
      verifyData(attrs.next(), OseeTypesFrameworkActiveAttrId + 1, OseeTypesFrameworkId, NEW, Name.getId(), COMMON, tx5,
         OseeTypesFrameworkActiveGammaId + 1, "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "");
      verifyData(attrs.next(), OseeTypesFrameworkActiveAttrId + 2, OseeTypesFrameworkId, NEW,
         UriGeneralStringData.getId(), COMMON, tx5, OseeTypesFrameworkActiveGammaId + 2, "",
         "attr://" + (OseeTypesFrameworkActiveGammaId + 2) + "/" + OseeTypesFrameworkGuid + ".zip");

      verifyData(attrs.next(), OseeTypesClientDemoActiveAttrId, OseeTypesClientDemoId, NEW, Active.getId(), COMMON, tx5,
         OseeTypesClientDemoActiveGammaId, true, "");
      verifyData(attrs.next(), OseeTypesClientDemoActiveAttrId + 1, OseeTypesClientDemoId, NEW, Name.getId(), COMMON,
         tx5, OseeTypesClientDemoActiveGammaId + 1, "org.eclipse.osee.client.demo.OseeTypes_ClientDemo", "");
      verifyData(attrs.next(), OseeTypesClientDemoActiveAttrId + 2, OseeTypesClientDemoId, NEW,
         UriGeneralStringData.getId(), COMMON, tx5, OseeTypesClientDemoActiveGammaId + 2, "",
         "attr://" + (OseeTypesClientDemoActiveGammaId + 2) + "/" + OseeTypesClientDemoGuid + ".zip");

      verifyData(attrs.next(), UserGroupsNameAttrId, UserGroupsId, NEW, Name.getId(), COMMON, tx7,
         UserGroupsNameGammaId, "User Groups", "");

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      verifyRels(rels);
   }

   private void verifyRels(Iterator<RelationData> rels) {
      verifyData(rels.next(), artTokenToRelationId.get(CoreArtifactTokens.Everyone), UserGroupsId,
         CoreArtifactTokens.Everyone.getId().intValue(), "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         artTokenToRelationGammaId.get(CoreArtifactTokens.Everyone));
      verifyData(rels.next(), artTokenToRelationId.get(CoreArtifactTokens.DefaultHierarchyRoot),
         CoreArtifactTokens.DefaultHierarchyRoot.getId().intValue(), UserGroupsId, "", NEW,
         Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         artTokenToRelationGammaId.get(CoreArtifactTokens.DefaultHierarchyRoot));
      verifyData(rels.next(), artTokenToRelationId.get(CoreArtifactTokens.OseeAdmin), UserGroupsId,
         CoreArtifactTokens.OseeAdmin.getId().intValue(), "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx7,
         artTokenToRelationGammaId.get(CoreArtifactTokens.OseeAdmin));
      verifyData(rels.next(), artTokenToRelationId.get(CoreArtifactTokens.OseeAccessAdmin), UserGroupsId,
         CoreArtifactTokens.OseeAccessAdmin.getId().intValue(), "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON,
         tx10, artTokenToRelationGammaId.get(CoreArtifactTokens.OseeAccessAdmin));
      verifyData(rels.next(), artTokenToRelationId.get(AtsAdminToken), UserGroupsId, AtsAdminToken.getId().intValue(),
         "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx10, artTokenToRelationGammaId.get(AtsAdminToken));
      verifyData(rels.next(), artTokenToRelationId.get(AtsTempAdminToken), UserGroupsId,
         AtsTempAdminToken.getId().intValue(), "", NEW, Default_Hierarchical__Parent.getGuid(), COMMON, tx10,
         artTokenToRelationGammaId.get(AtsTempAdminToken));
   }

   private void verifyArts(Iterator<ArtifactData> arts) {
      verifyData(arts.next(), UserGroupsId, UserGroupsGuid, NEW, Folder.getId(), COMMON, tx7,
         UserGroupsArtifactGammaId);
      verifyData(arts.next(), OseeTypesClientDemoId, OseeTypesClientDemoGuid, NEW, OseeTypeDefinition.getId(), COMMON,
         tx5, OseeTypesClientDemoGammaId);
      verifyData(arts.next(), OseeTypesFrameworkId, OseeTypesFrameworkGuid, NEW, OseeTypeDefinition.getId(), COMMON,
         tx5, OseeTypesFrameworkGammaId);
   }

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

      verifyData(attrs.next(), OseeTypesFrameworkNameAttrId, OseeTypesFrameworkId, NEW, Name.getId(), COMMON, tx5,
         OseeTypesFrameworkNameGammaId, "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "");
      verifyData(attrs.next(), OseeTypesClientDemoNameAttrId, OseeTypesClientDemoId, NEW, Name.getId(), COMMON, tx5,
         OseeTypesClientDemoNameGammaId, "org.eclipse.osee.client.demo.OseeTypes_ClientDemo", "");
      verifyData(attrs.next(), UserGroupsNameAttrId, UserGroupsId, NEW, Name.getId(), COMMON, tx7,
         UserGroupsNameGammaId, "User Groups", "");

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      verifyRels(rels);
   }

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
         frameworkActiveAttr.getGammaId(), true, "");
      verifyData(attrs.next(), clientDemoActiveAttrId, OseeTypesClientDemoId, NEW, Active.getId(), COMMON, tx5,
         clientDemoActiveAttr.getGammaId(), true, "");

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      verifyData(rels.next(), 2, CoreArtifactTokens.DefaultHierarchyRoot.getId().intValue(), UserGroupsId, "", NEW,
         Default_Hierarchical__Parent.getGuid(), COMMON, tx7, defaultHierRootToUserGroupsRelationGammaId);
      verifyData(rels.next(), 3, UserGroupsId, CoreArtifactTokens.OseeAdmin.getId().intValue(), "", NEW,
         Default_Hierarchical__Parent.getGuid(), COMMON, tx7, userGroupsToOseeAdminRelationGammaId);
   }

   private AttributeReadable<Object> getActiveAttr(ArtifactReadable artifact) {
      for (AttributeReadable<Object> attr : artifact.getAttributes()) {
         if (attr.getAttributeType().equals(Active)) {
            return attr;
         }
      }
      return null;
   }

   public void testLoadByGuids() throws OseeCoreException {
      String[] ids = new String[] {OseeTypesFrameworkGuid, OseeTypesClientDemoGuid, UserGroupsGuid};
      DataLoader loader = loaderFactory.newDataLoaderFromGuids(session, COMMON, ids);
      loader.withLoadLevel(LoadLevel.ALL);
      verifyArtsAttrAndRelData(loader);
   }
}
