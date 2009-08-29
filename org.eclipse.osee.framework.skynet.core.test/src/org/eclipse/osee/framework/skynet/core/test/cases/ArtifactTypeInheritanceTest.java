package org.eclipse.osee.framework.skynet.core.test.cases;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;

public class ArtifactTypeInheritanceTest {

   @org.junit.Test
   public void testIsOfTypeWithNull() throws OseeCoreException {
      ArtifactType baseArtifactType = ArtifactTypeManager.getType("Artifact");
      Assert.assertFalse(baseArtifactType.inheritsFrom((ArtifactType) null));
   }

   @org.junit.Test
   public void testAllArtifactTypesInheritFromArtifactWithIsOfType() throws OseeCoreException {
      ArtifactType baseArtifactType = ArtifactTypeManager.getType("Artifact");
      for (ArtifactType artifactType : ArtifactTypeManager.getAllTypes()) {
         Assert.assertTrue(String.format("[%s] was not of type [%s]", artifactType.getName(),
               baseArtifactType.getName()), artifactType.inheritsFrom(baseArtifactType));
      }
   }

   @org.junit.Test
   public void testAttributeTypesOfDescendants() throws OseeCoreException {
      ArtifactType baseArtifactType = ArtifactTypeManager.getType("Artifact");
      Set<ArtifactType> allTypes = new HashSet<ArtifactType>(ArtifactTypeManager.getAllTypes());
      allTypes.remove(baseArtifactType);

      Collection<AttributeType> baseAttributeTypes =
            baseArtifactType.getAttributeTypes(BranchManager.getSystemRootBranch());

      Assert.assertTrue(baseAttributeTypes.size() > 0); // Must have at least name

      for (ArtifactType artifactType : allTypes) {
         Collection<AttributeType> childAttributeTypes =
               artifactType.getAttributeTypes(BranchManager.getSystemRootBranch());
         Collection<AttributeType> complement = Collections.setComplement(baseAttributeTypes, childAttributeTypes);
         Assert.assertTrue(String.format("[%s] did not inherit %s ", artifactType.getName(), complement),
               complement.isEmpty());
      }
   }
}
