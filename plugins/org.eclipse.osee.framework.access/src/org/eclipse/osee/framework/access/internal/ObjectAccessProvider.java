/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal;

import java.util.Collection;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ObjectAccessProvider implements IAccessProvider {

	private final AccessControlService accessService;

	public ObjectAccessProvider(AccessControlService accessService) {
		this.accessService = accessService;
	}

	@Override
	public void computeAccess(IBasicArtifact<?> userArtifact, Collection<?> objToCheck, AccessData accessData) throws OseeCoreException {
		for (Object object : objToCheck) {
			if (object instanceof Artifact) {
				setArtifactAccessData(userArtifact, (Artifact) object, accessData);
			} else if (object instanceof Branch) {
				setBranchAccessData(userArtifact, (Branch) object, accessData);
			} else {
				throw new OseeStateException("Unhandled object type for access control - " + object);
			}
		}
	}

	private void setArtifactAccessData(IBasicArtifact<?> userArtifact, Artifact artifact, AccessData accessData) throws OseeCoreException {
		PermissionEnum userPermission = accessService.getArtifactPermission(userArtifact, artifact);

		if (userPermission == null || isArtifactReadOnly(artifact)) {
			userPermission = PermissionEnum.READ;
		}
		accessData.add(artifact, new AccessDetail<IBasicArtifact<Artifact>>(artifact, userPermission));
	}

	public boolean isArtifactReadOnly(Artifact artifact) {
		return artifact.isDeleted() || artifact.isHistorical() || !artifact.getBranch().isEditable();
	}

	private void setBranchAccessData(IBasicArtifact<?> userArtifact, Branch branch, AccessData accessData) throws OseeCoreException {
		PermissionEnum userPermission = accessService.getBranchPermission(userArtifact, branch);
		accessData.add(branch, new AccessDetail<IOseeBranch>(branch, userPermission));
	}
}
