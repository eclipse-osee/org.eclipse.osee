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
package org.eclipse.osee.framework.skynet.core;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public interface AccessPolicy {

   boolean isReadOnly(Artifact artifact);

   void removePermissions(BranchId branch);

   PermissionStatus hasBranchPermission(BranchId branch, PermissionEnum permission, Level level);

   PermissionStatus hasAttributeTypePermission(Collection<? extends ArtifactToken> artifacts, AttributeTypeId attributeType, PermissionEnum permission, Level level);

   PermissionStatus hasArtifactTypePermission(BranchId branch, Collection<? extends ArtifactTypeId> artifactTypes, PermissionEnum permission, Level level);

   PermissionStatus hasArtifactPermission(Collection<Artifact> artifacts, PermissionEnum permission, Level level);

   PermissionStatus canRelationBeModified(Artifact subject, Collection<Artifact> toBeRelated, RelationTypeSide relationTypeSide, Level level);

}
