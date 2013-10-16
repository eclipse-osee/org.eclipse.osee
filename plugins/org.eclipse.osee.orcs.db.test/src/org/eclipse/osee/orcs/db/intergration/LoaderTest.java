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
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
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
import org.eclipse.osee.orcs.data.ArtifactTypes;
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
   public TestRule db = integrationRule(this, "osee.demo.hsql");

   // @formatter:off
	@OsgiService private IOseeDatabaseService dbService;
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

   @Before
   public void setUp() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      loaderFactory = dataStore.createDataModule(artTypes, attrTypes).getDataLoaderFactory();

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      when(artTypes.getByUuid(OseeTypeDefinition.getGuid())).thenReturn(OseeTypeDefinition);
      when(artTypes.getByUuid(Folder.getGuid())).thenReturn(Folder);

      when(attrTypes.getByUuid(Name.getGuid())).thenReturn(Name);
      when(attrTypes.getByUuid(UriGeneralStringData.getGuid())).thenReturn(UriGeneralStringData);
      when(attrTypes.getByUuid(Active.getGuid())).thenReturn(Active);

      when(attrTypes.getAttributeProviderId(Name)).thenReturn("DefaultAttributeDataProvider");
      when(attrTypes.getAttributeProviderId(UriGeneralStringData)).thenReturn("DefaultAttributeDataProvider");
      when(attrTypes.getAttributeProviderId(Active)).thenReturn("DefaultAttributeDataProvider");
   }

   @org.junit.Test
   public void testLoad() throws OseeCoreException {
      DataLoader loader = loaderFactory.fromBranchAndArtifactIds(session, CoreBranches.COMMON, 5, 6, 7);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(CoreBranches.COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(7)).onData(attributeCaptor.capture());
      verify(builder, times(3)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(arts.next(), 5, "AkA10I4aUSDLuFNIaegA", NEW, OseeTypeDefinition.getGuid(), 2, 5, -1, 15L);
		verifyData(arts.next(), 6, "AkA10LiAPEZLR4+jdFQA",  NEW, OseeTypeDefinition.getGuid(), 2, 5, -1, 16L);
		verifyData(arts.next(), 7, "AkA2AcT6AXe6ivMFRhAA",  NEW, Folder.getGuid(), 2, 6, -1, 43L);
		// @formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(attrs.next(), 9, 5, NEW, Name.getGuid(), 2, 5, -1, 5L, "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "");
		verifyData(attrs.next(), 10, 5, NEW, UriGeneralStringData.getGuid(), 2, 5, -1, 6L, "", "attr://6/AkA10I4aUSDLuFNIaegA.zip");
		verifyData(attrs.next(), 11, 5, NEW, Active.getGuid(), 2, 5, -1, 7L, "true", "");

		verifyData(attrs.next(), 12, 6, NEW, Name.getGuid(), 2, 5, -1, 8L, "org.eclipse.osee.coverage.OseeTypes_Coverage", "");
		verifyData(attrs.next(), 13, 6, NEW, UriGeneralStringData.getGuid(), 2, 5, -1, 9L, "", "attr://9/AkA10LiAPEZLR4+jdFQA.zip");
		verifyData(attrs.next(), 14, 6, NEW, Active.getGuid(), 2, 5, -1, 10L, "true", "");
		verifyData(attrs.next(), 17, 7, NEW, Name.getGuid(), 2, 6, -1, 33L, "User Groups", "");
		// @formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(rels.next(), 1, 7, 8, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 53L);
		verifyData(rels.next(), 2, 1, 7, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 52L);
		verifyData(rels.next(), 3, 7, 15, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 54L);
		// @formatter:on
   }

   @org.junit.Test
   public void testLoadByTypes() throws OseeCoreException {
      DataLoader loader = loaderFactory.fromBranchAndArtifactIds(session, CoreBranches.COMMON, 5, 6, 7);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.loadAttributeType(Name);
      loader.loadRelationType(Default_Hierarchical__Parent);

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(CoreBranches.COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(3)).onData(attributeCaptor.capture());
      verify(builder, times(3)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(arts.next(), 5, "AkA10I4aUSDLuFNIaegA",  NEW, OseeTypeDefinition.getGuid(), 2, 5, -1, 15L);
		verifyData(arts.next(), 6, "AkA10LiAPEZLR4+jdFQA",  NEW, OseeTypeDefinition.getGuid(), 2, 5, -1, 16L);
		verifyData(arts.next(), 7, "AkA2AcT6AXe6ivMFRhAA",  NEW, Folder.getGuid(), 2, 6, -1, 43L); 
		// @formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(attrs.next(), 9, 5, NEW, Name.getGuid(), 2, 5, -1, 5L, "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "");
		verifyData(attrs.next(), 12, 6, NEW, Name.getGuid(), 2, 5, -1, 8L, "org.eclipse.osee.coverage.OseeTypes_Coverage", "");
		verifyData(attrs.next(), 17, 7, NEW, Name.getGuid(), 2, 6, -1, 33L, "User Groups", "");
		// @formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(rels.next(), 1, 7, 8, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 53L);
		verifyData(rels.next(), 2, 1, 7, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 52L);
		verifyData(rels.next(), 3, 7, 15, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 54L);
		// @formatter:on
   }

   @org.junit.Test
   public void testLoadByIds() throws OseeCoreException {
      DataLoader loader = loaderFactory.fromBranchAndArtifactIds(session, CoreBranches.COMMON, 5, 6, 7);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.loadAttributeLocalId(11, 14);
      loader.loadRelationLocalId(2, 3);

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(CoreBranches.COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(2)).onData(attributeCaptor.capture());
      verify(builder, times(2)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(arts.next(), 5, "AkA10I4aUSDLuFNIaegA", NEW, OseeTypeDefinition.getGuid(), 2, 5, -1, 15L);
		verifyData(arts.next(), 6, "AkA10LiAPEZLR4+jdFQA",  NEW, OseeTypeDefinition.getGuid(), 2, 5, -1, 16L);
		verifyData(arts.next(), 7, "AkA2AcT6AXe6ivMFRhAA",  NEW, Folder.getGuid(), 2, 6, -1, 43L);
		// @formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(attrs.next(), 11, 5, NEW, Active.getGuid(), 2, 5, -1, 7L, "true", "");
		verifyData(attrs.next(), 14, 6, NEW, Active.getGuid(), 2, 5, -1, 10L, "true", "");
		// @formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(rels.next(), 2, 1, 7, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 52L);
		verifyData(rels.next(), 3, 7, 15, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 54L);
		// @formatter:on
   }

   @org.junit.Test
   public void testLoadByGuids() throws OseeCoreException {
      String[] ids = new String[] {"AkA10I4aUSDLuFNIaegA", "AkA10LiAPEZLR4+jdFQA", "AkA2AcT6AXe6ivMFRhAA"};
      DataLoader loader = loaderFactory.fromBranchAndIds(session, CoreBranches.COMMON, ids);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.load(cancellation, builder);

      verify(builder).onLoadStart();
      verify(builder).onLoadDescription(descriptorCaptor.capture());
      verify(builder).onLoadEnd();

      LoadDescription descriptor = descriptorCaptor.getValue();
      assertEquals(CoreBranches.COMMON, descriptor.getBranch());

      verify(builder, times(3)).onData(artifactCaptor.capture());
      verify(builder, times(7)).onData(attributeCaptor.capture());
      verify(builder, times(3)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      // @formatter:off
      verifyData(arts.next(), 5, "AkA10I4aUSDLuFNIaegA",  NEW, OseeTypeDefinition.getGuid(), 2, 5, -1, 15L);
      verifyData(arts.next(), 6, "AkA10LiAPEZLR4+jdFQA",  NEW, OseeTypeDefinition.getGuid(), 2, 5, -1, 16L);
      verifyData(arts.next(), 7, "AkA2AcT6AXe6ivMFRhAA",  NEW, Folder.getGuid(), 2, 6, -1, 43L);
      // @formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      // @formatter:off
      verifyData(attrs.next(), 9, 5, NEW, Name.getGuid(), 2, 5, -1, 5L, "org.eclipse.osee.framework.skynet.core.OseeTypes_Framework", "");
      verifyData(attrs.next(), 10, 5, NEW, UriGeneralStringData.getGuid(), 2, 5, -1, 6L, "", "attr://6/AkA10I4aUSDLuFNIaegA.zip");
      verifyData(attrs.next(), 11, 5, NEW, Active.getGuid(), 2, 5, -1, 7L, "true", "");

      verifyData(attrs.next(), 12, 6, NEW, Name.getGuid(), 2, 5, -1, 8L, "org.eclipse.osee.coverage.OseeTypes_Coverage", "");
      verifyData(attrs.next(), 13, 6, NEW, UriGeneralStringData.getGuid(), 2, 5, -1, 9L, "", "attr://9/AkA10LiAPEZLR4+jdFQA.zip");
      verifyData(attrs.next(), 14, 6, NEW, Active.getGuid(), 2, 5, -1, 10L, "true", "");
      verifyData(attrs.next(), 17, 7, NEW, Name.getGuid(), 2, 6, -1, 33L, "User Groups", "");
      // @formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      // @formatter:off
      verifyData(rels.next(), 1, 7, 8, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 53L);
      verifyData(rels.next(), 2, 1, 7, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 52L);
      verifyData(rels.next(), 3, 7, 15, "", NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 54L);
      // @formatter:on
   }
}
