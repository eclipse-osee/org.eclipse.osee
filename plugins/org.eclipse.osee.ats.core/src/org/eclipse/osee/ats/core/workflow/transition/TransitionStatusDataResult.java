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
package org.eclipse.osee.ats.core.workflow.transition;

import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class TransitionStatusDataResult {

   public static final Result INVALID__UNCOMPLETE_PERCENT =
      new Result("Percent must be between 0 and 99.  Use Transition-To for completed.");
   public static final Result INVALID__PERCENT_MUST_BE_ENTERED = new Result("Percent must be entered.");
   public static final Result INVALID__HOURS_MUST_BE_SET = new Result("Hours must be entered.");
   public static final Result INVALID__SELECT_EITHER_SPLIT_OR_APPLY =
      new Result("Either \"Split Hours Spent\" or \"Apply Hours Spent\" must be selected");
   public static final Result INVALID__SELECT_ONLY_ONE_SPLIT_OR_APPLY =
      new Result("Select only \"Split Hours Spent\" or \"Apply Hours Spent\"");

}
