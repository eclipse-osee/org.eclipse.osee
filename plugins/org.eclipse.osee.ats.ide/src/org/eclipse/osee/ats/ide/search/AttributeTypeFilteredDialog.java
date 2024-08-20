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
package org.eclipse.osee.ats.ide.search;

import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.ats.api.util.AttributeValues.AttrValueType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredListDialog;
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
public class AttributeTypeFilteredDialog extends FilteredListDialog<AttributeTypeToken> {

   private AttrValueType attrValueType = AttrValueType.Value;
   private AttributeTypeToken selectedType = AttributeTypeToken.SENTINEL;

   public AttributeTypeFilteredDialog(Collection<AttributeTypeToken> attributeTypes) {
      super("Select Attribute Type", "Select Attribute Type", new AttributeTypeLabelProvider());
      setInput(attributeTypes);
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control control = super.createDialogArea(container);
      Composite composite = new Composite((Composite) control, SWT.None);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData());

      final Button existsButton = new Button(composite, SWT.CHECK);
      existsButton.setText("Exists");
      existsButton.setToolTipText("Select to add Exists Attribute Type to query");
      existsButton.setSelection(false);
      existsButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Object[] selectedElements = getSelectedElements();
            if (selectedElements.length == 0) {
               AWorkbench.popup("Must Select an Attribute Type");
               return;
            }
            selectedType = (AttributeTypeToken) selectedElements[0];
            attrValueType = AttrValueType.AttrExists;
            close();
         }
      });

      final Button notExistsButton = new Button(composite, SWT.CHECK);
      notExistsButton.setText("Not Exists");
      notExistsButton.setToolTipText("Select to add Not Exists Attribute Type to query");
      notExistsButton.setSelection(false);
      notExistsButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Object[] selectedElements = getSelectedElements();
            if (selectedElements.length == 0) {
               AWorkbench.popup("Must Select an Attribute Type");
               return;
            }
            selectedType = (AttributeTypeToken) selectedElements[0];
            attrValueType = AttrValueType.AttrNotExists;
            close();
         }
      });

      final Button existsValue = new Button(composite, SWT.CHECK);
      existsValue.setText("Exists Value");
      existsValue.setToolTipText("Select to add Exists Value Attribute Type to query");
      existsValue.setSelection(false);
      existsValue.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Object[] selectedElements = getSelectedElements();
            if (selectedElements.length == 0) {
               AWorkbench.popup("Must Select an Attribute Type");
               return;
            }
            selectedType = (AttributeTypeToken) selectedElements[0];
            attrValueType = AttrValueType.AttrExistsValue;
            close();
         }
      });
      return control;
   }

   public static class AttributeTypeLabelProvider implements ILabelProvider {

      @Override
      public String getText(Object arg0) {
         AttributeTypeToken type = (AttributeTypeToken) arg0;
         if (Strings.isValid(type.getDescription())) {
            return String.format("%s - %s", type.getName(), type.getDescription());
         }
         return type.getName();
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

      @Override
      public Image getImage(Object element) {
         return null;
      }

   }

   public AttributeTypeToken getSelectedType() {
      if (selectedType.isInvalid()) {
         return getSelected();
      }
      return selectedType;
   }

   public final AttrValueType getAttrValueType() {
      return attrValueType;
   }

   public final void setAttrValueType(AttrValueType attrValueType) {
      this.attrValueType = attrValueType;
   }

   public boolean isExistsValue() {
      return attrValueType == AttrValueType.AttrExistsValue;
   }

   public boolean isNonExists() {
      return attrValueType == AttrValueType.AttrNotExists;
   }

   public boolean isExists() {
      return attrValueType == AttrValueType.AttrExists;
   }

   public boolean isValue() {
      return attrValueType == AttrValueType.Value;
   }

}
