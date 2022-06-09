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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
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

   private boolean nonExists = false;
   private AttributeTypeToken selectedElement = AttributeTypeToken.SENTINEL;
   private final AtsApi atsApi;

   public AttributeTypeFilteredDialog(Collection<AttributeTypeToken> attributeTypes) {
      super("Select Attribute Type", "Select Attribute Type", new AttributeTypeLabelProvider());
      setInput(attributeTypes);
      atsApi = AtsApiService.get();
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control control = super.createDialogArea(container);

      Composite composite = new Composite((Composite) control, SWT.None);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData());

      final Button notExistsButton = new Button(composite, SWT.PUSH);
      notExistsButton.setText("\"Not Exists\" and Close");
      notExistsButton.setToolTipText("Select to add Not Exists Attribute Type to query");
      notExistsButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Object[] selectedElements = getSelectedElements();
            if (selectedElements.length == 0) {
               AWorkbench.popup("Must Select at Attribute Type");
               return;
            }
            selectedElement = (AttributeTypeToken) selectedElements[0];
            nonExists = true;
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

   public boolean isNonExists() {
      return nonExists;
   }

   public AttributeTypeToken getSelectedElement() {
      return selectedElement;
   }
}
