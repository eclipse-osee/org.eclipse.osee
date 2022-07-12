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
package org.eclipse.osee.ats.ide.workflow.chgtype;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredListDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeDialog extends FilteredListDialog<ChangeTypes> {

   private final Collection<IAtsTeamWorkflow> teamWfs;
   private boolean clearAllowed = true;
   private Composite container;
   private ChangeTypes initialChgType = null;

   public ChangeTypeDialog(Collection<IAtsTeamWorkflow> teamWfs, List<ChangeTypes> changeTypes) {
      super("Select Change Type", "Select Change Type", new ChangeLabelProvider());
      if (teamWfs != null) {
         this.teamWfs = teamWfs;
         setInput(getValues());
      } else {
         this.teamWfs = null;
         setInput(changeTypes);
      }
   }

   @Override
   protected Control createDialogArea(Composite container) {

      this.container = container;
      Control control = super.createDialogArea(container);

      if (initialChgType != null) {
         super.setSelection(Arrays.asList(initialChgType).toArray());
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

   private Collection<ChangeTypes> getValues() {

      Pair<Boolean, Collection<ChangeTypes>> pair =
         AtsApiService.get().getWorkItemService().hasSameChangeTypes(teamWfs);
      boolean sameChangeTypes = pair.getFirst();
      Collection<ChangeTypes> changeTypes = pair.getSecond();

      if (!sameChangeTypes) {
         AWorkbench.popup("Can not change Change Type for teams with different Change Types");
         return java.util.Collections.emptyList();
      }

      return changeTypes;
   }

   public static class ChangeLabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object arg0) {
         ChangeTypes type = (ChangeTypes) arg0;
         return ChangeTypeColumnUI.getImage(type);
      }

      @Override
      public String getText(Object arg0) {
         ChangeTypes type = (ChangeTypes) arg0;
         if (Strings.isValid(type.getDescription())) {
            return String.format("%s - %s", type.name(), type.getDescription());
         }
         return type.name();
      }

      @Override
      public void addListener(ILabelProviderListener arg0) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener arg0) {
         // do nothing
      }

   }

   public void setSelected(ChangeTypes changeType) {
      if (Widgets.isAccessible(container)) {
         super.setSelection(Arrays.asList(changeType).toArray());
      } else {
         initialChgType = changeType;
      }
   }
}
