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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactFactoryTest {

   @Test
   public void test() {
      Assert.assertFalse(ArtifactTypeManager.isUserCreationAllowed(AtsArtifactTypes.TeamWorkflow));

      Assert.assertFalse(ArtifactTypeManager.isUserCreationAllowed(DemoArtifactTypes.DemoCodeTeamWorkflow));

      Assert.assertTrue(ArtifactTypeManager.isUserCreationAllowed(AtsArtifactTypes.AgileFeatureGroup));
   }
}
