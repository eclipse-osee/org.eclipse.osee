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
package org.eclipse.osee.framework.skynet.core.test.cases;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * High-level test to ensure demo artifact types correctly inherit from artifact
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactTypeInheritanceTest {
   @org.junit.Test
   public void testIsOfTypeWithNull() throws OseeCoreException {
      ArtifactType baseArtifactType = ArtifactTypeManager.getType(CoreArtifactTypes.Artifact);
      Assert.assertFalse(baseArtifactType.inheritsFrom((ArtifactType) null));
   }

   @org.junit.Test
   public void testAllArtifactTypesInheritFromArtifactWithIsOfType() throws OseeCoreException {
      for (ArtifactType artifactType : ArtifactTypeManager.getAllTypes()) {
         Assert.assertTrue(String.format("[%s] was not of type [%s]", artifactType, CoreArtifactTypes.Artifact),
            artifactType.inheritsFrom(CoreArtifactTypes.Artifact));
      }
   }

   @org.junit.Test
   public void testAttributeTypesOfDescendants() throws OseeCoreException {
      ArtifactType baseArtifactType = ArtifactTypeManager.getType(CoreArtifactTypes.Artifact);
      Set<ArtifactType> allTypes = new HashSet<ArtifactType>(ArtifactTypeManager.getAllTypes());
      allTypes.remove(baseArtifactType);

      Branch branch = BranchManager.getBranch(CoreBranches.SYSTEM_ROOT);
      Collection<IAttributeType> baseAttributeTypes = baseArtifactType.getAttributeTypes(branch);

      Assert.assertTrue(baseAttributeTypes.size() > 0); // Must have at least name

      for (ArtifactType artifactType : allTypes) {
         Collection<IAttributeType> childAttributeTypes = artifactType.getAttributeTypes(branch);
         Collection<IAttributeType> complement = Collections.setComplement(baseAttributeTypes, childAttributeTypes);
         Assert.assertTrue(String.format("[%s] did not inherit %s ", artifactType.getName(), complement),
            complement.isEmpty());
      }
   }
}
