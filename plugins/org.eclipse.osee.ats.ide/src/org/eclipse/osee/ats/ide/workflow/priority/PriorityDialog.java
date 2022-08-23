/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ats.ide.workflow.priority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredListDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class PriorityDialog extends FilteredListDialog<Priorities> {

   private final Collection<IAtsTeamWorkflow> teamWfs;
   private boolean clearAllowed = true;
   private Composite container;
   private Priorities initialPriority = null;

   public PriorityDialog(Collection<IAtsTeamWorkflow> teamWfs, List<Priorities> priorities) {
      super("Select Priority", "Select Priority", new PriorityLabelProvider());
      if (teamWfs != null) {
         this.teamWfs = teamWfs;
         setInput(getValues());
      } else {
         this.teamWfs = null;
         setInput(priorities);
      }
   }

   @Override
   protected Control createDialogArea(Composite container) {

      this.container = container;
      Control control = super.createDialogArea(container);

      if (initialPriority != null) {
         super.setSelection(Arrays.asList(initialPriority).toArray());
      }

      if (clearAllowed) {
         Composite composite = new Composite((Composite) control, SWT.None);
         composite.setLayout(new GridLayout());
         composite.setLayoutData(new GridData());

         final Button button = new Button(composite, SWT.PUSH);
         button.setText("Clear and Close");
         button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               setSelection(null);
               close();
            }
         });
      }

      return control;
   }

   public boolean isClearAllowedSelected() {
      if (!clearAllowed) {
         return false;
      } else {
         return clearAllowed;
      }
   }

   public String getSelectedOption() {
      if (clearAllowed || getSelected() == null) {
         return "";
      }
      return getSelected().getName();
   }

   public boolean isclearAllowed() {
      return clearAllowed;
   }

   public void setclearAllowed(boolean clearAllowed) {
      this.clearAllowed = clearAllowed;
   }

   private Collection<Priorities> getValues() {

      Pair<Boolean, Collection<Priorities>> pair = AtsApiService.get().getWorkItemService().hasSamePriorities(teamWfs);
      boolean samePriorities = pair.getFirst();
      Collection<Priorities> changeTypes = pair.getSecond();

      if (!samePriorities) {
         AWorkbench.popup("Can not change Priority for teams with different Priorities");
         return java.util.Collections.emptyList();
      }

      return changeTypes;
   }

   public void setSelected(Priorities priorities) {
      if (Widgets.isAccessible(container)) {
         super.setSelection(Arrays.asList(priorities).toArray());
      } else {
         initialPriority = priorities;
      }
   }
}
