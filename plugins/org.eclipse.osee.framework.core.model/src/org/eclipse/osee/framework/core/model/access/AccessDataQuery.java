/*
 * Created on Jul 22, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.jdk.core.util.GUID;

public class AccessDataQuery {

   private final AccessData accessData;

   public AccessDataQuery(AccessData accessData) {
      this.accessData = accessData;
   }

   public void branchMatches(PermissionEnum permissionToMatch, IOseeBranch branchToMatch, PermissionStatus permissionStatus) throws OseeCoreException {
      Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
      checkAccess(branchAccessDetails, branchToMatch, permissionToMatch, permissionStatus);
   }

   public void branchArtifactTypeMatches(PermissionEnum permissionToMatch, IOseeBranch branchToMatch, IArtifactType artifactType, PermissionStatus permissionStatus) throws OseeCoreException {
      branchMatches(permissionToMatch, branchToMatch, permissionStatus);
      if (permissionStatus.matches()) {
         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
         checkAccess(branchAccessDetails, artifactType, permissionToMatch, permissionStatus);
      }
   }

   public void branchAttributeTypeMatches(PermissionEnum permissionToMatch, IOseeBranch branchToMatch, IAttributeType attributeType, PermissionStatus permissionStatus) throws OseeCoreException {
      branchMatches(permissionToMatch, branchToMatch, permissionStatus);
      if (permissionStatus.matches()) {
         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
         checkAccess(branchAccessDetails, attributeType, permissionToMatch, permissionStatus);
      }
   }

   public void branchRelationTypeMatches(PermissionEnum permissionToMatch, IOseeBranch branchToMatch, IRelationType relationType, PermissionStatus permissionStatus) throws OseeCoreException {
      branchMatches(permissionToMatch, branchToMatch, permissionStatus);
      if (permissionStatus.matches()) {
         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(branchToMatch);
         checkAccess(branchAccessDetails, relationType, permissionToMatch, permissionStatus);
      }
   }

   public void artifactTypeMatches(PermissionEnum permissionToMatch, IBasicArtifact<?> artifact, PermissionStatus permissionStatus) throws OseeCoreException {
      IArtifactType typeToMatch = artifact.getArtifactType();
      IOseeBranch branchToMatch = artifact.getBranch();
      branchArtifactTypeMatches(permissionToMatch, branchToMatch, typeToMatch, permissionStatus);
      if (permissionStatus.matches()) {
         Collection<AccessDetail<?>> artifactAccessDetails = accessData.getAccess(artifact);
         checkAccess(artifactAccessDetails, typeToMatch, permissionToMatch, permissionStatus);
      }
   }

   public void artifactMatches(PermissionEnum permissionToMatch, IBasicArtifact<?> artifact, PermissionStatus permissionStatus) throws OseeCoreException {
      artifactTypeMatches(permissionToMatch, artifact, permissionStatus);
      if (permissionStatus.matches()) {
         Collection<AccessDetail<?>> artifactAccessDetails = accessData.getAccess(artifact);
         checkAccess(artifactAccessDetails, artifact, permissionToMatch, permissionStatus);
      }
   }

   public void attributeTypeMatches(PermissionEnum permissionToMatch, IBasicArtifact<?> artifact, IAttributeType attributeType, PermissionStatus permissionStatus) throws OseeCoreException {
      artifactMatches(permissionToMatch, artifact, permissionStatus);
      if (permissionStatus.matches()) {

         Collection<AccessDetail<?>> branchAccessDetails = accessData.getAccess(artifact.getBranch());
         checkAccess(branchAccessDetails, attributeType, permissionToMatch, permissionStatus);

         if (permissionStatus.matches()) {
            Collection<AccessDetail<?>> artifactAccessDetails = accessData.getAccess(artifact);
            checkAccess(artifactAccessDetails, attributeType, permissionToMatch, permissionStatus);
         }
      }
   }

   public void relationTypeMatches(PermissionEnum permissionToMatch) throws OseeCoreException {
   }

   PermissionStatus permissionStatus = new PermissionStatus();

   public boolean matchesAll(Collection<IBasicArtifact<?>> toCheck, PermissionEnum permissionToMatch) throws OseeCoreException {
      for (IBasicArtifact<?> artifact : toCheck) {
         artifactMatches(permissionToMatch, artifact, permissionStatus);

         if (!permissionStatus.matches()) {
            break;
         }
      }
      return permissionStatus.matches();
   }

   private <T> void checkAccess(Collection<AccessDetail<?>> accessList, T itemToMatch, PermissionEnum permissionToMatch, PermissionStatus status) {
      for (AccessDetail<?> data : accessList) {
         Object object = data.getAccessObject();
         if (itemToMatch.equals(object)) {
            boolean matches = data.getPermission().matches(permissionToMatch);
            if (!matches) {
               status.setReason(data.getReason());
            }
            status.setMatches(matches);
            break;
         }
      }
   }

   //	public PermissionStatus matches(PermissionEnum permissionToMatch, IBasicArtifact<?> artifact, IOseeBranch branchToMatch, IArtifactType artTypeToMatch, IAttributeType attrTypeToMatch, IRelationType relationTypeToMatch) throws OseeCoreException {
   //		// Filter 1 - Branch - input Branch
   //		// input branch
   //		// output matched
   //		PermissionStatus permissionStatus = new PermissionStatus();
   //		Collection<AccessDetail<?>> branchAccessDetails = getAccess(branchToMatch);
   //		checkAccess(branchAccessDetails, branchToMatch, permissionToMatch, permissionStatus);
   //
   //		// Filter 5 - RelationType
   //		// input artifact, relationType
   //		// output matched
   //
   //		Collection<AccessDetail<?>> artifactAccessDetails = getAccess(artifact);
   //		//		checkAccess(accessDetails, relationTypeToMatch, permissionToMatch, matchResults);
   //
   //		if (permissionStatus.matches()) {
   //			if (permissionStatus.matches()) {
   //				// Filter 2  - Artifact Type
   //				// input artifact, artifactType
   //				// output matched
   //				checkAccess(artifactAccessDetails, artTypeToMatch, permissionToMatch, permissionStatus);
   //
   //				if (permissionStatus.matches()) {
   //					// Filter 3 - Artifact
   //					// input artifact
   //					// output matched
   //					checkAccess(artifactAccessDetails, artifact, permissionToMatch, permissionStatus);
   //
   //					if (permissionStatus.matches()) {
   //						// Filter 4 - Attribute Type
   //						// input artifact, attributeType
   //						// output matched
   //						checkAccess(artifactAccessDetails, attrTypeToMatch, permissionToMatch, permissionStatus);
   //					}
   //				}
   //			}
   //		}
   //		return permissionStatus;
   //	}

   public static void main(String[] args) throws OseeCoreException {
      IOseeBranch branchToCheck = CoreBranches.SYSTEM_ROOT;
      IArtifactType artifactType = CoreArtifactTypes.AbstractSoftwareRequirement;
      IAttributeType attributeType = CoreAttributeTypes.PARAGRAPH_NUMBER;
      IAttributeType wordAttributeType = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;

      IBasicArtifact<?> artifactToCheck = new DefaultBasicArtifact(12, GUID.create(), "Hello");

      AccessData data = new AccessData();
      data.add(branchToCheck, new AccessDetail<IOseeBranch>(branchToCheck, PermissionEnum.WRITE));

      //		data.add(artifactToCheck, new Access<IBasicArtifact<?>>(artifactToCheck, PermissionEnum.READ));
      data.add(artifactToCheck, new AccessDetail<IBasicArtifact<?>>(artifactToCheck, PermissionEnum.WRITE));

      data.add(artifactToCheck, new AccessDetail<IArtifactType>(artifactType, PermissionEnum.WRITE));

      data.add(artifactToCheck, new AccessDetail<IAttributeType>(attributeType, PermissionEnum.WRITE));
      data.add(artifactToCheck, new AccessDetail<IAttributeType>(wordAttributeType, PermissionEnum.READ));

      //		System.out.println(data.matches(PermissionEnum.WRITE, artifactToCheck, branchToCheck, artifactType,
      //					attributeType, null));
      //		System.out.println(data.matches(PermissionEnum.WRITE, artifactToCheck, branchToCheck, artifactType,
      //					wordAttributeType, null));
      System.out.println(data);
   }

   //	Branch
   // IBasicArtifact<?>
   // ArtifactType
   // AttributeType(s)
   // RelationType(s)

   // Branch
   // Artifact, ArtifactType
   // AttributeType
   // Branch, ArtifactType
   // AttributeType ?
}
