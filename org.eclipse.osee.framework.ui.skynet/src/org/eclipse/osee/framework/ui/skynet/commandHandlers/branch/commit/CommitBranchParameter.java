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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.commit;

import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterValuesException;

/**
 * @author Jeff C. Phillips
 *
 */
public class CommitBranchParameter implements IParameter {
   public static String ARCHIVE_PARENT_BRANCH = "archive_parent_branch";
   public String getId() {
      return ARCHIVE_PARENT_BRANCH;
   }

   public String getName() {
      return "Branch Commit parameter";
   }

   public IParameterValues getValues() throws ParameterValuesException {
      throw new ParameterValuesException("Branch Commit has no parameters", null);
   }

   public boolean isOptional() {
      return false;
   }
}
