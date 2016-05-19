/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.db.mocks.AtsClassDatabase;
import org.eclipse.osee.ats.db.mocks.AtsIntegrationByClassRule;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSetTest {

   // @formatter:off
   @Rule public TestRule db = AtsIntegrationByClassRule.integrationRule(this);
   @OsgiService public IAtsServer atsServer;
   // @formatter:on

   @AfterClass
   public static void setup() throws Exception {
      AtsClassDatabase.cleanup();
   }

   @Test
   public void testCreateArtifact() {
      String className = getClass().getSimpleName();
      IAtsChangeSet changes =
         atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      Long uuid = Lib.generateArtifactIdAsInt();
      String guid = GUID.create();
      String name = "Ver 1 " + className;
      changes.createArtifact(AtsArtifactTypes.Version, name, guid, uuid);
      changes.execute();

      ArtifactReadable verArt = atsServer.getOrcsApi().getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andUuid(
         uuid).getResults().getAtMostOneOrNull();
      assertNotNull(verArt);
      assertEquals(uuid.longValue(), verArt.getUuid().longValue());
      assertEquals(guid, verArt.getGuid());
      assertEquals(name, verArt.getName());
   }

   /**
    * Test relate in both directions and unrelateAll in both directions
    */
   @Test
   public void testCreateRelationsAndUnrelateAll() {
      String className = getClass().getSimpleName();
      IAtsChangeSet changes =
         atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      ArtifactId folderArt = changes.createArtifact(CoreArtifactTypes.Folder, "Folder 2 " + className);
      ArtifactId verArt = changes.createArtifact(AtsArtifactTypes.Version, "Ver 2 " + className);
      changes.execute();

      ArtifactReadable folder = atsServer.getArtifact(folderArt.getUuid());
      assertNotNull(folder);
      IAtsVersion version = atsServer.getConfigItemFactory().getVersion(atsServer.getArtifact(verArt.getUuid()));
      assertNotNull(version);

      // add relation from folder to version
      changes = atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      changes.relate(folder, CoreRelationTypes.SupportingInfo_SupportingInfo, version);
      changes.execute();

      // test that folder to version is valid for the correct direction
      ArtifactReadable folderArt2 = atsServer.getArtifact(folder.getUuid());
      ArtifactReadable verArt2 = atsServer.getArtifact(version.getUuid());
      assertTrue(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt2));
      assertFalse(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, verArt2));
      assertTrue(verArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, folderArt2));
      assertFalse(verArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt2));

      // unrelate all on folder
      changes = atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      changes.unrelateAll(folderArt2, CoreRelationTypes.SupportingInfo_SupportingInfo);
      changes.execute();

      // test that folder and version are not related in any direction
      ArtifactReadable folderArt21 = atsServer.getArtifact(folder.getUuid());
      ArtifactReadable verArt21 = atsServer.getArtifact(version.getUuid());
      assertFalse(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt21));
      assertFalse(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, verArt21));
      assertFalse(verArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, folderArt21));
      assertFalse(verArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt21));

      // relate version to folder with same relation, but opposite order
      folder = atsServer.getArtifact(folderArt.getUuid());
      assertNotNull(folder);
      version = atsServer.getConfigItemFactory().getVersion(atsServer.getArtifact(verArt.getUuid()));
      assertNotNull(version);
      changes = atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      changes.relate(version, CoreRelationTypes.SupportingInfo_SupportedBy, folder);
      changes.execute();

      // test that version and folder are related as expected
      ArtifactReadable folderArt22 = atsServer.getArtifact(folder.getUuid());
      ArtifactReadable verArt22 = atsServer.getArtifact(version.getUuid());
      assertTrue(folderArt22.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt22));
      assertFalse(folderArt22.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, verArt22));
      assertTrue(verArt22.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, folderArt22));
      assertFalse(verArt22.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt22));

      // unrelate all on version
      changes = atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      changes.unrelateAll(verArt22, CoreRelationTypes.SupportingInfo_SupportedBy);
      changes.execute();

      // test that all version and folder are not related in any direction
      ArtifactReadable folderArt221 = atsServer.getArtifact(folder.getUuid());
      ArtifactReadable verArt221 = atsServer.getArtifact(version.getUuid());
      assertFalse(folderArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt221));
      assertFalse(folderArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, verArt221));
      assertFalse(verArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, folderArt221));
      assertFalse(verArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt221));
   }

   @Test
   public void testUnrelate() {
      String className = getClass().getSimpleName();
      IAtsChangeSet changes =
         atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      ArtifactId folderArt = changes.createArtifact(CoreArtifactTypes.Folder, "Folder 3 " + className);
      ArtifactId verArt = changes.createArtifact(AtsArtifactTypes.Version, "Ver 3 " + className);
      changes.relate(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo, verArt);
      changes.execute();

      // test that folder to version is valid for the correct direction
      ArtifactReadable folderArt2 = atsServer.getArtifact(folderArt.getUuid());
      ArtifactReadable verArt2 = atsServer.getArtifact(verArt.getUuid());
      assertTrue(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt2));
      assertFalse(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, verArt2));
      assertTrue(verArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, folderArt2));
      assertFalse(verArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt2));

      // unrelate folder from version
      changes = atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      changes.unrelate(folderArt2, CoreRelationTypes.SupportingInfo_SupportingInfo, verArt2);
      changes.execute();

      ArtifactReadable folderArt221 = atsServer.getArtifact(folderArt.getUuid());
      ArtifactReadable verArt221 = atsServer.getArtifact(verArt.getUuid());
      assertFalse(folderArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt221));
      assertFalse(folderArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, verArt221));
      assertFalse(verArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportedBy, folderArt221));
      assertFalse(verArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt221));
   }

   @Test
   public void testSetRelationAndRelations() {
      String className = getClass().getSimpleName();
      IAtsChangeSet changes =
         atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      ArtifactId folderArt = changes.createArtifact(CoreArtifactTypes.Folder, "Folder 4 " + className);
      ArtifactId ver1ArtId = changes.createArtifact(AtsArtifactTypes.Version, "Ver 4.1 " + className);
      changes.relate(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo, ver1ArtId);

      ArtifactId ver2ArtId = changes.createArtifact(AtsArtifactTypes.Version, "Ver 4.2 " + className);
      ArtifactId ver3ArtId = changes.createArtifact(AtsArtifactTypes.Version, "Ver 4.3 " + className);
      changes.execute();

      // test that version 1 is related and not 2 and 3
      ArtifactReadable folderArt2 = atsServer.getArtifact(folderArt.getUuid());
      ArtifactReadable verArt1 = atsServer.getArtifact(ver1ArtId.getUuid());
      ArtifactReadable verArt2 = atsServer.getArtifact(ver2ArtId.getUuid());
      ArtifactReadable verArt3 = atsServer.getArtifact(ver3ArtId.getUuid());
      assertTrue(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt1));
      assertFalse(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt2));
      assertFalse(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt3));

      // setRelations to 2 and 3
      changes = atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      changes.setRelations(folderArt2, CoreRelationTypes.SupportingInfo_SupportingInfo,
         Arrays.asList(verArt2, verArt3));
      changes.execute();

      // confirm 2 and 3 are related and not 1
      ArtifactReadable folderArt21 = atsServer.getArtifact(folderArt.getUuid());
      ArtifactReadable verArt11 = atsServer.getArtifact(ver1ArtId.getUuid());
      ArtifactReadable verArt21 = atsServer.getArtifact(ver2ArtId.getUuid());
      ArtifactReadable verArt31 = atsServer.getArtifact(ver3ArtId.getUuid());
      assertFalse(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt11));
      assertTrue(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt21));
      assertTrue(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt31));

      // setRelations to 1 and 2
      changes = atsServer.getStoreService().createAtsChangeSet(className, atsServer.getUserService().getCurrentUser());
      changes.setRelations(folderArt21, CoreRelationTypes.SupportingInfo_SupportingInfo,
         Arrays.asList(verArt11, verArt21));
      changes.execute();

      // confirm 1 and 2 are related and not 3
      ArtifactReadable folderArt211 = atsServer.getArtifact(folderArt.getUuid());
      ArtifactReadable verArt111 = atsServer.getArtifact(ver1ArtId.getUuid());
      ArtifactReadable verArt211 = atsServer.getArtifact(ver2ArtId.getUuid());
      ArtifactReadable verArt311 = atsServer.getArtifact(ver3ArtId.getUuid());
      assertTrue(folderArt211.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt111));
      assertTrue(folderArt211.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt211));
      assertFalse(folderArt211.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt311));

   }
}
