/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.orcs.rest.model.OrcsWriterEndpoint;
import org.eclipse.osee.orcs.writer.model.reader.OwArtifact;
import org.eclipse.osee.orcs.writer.model.reader.OwCollector;
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
      writer = AtsClientService.getOrcsWriter();
   }

   @Test
   public void testGetOrcsWriterInputDefaultJson() throws Exception {
      OwCollector collector = getDefaultOwCollector();
      assertEquals(3, collector.getCreate().size());
   }

   private OwCollector getDefaultOwCollector() throws Exception {
      Response response = writer.getOrcsWriterInputDefaultJson();
      assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
      OwCollector collector = response.readEntity(OwCollector.class);
      return collector;
   }

   @Test
   public void testGetOrcsWriterInputDefault() throws Exception {
      Response response = writer.getOrcsWriterInputDefault();
      assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
      String excelXml = response.readEntity(String.class);
      assertTrue(excelXml.contains("Orcs Writer Import Folder"));
   }

   @Test
   public void testValidate() throws Exception {
      OwCollector collector = getDefaultOwCollector();
      Response response = writer.getOrcsWriterValidate(collector);
      assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());
   }

   @Test
   public void testPersist() throws Exception {
      OwCollector collector = getDefaultOwCollector();
      Response response = writer.getOrcsWriterPersist(collector);
      assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(), response.getStatus());

      for (OwArtifact art : collector.getCreate()) {
         long artTypeUuid = art.getType().getUuid();
         ArtifactType typeByGuid = ArtifactTypeManager.getTypeByGuid(artTypeUuid);
         assertNotNull(typeByGuid);
         if (typeByGuid.equals(CoreArtifactTypes.Folder)) {
            long artUuid = art.getUuid();
            Artifact folderArt = AtsClientService.get().getArtifact(artUuid);
            assertNotNull(folderArt);
            assertEquals(2, folderArt.getChildren().size());
            for (Artifact child : folderArt.getChildren()) {
               assertTrue(child.getName().equals("Software Requirement 1") || child.getName().equals(
                  "Software Requirement 2"));
            }
         }
      }
   }
}
