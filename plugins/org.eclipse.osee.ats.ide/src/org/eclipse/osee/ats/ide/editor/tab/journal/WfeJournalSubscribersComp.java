/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.journal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class WfeJournalSubscribersComp extends Composite {

   private final static String LABEL = "Journal Subscriber(s):";
   Label valueLabel;
   private final IAtsWorkItem workItem;

   public WfeJournalSubscribersComp(Composite parent, int style, final IAtsWorkItem workItem, final boolean isEditable, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, false));
      GridLayout layout = new GridLayout(2, false);
      layout.marginLeft = 0;
      setLayout(layout);
      editor.getToolkit().adapt(this);

      if (!workItem.isCancelled() && !workItem.isCompleted()) {
         Hyperlink link = editor.getToolkit().createHyperlink(this, LABEL, SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

            @Override
            public void linkEntered(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkExited(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkActivated(HyperlinkEvent e) {
               try {
                  if (editor.isDirty()) {
                     editor.doSave(null);
                  }
                  if (!isEditable && !workItem.getStateMgr().getAssignees().contains(
                     AtsCoreUsers.UNASSIGNED_USER) && !workItem.getStateMgr().getAssignees().contains(
                        AtsApiService.get().getUserService().getCurrentUser())) {
                     AWorkbench.popup("ERROR", "You must be assigned to modify assignees.\nContact current Assignee.");
                     return;
                  }
                  promptChangeJournalSubscribers(workItem);
                  refresh();
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      } else {
         Label origLabel = editor.getToolkit().createLabel(this, LABEL);
         origLabel.setLayoutData(new GridData());
      }
      valueLabel = editor.getToolkit().createLabel(this, Widgets.NOT_SET);
      valueLabel.setLayoutData(new GridData());
      refresh();
   }

   public void refresh() {
      if (Widgets.isAccessible(valueLabel)) {
         String value = "";
         try {
            value = AtsObjects.toString("; ",
               AtsApiService.get().getNotificationService().getJournalSubscribedUsers(workItem));
            valueLabel.setToolTipText(value);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            value = ex.getLocalizedMessage();
            valueLabel.setToolTipText(value);
         }
         valueLabel.setText(Strings.truncate(value, 300, true));
         layout(true);
         getParent().layout(true);
      }
   }

   private boolean promptChangeJournalSubscribers(final IAtsWorkItem workItem) {
      if (workItem.isCompleted()) {
         AWorkbench.popup("ERROR",
            "Can't subscribe to completed " + workItem.getArtifactTypeName() + " (" + workItem.getAtsId() + ")");
         return false;
      } else if (workItem.isCancelled()) {
         AWorkbench.popup("ERROR",
            "Can't subscribe to cancelled " + workItem.getArtifactTypeName() + " (" + workItem.getAtsId() + ")");
         return false;
      }
      Set<AtsUser> users = new HashSet<>();
      users.addAll(AtsApiService.get().getUserService().getUsers(Active.Active));
      Collection<AtsUser> subscribedUsers =
         AtsApiService.get().getNotificationService().getJournalSubscribedUsers(workItem);
      users.addAll(subscribedUsers);

      // unassigned is not useful in the selection choice dialog
      users.remove(AtsCoreUsers.UNASSIGNED_USER);
      UserCheckTreeDialog uld = new UserCheckTreeDialog("Select Journal Subscribers",
         "Select to subscribe.\nDeSelect to un-subscribe.", users);
      uld.setIncludeAutoSelectButtons(false);

      IAtsTeamWorkflow parentWorklfow = workItem.getParentTeamWorkflow();
      if (parentWorklfow != null) {
         uld.setTeamMembers(
            AtsApiService.get().getTeamDefinitionService().getMembersAndLeads(parentWorklfow.getTeamDefinition()));
      }
      uld.setInitialSelections(subscribedUsers);

      if (uld.open() != 0) {
         return false;
      }
      Collection<AtsUser> selected = uld.getUsersSelected();
      AtsApiService.get().getNotificationService().setJournalSubscribedUsers(workItem, selected);

      return true;
   }

}
