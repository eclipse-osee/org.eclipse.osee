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
package org.eclipse.osee.ats.client.integration.tests.ats.workflow;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
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
