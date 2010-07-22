/*
 * Created on Jul 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class BranchAccessFilter implements IAccessFilter {
	private final PermissionEnum branchPermission;
	private final IBasicArtifact<?> artifact;

	public BranchAccessFilter(IBasicArtifact<?> artifact, PermissionEnum branchPermission) {
		this.artifact = artifact;
		this.branchPermission = branchPermission;
	}

	@Override
	public int getPriority() {
		return 40;
	}

	@Override
	public PermissionEnum filter(IBasicArtifact<?> artifact, Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain) {
		PermissionEnum toReturn = agrPermission;

		if (this.artifact.equals(artifact)) {
			agrPermission = branchPermission;
		}

		return toReturn;
	}

	@Override
	public boolean acceptToObject(Object object) {
		return true;
	}
}
