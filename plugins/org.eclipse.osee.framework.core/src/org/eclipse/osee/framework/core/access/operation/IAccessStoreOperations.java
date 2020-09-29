/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access.operation;

import org.eclipse.osee.framework.core.access.AccessControlData;
import org.eclipse.osee.framework.core.access.object.AccessObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;

/**
 * @author Donald G. Dunne
 */
public interface IAccessStoreOperations {

   void removePermissions(BranchId branch);

   void setPermission(ArtifactToken subject, BranchId branch, PermissionEnum permission);

   void setPermission(ArtifactToken subject, ArtifactToken artifact, PermissionEnum permission);

   void persistPermission(AccessControlData data);

   void persistPermission(AccessControlData data, boolean recurse);

   void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data);

   void removeFromDatabase(AccessObject accessControlledObject, ArtifactId subjectId);

   void setCache(AccessCache cache);

}