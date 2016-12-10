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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButton;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class EnumSelectionDialog extends FilteredCheckboxTreeDialog {

   private final XRadioButton addSelectedRadioButton =
      new XRadioButton("Add selected item(s) to existing if not already chosen.");
   private final XRadioButton replaceAllRadioButton = new XRadioButton("Replace all existing with selected item(s)");
   private final XRadioButton deleteSelectedRadioButton =
      new XRadioButton("Remove selected item(s) if already chosen.");
   private final XRadioButton removeAll = new XRadioButton("Remove all items.");
   private boolean isSingletonAttribute = false;
   private boolean isRemoveAllAllowed = true;
   public static enum Selection {
      AddSelection,
      ReplaceAll,
      DeleteSelected,
      RemoveAll
   };
   private Selection selected = Selection.AddSelection;

   public EnumSelectionDialog(AttributeTypeId attributeType, Collection<? extends Artifact> artifacts) {
      super("Select Options" + (isSingletonAttribute(attributeType) ? " - (Singleton)" : ""),
         "Select option(s) to add, delete or replace.", new ArrayTreeContentProvider(), new StringLabelProvider(),
         new StringViewerSorter());
      Set<String> options;
      try {
         options = AttributeTypeManager.getEnumerationValues(attributeType);
         isSingletonAttribute =
            isRemoveAllAllowed = AttributeTypeManager.checkIfRemovalAllowed(attributeType, artifacts);
         if (isSingletonAttribute) {
            selected = Selection.ReplaceAll;
         }
      } catch (OseeCoreException ex) {
         options = new HashSet<>();
         options.add(ex.getLocalizedMessage());
      }

      setInput(options);
   }

   private static boolean isSingletonAttribute(AttributeTypeId attributeType) {
      return AttributeTypeManager.getMaxOccurrences(attributeType) == 1;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));

      if (!isSingletonAttribute) {
         addSelectedRadioButton.createWidgets(comp, 2);
         addSelectedRadioButton.setSelected(selected == Selection.AddSelection);
         addSelectedRadioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               if (addSelectedRadioButton.isSelected()) {
                  selected = Selection.AddSelection;
               }
            }
         });
      }

      replaceAllRadioButton.createWidgets(comp, 2);
      replaceAllRadioButton.setSelected(selected == Selection.ReplaceAll);
      replaceAllRadioButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            if (replaceAllRadioButton.isSelected()) {
               selected = Selection.ReplaceAll;
            }
         }
      });

      if (!isSingletonAttribute) {
         deleteSelectedRadioButton.createWidgets(comp, 2);
         deleteSelectedRadioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               if (deleteSelectedRadioButton.isSelected()) {
                  selected = Selection.DeleteSelected;
               }
            }
         });
      } else if (isRemoveAllAllowed) {
         removeAll.createWidgets(comp, 2);
         removeAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               if (removeAll.isSelected()) {
                  selected = Selection.RemoveAll;
               }
            }
         });

      }
      return c;
   }

   public Selection getSelected() {
      return selected;
   }

}
