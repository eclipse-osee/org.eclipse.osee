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
package org.eclipse.osee.framework.core.services;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

public interface IAccessControlService {

   /**
    * @param object Artifact, Branch or collection of either
    */
   boolean hasPermission(Object object, PermissionEnum permission) throws OseeCoreException;

   void removePermissions(BranchId branch) throws OseeCoreException;

   AccessDataQuery getAccessData(ArtifactToken userArtifact, Collection<?> itemsToCheck) throws OseeCoreException;
}
