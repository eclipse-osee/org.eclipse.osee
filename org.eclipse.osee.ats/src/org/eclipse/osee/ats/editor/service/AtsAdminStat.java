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

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class AtsAdminStat extends WorkPageService {

   private Label label;

   public AtsAdminStat(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("AtsAdmin", smaMgr, page, toolkit, section, ServicesArea.ADMIN_CATEGORY, Location.Global);
   }

   @Override
   public boolean displayService() {
      return AtsPlugin.isAtsAdmin();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#create()
    */
   @Override
   public void create(Group workComp) {
      label = toolkit.createLabel(workComp, "AtsAdmin");
      label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

      if (AtsPlugin.isAtsUseWorkflowFiles()) {
         label = toolkit.createLabel(workComp, "AtsUseWorkflowFiles");
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      }

      if (AtsPlugin.isAtsIgnoreConfigUpgrades()) {
         label = toolkit.createLabel(workComp, "AtsIgnoreConfigUpgrades");
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      }

      if (AtsPlugin.isAtsDisableEmail()) {
         label = toolkit.createLabel(workComp, "AtsDisableEmail");
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      }

      if (AtsPlugin.isAtsAlwaysEmailMe()) {
         label = toolkit.createLabel(workComp, "AtsAlwaysEmailMe");
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      }

      refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.statistic.WorkPageStatistic#refresh()
    */
   @Override
   public void refresh() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#dispose()
    */
   @Override
   public void dispose() {
   }
}
