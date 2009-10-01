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
package org.eclipse.osee.ats.editor.widget;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class CurrentAssigneesXWidget extends XHyperlinkLabelCmdValueSelection {

   private final SMAManager smaMgr;
   private final boolean isEditable;

   public CurrentAssigneesXWidget(IManagedForm managedForm, final SMAManager smaMgr, Composite composite, int horizontalSpan, XModifiedListener xModListener, boolean isEditable) throws OseeStateException {
      super("Assignee(s)");
      this.smaMgr = smaMgr;
      this.isEditable = isEditable;
      if (xModListener != null) {
         addXModifiedListener(xModListener);
      }
      setEditable(!smaMgr.getSma().isReadOnly());
      super.createWidgets(managedForm, composite, horizontalSpan);
   }

   private void handleChangeCurrentAssignees() throws OseeCoreException {
      if (!isEditable && !smaMgr.getStateMgr().getAssignees().contains(UserManager.getUser(SystemUser.UnAssigned)) && !smaMgr.getStateMgr().getAssignees().contains(
            UserManager.getUser())) {
         AWorkbench.popup(
               "ERROR",
               "You must be assigned to modify assignees.\nContact current Assignee or Select Priviledged Edit for Authorized Overriders.");
         return;
      }
      if (smaMgr.promptChangeAssignees(false)) {
         refresh();
         smaMgr.getEditor().onDirtied();
      }
   }

   @Override
   public String getHyperlinkLabelString() {
      return "<edit>";
   }

   @Override
   public boolean handleSelection() {
      try {
         handleChangeCurrentAssignees();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }

   @Override
   public String getCurrentValue() {
      try {
         if (smaMgr.getStateMgr().getAssignees().size() == 0) {
            setErrorState(true);
            return "Error: State has no assignees";
         } else {
            setToolTip(smaMgr.getStateMgr().getAssigneesStr());
            setErrorState(false);
            return smaMgr.getStateMgr().getAssigneesStr(80);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return ex.getLocalizedMessage();
      }
   }

}
