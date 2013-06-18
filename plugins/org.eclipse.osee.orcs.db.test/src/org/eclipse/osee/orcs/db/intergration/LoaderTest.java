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
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.integrationRule;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.sort;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.verifyData;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Iterator;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactBuilder;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandler;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.OrcsDataStore;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
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
	@Mock private ArtifactBuilder builder;
	@Mock private ArtifactDataHandler artifactHandler;
	@Mock private AttributeDataHandler attributeHandler;
	@Mock private RelationDataHandler relationHandler;
	@Captor private ArgumentCaptor<ArtifactData> artifactCaptor;
	@Captor private ArgumentCaptor<AttributeData> attributeCaptor;
	@Captor private ArgumentCaptor<RelationData> relationCaptor;
	// @formatter:on

   private String sessionId;
   private HasCancellation cancellation;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      sessionId = GUID.create();

      when(builder.createArtifactDataHandler()).thenReturn(artifactHandler);
      when(builder.createAttributeDataHandler()).thenReturn(attributeHandler);
      when(builder.createRelationDataHandler()).thenReturn(relationHandler);
   }

   @org.junit.Test
   public void testLoad() throws OseeCoreException {
      DataLoaderFactory loaderFactory = dataStore.getDataLoaderFactory();
      DataLoader loader = loaderFactory.fromBranchAndArtifactIds(sessionId, CoreBranches.COMMON, 5, 6, 7);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.load(cancellation, builder);

      verify(builder).createArtifactDataHandler();
      verify(builder).createAttributeDataHandler();
      verify(builder).createRelationDataHandler();

      verify(artifactHandler, times(3)).onData(artifactCaptor.capture());
      verify(attributeHandler, times(7)).onData(attributeCaptor.capture());
      verify(relationHandler, times(3)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(arts.next(), 5, "AkA10I4aUSDLuFNIaegA", "3VY6B",
				ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,
				15L);
		verifyData(arts.next(), 6, "AkA10LiAPEZLR4+jdFQA", "N782Y",
				ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,
				16L);
		verifyData(arts.next(), 7, "AkA2AcT6AXe6ivMFRhAA", "LBVP3",
				ModificationType.NEW, Folder.getGuid(), 2, 6, -1, 43L);
		// @formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(attrs.next(), 9, 5, ModificationType.NEW, Name.getGuid(), 2,
				5, -1, 5L,
				"org.eclipse.osee.framework.skynet.core.OseeTypes_Framework",
				"");
		verifyData(attrs.next(), 10, 5, ModificationType.NEW,
				UriGeneralStringData.getGuid(), 2, 5, -1, 6L, "",
				"attr://6/AkA10I4aUSDLuFNIaegA.zip");
		verifyData(attrs.next(), 11, 5, ModificationType.NEW, Active.getGuid(),
				2, 5, -1, 7L, "true", "");

		verifyData(attrs.next(), 12, 6, ModificationType.NEW, Name.getGuid(),
				2, 5, -1, 8L, "org.eclipse.osee.coverage.OseeTypes_Coverage",
				"");
		verifyData(attrs.next(), 13, 6, ModificationType.NEW,
				UriGeneralStringData.getGuid(), 2, 5, -1, 9L, "",
				"attr://9/AkA10LiAPEZLR4+jdFQA.zip");
		verifyData(attrs.next(), 14, 6, ModificationType.NEW, Active.getGuid(),
				2, 5, -1, 10L, "true", "");

		verifyData(attrs.next(), 17, 7, ModificationType.NEW, Name.getGuid(),
				2, 6, -1, 33L, "User Groups", "");
		// @formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(rels.next(), 1, 7, 7, 8, "", ModificationType.NEW,
				Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 53L);
		verifyData(rels.next(), 2, 7, 1, 7, "", ModificationType.NEW,
				Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 52L);
		verifyData(rels.next(), 3, 7, 7, 15, "", ModificationType.NEW,
				Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 54L);
		// @formatter:on
   }

   @org.junit.Test
   public void testLoadByTypes() throws OseeCoreException {
      DataLoaderFactory loaderFactory = dataStore.getDataLoaderFactory();
      DataLoader loader = loaderFactory.fromBranchAndArtifactIds(sessionId, CoreBranches.COMMON, 5, 6, 7);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.loadAttributeType(Name);
      loader.loadRelationType(Default_Hierarchical__Parent);

      loader.load(cancellation, builder);

      verify(builder).createArtifactDataHandler();
      verify(builder).createAttributeDataHandler();
      verify(builder).createRelationDataHandler();

      verify(artifactHandler, times(3)).onData(artifactCaptor.capture());
      verify(attributeHandler, times(3)).onData(attributeCaptor.capture());
      verify(relationHandler, times(3)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(arts.next(), 5, "AkA10I4aUSDLuFNIaegA", "3VY6B",
				ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,
				15L);
		verifyData(arts.next(), 6, "AkA10LiAPEZLR4+jdFQA", "N782Y",
				ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,
				16L);
		verifyData(arts.next(), 7, "AkA2AcT6AXe6ivMFRhAA", "LBVP3",
				ModificationType.NEW, Folder.getGuid(), 2, 6, -1, 43L);
		// @formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(attrs.next(), 9, 5, ModificationType.NEW, Name.getGuid(), 2,
				5, -1, 5L,
				"org.eclipse.osee.framework.skynet.core.OseeTypes_Framework",
				"");
		verifyData(attrs.next(), 12, 6, ModificationType.NEW, Name.getGuid(),
				2, 5, -1, 8L, "org.eclipse.osee.coverage.OseeTypes_Coverage",
				"");
		verifyData(attrs.next(), 17, 7, ModificationType.NEW, Name.getGuid(),
				2, 6, -1, 33L, "User Groups", "");
		// @formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(rels.next(), 1, 7, 7, 8, "", ModificationType.NEW,
				Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 53L);
		verifyData(rels.next(), 2, 7, 1, 7, "", ModificationType.NEW,
				Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 52L);
		verifyData(rels.next(), 3, 7, 7, 15, "", ModificationType.NEW,
				Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 54L);
		// @formatter:on
   }

   @org.junit.Test
   public void testLoadByIds() throws OseeCoreException {
      DataLoaderFactory loaderFactory = dataStore.getDataLoaderFactory();
      DataLoader loader = loaderFactory.fromBranchAndArtifactIds(sessionId, CoreBranches.COMMON, 5, 6, 7);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.loadAttributeLocalId(11, 14);
      loader.loadRelationLocalId(2, 3);

      loader.load(cancellation, builder);

      verify(builder).createArtifactDataHandler();
      verify(builder).createAttributeDataHandler();
      verify(builder).createRelationDataHandler();

      verify(artifactHandler, times(3)).onData(artifactCaptor.capture());
      verify(attributeHandler, times(2)).onData(attributeCaptor.capture());
      verify(relationHandler, times(2)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(arts.next(), 5, "AkA10I4aUSDLuFNIaegA", "3VY6B",
				ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,
				15L);
		verifyData(arts.next(), 6, "AkA10LiAPEZLR4+jdFQA", "N782Y",
				ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,
				16L);
		verifyData(arts.next(), 7, "AkA2AcT6AXe6ivMFRhAA", "LBVP3",
				ModificationType.NEW, Folder.getGuid(), 2, 6, -1, 43L);
		// @formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(attrs.next(), 11, 5, ModificationType.NEW, Active.getGuid(),
				2, 5, -1, 7L, "true", "");
		verifyData(attrs.next(), 14, 6, ModificationType.NEW, Active.getGuid(),
				2, 5, -1, 10L, "true", "");
		// @formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      // @formatter:off
		verifyData(rels.next(), 2, 7, 1, 7, "", ModificationType.NEW,
				Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 52L);
		verifyData(rels.next(), 3, 7, 7, 15, "", ModificationType.NEW,
				Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 54L);
		// @formatter:on
   }

}
