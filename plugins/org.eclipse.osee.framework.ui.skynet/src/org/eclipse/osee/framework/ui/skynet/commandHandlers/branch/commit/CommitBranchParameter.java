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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit;

import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterValuesException;

/**
 * @author Jeff C. Phillips
 */
public final class CommitBranchParameter implements IParameter {
   public static final String ARCHIVE_PARENT_BRANCH = "archive_parent_branch";

   @Override
   public String getId() {
      return ARCHIVE_PARENT_BRANCH;
   }

   @Override
   public String getName() {
      return "Branch Commit parameter";
   }

   @Override
   public IParameterValues getValues() throws ParameterValuesException {
      throw new ParameterValuesException("Branch Commit has no parameters", null);
   }

   @Override
   public boolean isOptional() {
      return false;
   }
}
