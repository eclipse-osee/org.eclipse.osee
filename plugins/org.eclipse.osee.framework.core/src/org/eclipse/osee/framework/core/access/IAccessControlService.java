/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;

/**
 * @author Roberto E. Escobar
 */
public interface IAccessControlService extends IArtifactCheck {

   /**
    * @param object Artifact, Branch or collection of either
    */
   boolean hasPermission(Object object, PermissionEnum permission);

   void removePermissions(BranchId branch);

   AccessDataQuery getAccessData(ArtifactToken userArtifact, Collection<?> itemsToCheck);

   void ensurePopulated();

}