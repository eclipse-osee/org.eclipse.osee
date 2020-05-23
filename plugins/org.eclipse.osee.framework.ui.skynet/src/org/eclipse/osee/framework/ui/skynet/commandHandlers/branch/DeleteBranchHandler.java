/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Karol M. Wilk
 * @author Roberto E. Escobar
 */
public final class DeleteBranchHandler extends GeneralBranchHandler {

   public DeleteBranchHandler() {
      super(OpTypeEnum.DELETE);
   }

   @Override
   public void performOperation(List<IOseeBranch> branches) {
      BranchManager.deleteBranch(branches);
   }
}
