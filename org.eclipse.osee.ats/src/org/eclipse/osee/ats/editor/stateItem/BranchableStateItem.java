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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.editor.service.branch.CommitManagerService;
import org.eclipse.osee.ats.editor.service.branch.CommitWorkingBranchService;
import org.eclipse.osee.ats.editor.service.branch.CreateWorkingBranchService;
import org.eclipse.osee.ats.editor.service.branch.DeleteWorkingBranch;
import org.eclipse.osee.ats.editor.service.branch.ShowChangeReportService;
import org.eclipse.osee.ats.editor.service.branch.ShowWorkingBranchService;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public abstract class BranchableStateItem extends AtsStateItem {

   private boolean allowCommit = true;
   public static String BRANCH_CATEGORY = "Branch Changes";

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getId()
    */
   public abstract String getId();

   @Override
   public List<WorkPageService> getServices(SMAManager smaMgr) {
      List<WorkPageService> services = new ArrayList<WorkPageService>();
      services.add(new CreateWorkingBranchService(smaMgr));
      services.add(new CommitManagerService(smaMgr));
      services.add(new ShowWorkingBranchService(smaMgr));
      services.add(new ShowChangeReportService(smaMgr));
      services.add(new CommitWorkingBranchService(smaMgr, false));
      if (AtsPlugin.isAtsAdmin()) services.add(new CommitWorkingBranchService(smaMgr, true));
      services.add(new DeleteWorkingBranch(smaMgr));
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
      try {
         if (smaMgr.getBranchMgr().isWorkingBranch()) return new Result(
               "Working Branch exists.  Please commit or delete working branch before transition.");
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return new Result("Problem determining status of working branch.  See error log. " + ex.getLocalizedMessage());
      }
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
