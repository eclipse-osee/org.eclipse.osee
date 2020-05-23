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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.merge;

import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;

/**
 * @author Jeff C. Phillips
 */
public class BranchIdParameter implements IParameter {
   @Override
   public String getId() {
      return BranchView.BRANCH_ID;
   }

   @Override
   public String getName() {
      return "Branch Uuid";
   }

   @Override
   public IParameterValues getValues() throws ParameterValuesException {
      throw new ParameterValuesException("Branch View has no parameters", null);
   }

   @Override
   public boolean isOptional() {
      return false;
   }
}
