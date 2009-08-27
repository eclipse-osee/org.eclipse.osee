package org.eclipse.osee.framework.skynet.core.test.cases;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.TypeValidityManager;

public class ArtifactTypeInheritanceTest {

   @org.junit.Test
   public void testIsOfTypeWithNull() throws OseeCoreException {
      ArtifactType baseArtifactType = ArtifactTypeManager.getType("Artifact");
      Assert.assertFalse(baseArtifactType.isOfType(null));
   }

   @org.junit.Test
   public void testAllArtifactTypesInheritFromArtifactWithIsOfType() throws OseeCoreException {
      ArtifactType baseArtifactType = ArtifactTypeManager.getType("Artifact");
      for (ArtifactType artifactType : ArtifactTypeManager.getAllTypes()) {
         Assert.assertTrue(String.format("[%s] was not of type [%s]", artifactType.getName(),
               baseArtifactType.getName()), artifactType.isOfType(baseArtifactType));
      }
   }

   @org.junit.Test
   public void testAllArtifactTypesInheritFromArtifactWithDescendants() throws OseeCoreException {
      ArtifactType baseArtifactType = ArtifactTypeManager.getType("Artifact");
      Set<ArtifactType> allTypes = new HashSet<ArtifactType>(ArtifactTypeManager.getAllTypes());
      allTypes.remove(baseArtifactType);

      Collection<ArtifactType> descendantTypes = ArtifactTypeManager.getDescendants(baseArtifactType, true);
      Collection<ArtifactType> types = Collections.setIntersection(allTypes, descendantTypes);
      Assert.assertTrue(String.format("%s are not descendants of [%s]", types, baseArtifactType), types.isEmpty());
   }

   @org.junit.Test
   public void testAttributeTypesOfDescendants() throws OseeCoreException {
      //      ArtifactType baseArtifactType = ArtifactTypeManager.getType("Artifact");
      for (ArtifactType baseArtifactType : ArtifactTypeManager.getAllTypes()) {
         Collection<AttributeType> attributeTypes =
               TypeValidityManager.getAttributeTypesFromArtifactType(baseArtifactType,
                     BranchManager.getSystemRootBranch());

         for (ArtifactType artifactType : ArtifactTypeManager.getDescendants(baseArtifactType)) {
            Collection<AttributeType> childAttributeTypes =
                  TypeValidityManager.getAttributeTypesFromArtifactType(artifactType,
                        BranchManager.getSystemRootBranch());
            Collection<AttributeType> complement = Collections.setComplement(attributeTypes, childAttributeTypes);
            Assert.assertTrue(String.format("[%s] did not inherit %s ", complement), complement.isEmpty());
         }
      }
   }
}
