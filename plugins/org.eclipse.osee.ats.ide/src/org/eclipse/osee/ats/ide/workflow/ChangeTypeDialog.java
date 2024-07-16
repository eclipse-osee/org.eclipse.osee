/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.workflow;

import java.util.ArrayList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.workflow.IAtsDatabaseTypeProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeDialog extends ListDialog {

   ChangeType selected = null;

   public ChangeTypeDialog(Shell parent) {
      super(parent);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new ChangeLabelProvider());
      setInput(getValues());
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle("Select Change Type");
   }

   public ChangeType getSelection() {
      return (ChangeType) getResult()[0];
   }

   private ChangeType[] getValues() {
      for (IAtsDatabaseTypeProvider provider : AtsApiService.get().getDatabaseTypeProviders()) {
         if (provider.useFactory()) {
            if (provider.getChangeTypeValues() != null) {
               return provider.getChangeTypeValues().toArray(new ChangeType[provider.getChangeTypeValues().size()]);
            }
         }
      }
      return ChangeType.values();
   }

   @Override
   protected Control createDialogArea(Composite container) {

      new Label(container, SWT.NONE).setText("     Select Change Type:");

      Control c = super.createDialogArea(container);
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 80;
      gd.widthHint = 300;
      getTableViewer().getTable().setLayoutData(gd);
      GridLayout layout = ALayout.getZeroMarginLayout();
      layout.marginWidth = 20;
      getTableViewer().getTable().getParent().setLayout(layout);
      if (selected != null) {
         ArrayList<Object> sel = new ArrayList<>();
         sel.add(selected);
         getTableViewer().setSelection(new StructuredSelection(sel.toArray(new Object[sel.size()])));
         getTableViewer().getTable().setFocus();
      }
      return c;
   }

   @Override
   protected void okPressed() {
      if (getTableViewer().getSelection().isEmpty()) {
         AWorkbench.popup("ERROR", "Must make selection.");
         return;
      }
      super.okPressed();
   }

   public class ChangeLabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object arg0) {
         ChangeType type = (ChangeType) arg0;
         return ChangeTypeToSwtImage.getImage(type);
      }

      @Override
      public String getText(Object arg0) {
         ChangeType type = (ChangeType) arg0;
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

   public void setSelected(ChangeType selected) {
      this.selected = selected;
   }

}
