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
package org.eclipse.osee.ats.editor.stateItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.editor.service.branch.CommitWorkingBranchService;
import org.eclipse.osee.ats.editor.service.branch.CreateWorkingBranchService;
import org.eclipse.osee.ats.editor.service.branch.DeleteWorkingBranch;
import org.eclipse.osee.ats.editor.service.branch.ShowChangeReportService;
import org.eclipse.osee.ats.editor.service.branch.ShowWorkingBranchService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class BranchableStateItem extends AtsStateItem {

   private boolean allowCommit = true;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getId()
    */
   public abstract String getId();

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getServices()
    */
   public List<WorkPageService> getServices(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      return getServices(smaMgr, page, toolkit, section, true, allowCommit);
   }

   public static List<WorkPageService> getServices(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section, boolean allowCreate, boolean allowCommit) {
      List<WorkPageService> services = new ArrayList<WorkPageService>();
      if (allowCreate) services.add(new CreateWorkingBranchService(smaMgr, page, toolkit, section));
      services.add(new ShowWorkingBranchService(smaMgr, page, toolkit, section));
      services.add(new ShowChangeReportService(smaMgr, page, toolkit, section));
      if (allowCommit) {
         services.add(new CommitWorkingBranchService(smaMgr, page, toolkit, section, false));
         if (AtsPlugin.isAtsAdmin()) services.add(new CommitWorkingBranchService(smaMgr, page, toolkit, section, true));
      }
      if (allowCreate || allowCommit) services.add(new DeleteWorkingBranch(smaMgr, page, toolkit, section));
      return services;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#transitioning(java.lang.String,
    *      java.lang.String, java.util.Collection)
    */
   public Result transitioning(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) {
      if (!allowCommit) return Result.TrueResult;
      if (smaMgr.getBranchMgr().isWorkingBranch()) return new Result(
            "Working Branch exists.  Please commit or delete working branch before transition.");
      return Result.TrueResult;
   }

   /**
    * @return the allowCommit
    */
   public boolean isAllowCommit() {
      return allowCommit;
   }

   /**
    * @param allowCommit the allowCommit to set
    */
   public void setAllowCommit(boolean allowCommit) {
      this.allowCommit = allowCommit;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getDescription()
    */
   public String getDescription() {
      return "add Branch/Commit block.";
   }

}
