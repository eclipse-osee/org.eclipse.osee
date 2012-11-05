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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.*;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.*;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.*;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.*;
import static org.mockito.Mockito.*;
import java.util.Iterator;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiRule;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Roberto E. Escobar
 */
public class LoaderTest {

   @Rule
   public OsgiRule osgi = new OsgiRule(this);

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   // @formatter:off
   @OsgiService private OrcsDataStore dataStore;
   
   @Mock private ArtifactBuilder builder;
   
   @Mock private ArtifactDataHandler artifactHandler;
   @Mock private AttributeDataHandler attributeHandler;
   @Mock private RelationDataHandler relationHandler;
   
   @Captor ArgumentCaptor<ArtifactData> artifactCaptor;
   @Captor ArgumentCaptor<AttributeData> attributeCaptor;
   @Captor ArgumentCaptor<RelationData> relationCaptor;
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
      DataLoader loader = loaderFactory.fromBranchAndArtifactIds(sessionId, CoreBranches.COMMON, 6, 7, 8);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.load(cancellation, builder);

      verify(builder).createArtifactDataHandler();
      verify(builder).createAttributeDataHandler();
      verify(builder).createRelationDataHandler();

      verify(artifactHandler, times(3)).onData(artifactCaptor.capture());
      verify(attributeHandler, times(7)).onData(attributeCaptor.capture());
      verify(relationHandler, times(4)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      //@formatter:off
      verifyData(arts.next(), 6, "AEmKsTkcwh02JspUtYQA", "NYN46", ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,  7L);
      verifyData(arts.next(), 7, "AEmKsWXLBwVrvjcQvPwA", "7NPJR", ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,  9L);
      verifyData(arts.next(), 8, "AEmK_YNYKmA66ynLWVgA", "QHXXC", ModificationType.NEW, Folder.getGuid(), 2, 6, -1, 33L);
      //@formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      //@formatter:off
      verifyData(attrs.next(), 12, 6, ModificationType.NEW, Name.getGuid(), 2, 5, -1, 21L, "org.eclipse.osee.coverage.OseeTypes_Coverage", "");
      verifyData(attrs.next(), 13, 6, ModificationType.NEW, UriGeneralStringData.getGuid(), 2, 5, -1, 24L, "", "attr://24/AEmKsTkcwh02JspUtYQA.zip");
      verifyData(attrs.next(), 14, 6, ModificationType.NEW, Active.getGuid(), 2, 5, -1, 23L, "true", "");
      
      verifyData(attrs.next(), 15, 7, ModificationType.NEW, Name.getGuid(), 2, 5, -1, 10L, "org.eclipse.osee.ats.config.demo.OseeTypes_Demo", "");
      verifyData(attrs.next(), 16, 7, ModificationType.NEW, UriGeneralStringData.getGuid(), 2, 5, -1, 11L, "", "attr://11/AEmKsWXLBwVrvjcQvPwA.zip");
      verifyData(attrs.next(), 17, 7, ModificationType.NEW, Active.getGuid(), 2, 5, -1, 12L, "true", "");
      
      verifyData(attrs.next(), 20, 8, ModificationType.NEW, Name.getGuid(), 2, 6, -1, 48L, "User Groups", "");
      //@formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      //@formatter:off
      verifyData(rels.next(), 1, 8, 8, 9, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 41L);
      verifyData(rels.next(), 2, 8, 1, 8, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 36L);
      verifyData(rels.next(), 3, 8, 8, 16, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 37L);
      verifyData(rels.next(), 173, 8, 8, 121, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 16, -1, 699L);
      //@formatter:on
   }

   @org.junit.Test
   public void testLoadByTypes() throws OseeCoreException {
      DataLoaderFactory loaderFactory = dataStore.getDataLoaderFactory();
      DataLoader loader = loaderFactory.fromBranchAndArtifactIds(sessionId, CoreBranches.COMMON, 6, 7, 8);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.loadAttributeType(Name);
      loader.loadRelationType(Default_Hierarchical__Parent);

      loader.load(cancellation, builder);

      verify(builder).createArtifactDataHandler();
      verify(builder).createAttributeDataHandler();
      verify(builder).createRelationDataHandler();

      verify(artifactHandler, times(3)).onData(artifactCaptor.capture());
      verify(attributeHandler, times(3)).onData(attributeCaptor.capture());
      verify(relationHandler, times(4)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      //@formatter:off
      verifyData(arts.next(), 6, "AEmKsTkcwh02JspUtYQA", "NYN46", ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,  7L);
      verifyData(arts.next(), 7, "AEmKsWXLBwVrvjcQvPwA", "7NPJR", ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,  9L);
      verifyData(arts.next(), 8, "AEmK_YNYKmA66ynLWVgA", "QHXXC", ModificationType.NEW, Folder.getGuid(), 2, 6, -1, 33L);
      //@formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      //@formatter:off
      verifyData(attrs.next(), 12, 6, ModificationType.NEW, Name.getGuid(), 2, 5, -1, 21L, "org.eclipse.osee.coverage.OseeTypes_Coverage", "");
      verifyData(attrs.next(), 15, 7, ModificationType.NEW, Name.getGuid(), 2, 5, -1, 10L, "org.eclipse.osee.ats.config.demo.OseeTypes_Demo", "");
      verifyData(attrs.next(), 20, 8, ModificationType.NEW, Name.getGuid(), 2, 6, -1, 48L, "User Groups", "");
      //@formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      //@formatter:off
      verifyData(rels.next(), 1, 8, 8, 9, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 41L);
      verifyData(rels.next(), 2, 8, 1, 8, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 36L);
      verifyData(rels.next(), 3, 8, 8, 16, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 37L);
      verifyData(rels.next(), 173, 8, 8, 121, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 16, -1, 699L);
      //@formatter:on
   }

   @org.junit.Test
   public void testLoadByIds() throws OseeCoreException {
      DataLoaderFactory loaderFactory = dataStore.getDataLoaderFactory();
      DataLoader loader = loaderFactory.fromBranchAndArtifactIds(sessionId, CoreBranches.COMMON, 6, 7, 8);
      loader.setLoadLevel(LoadLevel.FULL);

      loader.loadAttributeLocalId(14, 17);
      loader.loadRelationLocalId(2, 173);

      loader.load(cancellation, builder);

      verify(builder).createArtifactDataHandler();
      verify(builder).createAttributeDataHandler();
      verify(builder).createRelationDataHandler();

      verify(artifactHandler, times(3)).onData(artifactCaptor.capture());
      verify(attributeHandler, times(2)).onData(attributeCaptor.capture());
      verify(relationHandler, times(2)).onData(relationCaptor.capture());

      sort(artifactCaptor.getAllValues());
      Iterator<ArtifactData> arts = artifactCaptor.getAllValues().iterator();

      //@formatter:off
      verifyData(arts.next(), 6, "AEmKsTkcwh02JspUtYQA", "NYN46", ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,  7L);
      verifyData(arts.next(), 7, "AEmKsWXLBwVrvjcQvPwA", "7NPJR", ModificationType.NEW, OseeTypeDefinition.getGuid(), 2, 5, -1,  9L);
      verifyData(arts.next(), 8, "AEmK_YNYKmA66ynLWVgA", "QHXXC", ModificationType.NEW, Folder.getGuid(), 2, 6, -1, 33L);
      //@formatter:on

      sort(attributeCaptor.getAllValues());
      Iterator<AttributeData> attrs = attributeCaptor.getAllValues().iterator();

      //@formatter:off
      verifyData(attrs.next(), 14, 6, ModificationType.NEW, Active.getGuid(), 2, 5, -1, 23L, "true", "");
      verifyData(attrs.next(), 17, 7, ModificationType.NEW, Active.getGuid(), 2, 5, -1, 12L, "true", "");
      //@formatter:on

      sort(relationCaptor.getAllValues());
      Iterator<RelationData> rels = relationCaptor.getAllValues().iterator();

      //@formatter:off
      verifyData(rels.next(), 2, 8, 1, 8, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 6, -1, 36L);
      verifyData(rels.next(), 173, 8, 8, 121, "", ModificationType.NEW, Default_Hierarchical__Parent.getGuid(), 2, 16, -1, 699L);
      //@formatter:on
   }

}
