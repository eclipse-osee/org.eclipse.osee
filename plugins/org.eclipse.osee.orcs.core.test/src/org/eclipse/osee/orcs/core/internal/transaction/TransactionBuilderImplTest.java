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
package org.eclipse.osee.orcs.core.internal.transaction;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirement;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Active;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Annotation;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Company;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.FavoriteBranch;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.PlainTextContent;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.QualificationMethod;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DEFAULT_HIERARCHY;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.KeyValueOps;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.search.QueryModule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Test Case for {@link TransactionFactoryImpl}
 *
 * @author Roberto E. Escobar
 */
public class TransactionBuilderImplTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private OrcsSession session;
   @Mock private TxDataManager txDataManager;
   @Mock private TxCallableFactory txCallableFactory;
   @Mock private QueryFactory queryFactory;
   @Mock private QueryBuilder builder;
   @Mock private QueryModule query;
   @Mock private OrcsApi orcsApi;
   @Mock private KeyValueOps keyValueOps;

   @Mock private ArtifactReadable expectedAuthor;
   @Mock private ArtifactReadable expectedDestination;
   @Mock private ArtifactReadable node1;
   @Mock private ArtifactReadable node2;
   @Mock private Artifact artifact;
   @Mock private Artifact artifact2;
   @SuppressWarnings("rawtypes")
   @Mock private AttributeReadable attrId;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute attribute;

   @Mock private TxData txData;
   // @formatter:on

   private TransactionBuilderImpl factory;

   @SuppressWarnings("unchecked")
   @Before
   public void init() {
      initMocks(this);
      when(orcsApi.getQueryFactory()).thenReturn(queryFactory);
      factory = new TransactionBuilderImpl(txCallableFactory, txDataManager, txData, orcsApi, keyValueOps);

      when(attrId.getId()).thenReturn(12345L);
      when(txDataManager.getForWrite(txData, expectedAuthor)).thenReturn(artifact);
      when(artifact.getAttributeById(attrId)).thenReturn(attribute);
      when(expectedAuthor.getBranch()).thenReturn(COMMON);
      when(txData.getBranch()).thenReturn(COMMON);
   }

   @Test
   public void testGetComment() {
      when(factory.getComment()).thenReturn("This is a comment");
      String comment = factory.getComment();
      assertEquals(comment, "This is a comment");
      verify(txData).getComment();
   }

   public void testSetAuthor() {
      factory.setAuthor(SystemUser.OseeSystem);
      verify(txDataManager).setAuthor(txData, SystemUser.OseeSystem);
   }

   @Test
   public void testCreateArtifact() {
      factory.createArtifact(SoftwareRequirement, "Software Requirement");

      verify(txDataManager).createArtifact(txData, SoftwareRequirement, "Software Requirement", (String) null);
   }

   @Test
   public void testCopyArtifact() {
      factory.copyArtifact(expectedAuthor);
      verify(txDataManager).copyArtifact(txData, COMMON, expectedAuthor);
   }

   @Test
   public void testCopyArtifactWithList() {
      Collection<AttributeTypeToken> attributesToDuplicate = Arrays.asList(Name, Annotation);

      factory.copyArtifact(expectedAuthor, attributesToDuplicate);

      verify(txDataManager).copyArtifact(txData, COMMON, expectedAuthor, attributesToDuplicate);
   }

   @Test
   public void testIntroduceArtifactBranchException() {
      when(txData.isOnBranch(COMMON)).thenReturn(true);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Source branch is same branch as transaction branch[" + COMMON + "]");
      factory.introduceArtifact(COMMON, expectedAuthor);
   }

   @Test
   public void testIntroduceArtifact() {
      when(queryFactory.fromBranch(COMMON)).thenReturn(builder);
      when(builder.includeDeletedArtifacts()).thenReturn(builder);
      when(builder.andId(any())).thenReturn(builder);

      ResultSet<ArtifactReadable> source = ResultSets.singleton(expectedAuthor);
      when(builder.getResults()).thenReturn(source);

      factory.introduceArtifact(COMMON, expectedAuthor);

      verify(txDataManager).introduceArtifact(txData, COMMON, expectedAuthor, expectedAuthor);
   }

   @Test
   public void testCreateAttribute() {
      factory.createAttribute(expectedAuthor, QualificationMethod);

      verify(artifact).createAttribute(QualificationMethod);
   }

   @Test
   public void testCreateAttributeWithValue() {
      factory.createAttribute(expectedAuthor, QualificationMethod, "Demonstration");

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).createAttribute(QualificationMethod, "Demonstration");
   }

   @Test
   public void testSetSoleAttributeValue() {
      String value = "check this out";
      factory.setSoleAttributeValue(expectedAuthor, Company, value);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).setSoleAttributeValue(Company, value);
   }

   @Test
   public void testSetSoleAttributeFromStream() {
      InputStream inputStream = Mockito.mock(InputStream.class);

      factory.setSoleAttributeFromStream(expectedAuthor, Company, inputStream);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).setSoleAttributeFromStream(Company, inputStream);
   }

   @Test
   public void testSetSoleAttributeFromString() {
      factory.setSoleAttributeFromString(expectedAuthor, Name, "Name");

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).setSoleAttributeFromString(Name, "Name");
   }

   @Test
   public void testSetAttributesFromValues() {
      List<Boolean> values = Arrays.asList(true, true, false);
      factory.setAttributesFromValues(expectedAuthor, Active, values);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).setAttributesFromValues(Active, values);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSetAttributesFromValuesList() {
      factory.setAttributesFromValues(expectedAuthor, CoreAttributeTypes.StaticId, Collections.EMPTY_LIST);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).setAttributesFromValues(CoreAttributeTypes.StaticId, Collections.EMPTY_LIST);
   }

   @Test
   public void testSetAttributesFromStrings() {
      factory.setAttributesFromStrings(expectedAuthor, PlainTextContent, Arrays.asList("one", "two", "three"));

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).setAttributesFromStrings(PlainTextContent, Arrays.asList("one", "two", "three"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSetAttributesFromStringList() {
      factory.setAttributesFromStrings(expectedAuthor, PlainTextContent, Collections.EMPTY_LIST);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).setAttributesFromStrings(PlainTextContent, Collections.EMPTY_LIST);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSetAttributeByIdFromValue() {
      factory.setAttributeById(expectedAuthor, attrId, false);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(attribute).setValue(false);
   }

   @Test
   public void testSetAttributeByIdFromString() {
      factory.setAttributeById(expectedAuthor, attrId, "value");

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(attribute).setFromString("value");
   }

   @Test
   public void testSetAttributeById() {
      InputStream inputStream = Mockito.mock(InputStream.class);

      factory.setAttributeById(expectedAuthor, attrId, inputStream);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(attribute).setValueFromInputStream(inputStream);
   }

   @Test
   public void testDeleteByAttributeId() {
      factory.deleteByAttributeId(expectedAuthor, attrId);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(attribute).delete();
   }

   @Test
   public void testDeleteSoleAttribute() {
      factory.deleteSoleAttribute(expectedAuthor, Name);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).deleteSoleAttribute(Name);
   }

   @Test
   public void testDeleteAttributes() {
      factory.deleteAttributes(expectedAuthor, FavoriteBranch);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).deleteAttributes(FavoriteBranch);
   }

   @Test
   public void testDeleteAttributesWithValue() {
      factory.deleteAttributesWithValue(expectedAuthor, Active, true);

      verify(txDataManager).getForWrite(txData, expectedAuthor);
      verify(artifact).deleteAttributesWithValue(Active, true);
   }

   @Test
   public void testDeleteArtifact() {
      factory.deleteArtifact(expectedAuthor);

      verify(txDataManager).deleteArtifact(txData, expectedAuthor);
   }

   @Test
   public void testIsCommitInProgress() {
      when(factory.isCommitInProgress()).thenReturn(false);

      boolean condition = factory.isCommitInProgress();

      assertFalse(condition);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCommit() throws Exception {
      CancellableCallable<TransactionToken> callable = mock(CancellableCallable.class);

      when(txCallableFactory.createTx(txData)).thenReturn(callable);
      when(callable.call()).thenReturn(TransactionToken.SENTINEL);

      factory.commit();
      verify(txCallableFactory).createTx(txData);
   }

   @Test
   public void testCommitException() {
      Exception exception = new IllegalStateException("onCommit Exception");

      doThrow(exception).when(txCallableFactory).createTx(txData);

      thrown.expect(Exception.class);
      factory.commit();
   }

   @Test
   public void testSetRationale() {
      factory.setRationale(node1, DEFAULT_HIERARCHY, node2, "This is my rationale");

      verify(txDataManager).setRationale(txData, node1, DEFAULT_HIERARCHY, node2, "This is my rationale");
   }

   @Test
   public void testRelate() {
      factory.relate(node1, DEFAULT_HIERARCHY, node2);
      verify(txDataManager).relate(txData, node1, DEFAULT_HIERARCHY, node2);
   }

   @Test
   public void testRelateWithOrder() {
      factory.relate(node1, DEFAULT_HIERARCHY, node2, LEXICOGRAPHICAL_ASC);
      verify(txDataManager).relate(txData, node1, DEFAULT_HIERARCHY, node2, LEXICOGRAPHICAL_ASC);
   }

   @Test
   public void testSetRelations() {
      Iterable<? extends ArtifactId> artBs = Collections.emptyList();
      factory.setRelations(node1, DEFAULT_HIERARCHY, artBs);

      verify(txDataManager).setRelations(txData, node1, DEFAULT_HIERARCHY, artBs);
   }

   @Test
   public void testUnrelateWithAandB() {
      factory.unrelate(node1, CoreRelationTypes.Allocation_Requirement, node2);
      verify(txDataManager).unrelate(txData, node1, CoreRelationTypes.Allocation_Requirement, node2);
   }

   @Test
   public void testUnrelateFromAllWithSide() {
      RelationTypeSide asTypeSide = RelationTypeSide.create(CoreRelationTypes.Allocation_Requirement, SIDE_B);
      factory.unrelateFromAll(asTypeSide, expectedAuthor);
      verify(txDataManager).unrelateFromAll(txData, CoreRelationTypes.Allocation_Requirement, expectedAuthor, SIDE_B);
   }

   @Test
   public void testUnrelateFromAll() {
      factory.unrelateFromAll(expectedAuthor);
      verify(txDataManager).unrelateFromAll(txData, expectedAuthor);
   }

}
