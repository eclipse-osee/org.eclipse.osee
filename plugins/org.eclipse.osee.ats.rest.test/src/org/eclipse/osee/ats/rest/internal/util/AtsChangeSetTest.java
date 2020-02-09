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
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.test.db.AtsClassDatabase;
import org.eclipse.osee.ats.rest.test.db.AtsIntegrationByClassRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.AfterClass;
import org.junit.Assert;
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
      IAtsChangeSet changes = createAtsChangeSet();
      Long id = Lib.generateArtifactIdAsInt();
      String name = "Ver 1 " + className;
      changes.createArtifact(AtsArtifactTypes.Version, name, id);
      changes.execute();

      ArtifactToken verArt = atsServer.getQueryService().getArtifact(id);
      assertNotNull(verArt);
      assertEquals(id, verArt.getId());
      assertEquals(name, verArt.getName());
   }

   /**
    * Test relate in both directions and unrelateAll in both directions
    */
   @Test
   public void testCreateRelationsAndUnrelateAll() {
      String className = getClass().getSimpleName();
      IAtsChangeSet changes = createAtsChangeSet();
      ArtifactId folderArt = changes.createArtifact(CoreArtifactTypes.Folder, "Folder 2 " + className);
      ArtifactId verArt = changes.createArtifact(AtsArtifactTypes.Version, "Ver 2 " + className);
      changes.execute();

      ArtifactReadable folder = (ArtifactReadable) atsServer.getQueryService().getArtifact(folderArt.getId());
      assertNotNull(folder);
      IAtsVersion version =
         atsServer.getVersionService().getVersionById(atsServer.getQueryService().getArtifact(verArt.getId()));
      assertNotNull(version);

      // add relation from folder to version
      changes = createAtsChangeSet();
      changes.relate(folder, CoreRelationTypes.SupportingInfo_SupportingInfo, version);
      changes.execute();

      // test that folder to version is valid for the correct direction
      ArtifactReadable folderArt2 = (ArtifactReadable) atsServer.getQueryService().getArtifact(folder.getId());
      ArtifactReadable verArt2 = (ArtifactReadable) atsServer.getQueryService().getArtifact(version.getId());
      assertTrue(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt2));
      assertFalse(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, verArt2));
      assertTrue(verArt2.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, folderArt2));
      assertFalse(verArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt2));

      // unrelate all on folder
      changes = createAtsChangeSet();
      changes.unrelateAll(folderArt2, CoreRelationTypes.SupportingInfo_SupportingInfo);
      changes.execute();

      // test that folder and version are not related in any direction
      ArtifactReadable folderArt21 = (ArtifactReadable) atsServer.getQueryService().getArtifact(folder.getId());
      ArtifactReadable verArt21 = (ArtifactReadable) atsServer.getQueryService().getArtifact(version.getId());
      assertFalse(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt21));
      assertFalse(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, verArt21));
      assertFalse(verArt21.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, folderArt21));
      assertFalse(verArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt21));

      // relate version to folder with same relation, but opposite order
      folder = (ArtifactReadable) atsServer.getQueryService().getArtifact(folderArt.getId());
      assertNotNull(folder);
      version = atsServer.getVersionService().getVersionById(atsServer.getQueryService().getArtifact(verArt.getId()));
      assertNotNull(version);
      changes = createAtsChangeSet();
      changes.relate(version, CoreRelationTypes.SupportingInfo_IsSupportedBy, folder);
      changes.execute();

      // test that version and folder are related as expected
      ArtifactReadable folderArt22 = (ArtifactReadable) atsServer.getQueryService().getArtifact(folder.getId());
      ArtifactReadable verArt22 = (ArtifactReadable) atsServer.getQueryService().getArtifact(version.getId());
      assertTrue(folderArt22.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt22));
      assertFalse(folderArt22.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, verArt22));
      assertTrue(verArt22.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, folderArt22));
      assertFalse(verArt22.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt22));

      // unrelate all on version
      changes = createAtsChangeSet();
      changes.unrelateAll(verArt22, CoreRelationTypes.SupportingInfo_IsSupportedBy);
      changes.execute();

      // test that all version and folder are not related in any direction
      ArtifactReadable folderArt221 = (ArtifactReadable) atsServer.getQueryService().getArtifact(folder.getId());
      ArtifactReadable verArt221 = (ArtifactReadable) atsServer.getQueryService().getArtifact(version.getId());
      assertFalse(folderArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt221));
      assertFalse(folderArt221.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, verArt221));
      assertFalse(verArt221.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, folderArt221));
      assertFalse(verArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt221));
   }

   @Test
   public void testUnrelate() {
      String className = getClass().getSimpleName();
      IAtsChangeSet changes = createAtsChangeSet();
      ArtifactId folderArt = changes.createArtifact(CoreArtifactTypes.Folder, "Folder 3 " + className);
      ArtifactId verArt = changes.createArtifact(AtsArtifactTypes.Version, "Ver 3 " + className);
      changes.relate(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo, verArt);
      changes.execute();

      // test that folder to version is valid for the correct direction
      ArtifactReadable folderArt2 = (ArtifactReadable) atsServer.getQueryService().getArtifact(folderArt.getId());
      ArtifactReadable verArt2 = (ArtifactReadable) atsServer.getQueryService().getArtifact(verArt.getId());
      assertTrue(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt2));
      assertFalse(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, verArt2));
      assertTrue(verArt2.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, folderArt2));
      assertFalse(verArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt2));

      // unrelate folder from version
      changes = createAtsChangeSet();
      changes.unrelate(folderArt2, CoreRelationTypes.SupportingInfo_SupportingInfo, verArt2);
      changes.execute();

      ArtifactReadable folderArt221 = (ArtifactReadable) atsServer.getQueryService().getArtifact(folderArt.getId());
      ArtifactReadable verArt221 = (ArtifactReadable) atsServer.getQueryService().getArtifact(verArt.getId());
      assertFalse(folderArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt221));
      assertFalse(folderArt221.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, verArt221));
      assertFalse(verArt221.areRelated(CoreRelationTypes.SupportingInfo_IsSupportedBy, folderArt221));
      assertFalse(verArt221.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, folderArt221));
   }

   @Test
   public void testSetRelationAndRelations() {
      String className = getClass().getSimpleName();
      IAtsChangeSet changes = createAtsChangeSet();
      ArtifactId folderArt = changes.createArtifact(CoreArtifactTypes.Folder, "Folder 4 " + className);
      ArtifactId ver1ArtId = changes.createArtifact(AtsArtifactTypes.Version, "Ver 4.1 " + className);
      changes.relate(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo, ver1ArtId);

      ArtifactId ver2ArtId = changes.createArtifact(AtsArtifactTypes.Version, "Ver 4.2 " + className);
      ArtifactId ver3ArtId = changes.createArtifact(AtsArtifactTypes.Version, "Ver 4.3 " + className);
      changes.execute();

      // test that version 1 is related and not 2 and 3
      ArtifactReadable folderArt2 = (ArtifactReadable) atsServer.getQueryService().getArtifact(folderArt.getId());
      ArtifactReadable verArt1 = (ArtifactReadable) atsServer.getQueryService().getArtifact(ver1ArtId.getId());
      ArtifactReadable verArt2 = (ArtifactReadable) atsServer.getQueryService().getArtifact(ver2ArtId.getId());
      ArtifactReadable verArt3 = (ArtifactReadable) atsServer.getQueryService().getArtifact(ver3ArtId.getId());
      assertTrue(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt1));
      assertFalse(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt2));
      assertFalse(folderArt2.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt3));

      // setRelations to 2 and 3
      changes = createAtsChangeSet();
      changes.setRelations(folderArt2, CoreRelationTypes.SupportingInfo_SupportingInfo,
         Arrays.asList(verArt2, verArt3));
      changes.execute();

      // confirm 2 and 3 are related and not 1
      ArtifactReadable folderArt21 = (ArtifactReadable) atsServer.getQueryService().getArtifact(folderArt.getId());
      ArtifactReadable verArt11 = (ArtifactReadable) atsServer.getQueryService().getArtifact(ver1ArtId.getId());
      ArtifactReadable verArt21 = (ArtifactReadable) atsServer.getQueryService().getArtifact(ver2ArtId.getId());
      ArtifactReadable verArt31 = (ArtifactReadable) atsServer.getQueryService().getArtifact(ver3ArtId.getId());
      assertFalse(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt11));
      assertTrue(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt21));
      assertTrue(folderArt21.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt31));

      // setRelations to 1 and 2
      changes = createAtsChangeSet();
      changes.setRelations(folderArt21, CoreRelationTypes.SupportingInfo_SupportingInfo,
         Arrays.asList(verArt11, verArt21));
      changes.execute();

      // confirm 1 and 2 are related and not 3
      ArtifactReadable folderArt211 = (ArtifactReadable) atsServer.getQueryService().getArtifact(folderArt.getId());
      ArtifactReadable verArt111 = (ArtifactReadable) atsServer.getQueryService().getArtifact(ver1ArtId.getId());
      ArtifactReadable verArt211 = (ArtifactReadable) atsServer.getQueryService().getArtifact(ver2ArtId.getId());
      ArtifactReadable verArt311 = (ArtifactReadable) atsServer.getQueryService().getArtifact(ver3ArtId.getId());
      assertTrue(folderArt211.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt111));
      assertTrue(folderArt211.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt211));
      assertFalse(folderArt211.areRelated(CoreRelationTypes.SupportingInfo_SupportingInfo, verArt311));
   }

   /**
    * Verify that if relation validity is many to one, setRelation/setRelations will successfully remove one and add
    * another
    */
   @Test
   public void testSetRelationAndRelations_OneToMany() {
      String className = getClass().getSimpleName();
      IAtsChangeSet changes = createAtsChangeSet();
      ArtifactId ver1 = changes.createArtifact(AtsArtifactTypes.Version, "Version 1 " + className);
      ArtifactId ver2 = changes.createArtifact(AtsArtifactTypes.Version, "Version 2 " + className);
      ArtifactToken teamWf = changes.createArtifact(AtsArtifactTypes.TeamWorkflow, "Workflow " + className);
      changes.execute();

      // setRelation/setRelations - teamWf to version
      changes = createAtsChangeSet();
      changes.setRelation(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, ver1);
      changes.execute();

      // reload artifacts to get latest stripe
      ver1 = atsServer.getQueryService().getArtifact(ver1.getId());
      ver2 = atsServer.getQueryService().getArtifact(ver2.getId());
      teamWf = atsServer.getQueryService().getArtifact(teamWf.getId());

      // ensure that teamWf is related to ver1
      Assert.assertEquals(ver1.getId(),
         atsServer.getVersionService().getTargetedVersion(atsServer.getWorkItemService().getTeamWf(teamWf)).getId());

      // setRelation/setRelations - replace ver1 with ver2
      changes = createAtsChangeSet();
      changes.setRelation(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, ver2);
      changes.execute();

      // reload artifacts to get latest stripe
      ver1 = atsServer.getQueryService().getArtifact(ver1.getId());
      ver2 = atsServer.getQueryService().getArtifact(ver2.getId());
      teamWf = atsServer.getQueryService().getArtifact(teamWf.getId());

      // ensure teamWf is related to ver2 and not related to ver1
      IAtsVersion targetedVersion =
         atsServer.getVersionService().getTargetedVersion(atsServer.getWorkItemService().getTeamWfNoCache(teamWf));
      Assert.assertEquals(ver2.getId(), targetedVersion.getId());
      Assert.assertEquals(0,
         ((ArtifactReadable) ver1).getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow).size());
   }

   private IAtsChangeSet createAtsChangeSet() {
      return atsServer.getStoreService().createAtsChangeSet(getClass().getSimpleName(),
         atsServer.getUserService().getCurrentUser());
   }

}
