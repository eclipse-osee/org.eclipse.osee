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
package org.eclipse.osee.ats.editor.service;

import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class TotalHoursSpentStat extends WorkPageService {

   private Label label;

   public TotalHoursSpentStat(SMAManager smaMgr) {
      super(smaMgr);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isShowSidebarService(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isShowSidebarService(AtsWorkPage page) throws OseeCoreException {
      return smaMgr.isCurrentState(page.getName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createSidebarService(org.eclipse.swt.widgets.Group, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.ui.skynet.XFormToolkit, org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   @Override
   public void createSidebarService(Group workGroup, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) throws OseeCoreException {
      label = toolkit.createLabel(workGroup, "", SWT.NONE);
      refresh();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Hours Spent";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getSidebarCategory()
    */
   @Override
   public String getSidebarCategory() {
      return ServicesArea.STATISTIC_CATEGORY;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageService#refresh()
    */
   @Override
   public void refresh() {
      try {
         if (label != null && !label.isDisposed()) label.setText("Total Hours Spent: " + AtsLib.doubleToStrString(smaMgr.getSma().getHoursSpentSMATotal()));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

}
