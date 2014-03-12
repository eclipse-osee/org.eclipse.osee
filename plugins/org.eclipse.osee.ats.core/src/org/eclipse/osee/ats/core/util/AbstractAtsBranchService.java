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
package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.framework.core.data.IOseeBranch;

/**
 * @author Donald G Dunne
 */
public abstract class AbstractAtsBranchService implements IAtsBranchService {

   @Override
   public IOseeBranch getBranch(ICommitConfigItem configObject) {
      return getBranch((IAtsConfigObject) configObject);
   }

   @Override
   public boolean isBranchValid(ICommitConfigItem configObject) {
      boolean validBranch = false;
      if (configObject.getBaselineBranchUuid() > 0) {
         validBranch = true;
      }
      return validBranch;
   }

}
