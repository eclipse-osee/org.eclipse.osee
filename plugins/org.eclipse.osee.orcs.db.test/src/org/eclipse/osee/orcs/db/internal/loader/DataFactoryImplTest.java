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
package org.eclipse.osee.orcs.db.internal.loader;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.OrcsObjectFactoryImpl;
import org.eclipse.osee.orcs.db.internal.loader.data.RelationDataImpl;
import org.eclipse.osee.orcs.db.internal.proxy.AttributeDataProxyFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link DataFactoryImplTest} and {@link OrcsObjectFactoryImpl}
 *
 * @author Roberto E. Escobar
 */
public class DataFactoryImplTest {
   private static final BranchId BRANCH = CoreBranches.SYSTEM_ROOT;
   private static final TransactionId tx333 = TransactionId.valueOf(333);
   private static final TransactionId tx444 = TransactionId.valueOf(444);
   private static final GammaId gamma222 = GammaId.valueOf(222);

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock private IdentityManager idFactory;
   @Mock private AttributeDataProxyFactory proxyFactory;
   @Mock private IdentityLocator identityService;
   @Mock private ArtifactTypes artifactCache;
   @Mock private RelationTypes relationTypes;

   @Mock private ArtifactData artData;
   @Mock private AttributeData attrData;
   @Mock private VersionData verData;
   @Mock private DataProxy<Integer> dataProxy;
   @Mock private DataProxy<Integer> otherDataProxy;

   //@formatter:on
   private final IArtifactType artifactType = CoreArtifactTypes.SoftwareRequirement;

   private final ArtifactId art88 = ArtifactId.valueOf(88);
   private final ArtifactId art99 = ArtifactId.valueOf(99);

   private DataFactory dataFactory;
   private final Integer expectedProxyValue = 45;
   private final String expectedProxyUri = "hello";
   private String guid;
   private RelationData relData;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      guid = GUID.create();

      OrcsObjectFactory objectFactory = new OrcsObjectFactoryImpl(proxyFactory, relationTypes);
      dataFactory = new DataFactoryImpl(idFactory, objectFactory, artifactCache);

      // VERSION
      when(verData.getBranch()).thenReturn(BRANCH);
      when(verData.getGammaId()).thenReturn(gamma222);
      when(verData.getTransactionId()).thenReturn(tx333);
      when(verData.getStripeId()).thenReturn(tx444);
      when(verData.isHistorical()).thenReturn(true);

      // ARTIFACT
      when(artData.getVersion()).thenReturn(verData);
      when(artData.getLocalId()).thenReturn(555);
      when(artData.getModType()).thenReturn(ModificationType.MODIFIED);
      when(artData.getTypeUuid()).thenReturn(666L);
      when(artData.getBaseModType()).thenReturn(ModificationType.NEW);
      when(artData.getBaseTypeUuid()).thenReturn(777L);
      when(artData.getGuid()).thenReturn("abcdefg");

      // ATTRIBUTE
      when(attrData.getVersion()).thenReturn(verData);
      when(attrData.getLocalId()).thenReturn(555);
      when(attrData.getModType()).thenReturn(ModificationType.MODIFIED);
      when(attrData.getTypeUuid()).thenReturn(666L);
      when(attrData.getBaseModType()).thenReturn(ModificationType.NEW);
      when(attrData.getBaseTypeUuid()).thenReturn(777L);
      when(attrData.getArtifactId()).thenReturn(art88.getId().intValue());
      when(attrData.getDataProxy()).thenReturn(dataProxy);

      when(dataProxy.getRawValue()).thenReturn(expectedProxyValue);
      when(dataProxy.getUri()).thenReturn(expectedProxyUri);
      when(proxyFactory.createProxy(666L, expectedProxyValue, expectedProxyUri)).thenReturn(otherDataProxy);
      when(otherDataProxy.getRawValue()).thenReturn(expectedProxyValue);
      when(otherDataProxy.getUri()).thenReturn(expectedProxyUri);

      // RELATION
      relData = new RelationDataImpl(verData);
      relData.setLocalId(555);
      relData.setModType(ModificationType.MODIFIED);
      relData.setTypeUuid(666);
      relData.setBaseModType(ModificationType.NEW);
      relData.setBaseTypeUuid(777);
      relData.setArtIdA(art88);
      relData.setArtIdB(art99);
      relData.setRationale("this is the rationale");
   }

   @Test
   public void testCreateArtifactDataUsingAbstratArtifactType() {
      when(artifactCache.get(artifactType)).thenReturn(artifactType);
      when(artifactCache.isAbstract(artifactType)).thenReturn(true);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(String.format("Cannot create an instance of abstract type [%s]", artifactType));
      dataFactory.create(COMMON, artifactType, guid);
   }

   @Test
   public void testCreateArtifactDataInvalidGuid() {
      when(artifactCache.get(artifactType)).thenReturn(artifactType);
      when(artifactCache.isAbstract(artifactType)).thenReturn(false);
      when(idFactory.getUniqueGuid(guid)).thenReturn("123");

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(String.format("Invalid guid [123] during artifact creation [type: %s]", artifactType));

      dataFactory.create(COMMON, artifactType, guid);
   }

   @Test
   public void testCreateArtifactData() {
      when(artifactCache.isAbstract(artifactType)).thenReturn(false);
      when(idFactory.getUniqueGuid(guid)).thenReturn(guid);
      when(idFactory.getNextArtifactId()).thenReturn(987);

      ArtifactData actual = dataFactory.create(COMMON, artifactType, guid);
      verify(idFactory).getUniqueGuid(guid);
      verify(idFactory).getNextArtifactId();

      VersionData actualVer = actual.getVersion();

      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      assertEquals(987, actual.getLocalId().intValue());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getModType());
      assertEquals(artifactType, actual.getTypeUuid());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getBaseModType());
      assertEquals(artifactType, actual.getBaseTypeUuid());
      assertEquals(guid, actual.getGuid());
   }

   @Test
   public void testCreateArtifactDataGenerateGuid() {
      when(artifactCache.get(artifactType)).thenReturn(artifactType);
      when(artifactCache.isAbstract(artifactType)).thenReturn(false);
      when(idFactory.getUniqueGuid(guid)).thenReturn(guid);
      when(idFactory.getNextArtifactId()).thenReturn(987);

      ArtifactData actual = dataFactory.create(COMMON, artifactType, guid);
      verify(idFactory).getUniqueGuid(guid);
      verify(idFactory).getNextArtifactId();

      VersionData actualVer = actual.getVersion();
      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      assertEquals(987, actual.getLocalId().intValue());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getModType());
      assertEquals(artifactType, actual.getTypeUuid());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getBaseModType());
      assertEquals(artifactType, actual.getBaseTypeUuid());
      assertEquals(guid, actual.getGuid());
   }

   @Test
   public void testCreateAttributeData() {
      AttributeTypeId attributeType = mock(AttributeTypeId.class);

      when(attributeType.getId()).thenReturn(2389L);
      when(proxyFactory.createProxy(2389L, 2389, "")).thenReturn(otherDataProxy);
      when(otherDataProxy.getRawValue()).thenReturn(2389);
      when(otherDataProxy.getUri()).thenReturn("");

      when(idFactory.getNextAttributeId()).thenReturn(1);

      AttributeData actual = dataFactory.create(artData, attributeType);

      VersionData actualVer = actual.getVersion();
      assertEquals(BRANCH, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      Assert.assertTrue("local id must be valid", actual.getLocalId() > 0);
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getModType());
      assertEquals(2389L, actual.getTypeUuid());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getBaseModType());
      assertEquals(2389L, actual.getBaseTypeUuid());

      assertEquals(555, actual.getArtifactId());
      assertNotSame(dataProxy, actual.getDataProxy());
   }

   @Test
   public void testCreateRelationData() {
      RelationTypeToken relationType = CoreRelationTypes.Default_Hierarchical__Child;

      ArtifactId aArt = ArtifactId.valueOf(4562);
      ArtifactId bArt = ArtifactId.valueOf(9513);
      when(idFactory.getNextRelationId()).thenReturn(1);

      RelationData actual = dataFactory.createRelationData(relationType, COMMON, aArt, bArt, "My rationale");

      VersionData actualVer = actual.getVersion();
      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      Assert.assertTrue("local id must be valid", actual.getLocalId() > 0);
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getModType());
      assertEquals(relationType.getId().longValue(), actual.getTypeUuid());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getBaseModType());
      assertEquals(relationType.getId().longValue(), actual.getBaseTypeUuid());

      assertEquals(aArt, actual.getArtifactIdA());
      assertEquals(bArt, actual.getArtifactIdB());
      assertEquals("My rationale", actual.getRationale());
   }

   @Test
   public void testIntroduceArtifactData() {
      ArtifactData actual = dataFactory.introduce(COMMON, artData);

      VersionData actualVer = actual.getVersion();
      assertNotSame(verData, actualVer);
      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(gamma222, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(artData.getVersion().getStripeId(), actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      assertEquals(555, actual.getLocalId().intValue());
      assertEquals(artData.getModType(), actual.getModType());
      assertEquals(666L, actual.getTypeUuid());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(777L, actual.getBaseTypeUuid());
      assertEquals("abcdefg", actual.getGuid());
   }

   @Test
   public void testIntroduceAttributeData() {
      AttributeData actual = dataFactory.introduce(COMMON, attrData);

      VersionData actualVer = actual.getVersion();
      assertNotSame(verData, actualVer);
      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(gamma222, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(attrData.getVersion().getStripeId(), actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      assertEquals(555, actual.getLocalId().intValue());
      assertEquals(attrData.getModType(), actual.getModType());
      assertEquals(666L, actual.getTypeUuid());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(777L, actual.getBaseTypeUuid());

      assertEquals(art88, Long.valueOf(actual.getArtifactId()));
      assertNotSame(dataProxy, actual.getDataProxy());

      assertEquals(expectedProxyValue, actual.getDataProxy().getRawValue());
      assertEquals(expectedProxyUri, actual.getDataProxy().getUri());
   }

   @Test
   public void testCopyArtifactData() {
      String newGuid = GUID.create();
      when(idFactory.getNextArtifactId()).thenReturn(987);
      when(idFactory.getUniqueGuid(null)).thenReturn(newGuid);

      ArtifactData actual = dataFactory.copy(COMMON, artData);
      verify(idFactory).getUniqueGuid(null);

      VersionData actualVer = actual.getVersion();
      assertNotSame(verData, actualVer);
      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      assertEquals(987, actual.getLocalId().intValue());
      assertEquals(ModificationType.NEW, actual.getModType());
      assertEquals(666L, actual.getTypeUuid());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(777L, actual.getBaseTypeUuid());
      assertEquals(newGuid, actual.getGuid());
   }

   @Test
   public void testCopyAttributeData() {
      AttributeData actual = dataFactory.copy(COMMON, attrData);

      VersionData actualVer = actual.getVersion();
      assertNotSame(verData, actualVer);
      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      assertEquals(RelationalConstants.DEFAULT_ITEM_ID, actual.getLocalId());
      assertEquals(ModificationType.NEW, actual.getModType());
      assertEquals(666L, actual.getTypeUuid());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(777L, actual.getBaseTypeUuid());

      assertEquals(art88, Long.valueOf(actual.getArtifactId()));
      assertNotSame(dataProxy, actual.getDataProxy());

      assertEquals(expectedProxyValue, actual.getDataProxy().getRawValue());
      assertEquals(expectedProxyUri, actual.getDataProxy().getUri());
   }

   @Test
   public void testCloneArtifactData() {
      ArtifactData actual = dataFactory.clone(artData);
      VersionData actualVer = actual.getVersion();

      assertNotSame(artData, actual);
      assertNotSame(verData, actualVer);

      assertEquals(BRANCH, actualVer.getBranch());
      assertEquals(gamma222, actualVer.getGammaId());
      assertEquals(tx333, actualVer.getTransactionId());
      assertEquals(tx444, actualVer.getStripeId());
      assertEquals(true, actualVer.isHistorical());
      assertEquals(true, actualVer.isInStorage());

      assertEquals(555, actual.getLocalId().intValue());
      assertEquals(ModificationType.MODIFIED, actual.getModType());
      assertEquals(666L, actual.getTypeUuid());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(777L, actual.getBaseTypeUuid());
      assertEquals("abcdefg", actual.getGuid());
   }

   @Test
   public void testCloneAttributeData() {
      AttributeData actual = dataFactory.clone(attrData);
      verify(proxyFactory).createProxy(666L, expectedProxyValue, expectedProxyUri);

      VersionData actualVer = actual.getVersion();

      assertNotSame(attrData, actual);
      assertNotSame(verData, actualVer);

      assertEquals(BRANCH, actualVer.getBranch());
      assertEquals(gamma222, actualVer.getGammaId());
      assertEquals(tx333, actualVer.getTransactionId());
      assertEquals(tx444, actualVer.getStripeId());
      assertEquals(true, actualVer.isHistorical());
      assertEquals(true, actualVer.isInStorage());

      assertEquals(555, actual.getLocalId().intValue());
      assertEquals(ModificationType.MODIFIED, actual.getModType());
      assertEquals(666L, actual.getTypeUuid());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(777L, actual.getBaseTypeUuid());

      assertEquals(art88, Long.valueOf(actual.getArtifactId()));
      assertNotSame(dataProxy, actual.getDataProxy());

      assertEquals(expectedProxyValue, actual.getDataProxy().getRawValue());
      assertEquals(expectedProxyUri, actual.getDataProxy().getUri());
   }

   @Test
   public void testCloneRelationData() {
      RelationData actual = dataFactory.clone(relData);
      VersionData actualVer = actual.getVersion();

      assertNotSame(relData, actual);
      assertNotSame(verData, actualVer);

      assertEquals(BRANCH, actualVer.getBranch());
      assertEquals(gamma222, actualVer.getGammaId());
      assertEquals(tx333, actualVer.getTransactionId());
      assertEquals(tx444, actualVer.getStripeId());
      assertEquals(true, actualVer.isHistorical());
      assertEquals(true, actualVer.isInStorage());

      assertEquals(555, actual.getLocalId().intValue());
      assertEquals(ModificationType.MODIFIED, actual.getModType());
      assertEquals(666L, actual.getTypeUuid());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(777L, actual.getBaseTypeUuid());

      assertEquals(art88, actual.getArtifactIdA());
      assertEquals(art99, actual.getArtifactIdB());
      assertEquals("this is the rationale", actual.getRationale());
   }

}