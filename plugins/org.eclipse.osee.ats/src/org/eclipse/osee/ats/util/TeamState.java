/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

public enum TeamState implements IWorkPage {
   Endorse(WorkPageType.Working),
   Analyze(WorkPageType.Working),
   Authorize(WorkPageType.Working),
   Implement(WorkPageType.Working),
   Completed(WorkPageType.Completed),
   Cancelled(WorkPageType.Cancelled);

   private final WorkPageType workPageType;

   private TeamState(WorkPageType workPageType) {
      this.workPageType = workPageType;
   }

   @Override
   public WorkPageType getWorkPageType() {
      return workPageType;
   }

   @Override
   public String getPageName() {
      return name();
   }

   @Override
   public boolean isCompletedOrCancelledPage() {
      return getWorkPageType().isCompletedOrCancelledPage();
   }

   @Override
   public boolean isCompletedPage() {
      return getWorkPageType().isCompletedPage();
   }

   @Override
   public boolean isCancelledPage() {
      return getWorkPageType().isCancelledPage();
   }

   @Override
   public boolean isWorkingPage() {
      return getWorkPageType().isWorkingPage();
   }

}
