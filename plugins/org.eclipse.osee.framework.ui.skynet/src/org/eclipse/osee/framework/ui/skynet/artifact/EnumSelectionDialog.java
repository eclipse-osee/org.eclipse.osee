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

package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeMultiplicitySelectionOption;
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
public class EnumSelectionDialog extends FilteredCheckboxTreeDialog<String> {

   private final XRadioButton addSelectedRadioButton =
      new XRadioButton("Add selected item(s) to existing if not already chosen.");
   private final XRadioButton replaceAllRadioButton = new XRadioButton("Replace all existing with selected item(s)");
   private final XRadioButton deleteSelectedRadioButton =
      new XRadioButton("Remove selected item(s) if already chosen.");
   private final XRadioButton removeAll = new XRadioButton("Remove all items.");
   private Set<AttributeMultiplicitySelectionOption> selectionOptions;
   private HashMap<AttributeMultiplicitySelectionOption, Boolean> optionMap;
   private boolean isRemovalAllowedAttr;
   private AttributeMultiplicitySelectionOption selected = AttributeMultiplicitySelectionOption.AddSelection;
   private final AttributeTypeEnum<?> attributeType;
   private final Collection<? extends Artifact> artifacts;

   public EnumSelectionDialog(AttributeTypeEnum<?> attributeType, Collection<? extends Artifact> artifacts) {
      super("Select Options" + (isSingletonAttribute(attributeType, artifacts) ? " - (Singleton)" : ""),
         "Select option(s) to add, delete or replace.", new ArrayTreeContentProvider(), new StringLabelProvider(),
         new StringViewerSorter());
      this.attributeType = attributeType;
      this.artifacts = artifacts;
      Set<String> options;
      try {
         options = attributeType.getEnumStrValues();
         getOptions(attributeType, artifacts);
         selectionOptions = getSelectionOptions();
         if (isSingletonAttribute(attributeType, artifacts)) {
            selected = AttributeMultiplicitySelectionOption.ReplaceAll;
         }
      } catch (OseeCoreException ex) {
         options = new HashSet<>();
         options.add(ex.getLocalizedMessage());
      }

      setInput(options);
   }

   public static boolean isSingletonAttribute(AttributeTypeToken attributeType, Collection<? extends Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         if (artifact.getArtifactType().getMax(attributeType) != 1) {
            return false;
         }
      }
      return true;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));

      if (selectionOptions.contains(AttributeMultiplicitySelectionOption.AddSelection)) {
         addSelectedRadioButton.createWidgets(comp, 2);
         addSelectedRadioButton.setSelected(selected == AttributeMultiplicitySelectionOption.AddSelection);
         addSelectedRadioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               if (addSelectedRadioButton.isSelected()) {
                  selected = AttributeMultiplicitySelectionOption.AddSelection;
               }
            }
         });
      }

      if (selectionOptions.contains(AttributeMultiplicitySelectionOption.ReplaceAll)) {
         replaceAllRadioButton.createWidgets(comp, 2);
         replaceAllRadioButton.setSelected(selected == AttributeMultiplicitySelectionOption.ReplaceAll);
         replaceAllRadioButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               if (replaceAllRadioButton.isSelected()) {
                  selected = AttributeMultiplicitySelectionOption.ReplaceAll;
               }
            }
         });
      }

      if (selectionOptions.contains(AttributeMultiplicitySelectionOption.DeleteSelected)) {
         deleteSelectedRadioButton.createWidgets(comp, 2);
         deleteSelectedRadioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               if (deleteSelectedRadioButton.isSelected()) {
                  selected = AttributeMultiplicitySelectionOption.DeleteSelected;
               }
            }
         });
      }

      if (selectionOptions.contains(AttributeMultiplicitySelectionOption.RemoveAll)) {
         removeAll.createWidgets(comp, 2);
         removeAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               if (removeAll.isSelected()) {
                  selected = AttributeMultiplicitySelectionOption.RemoveAll;
               }
            }
         });

      }
      return c;
   }

   public AttributeMultiplicitySelectionOption getSelected() {
      return selected;
   }

   public Set<AttributeMultiplicitySelectionOption> getSelectionOptions() {
      Set<AttributeMultiplicitySelectionOption> selectionOptions = new HashSet<>();
      if (!isSingletonAttribute(attributeType,
         artifacts) && optionMap.get(AttributeMultiplicitySelectionOption.AddSelection).equals(true)) {
         selectionOptions.add(AttributeMultiplicitySelectionOption.AddSelection);
      }
      if (!isSingletonAttribute(attributeType,
         artifacts) && optionMap.get(AttributeMultiplicitySelectionOption.DeleteSelected).equals(true)) {
         selectionOptions.add(AttributeMultiplicitySelectionOption.DeleteSelected);
      }
      if (optionMap.get(AttributeMultiplicitySelectionOption.ReplaceAll).equals(true)) {
         selectionOptions.add(AttributeMultiplicitySelectionOption.ReplaceAll);
      }
      if (isRemovalAllowed() && optionMap.get(AttributeMultiplicitySelectionOption.RemoveAll).equals(true)) {
         selectionOptions.add(AttributeMultiplicitySelectionOption.RemoveAll);
      }
      return selectionOptions;
   }

   public boolean isRemovalAllowed() {
      if (artifacts.isEmpty()) {
         return isRemovalAllowedAttr;
      }
      boolean canRemoved = true;
      for (Artifact art : artifacts) {
         canRemoved = art.isAttributeTypeValid(attributeType);
         if (!canRemoved) {
            break;
         }
      }
      return isRemovalAllowedAttr && canRemoved;
   }

   public void getOptions(AttributeTypeToken attributeType, Collection<? extends Artifact> artifacts) {
      optionMap = AttributeMultiplicitySelectionOption.getOptionMap();
      for (Artifact artifact : artifacts) {
         Multiplicity multiplicity = artifact.getArtifactType().getMultiplicity(attributeType);
         if (multiplicity.equals(Multiplicity.ZERO_OR_ONE)) {
            isRemovalAllowedAttr = true;
            optionMap.put(AttributeMultiplicitySelectionOption.AddSelection, true);
            break;
         } else if (multiplicity.equals(Multiplicity.ANY)) {
            isRemovalAllowedAttr = true;
            for (AttributeMultiplicitySelectionOption key : optionMap.keySet()) {
               optionMap.put(key, true);
            }
            break;
         } else if (multiplicity.equals(Multiplicity.EXACTLY_ONE)) {
            isRemovalAllowedAttr = false;
            optionMap.put(AttributeMultiplicitySelectionOption.ReplaceAll, true);
            break;
         } else if (multiplicity.equals(Multiplicity.AT_LEAST_ONE)) {
            isRemovalAllowedAttr = false;
            optionMap.put(AttributeMultiplicitySelectionOption.AddSelection, true);
            optionMap.put(AttributeMultiplicitySelectionOption.ReplaceAll, true);
            break;
         }
      }
   }

}
