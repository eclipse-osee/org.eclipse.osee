/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.artifact;

import static org.junit.Assert.assertTrue;
import java.util.Collections;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.support.test.util.DemoSawBuilds;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceWithAttributeTest {

   @org.junit.Test
   public void testReplaceAttributeVersion() throws Exception {
      Artifact artifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, DemoSawBuilds.SAW_Bld_1,
            getClass().getSimpleName());
      artifact.setAttributeValues(CoreAttributeTypes.Name, Collections.singletonList("Name"));
      artifact.persist();

      Attribute<?> nameAttribute = artifact.getAttributes(CoreAttributeTypes.Name).iterator().next();
      int previousGamma = nameAttribute.getGammaId();
      String previousName = nameAttribute.getDisplayableString();

      nameAttribute.setFromString("New Name");
      nameAttribute.getArtifact().persist();

      nameAttribute.replaceWithVersion(previousGamma);
      assertTrue(nameAttribute.getGammaId() == previousGamma);
      assertTrue(artifact.getAttributes(CoreAttributeTypes.Name).iterator().next().getValue().equals(previousName));
   }
}
