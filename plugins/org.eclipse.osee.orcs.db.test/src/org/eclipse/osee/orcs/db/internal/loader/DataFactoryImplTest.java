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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.DesignMsWord;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Active;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DEFAULT_HIERARCHY;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Design_Design;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataImpl;
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
   private static final Long ART_ID = 987L;
   private static final Long SHARED_ID = 555L;
   private static final ArtifactId artifactId555 = ArtifactId.valueOf(SHARED_ID);
   private static final ArtifactTypeToken artifactType = CoreArtifactTypes.SoftwareRequirementMsWord;

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   //@formatter:off
   @Mock private IdentityManager idFactory;
   @Mock private AttributeDataProxyFactory proxyFactory;
   @Mock private IdentityLocator identityService;
   @Mock private ArtifactTypes artifactCache;
   @Mock private RelationTypes relationTypes;
   @Mock private AttributeData attrData;
   @Mock private VersionData verData;
   @Mock private DataProxy<Integer> dataProxy;
   @Mock private DataProxy<Integer> otherDataProxy;
   //@formatter:on

   private ArtifactData artData;
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

      OrcsObjectFactory objectFactory = new OrcsObjectFactoryImpl(proxyFactory, relationTypes, artifactCache);
      dataFactory = new DataFactoryImpl(idFactory, objectFactory, artifactCache);
      when(idFactory.getNextArtifactId()).thenReturn(ART_ID.intValue());

      // VERSION
      when(verData.getBranch()).thenReturn(BRANCH);
      when(verData.getGammaId()).thenReturn(gamma222);
      when(verData.getTransactionId()).thenReturn(tx333);
      when(verData.getStripeId()).thenReturn(tx444);
      when(verData.isHistorical()).thenReturn(true);

      // ARTIFACT
      artData = new ArtifactDataImpl(verData);
      artData.setLocalId(SHARED_ID.intValue());
      artData.setModType(ModificationType.MODIFIED);
      artData.setType(Artifact);
      artData.setBaseModType(ModificationType.NEW);
      artData.setBaseType(Folder);

      // ATTRIBUTE
      when(attrData.getVersion()).thenReturn(verData);
      when(attrData.getLocalId()).thenReturn(SHARED_ID.intValue());
      when(attrData.getId()).thenReturn(SHARED_ID);
      when(attrData.getModType()).thenReturn(ModificationType.MODIFIED);
      when(attrData.getType()).thenReturn(Name);
      when(attrData.getBaseModType()).thenReturn(ModificationType.NEW);
      when(attrData.getBaseType()).thenReturn(Active);
      when(attrData.getArtifactId()).thenReturn(art88);
      when(attrData.getDataProxy()).thenReturn(dataProxy);

      when(dataProxy.getRawValue()).thenReturn(expectedProxyValue);
      when(dataProxy.getUri()).thenReturn(expectedProxyUri);
      when(proxyFactory.createProxy(Name, expectedProxyValue, expectedProxyUri)).thenReturn(otherDataProxy);
      when(otherDataProxy.getRawValue()).thenReturn(expectedProxyValue);
      when(otherDataProxy.getUri()).thenReturn(expectedProxyUri);

      // RELATION
      relData = new RelationDataImpl(verData);
      relData.setLocalId(SHARED_ID.intValue());
      relData.setModType(ModificationType.MODIFIED);
      relData.setType(Design_Design);
      relData.setBaseModType(ModificationType.NEW);
      relData.setBaseType(DEFAULT_HIERARCHY);
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
      when(idFactory.getUniqueGuid(guid)).thenReturn("123");

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(String.format("Invalid guid [123] during artifact creation [type: %s]", artifactType));

      dataFactory.create(COMMON, artifactType, guid);
   }

   @Test
   public void testCreateArtifactData() {
      when(artifactCache.isAbstract(DesignMsWord)).thenReturn(false);
      when(idFactory.getUniqueGuid(guid)).thenReturn(guid);

      ArtifactData actual = dataFactory.create(COMMON, DesignMsWord, guid);
      verify(idFactory).getUniqueGuid(guid);
      verify(idFactory).getNextArtifactId();

      VersionData actualVer = actual.getVersion();

      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      assertEquals(ART_ID, actual.getId());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getModType());
      assertEquals(DesignMsWord, actual.getType());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getBaseModType());
      assertEquals(DesignMsWord, actual.getBaseType());
      assertEquals(guid, actual.getGuid());
   }

   @Test
   public void testCreateArtifactDataGenerateGuid() {
      when(idFactory.getUniqueGuid(guid)).thenReturn(guid);

      ArtifactData actual = dataFactory.create(COMMON, Artifact, guid);
      verify(idFactory).getUniqueGuid(guid);
      verify(idFactory).getNextArtifactId();

      VersionData actualVer = actual.getVersion();
      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      assertEquals(ART_ID, actual.getId());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getModType());
      assertEquals(Artifact, actual.getType());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getBaseModType());
      assertEquals(Artifact, actual.getBaseType());
      assertEquals(guid, actual.getGuid());
   }

   @Test
   public void testCreateAttributeData() {
      when(proxyFactory.createProxy(Name, "", "")).thenReturn(otherDataProxy);
      when(otherDataProxy.getRawValue()).thenReturn(2389);
      when(otherDataProxy.getUri()).thenReturn("");

      when(idFactory.getNextAttributeId()).thenReturn(1);

      AttributeData actual = dataFactory.create(artData, Name);

      VersionData actualVer = actual.getVersion();
      assertEquals(BRANCH, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      Assert.assertTrue("local id must be valid", actual.isValid());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getModType());
      assertEquals(Name, actual.getType());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getBaseModType());
      assertEquals(Name, actual.getBaseType());

      assertEquals(artifactId555, actual.getArtifactId());
      assertNotSame(dataProxy, actual.getDataProxy());
   }

   @Test
   public void testCreateRelationData() {
      ArtifactId aArt = ArtifactId.valueOf(4562);
      ArtifactId bArt = ArtifactId.valueOf(9513);
      when(idFactory.getNextRelationId()).thenReturn(1);

      RelationData actual = dataFactory.createRelationData(Design_Design, COMMON, aArt, bArt, "My rationale");

      VersionData actualVer = actual.getVersion();
      assertEquals(COMMON, actualVer.getBranch());
      assertEquals(GammaId.SENTINEL, actualVer.getGammaId());
      assertEquals(TransactionId.SENTINEL, actualVer.getTransactionId());
      assertEquals(TransactionId.SENTINEL, actualVer.getStripeId());
      assertEquals(false, actualVer.isHistorical());
      assertEquals(false, actualVer.isInStorage());

      Assert.assertTrue("local id must be valid", actual.getLocalId() > 0);
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getModType());
      assertEquals(Design_Design, actual.getType());
      assertEquals(RelationalConstants.DEFAULT_MODIFICATION_TYPE, actual.getBaseModType());
      assertEquals(Design_Design, actual.getBaseType());

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

      assertEquals(artifactId555, actual);
      assertEquals(artData.getModType(), actual.getModType());
      assertEquals(Artifact, actual.getType());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(Folder, actual.getBaseType());
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

      assertEquals(SHARED_ID, actual.getId());
      assertEquals(attrData.getModType(), actual.getModType());
      assertEquals(Name, actual.getType());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(Active, actual.getBaseType());

      assertEquals(art88, actual.getArtifactId());
      assertNotSame(dataProxy, actual.getDataProxy());

      assertEquals(expectedProxyValue, actual.getDataProxy().getRawValue());
      assertEquals(expectedProxyUri, actual.getDataProxy().getUri());
   }

   @Test
   public void testCopyArtifactData() {
      String newGuid = GUID.create();

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

      assertEquals(ART_ID, actual.getId());
      assertEquals(ModificationType.NEW, actual.getModType());
      assertEquals(actual.getType(), Artifact);
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(Folder, actual.getBaseType());
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
      assertEquals(Name, actual.getType());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(Active, actual.getBaseType());

      assertEquals(art88, actual.getArtifactId());
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

      assertEquals(artifactId555, actual);
      assertEquals(ModificationType.MODIFIED, actual.getModType());
      assertEquals(Artifact, actual.getType());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(Folder, actual.getBaseType());
   }

   @Test
   public void testCloneAttributeData() {
      AttributeData actual = dataFactory.clone(attrData);
      verify(proxyFactory).createProxy(Name, expectedProxyValue, expectedProxyUri);

      VersionData actualVer = actual.getVersion();

      assertNotSame(attrData, actual);
      assertNotSame(verData, actualVer);

      assertEquals(BRANCH, actualVer.getBranch());
      assertEquals(gamma222, actualVer.getGammaId());
      assertEquals(tx333, actualVer.getTransactionId());
      assertEquals(tx444, actualVer.getStripeId());
      assertEquals(true, actualVer.isHistorical());
      assertEquals(true, actualVer.isInStorage());

      assertEquals(SHARED_ID, actual.getId());
      assertEquals(ModificationType.MODIFIED, actual.getModType());
      assertEquals(Name, actual.getType());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(Active, actual.getBaseType());

      assertEquals(art88, actual.getArtifactId());
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

      assertEquals(SHARED_ID, actual.getId());
      assertEquals(ModificationType.MODIFIED, actual.getModType());
      assertEquals(Design_Design, actual.getType());
      assertEquals(ModificationType.NEW, actual.getBaseModType());
      assertEquals(DEFAULT_HIERARCHY, actual.getBaseType());

      assertEquals(art88, actual.getArtifactIdA());
      assertEquals(art99, actual.getArtifactIdB());
      assertEquals("this is the rationale", actual.getRationale());
   }
}