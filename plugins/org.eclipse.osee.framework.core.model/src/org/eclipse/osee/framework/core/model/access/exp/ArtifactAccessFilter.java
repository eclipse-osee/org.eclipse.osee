/*
 * Created on Jul 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class ArtifactAccessFilter implements IAccessFilter {

	private final PermissionEnum artifactPermission;
	private final IBasicArtifact<?> artifact;

	public ArtifactAccessFilter(IBasicArtifact<?> artifact, PermissionEnum artifactPermission) {
		super();
		this.artifactPermission = artifactPermission;
		this.artifact = artifact;
	}

	@Override
	public int getPriority() {
		return 30;
	}

	@Override
	public PermissionEnum filter(IBasicArtifact<?> artifact, Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain) {
		PermissionEnum toReturn = null;

		if (this.artifact.equals(artifact)) {
			if (agrPermission != PermissionEnum.DENY && artifactPermission != null) {
				agrPermission = artifactPermission;
			}
			toReturn = agrPermission;
		}
		return toReturn;
	}

	@Override
	public boolean acceptToObject(Object object) {
		//Return false if the object to be checked is a branch.
		return !(object instanceof IOseeBranch);
	}
}
