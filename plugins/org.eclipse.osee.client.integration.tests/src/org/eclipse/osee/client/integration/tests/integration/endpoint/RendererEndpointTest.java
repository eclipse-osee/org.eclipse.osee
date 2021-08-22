/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.endpoint;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import javax.ws.rs.core.Response;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.define.api.RenderEndpoint;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author David W. Miller
 */
public class RendererEndpointTest {
   private static final String beginWordString = "<w:p><w:r><w:t>";
   private static final String endWordString = "</w:t></w:r></w:p>";

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   private static RenderEndpoint renderEndpoint;
   private static final BranchToken branch = DemoBranches.SAW_PL;
   private static final Artifact parent =
      ArtifactQuery.getArtifactFromId(CoreArtifactTokens.SoftwareRequirementsFolder, branch);
   private static final Artifact template = ArtifactQuery.getArtifactFromTypeAndName(
      CoreArtifactTypes.RendererTemplateWholeWord, "PREVIEW_ALL_RECURSE_NO_ATTRIBUTES", CoreBranches.COMMON);
   @ClassRule
   public static TemporaryFolder folder = new TemporaryFolder();

   @BeforeClass
   public static void testSetup() {
      OseeClient oseeclient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
      renderEndpoint = oseeclient.getRenderEndpoint();
      setUpSWReq(parent, branch);
      parent.persist("Setup for importing software requirements");
   }

   @Test
   public void testImport() throws IOException {
      try (Response response = renderEndpoint.msWordTemplatePublish(branch, template, parent, ArtifactId.SENTINEL)) {
         Object attachment = response.getEntity();

         File wordPublish = folder.newFile("wordPublish.xml");
         if (attachment instanceof GZIPInputStream) {
            GZIPInputStream gzi = (GZIPInputStream) attachment;
            saveAttachement(gzi, wordPublish);
         }
         String fileContents = Lib.fileToString(wordPublish);

         Assert.assertTrue(fileContents.contains("Communication Subsystem Crew Interface"));
      }
   }

   private static void setUpSWReq(Artifact swReqFolder, BranchToken branch) {
      Artifact crewReq =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch, "Crew Station Requirements");
      Artifact commReq = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, branch,
         "Communication Subsystem Crew Interface");
      Artifact navReq = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, branch,
         "Navigation Subsystem Crew Interface");
      Artifact airReq = ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch,
         "Aircraft Systems Management Subsystem Crew Interface");
      Artifact airDrawReq =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.HeadingMsWord, branch, "Aircraft Drawing");
      Artifact ventReq =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, branch, "Ventilation");

      swReqFolder.addChild(crewReq);
      crewReq.addChild(USER_DEFINED, commReq);
      crewReq.addChild(USER_DEFINED, navReq);
      crewReq.addChild(USER_DEFINED, airReq);
      airReq.addChild(USER_DEFINED, airDrawReq);
      airReq.addChild(USER_DEFINED, ventReq);

      commReq.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the list of Communication crew station requirements." + endWordString);
      navReq.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the list of Navigation crew station requirements." + endWordString);
      airReq.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the list of Aircraft Management crew station requirements." + endWordString);
      ventReq.setSoleAttributeValue(CoreAttributeTypes.WordTemplateContent,
         beginWordString + "This is the Ventilation crew station requirements." + endWordString);
   }

   private static void saveAttachement(GZIPInputStream given, File output) throws IOException {
      OutputStream outputStream = null;
      try {
         outputStream = new BufferedOutputStream(new FileOutputStream(output));
         Lib.inputStreamToOutputStream(given, outputStream);
      } finally {
         Lib.close(outputStream);
      }
   }
}