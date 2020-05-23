/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.version;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsVersion extends IAtsConfigObject {

   BranchId getBaselineBranch();

   boolean isAllowCreateBranch();

   boolean isAllowCommitBranch();

   boolean isReleased();

   boolean isLocked();

   boolean isNextVersion();

   default boolean isBranchValid() {
      return getBaselineBranch().isValid();
   }

   default boolean isBranchInvalid() {
      return getBaselineBranch().isInvalid();
   }

}