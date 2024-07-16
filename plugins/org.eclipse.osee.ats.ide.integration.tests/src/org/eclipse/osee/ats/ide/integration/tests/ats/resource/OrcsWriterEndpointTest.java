/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.orcs.rest.model.OrcsWriterEndpoint;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifact;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;
import org.junit.Before;
import org.junit.Test;

/**
 * Test unit for {@link OrcsWriterEndpoint}
 *
 * @author Donald G. Dunne
 */
public class OrcsWriterEndpointTest extends AbstractRestTest {
   private OrcsWriterEndpoint writer;

   @Before
   public void setup() {
      writer = AtsApiService.get().getOseeClient().getOrcsWriterEndpoint();
   }

   @Test
   public void testGetOrcsWriterInputDefaultJson() throws Exception {
      OwCollector collector = getDefaultOwCollector();
      assertEquals(4, collector.getCreate().size());
   }

   private OwCollector getDefaultOwCollector() throws Exception {
      try (Response response = writer.getOrcsWriterInputDefaultJson()) {
         assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());

         OwCollector collector = response.readEntity(OwCollector.class);
         return collector;
      }
   }

   @Test
   public void testGetOrcsWriterInputDefault() throws Exception {
      try (Response response = writer.getOrcsWriterInputDefault()) {
         assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
         String excelXml = response.readEntity(String.class);

         assertTrue(excelXml.contains("Orcs Writer Import Folder"));
      }
   }

   @Test
   public void testValidate() throws Exception {
      OwCollector collector = getDefaultOwCollector();
      collector.setAsUserId(DemoUsers.Joe_Smith.getUserId());
      collector.setPersistComment(getClass().getSimpleName() + " - testValidate");
      try (Response response = writer.getOrcsWriterValidate(collector)) {
         assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
      }
   }

   @Test
   public void testPersist() throws Exception {
      OwCollector collector = getDefaultOwCollector();
      collector.setAsUserId(DemoUsers.Joe_Smith.getUserId());
      collector.setPersistComment(getClass().getSimpleName() + " - testPersist");
      try (Response response = writer.getOrcsWriterPersist(collector)) {
         assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
      }
      for (OwArtifact art : collector.getCreate()) {
         long artTypeId = art.getType().getId();
         if (CoreArtifactTypes.Folder.equals(artTypeId)) {
            long artId = art.getId();
            Artifact folderArt = AtsApiService.get().getQueryServiceIde().getArtifact(artId);
            assertNotNull(folderArt);
            assertEquals(3, folderArt.getChildren().size());
            for (Artifact child : folderArt.getChildren()) {
               assertTrue(child.getName().equals("MSWordRequirement3") || child.getName().equals(
                  "Software Requirement 1") || child.getName().equals("Software Requirement 2"));
               if (child.getName().equals("MSWordRequirement3")) {
                  assertTrue(child.getAttributes().get(3).getValue().toString().contains("<w:p><w:r><w:t>"));
               }
            }
         }
      }

      OwArtifact userGroupOwArt = collector.getUpdate().iterator().next();
      Artifact userGroupArt = AtsApiService.get().getQueryServiceIde().getArtifact(userGroupOwArt.getId());

      assertNotNull(userGroupArt);
      userGroupArt.reloadAttributesAndRelations();
      assertEquals("test static id", userGroupArt.getSoleAttributeValue(CoreAttributeTypes.StaticId, null));
      assertEquals("test annotation", userGroupArt.getSoleAttributeValue(CoreAttributeTypes.Annotation, null));

   }

   @Test
   public void testDelete() throws Exception {
      Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, CoreBranches.COMMON,
         getClass().getSimpleName());
      artifact.persist(getClass().getSimpleName());

      Artifact artifactFromId1 = ArtifactQuery.getArtifactFromToken(artifact);

      assertNotNull(artifactFromId1);

      OwCollector collector = getDefaultOwCollector();
      collector.getCreate().clear();
      collector.getUpdate().clear();
      collector.getDelete().add(artifact);

      collector.setAsUserId(DemoUsers.Joe_Smith.getUserId());
      collector.setPersistComment(getClass().getSimpleName() + " - testValidate");

      try (Response response = writer.getOrcsWriterPersist(collector)) {
         assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
      }
      ArtifactCache.deCache(artifactFromId1);

      Artifact artifactReloaded = ArtifactQuery.getArtifactFromToken(artifact, DeletionFlag.INCLUDE_DELETED);
      assertTrue(artifactReloaded.isDeleted());
   }
}