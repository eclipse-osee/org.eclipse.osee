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
package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.ArrayList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsPriority;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.HyperLinkLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class AtsPriorityDialog extends ListDialog {

   PriorityType selected = null;

   public AtsPriorityDialog(Shell parent) {
      super(parent);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new PriorityLabelProvider());
      setInput(AtsPriority.PriorityType.values());
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle("Select Priority");
      setMessage("Select Priority - Click for Help");
   }

   public AtsPriority.PriorityType getSelection() {
      return (AtsPriority.PriorityType) getResult()[0];
   }

   @Override
   protected Label createMessageArea(Composite composite) {
      Label label = super.createMessageArea(composite);
      label.addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(Event event) {
            AtsPriority.openHelp();
         }
      });
      HyperLinkLabel.adapt(label);
      return label;
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control c = super.createDialogArea(container);
      if (selected != null) {
         ArrayList<Object> sel = new ArrayList<Object>();
         sel.add(selected);
         getTableViewer().setSelection(new StructuredSelection(sel.toArray(new Object[sel.size()])));
         getTableViewer().getTable().setFocus();
      }
      AtsPlugin.getInstance().setHelp(getTableViewer().getControl(), AtsPriority.PRIORITY_HELP_CONTEXT_ID,
            "org.eclipse.osee.ats.help.ui");
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

   public static class PriorityLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         PriorityType type = (PriorityType) arg0;
         if (type == PriorityType.None)
            return type.name();
         return type.getShortName();
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }

   }

   public void setSelected(PriorityType selected) {
      this.selected = selected;
   }

}
