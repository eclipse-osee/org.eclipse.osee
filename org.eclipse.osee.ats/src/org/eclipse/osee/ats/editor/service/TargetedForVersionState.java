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
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class TargetedForVersionState extends WorkPageService {

   private Hyperlink link;
   private Label label;

   public TargetedForVersionState(SMAManager smaMgr, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      super("Target Version", smaMgr, page, toolkit, section, ServicesArea.STATISTIC_CATEGORY, Location.CurrentState);
   }

   @Override
   public boolean displayService() {
      return (smaMgr.isTeamUsesVersions());
   }

   @Override
   public void create(Group workComp) {
      if (!smaMgr.isReleased()) {
         link = toolkit.createHyperlink(workComp, "", SWT.NONE);
         if (smaMgr.getSma().isReadOnly())
            link.addHyperlinkListener(readOnlyHyperlinkListener);
         else
            link.addHyperlinkListener(new IHyperlinkListener() {

               public void linkEntered(HyperlinkEvent e) {
               }

               public void linkExited(HyperlinkEvent e) {
               }

               public void linkActivated(HyperlinkEvent e) {
                  try {
                     if (smaMgr.promptChangeVersion(false)) {
                        refresh();
                        section.refreshStateServices();
                     }
                  } catch (SQLException ex) {
                     OSEELog.logException(AtsPlugin.class, ex, true);
                  }
               }
            });
      } else
         label = toolkit.createLabel(workComp, "", SWT.NONE);
      refresh();
   }

   @Override
   public void refresh() {
      try {
         String str = "Target Version: ";
         if (smaMgr.getTargetedForVersion() != null) str += smaMgr.getTargetedForVersion().getDescriptiveName();
         if (link != null && !link.isDisposed())
            link.setText(str);
         else if (label != null && !label.isDisposed()) label.setText(str);
      } catch (SQLException ex) {
         // Do Nothing
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
