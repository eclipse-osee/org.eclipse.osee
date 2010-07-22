/*
 * Created on Jul 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

public class AttributeTypeAccessFilter implements IAccessFilter {
	private final PermissionEnum permission;
	private final IBasicArtifact<?> artifact;
	private final IAttributeType type;

	public AttributeTypeAccessFilter(PermissionEnum permission, IBasicArtifact<?> artifact, IAttributeType type) {
		super();
		this.permission = permission;
		this.artifact = artifact;
		this.type = type;
	}

	@Override
	public int getPriority() {
		return 10;
	}

	@Override
	public boolean acceptToObject(Object object) {
		//Return false if the object to be checked is a branch or artifact.
		return (!(object instanceof Branch) && !(object instanceof IBasicArtifact<?>));
	}

	@Override
	public PermissionEnum filter(IBasicArtifact<?> artifact, Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain) {
		PermissionEnum toReturn = agrPermission;

		if (this.artifact.equals(artifact) && type.equals(object)) {
			if (agrPermission != PermissionEnum.DENY && permission != null) {
				agrPermission = permission;
				toReturn = agrPermission;
			}
		}
		return toReturn;
	}

}
