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

package org.eclipse.osee.ats.ide.search.widget;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredListEnumDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class EntryEnumDialog extends EntryDialog {

   private final AttributeTypeToken attrType;
   private final AtsApi atsApi;

   public EntryEnumDialog(String dialogTitle, String dialogMessage, AttributeTypeToken attrType) {
      super(dialogTitle, dialogMessage);
      this.attrType = attrType;
      atsApi = AtsApiService.get();
   }

   @Override
   protected void createExtendedArea(Composite parent) {

      (new org.eclipse.swt.widgets.Label(parent, SWT.NONE)).setText(" ");

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayoutData(new GridData());
      comp.setLayout(ALayout.getZeroMarginLayout(2, false));

      final Button enumValues = new Button(comp, SWT.PUSH);
      enumValues.setText("Show Active Enum Values");
      enumValues.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Set<EnumToken> enums = new HashSet<>();
            AttributeTypeEnum<?> enumeratedType =
               (AttributeTypeEnum<?>) atsApi.tokenService().getAttributeType(attrType.getId());
            for (ArtifactTypeToken art : atsApi.tokenService().getArtifactTypes()) {
               enums.addAll(art.getValidEnumValues(enumeratedType));
            }
            FilteredListEnumDialog diag = new FilteredListEnumDialog("Select Enum", "Select Enum", enums);
            if (diag.open() == Window.OK) {
               EnumToken selected = diag.getSelected();
               setEntry(selected.getName());
            }
         }
      });
   }

}
