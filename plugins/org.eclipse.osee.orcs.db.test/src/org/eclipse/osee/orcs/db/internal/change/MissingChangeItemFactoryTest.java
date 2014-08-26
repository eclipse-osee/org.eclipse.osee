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
package org.eclipse.osee.orcs.db.internal.change;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.change.ArtifactChangeItem;
import org.eclipse.osee.framework.core.model.change.AttributeChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.RelationChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author John Misinco
 */
@RunWith(value = Parameterized.class)
public class MissingChangeItemFactoryTest {

   // @formatter:off
   @Mock private DataLoaderFactory dataLoaderFactory;
   @Mock private IdentityLocator identityService;
   @Mock private DataLoader sourceDataLoader;
   @Mock private DataLoader destDataLoader;
   @Mock private Branch sourceBranch;
   @Mock private Branch destBranch;
   @Mock private TransactionReadable sourceTx;
   @Mock private TransactionReadable destTx;
   @Mock private OrcsSession session;
   @Mock private HasCancellation cancellation;
   // @formatter:on

   private MissingChangeItemFactory changeItemFactory;
   private final List<ChangeItem> changes;
   private final List<ChangeItem> expectedMissingChanges;
   private final List<AttributeData> attributeData;
   private final List<ArtifactData> artifactData;
   private final List<ArtifactData> destArtifactData;
   private final List<RelationData> relationData;

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> params = new LinkedList<Object[]>();
      params.add(testCase_missingAttribute());
      params.add(testCase_missingRelation());
      params.add(testCase_deletedAttribute());
      params.add(testCase_artifactDeletedAttribute());
      return params;
   }

   public MissingChangeItemFactoryTest(List<ChangeItem> changes, List<ChangeItem> expectedMissingChanges, List<AttributeData> attributeData, List<ArtifactData> artifactData, List<RelationData> relationData, List<ArtifactData> destArtifactData) {
      this.changes = changes;
      this.expectedMissingChanges = expectedMissingChanges;
      this.attributeData = attributeData;
      this.artifactData = artifactData;
      this.relationData = relationData;
      this.destArtifactData = destArtifactData;
   }

   @SuppressWarnings("unchecked")
   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      String sessionGuid = GUID.create();
      when(session.getGuid()).thenReturn(sessionGuid);
      when(dataLoaderFactory.newDataLoaderFromIds(any(OrcsSession.class), eq(sourceBranch), any(Collection.class))).thenReturn(
         sourceDataLoader);
      when(dataLoaderFactory.newDataLoaderFromIds(any(OrcsSession.class), eq(destBranch), any(Collection.class))).thenReturn(
         destDataLoader);
      when(sourceTx.getBranchId()).thenReturn(sourceBranch.getGuid());
      when(destTx.getBranchId()).thenReturn(destBranch.getGuid());
      changeItemFactory = new MissingChangeItemFactoryImpl(identityService, dataLoaderFactory);
   }

   @Test
   public void testCreateMissingChanges() throws OseeCoreException {
      doAnswer(new Answer<Object>() {

         @Override
         public Object answer(InvocationOnMock invocation) throws Throwable {
            LoadDataHandler handler = (LoadDataHandler) invocation.getArguments()[1];

            if (artifactData != null) {
               for (ArtifactData data : artifactData) {
                  handler.onData(data);
               }
            }

            if (attributeData != null) {
               for (AttributeData data : attributeData) {
                  handler.onData(data);
               }
            }

            if (relationData != null) {
               for (RelationData data : relationData) {
                  handler.onData(data);
               }
            }

            return null;
         }
      }).when(sourceDataLoader).load(any(HasCancellation.class), any(LoadDataHandler.class));

      doAnswer(new Answer<Object>() {

         @Override
         public Object answer(InvocationOnMock invocation) throws Throwable {
            LoadDataHandler handler = (LoadDataHandler) invocation.getArguments()[1];

            if (destArtifactData != null) {
               for (ArtifactData data : destArtifactData) {
                  handler.onData(data);
               }
            }

            return null;
         }
      }).when(destDataLoader).load(any(HasCancellation.class), any(LoadDataHandler.class));

      Collection<ChangeItem> results =
         changeItemFactory.createMissingChanges(cancellation, session, changes, sourceTx, destTx);
      if (expectedMissingChanges == null) {
         Assert.assertTrue(results.isEmpty());
      } else {
         Assert.assertEquals(expectedMissingChanges.size(), results.size());
         for (ChangeItem change : results) {
            ChangeItem expected = getMatchingChangeItem(change);
            Assert.assertNotNull(expected);
            Assert.assertEquals(expected.getClass().getSimpleName(), change.getClass().getSimpleName());
            Assert.assertEquals(expected.getCurrentVersion().getGammaId(), change.getCurrentVersion().getGammaId());
            Assert.assertTrue(expected.getCurrentVersion().getModType() == change.getCurrentVersion().getModType());
         }
      }
   }

   /**
    * This tests when a modified attribute causes its artifact and non-modified attribute to be introduced
    */
   private static Object[] testCase_missingAttribute() {
      List<ChangeItem> changes = new LinkedList<ChangeItem>();
      final int ci1AttrId = 1;
      final int artId = 3;
      final long missingGamma = 9L;
      long artGamma = 7L;

      ChangeItem ci1 =
         new AttributeChangeItem(ci1AttrId, 2, artId, 4L, ModificationType.MODIFIED, Strings.EMPTY_STRING);
      changes.add(ci1);

      List<AttributeData> attrDatas = new LinkedList<AttributeData>();
      AttributeData attrData1 = createAttributeData(artId, ci1AttrId, 1L, ModificationType.MODIFIED);
      AttributeData attrData2 = createAttributeData(artId, ci1AttrId + 1, missingGamma, ModificationType.INTRODUCED);
      attrDatas.add(attrData1);
      attrDatas.add(attrData2);

      List<ArtifactData> artData = new LinkedList<ArtifactData>();
      ArtifactData artData1 = createArtifactData(artId, artGamma, ModificationType.NEW);
      artData.add(artData1);

      List<ChangeItem> expected = new LinkedList<ChangeItem>();
      expected.add(createExpected(attrData2));
      expected.add(createExpected(artData1));

      return new Object[] {changes, expected, attrDatas, artData, null, null};
   }

   /**
    * create a relation change item who's artifact and other attributes and relations need to be introduced
    */
   private static Object[] testCase_missingRelation() {
      List<ChangeItem> changes = new LinkedList<ChangeItem>();
      final int ci1AttrId = 22;
      final long missingGamma = 9L;
      long artGamma = 7L;

      int relId = 6;
      int artA = 65;
      int artB = 2;
      long srcGamma = 7L;
      ChangeItem ci1 = new RelationChangeItem(relId, 0, srcGamma, ModificationType.NEW, artA, artB, "");
      changes.add(ci1);

      List<AttributeData> attrDatas = new LinkedList<AttributeData>();
      AttributeData attrData1 = createAttributeData(artA, ci1AttrId, 1L, ModificationType.MODIFIED);
      AttributeData attrData2 = createAttributeData(artA, ci1AttrId + 1, missingGamma, ModificationType.INTRODUCED);
      attrDatas.add(attrData1);
      attrDatas.add(attrData2);

      List<ArtifactData> artData = new LinkedList<ArtifactData>();
      ArtifactData artData1 = createArtifactData(artA, artGamma, ModificationType.NEW);
      artData.add(artData1);

      List<RelationData> relDatas = new LinkedList<RelationData>();
      RelationData relData1 = createRelationData(11, artA, artB + 1, 88, ModificationType.NEW);
      relDatas.add(relData1);

      List<ArtifactData> destArtData = new LinkedList<ArtifactData>();
      ArtifactData destArt = createArtifactData(artB + 1, 99, ModificationType.NEW);
      destArtData.add(destArt);

      List<ChangeItem> expected = new LinkedList<ChangeItem>();
      expected.add(createExpected(attrData1));
      expected.add(createExpected(attrData2));
      expected.add(createExpected(artData1));
      expected.add(createExpected(relData1));

      return new Object[] {changes, expected, attrDatas, artData, relDatas, destArtData};
   }

   /**
    * create a deleted attribute who's artifact needs to be introduced
    */
   private static Object[] testCase_deletedAttribute() {
      List<ChangeItem> changes = new LinkedList<ChangeItem>();
      final int artId = 3;

      ChangeItem ci1 = new AttributeChangeItem(22, 2, artId, 4L, ModificationType.DELETED, Strings.EMPTY_STRING);
      changes.add(ci1);

      List<AttributeData> attrDatas = new LinkedList<AttributeData>();
      AttributeData attrData1 = createAttributeData(artId, 33, 1L, ModificationType.MODIFIED);
      AttributeData attrData2 = createAttributeData(artId, 44 + 1, 67L, ModificationType.NEW);
      attrDatas.add(attrData1);
      attrDatas.add(attrData2);

      List<ArtifactData> artData = new LinkedList<ArtifactData>();
      ArtifactData artData1 = createArtifactData(artId, 89L, ModificationType.NEW);
      artData.add(artData1);

      List<ChangeItem> expected = new LinkedList<ChangeItem>();
      expected.add(createExpected(attrData1));
      expected.add(createExpected(attrData2));
      expected.add(createExpected(artData1));

      return new Object[] {changes, expected, attrDatas, artData, null, null};
   }

   /**
    * create an artifact_deleted attribute who's artifact needs to be introduced deleted
    */
   private static Object[] testCase_artifactDeletedAttribute() {
      List<ChangeItem> changes = new LinkedList<ChangeItem>();
      final int artId = 3;

      ChangeItem ci1 =
         new AttributeChangeItem(22, 2, artId, 4L, ModificationType.ARTIFACT_DELETED, Strings.EMPTY_STRING);
      changes.add(ci1);

      List<AttributeData> attrDatas = new LinkedList<AttributeData>();
      AttributeData attrData1 = createAttributeData(artId, 33, 1L, ModificationType.ARTIFACT_DELETED);
      AttributeData attrData2 = createAttributeData(artId, 44 + 1, 67L, ModificationType.ARTIFACT_DELETED);
      attrDatas.add(attrData1);
      attrDatas.add(attrData2);

      List<ArtifactData> artData = new LinkedList<ArtifactData>();
      ArtifactData artData1 = createArtifactData(artId, 89L, ModificationType.DELETED);
      artData.add(artData1);

      List<ChangeItem> expected = new LinkedList<ChangeItem>();
      expected.add(createExpected(attrData1));
      expected.add(createExpected(attrData2));
      expected.add(createExpected(artData1));

      return new Object[] {changes, expected, attrDatas, artData, null, null};
   }

   private static ModificationType determineModType(OrcsData data) {
      if (data.getModType().matches(ModificationType.DELETED, ModificationType.ARTIFACT_DELETED)) {
         return data.getModType();
      } else {
         return ModificationType.INTRODUCED;
      }
   }

   private static RelationChangeItem createExpected(RelationData data) {
      return new RelationChangeItem(data.getLocalId(), 0, data.getVersion().getGammaId(), determineModType(data),
         data.getArtIdA(), data.getArtIdB(), "");
   }

   private static AttributeChangeItem createExpected(AttributeData data) {
      return new AttributeChangeItem(data.getLocalId(), 0, data.getArtifactId(), data.getVersion().getGammaId(),
         determineModType(data), "");
   }

   private static ArtifactChangeItem createExpected(ArtifactData data) {
      return new ArtifactChangeItem(data.getLocalId(), 0, data.getVersion().getGammaId(), determineModType(data));
   }

   private ChangeItem getMatchingChangeItem(ChangeItem item) {
      for (ChangeItem change : expectedMissingChanges) {
         if (change.getItemId() == item.getItemId() && //
         change.getArtId() == item.getArtId() && //
         change.getClass().getSimpleName().equals(item.getClass().getSimpleName())) {
            return change;
         }
      }
      return null;
   }

   private static AttributeData createAttributeData(int artId, int attrId, long gamma, ModificationType modType) {
      AttributeData data = mock(AttributeData.class);
      VersionData version = mock(VersionData.class);
      DataProxy proxy = mock(DataProxy.class);
      when(data.getModType()).thenReturn(modType);
      when(data.getVersion()).thenReturn(version);
      when(data.getArtifactId()).thenReturn(artId);
      when(data.getLocalId()).thenReturn(attrId);
      when(data.getDataProxy()).thenReturn(proxy);
      when(data.getVersion().getGammaId()).thenReturn(gamma);
      return data;
   }

   private static ArtifactData createArtifactData(int artId, long gamma, ModificationType modType) {
      ArtifactData data = mock(ArtifactData.class);
      VersionData version = mock(VersionData.class);
      when(data.getModType()).thenReturn(modType);
      when(data.getLocalId()).thenReturn(artId);
      when(version.getGammaId()).thenReturn(gamma);
      when(data.getVersion()).thenReturn(version);
      return data;
   }

   private static RelationData createRelationData(int relId, int artA, int artB, long gamma, ModificationType modType) {
      RelationData data = mock(RelationData.class);
      VersionData version = mock(VersionData.class);
      when(data.getLocalId()).thenReturn(relId);
      when(data.getArtIdA()).thenReturn(artA);
      when(data.getArtIdB()).thenReturn(artB);
      when(version.getGammaId()).thenReturn(gamma);
      when(data.getVersion()).thenReturn(version);
      when(data.getModType()).thenReturn(modType);
      return data;
   }
}
