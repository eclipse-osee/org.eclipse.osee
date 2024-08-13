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

package org.eclipse.osee.ats.ide.integration.tests.framework.skynet.core.artifact;

import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeManagerTest {

   @Test
   public void testUniqueArtIdConstraint() {
      boolean exceptionThrown = false;
      try {
         AtsApiIde atsApi = AtsApiService.get();
         IAtsChangeSet changes = atsApi.createChangeSet("testUniqueArtIdConstraint");
         changes.createArtifact(AtsArtifactToken.AtsTopFolder);
         changes.execute();
      } catch (OseeArgumentException ex) {
         if (ex.getLocalizedMessage().contains("Artifact with Id [114713] already exists")) {
            exceptionThrown = true;
         }
      }
      Assert.assertTrue("ARTIFACT_TABLE.ART_ID constraint should have thrown exception.", exceptionThrown);
   }

   @Test
   public void test() {
      Assert.assertFalse(ArtifactTypeManager.isUserCreationAllowed(CoreArtifactTypes.User));

      Assert.assertTrue(ArtifactTypeManager.isUserCreationAllowed(CoreArtifactTypes.SoftwareRequirementMsWord));
   }
}
