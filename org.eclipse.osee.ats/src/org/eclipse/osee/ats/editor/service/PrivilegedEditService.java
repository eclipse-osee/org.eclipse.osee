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
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class PrivilegedEditService extends WorkPageService {

   private Hyperlink link;

   public PrivilegedEditService(SMAManager smaMgr) {
      super(smaMgr);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isShowSidebarService(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isShowSidebarService(AtsWorkPage page) {
      return isCurrentState(page);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createSidebarService(org.eclipse.swt.widgets.Group, org.eclipse.osee.ats.workflow.AtsWorkPage, org.eclipse.osee.framework.ui.skynet.XFormToolkit, org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   @Override
   public void createSidebarService(Group workGroup, AtsWorkPage page, XFormToolkit toolkit, SMAWorkFlowSection section) {
      link = toolkit.createHyperlink(workGroup, getName(), SWT.NONE);
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
                  if (smaMgr.getEditor().getPriviledgedEditMode() != SMAEditor.PriviledgedEditMode.Off) {
                     if (MessageDialog.openQuestion(
                           Display.getCurrent().getActiveShell(),
                           "Diable Privileged Edit",
                           "Privileged Edit Mode Enabled.\n\nDisable?\n\nNote: (changes will be saved)")) {
                        smaMgr.getEditor().setPriviledgedEditMode(
                              SMAEditor.PriviledgedEditMode.Off);
                     }
                  } else {
                     Set<User> users = smaMgr.getPrivilegedUsers();
                     if (AtsPlugin.isAtsAdmin()) users.add(SkynetAuthentication.getUser());
                     StringBuffer sb = new StringBuffer();
                     for (User user : users)
                        sb.append(user.getName() + "\n");
                     String buttons[];
                     boolean iAmPrivileged =
                           users.contains(SkynetAuthentication.getUser());
                     if (iAmPrivileged)
                        buttons = new String[] {"Override and Edit", "Cancel"};
                     else
                        buttons = new String[] {"Cancel"};
                     MessageDialog ed =
                           new MessageDialog(
                                 Display.getCurrent().getActiveShell(),
                                 "Privileged Edit",
                                 null,
                                 "The following users have the ability to edit this " + smaMgr.getSma().getArtifactTypeName() + " in case of emergency.\n\n" + sb.toString(),
                                 MessageDialog.QUESTION, buttons, 0);
                     int result = ed.open();
                     if (iAmPrivileged && result == 0) smaMgr.getEditor().setPriviledgedEditMode(
                           SMAEditor.PriviledgedEditMode.Global);
                  }

               } catch (SQLException ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
               } catch (OseeCoreException ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
               }
            }
         });
      refresh();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getSidebarCategory()
    */
   @Override
   public String getSidebarCategory() {
      return ServicesArea.OPERATION_CATEGORY;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.operation.WorkPageService#refresh()
    */
   @Override
   public void refresh() {
      if (link == null || link.isDisposed()) return;
      if (smaMgr.getEditor().getPriviledgedEditMode() != SMAEditor.PriviledgedEditMode.Off)
         link.setText("Privileged Edit Enabled - " + smaMgr.getEditor().getPriviledgedEditMode().name());
      else
         link.setText("Privileged Edit");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Privileged Edit";
   }
}
