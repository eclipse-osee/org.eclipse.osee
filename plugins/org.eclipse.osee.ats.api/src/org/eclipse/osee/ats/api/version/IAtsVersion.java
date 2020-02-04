/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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