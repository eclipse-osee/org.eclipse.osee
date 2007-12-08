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

import java.util.List;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchCreateCommitStateItem extends AtsStateItem {

   /**
    * 
    */
   public AtsBranchCreateCommitStateItem() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getDescription()
    */
   public String getDescription() {
      return "AtsBranchCreateCommitStateItem";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.AtsStateItem#getServices(org.eclipse.osee.ats.editor.SMAManager,
    *      org.eclipse.osee.ats.workflow.AtsWorkPage,
    *      org.eclipse.osee.framework.ui.skynet.XFormToolkit,
    *      org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   @Override
   public List<WorkPageService> getServices(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection session) {
      List<WorkPageService> services = super.getServices(smaMgr, page, toolkit, session);
      if (page.isAllowCreateBranch() || page.isAllowCommitBranch()) {
         services.addAll(BranchableStateItem.getServices(smaMgr, page, toolkit, session, page.isAllowCreateBranch(),
               page.isAllowCommitBranch()));
      }
      return services;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.AtsStateItem#getId()
    */
   @Override
   public String getId() {
      return ALL_STATE_IDS;
   }

}
