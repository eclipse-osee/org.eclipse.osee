/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.proxy.impl;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Active;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Annotation;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitCommitAuthorDate;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ReviewId;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Allocation__Requirement;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DEFAULT_HIERARCHY;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Child;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Default_Hierarchical__Parent;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.IS_CHILD;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.IS_PARENT;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for @{link ArtifactReadOnlyImpl}
 *
 * @author Roberto E. Escobar
 */
public class ArtifactReadOnlyImplTest {

   //@formatter:off
   @Mock private ExternalArtifactManager proxyManager;
   @Mock private RelationManager relationManager;
   @Mock private Artifact proxiedObject;
   @Mock private Artifact artifact1;
   @Mock private Attribute<String> attribute1;
   @Mock private AttributeReadable<String> attributeReadable1;
   //@formatter:on

   private final OrcsSession session = null;
   private final AttributeId attributeId = AttributeId.valueOf(12345);
   private ArtifactReadable readOnly;
   private final ArtifactReadable readable1 = ArtifactReadable.SENTINEL;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      readOnly = new ArtifactReadOnlyImpl(proxyManager, relationManager, session, proxiedObject, Artifact);
   }

   @Test
   public void testGetBranchUuid() {
      when(proxiedObject.getBranch()).thenReturn(COMMON);

      BranchId actual = readOnly.getBranch();

      assertEquals(COMMON, actual);
      verify(proxiedObject).getBranch();
   }

   @Test
   public void testGetGuid() {
      String guid = GUID.create();
      when(proxiedObject.getGuid()).thenReturn(guid);

      String actual = readOnly.getGuid();

      assertEquals(guid, actual);
      verify(proxiedObject).getGuid();
   }

   @Test
   public void testGetName() {
      String name = "This is my name";
      when(proxiedObject.getName()).thenReturn(name);

      String actual = readOnly.getName();

      assertEquals(name, actual);
      verify(proxiedObject).getName();
   }

   @Test
   public void testGetTransaction() {
      TransactionId transaction = TransactionId.valueOf(411232);
      when(proxiedObject.getTransaction()).thenReturn(transaction);
      assertEquals(transaction, readOnly.getTransaction());
      verify(proxiedObject).getTransaction();
   }

   @Test
   public void testIsDeleted() {
      when(proxiedObject.getModificationType()).thenReturn(ModificationType.DELETED);
      when(proxiedObject.isDeleted()).thenReturn(true);

      boolean actual = readOnly.isDeleted();

      assertEquals(true, actual);

      verify(proxiedObject).isDeleted();
   }

   @Test
   public void testIsOfType() {
      ArtifactTypeToken type1 = mock(ArtifactTypeToken.class);
      ArtifactTypeToken type2 = mock(ArtifactTypeToken.class);
      when(proxiedObject.isOfType(type1, type2)).thenReturn(true);

      boolean actual = readOnly.isOfType(type1, type2);

      assertEquals(true, actual);
      verify(proxiedObject).isOfType(type1, type2);
   }

   @Test
   public void testGetExistingAttributeTypes() {
      List<? extends AttributeTypeId> types = Arrays.asList(Active, Name);
      when(proxiedObject.getExistingAttributeTypes()).thenAnswer(answer(types));

      Collection<AttributeTypeToken> actual = readOnly.getExistingAttributeTypes();

      assertEquals(types, actual);
      verify(proxiedObject).getExistingAttributeTypes();
   }

   @Test
   public void testGetValidAttributeTypes() {
      List<? extends AttributeTypeId> types = Arrays.asList(Active, Name, Annotation);
      when(proxiedObject.getValidAttributeTypes()).thenAnswer(answer(types));

      Collection<AttributeTypeToken> actual = readOnly.getValidAttributeTypes();

      assertEquals(types, actual);
      verify(proxiedObject).getValidAttributeTypes();
   }

   @Test
   public void testGetAttributeCount1() {
      when(proxiedObject.getAttributeCount(Name)).thenReturn(45);

      int actual = readOnly.getAttributeCount(Name);

      assertEquals(45, actual);
      verify(proxiedObject).getAttributeCount(Name);
   }

   @Test
   public void testGetAttributeCount2() {
      when(proxiedObject.getAttributeCount(Name, EXCLUDE_DELETED)).thenReturn(47);

      int actual = readOnly.getAttributeCount(Name, EXCLUDE_DELETED);

      assertEquals(47, actual);
      verify(proxiedObject).getAttributeCount(Name, EXCLUDE_DELETED);
   }

   @Test
   public void testIsAttributeTypeValid() {
      when(proxiedObject.isAttributeTypeValid(Name)).thenReturn(true);

      boolean actual = readOnly.isAttributeTypeValid(Name);

      assertEquals(true, actual);
      verify(proxiedObject).isAttributeTypeValid(Name);
   }

   @Test
   public void testGetAttributeValues() {
      List<Integer> values = Arrays.asList(1, 2, 3);
      when(proxiedObject.getAttributeValues(ReviewId)).thenAnswer(answer(values));

      List<Integer> actual = readOnly.getAttributeValues(ReviewId);

      assertEquals(values, actual);
      verify(proxiedObject).getAttributeValues(ReviewId);
   }

   @Test
   public void testGetSoleAttributeValue() {
      Date date = new Date();
      when(proxiedObject.getSoleAttributeValue(GitCommitAuthorDate)).thenReturn(date);

      Date actual = readOnly.getSoleAttributeValue(GitCommitAuthorDate);

      assertEquals(date, actual);
      verify(proxiedObject).getSoleAttributeValue(GitCommitAuthorDate);
   }

   @Test
   public void testGetSoleAttributeAsString1() {
      String expected = "Hello";
      when(proxiedObject.getSoleAttributeAsString(Name)).thenReturn(expected);

      String actual = readOnly.getSoleAttributeAsString(Name);

      assertEquals(expected, actual);
      verify(proxiedObject).getSoleAttributeAsString(Name);
   }

   @Test
   public void testGetSoleAttributeAsString2() {
      String expected = "AnotherValue";
      when(proxiedObject.getSoleAttributeAsString(Name, "Hello")).thenReturn(expected);

      String actual = readOnly.getSoleAttributeAsString(Name, "Hello");

      assertEquals(expected, actual);
      verify(proxiedObject).getSoleAttributeAsString(Name, "Hello");
   }

   @Test
   public void testGetValidRelationTypes() {
      List<? extends IRelationType> types = Arrays.asList(CoreRelationTypes.Default_Hierarchical__Child);

      when(relationManager.getValidRelationTypes(proxiedObject)).thenAnswer(answer(types));

      assertEquals(types, readOnly.getValidRelationTypes());
      verify(relationManager).getValidRelationTypes(proxiedObject);
   }

   @Test
   public void testAreRelated() {
      when(proxyManager.asInternalArtifact(readable1)).thenReturn(artifact1);
      when(relationManager.areRelated(proxiedObject, DEFAULT_HIERARCHY, artifact1)).thenReturn(true);

      boolean actual = readOnly.areRelated(Default_Hierarchical__Child, readable1);

      assertEquals(true, actual);
      verify(proxyManager).asInternalArtifact(readable1);
      verify(relationManager).areRelated(proxiedObject, DEFAULT_HIERARCHY, artifact1);
   }

   @Test
   public void testGetParent() {
      when(relationManager.getParent(session, proxiedObject)).thenReturn(artifact1);
      when(proxyManager.asExternalArtifact(session, artifact1)).thenReturn(readable1);

      ArtifactReadable actual = readOnly.getParent();

      assertEquals(readable1, actual);
      verify(relationManager).getParent(session, proxiedObject);
      verify(proxyManager).asExternalArtifact(session, artifact1);
   }

   @Test
   public void testGetExistingRelationTypes() {
      List<? extends IRelationType> types = Arrays.asList(Allocation__Requirement);
      when(relationManager.getExistingRelationTypes(proxiedObject)).thenAnswer(answer(types));

      Collection<RelationTypeId> actual = readOnly.getExistingRelationTypes();

      assertEquals(types, actual);
      verify(relationManager).getExistingRelationTypes(proxiedObject);
   }

   @Test
   public void testGetMaximumRelationAllowed() {
      RelationTypeSide typeAndSide = Default_Hierarchical__Child;
      when(relationManager.getMaximumRelationAllowed(DEFAULT_HIERARCHY, proxiedObject, IS_PARENT)).thenReturn(6);

      int actual = readOnly.getMaximumRelationAllowed(typeAndSide);

      assertEquals(6, actual);
      verify(relationManager).getMaximumRelationAllowed(DEFAULT_HIERARCHY, proxiedObject, IS_PARENT);
   }

   @Test
   public void testGetRationale() {
      String expected = "This is my rationale";
      when(proxyManager.asInternalArtifact(readable1)).thenReturn(artifact1);
      when(relationManager.getRationale(artifact1, DEFAULT_HIERARCHY, proxiedObject)).thenReturn(expected);

      String actual = readOnly.getRationale(Default_Hierarchical__Parent, readable1);

      assertEquals(expected, actual);

      verify(relationManager).getRationale(artifact1, DEFAULT_HIERARCHY, proxiedObject);
   }

   @Test
   public void testGetRelatedCount() {
      int expected = 35;
      when(relationManager.getRelatedCount(DEFAULT_HIERARCHY, proxiedObject, IS_CHILD)).thenReturn(expected);

      int actual = readOnly.getRelatedCount(Default_Hierarchical__Parent);

      assertEquals(expected, actual);
      verify(relationManager).getRelatedCount(DEFAULT_HIERARCHY, proxiedObject, IS_CHILD);
   }

   @Test
   public void testGetRelated() {
      ResultSet<ArtifactReadable> expected = ResultSets.singleton(readable1);

      ResultSet<Artifact> resultSet1 = ResultSets.singleton(artifact1);
      when(relationManager.getRelated(session, DEFAULT_HIERARCHY, proxiedObject, IS_CHILD,
         DeletionFlag.EXCLUDE_DELETED)).thenAnswer(answer(resultSet1));
      when(proxyManager.asExternalArtifacts(session, resultSet1)).thenReturn(expected);

      ResultSet<ArtifactReadable> actual = readOnly.getRelated(Default_Hierarchical__Parent);

      assertEquals(expected, actual);
      verify(relationManager).getRelated(session, DEFAULT_HIERARCHY, proxiedObject, IS_CHILD,
         DeletionFlag.EXCLUDE_DELETED);
      verify(proxyManager).asExternalArtifacts(session, resultSet1);
   }

   @Test
   public void testGetAttributeById() {
      when(proxiedObject.getAttributeById(attributeId)).thenAnswer(answer(attribute1));
      when(proxyManager.asExternalAttribute(session, attribute1)).thenReturn(attributeReadable1);

      AttributeReadable<Object> actual = readOnly.getAttributeById(attributeId);

      assertEquals(attributeReadable1, actual);
      verify(proxiedObject).getAttributeById(attributeId);
      verify(proxyManager).asExternalAttribute(session, attribute1);
   }

   @Test
   public void testGetAttributes1() {
      List<Attribute<String>> attributes1 = Collections.singletonList(attribute1);
      ResultSet<? extends AttributeReadable<String>> expected = ResultSets.singleton(attributeReadable1);
      when(proxiedObject.getAttributes()).thenAnswer(answer(attributes1));
      when(proxyManager.asExternalAttributes(session, attributes1)).thenAnswer(answer(expected));

      ResultSet<? extends AttributeReadable<Object>> actual = readOnly.getAttributes();

      assertEquals(expected, actual);
      verify(proxiedObject).getAttributes();
      verify(proxyManager).asExternalAttributes(session, attributes1);
   }

   @Test
   public void testGetAttributes2() {
      List<Attribute<String>> attributes1 = Collections.singletonList(attribute1);
      ResultSet<? extends AttributeReadable<String>> expected = ResultSets.singleton(attributeReadable1);
      when(proxiedObject.getAttributes(EXCLUDE_DELETED)).thenAnswer(answer(attributes1));
      when(proxyManager.asExternalAttributes(session, attributes1)).thenAnswer(answer(expected));

      ResultSet<? extends AttributeReadable<Object>> actual = readOnly.getAttributes(EXCLUDE_DELETED);

      assertEquals(expected, actual);
      verify(proxiedObject).getAttributes(EXCLUDE_DELETED);
      verify(proxyManager).asExternalAttributes(session, attributes1);
   }

   @Test
   public void testGetAttributes3() {
      List<Attribute<String>> attributes1 = Collections.singletonList(attribute1);
      ResultSet<? extends AttributeReadable<String>> expected = ResultSets.singleton(attributeReadable1);
      when(proxiedObject.getAttributes(Name)).thenAnswer(answer(attributes1));
      when(proxyManager.asExternalAttributes(session, attributes1)).thenAnswer(answer(expected));

      ResultSet<? extends AttributeReadable<Object>> actual = readOnly.getAttributes(Name);

      assertEquals(expected, actual);
      verify(proxiedObject).getAttributes(Name);
      verify(proxyManager).asExternalAttributes(session, attributes1);
   }

   @Test
   public void testGetAttributes4() {
      List<Attribute<String>> attributes1 = Collections.singletonList(attribute1);
      ResultSet<? extends AttributeReadable<String>> expected = ResultSets.singleton(attributeReadable1);
      when(proxiedObject.getAttributes(Name, EXCLUDE_DELETED)).thenAnswer(answer(attributes1));
      when(proxyManager.asExternalAttributes(session, attributes1)).thenAnswer(answer(expected));

      ResultSet<? extends AttributeReadable<Object>> actual = readOnly.getAttributes(Name, EXCLUDE_DELETED);

      assertEquals(expected, actual);
      verify(proxiedObject).getAttributes(Name, EXCLUDE_DELETED);
      verify(proxyManager).asExternalAttributes(session, attributes1);
   }

   @Test
   public void testMatches() {
      ArtifactId artifact = mock(ArtifactId.class);
      when(proxiedObject.matches(artifact)).thenReturn(true);

      boolean actual = readOnly.matches(artifact);

      assertEquals(true, actual);
      verify(proxiedObject).matches(artifact);
   }

   private static <T> Answer<T> answer(final T value) {
      return new Answer<T>() {

         @Override
         public T answer(InvocationOnMock invocation) throws Throwable {
            return value;
         }
      };
   }
}
