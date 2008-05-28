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
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.StringViewerSorter;
import org.eclipse.osee.framework.ui.skynet.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class EnumSelectionDialog extends CheckedTreeSelectionDialog {

   private XRadioButton addSelectedRadioButton =
         new XRadioButton("Add selected item(s) to existing if not already chosen.");
   private XRadioButton replaceAllRadioButton = new XRadioButton("Replace all existing with selected item(s)");
   private XRadioButton deleteSelectedRadioButton = new XRadioButton("Remove selected item(s) if already chosen.");
   public static enum Selection {
      AddSelection, ReplaceAll, DeleteSelected
   };
   private Selection selected = Selection.AddSelection;

   /**
    * @param parent
    * @param artifacts
    */
   public EnumSelectionDialog(String attributeName, Collection<? extends Artifact> artifacts) {
      super(Display.getCurrent().getActiveShell(), new StringLabelProvider(), new ArrayTreeContentProvider());
      setTitle("Select Options");
      setMessage("Select option(s) to add, delete or replace.");
      Set<String> options;
      try {
         options =
               ConfigurationPersistenceManager.getValidEnumerationAttributeValues(attributeName,
                     artifacts.iterator().next().getBranch());
      } catch (Exception ex) {
         options = new HashSet<String>();
         options.add(ex.getLocalizedMessage());
      }
      setInput(options);
      setComparator(new StringViewerSorter());
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));

      addSelectedRadioButton.createWidgets(comp, 2);
      addSelectedRadioButton.setSelected(true);
      addSelectedRadioButton.addSelectionListener(new SelectionAdapter() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            if (addSelectedRadioButton.isSelected()) selected = Selection.AddSelection;
         }
      });

      replaceAllRadioButton.createWidgets(comp, 2);
      replaceAllRadioButton.addSelectionListener(new SelectionAdapter() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            if (replaceAllRadioButton.isSelected()) selected = Selection.ReplaceAll;
         }
      });

      deleteSelectedRadioButton.createWidgets(comp, 2);
      deleteSelectedRadioButton.addSelectionListener(new SelectionAdapter() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            if (deleteSelectedRadioButton.isSelected()) selected = Selection.DeleteSelected;
         }
      });
      return c;
   }

   public Selection getSelected() {
      return selected;
   }

}
