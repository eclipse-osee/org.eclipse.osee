/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class FilteredTreeArtifactDialog extends FilteredTreeDialog {

   private Collection<? extends Artifact> selectable;
   private XCheckBox checkbox;
   private String checkBoxLabel;
   private boolean defaultChecked;
   private boolean checked;
   private XText text;
   private String textLabel;
   private String enteredText;

   public FilteredTreeArtifactDialog(String title, String message, Collection<? extends Artifact> selectable, ILabelProvider labelProvider) {
      this(title, message, selectable, new ArrayTreeContentProvider(), labelProvider);
   }

   public FilteredTreeArtifactDialog(String title, String message, Collection<? extends Artifact> selectable, ITreeContentProvider contentProvider, ILabelProvider labelProvider) {
      this(title, message, selectable, contentProvider, labelProvider, new ArtifactViewerSorter());
   }

   public FilteredTreeArtifactDialog(String title, String message, Collection<? extends Artifact> selectable, ITreeContentProvider contentProvider, ILabelProvider labelProvider, ViewerComparator comparator) {
      super(title, message, contentProvider, labelProvider, comparator);
      this.selectable = selectable;
   }

   public FilteredTreeArtifactDialog(String title, Collection<? extends Artifact> selectable) {
      this(title, title, selectable, new ArtifactLabelProvider());
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setInput(selectable);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      if (Strings.isValid(checkBoxLabel)) {
         Composite comp1 = new Composite(container, SWT.NONE);
         comp1.setLayout(new GridLayout(2, false));
         comp1.setLayoutData(new GridData(GridData.FILL_BOTH));

         checkbox = new XCheckBox(checkBoxLabel);
         checkbox.createWidgets(comp1, 2);
         checkbox.set(defaultChecked);
         checkbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               checked = checkbox.isChecked();
            };
         });
      }

      if (Strings.isValid(textLabel)) {
         Composite comp1 = new Composite(container, SWT.NONE);
         comp1.setLayout(new GridLayout(1, false));
         comp1.setLayoutData(new GridData(GridData.FILL_BOTH));

         text = new XText(textLabel);
         text.setFillVertically(true);
         text.createWidgets(comp1, 2);
         if (Strings.isValid(enteredText)) {
            text.set(enteredText);
         }
         text.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               enteredText = text.get();
            }
         });
      }

      return comp;
   }

   @Override
   protected Result isComplete() {
      return super.isComplete();
   }

   public void setSelectable(Collection<Artifact> selectable) {
      this.selectable = selectable;
   }

   @Override
   public void setComparator(ViewerComparator comparator) {
      getTreeViewer().getViewer().setComparator(comparator);
   }

   public void addCheckbox(String label, boolean defaultChecked) {
      checkBoxLabel = label;
      this.defaultChecked = defaultChecked;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Artifact getSelectedFirst() {
      return (Artifact) super.getSelectedFirst();
   }

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   public void addTextBox(String label) {
      textLabel = label;
   }

   public String getEnteredText() {
      return enteredText;
   }

   public void setEnteredText(String enteredText) {
      this.enteredText = enteredText;
   }

}
