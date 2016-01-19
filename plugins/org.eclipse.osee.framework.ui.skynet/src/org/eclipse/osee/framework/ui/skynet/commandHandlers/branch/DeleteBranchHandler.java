/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
