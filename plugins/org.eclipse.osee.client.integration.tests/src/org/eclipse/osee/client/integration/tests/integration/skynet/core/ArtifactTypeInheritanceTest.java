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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

/**
 * High-level test to ensure demo artifact types correctly inherit from artifact
 *
 * @author Roberto E. Escobar
 */
public class ArtifactTypeInheritanceTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   @Test
   public void testIsOfTypeWithNull() {
      Assert.assertFalse(Artifact.inheritsFrom((ArtifactTypeToken) null));
   }

   @Test
   public void testAllArtifactTypesInheritFromArtifactWithIsOfType() {
      for (ArtifactTypeToken artifactType : ArtifactTypeManager.getAllTypes()) {
         Assert.assertTrue(String.format("[%s] was not of type [%s]", artifactType, CoreArtifactTypes.Artifact),
            artifactType.inheritsFrom(CoreArtifactTypes.Artifact));
      }
   }

   @Test
   public void testAttributeTypesOfDescendants() {
      ArtifactType baseArtifactType = ArtifactTypeManager.getFullType(CoreArtifactTypes.Artifact);
      Set<ArtifactTypeToken> allTypes = new HashSet<>(ArtifactTypeManager.getAllTypes());
      allTypes.remove(baseArtifactType);

      Branch branch = BranchManager.getBranch(CoreBranches.SYSTEM_ROOT);
      Collection<AttributeTypeToken> baseAttributeTypes = baseArtifactType.getAttributeTypes(branch);

      Assert.assertTrue(baseAttributeTypes.size() > 0); // Must have at least name

      for (ArtifactTypeToken artifactType : allTypes) {
         Collection<AttributeTypeToken> childAttributeTypes =
            ArtifactTypeManager.getFullType(artifactType).getAttributeTypes(branch);
         Collection<AttributeTypeToken> complement = Collections.setComplement(baseAttributeTypes, childAttributeTypes);
         Assert.assertTrue(String.format("[%s] did not inherit %s ", artifactType.getName(), complement),
            complement.isEmpty());
      }
   }
}