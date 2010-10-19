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
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;

public interface IAccessPolicyHandlerService {
   PermissionStatus hasAttributeTypePermission(Collection<? extends IBasicArtifact<?>> artifacts, IAttributeType attributeType, PermissionEnum permission, Level level) throws OseeCoreException;

   PermissionStatus hasRelationSidePermission(Collection<RelationTypeSide> relationTypeSides, PermissionEnum permission, Level level) throws OseeCoreException;

   PermissionStatus hasArtifactTypePermission(IOseeBranch branch, Collection<? extends IArtifactType> artifactTypes, PermissionEnum permission, Level level) throws OseeCoreException;
}
