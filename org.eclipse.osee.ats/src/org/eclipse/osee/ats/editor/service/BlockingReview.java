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

import java.sql.SQLException;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class BlockingReview extends WorkPageService {

   private Label label;

   public BlockingReview(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Blocking Review", smaMgr, page, toolkit, section, ServicesArea.STATISTIC_CATEGORY, Location.Global);
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
      if (!(smaMgr.getSma() instanceof ReviewSMArtifact)) return;
      try {
         if (smaMgr.getSma().getParentSMA() == null) {
            StringBuffer sb = new StringBuffer("Stand-alone Review");
            for (ActionableItemArtifact aia : ((ReviewSMArtifact) smaMgr.getSma()).getActionableItemsDam().getActionableItems())
               sb.append("\n - " + aia.getDescriptiveName());
            toolkit.createLabel(workComp, name, SWT.None).setText(sb.toString());
         } else
            label = toolkit.createLabel(workComp, "");
      } catch (SQLException ex) {
         return;
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
      try {
         if (label != null && !label.isDisposed()) {
            boolean blocking = ((ReviewSMArtifact) smaMgr.getSma()).isBlocking();
            label.setText(blocking ? "Blocking Review" : "Non-Blocking Review");
            label.setForeground(blocking ? Display.getCurrent().getSystemColor(SWT.COLOR_RED) : null);
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
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
