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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredListDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class EnumSingletonSelectionDialog extends FilteredListDialog<EnumToken> {

   private boolean removeAllAllowed = true;
   private boolean removeAllSelected = false;

   public EnumSingletonSelectionDialog(AttributeTypeToken attributeType, Collection<? extends Artifact> artifacts) {
      super("Select Option", "Select Option");
      Artifact artifact = artifacts.iterator().next();
      ArtifactTypeToken artType = artifact.getArtifactType();
      @SuppressWarnings("unchecked")
      List<EnumToken> validEnumValues = artType.getValidEnumValues((AttributeTypeEnum<EnumToken>) attributeType);
      removeAllAllowed = AttributeTypeManager.checkIfRemovalAllowed(attributeType, artifacts);
      List<EnumToken> optsList = new ArrayList<>();
      optsList.addAll(validEnumValues);
      Collections.sort(validEnumValues);
      setInput(optsList);
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control control = null;
      if (removeAllAllowed) {
         Composite composite = new Composite(container, SWT.None);
         composite.setLayout(new GridLayout(1, false));
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         final Button button = new Button(composite, SWT.PUSH);
         button.setText("Remove All and Close");
         button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               removeAllSelected = true;
               close();
            }
         });
         super.createDialogArea(composite);
         control = composite;
      } else {
         control = super.createDialogArea(container);
      }
      return control;
   }

   public boolean isRemoveAllSelected() {
      if (!removeAllAllowed) {
         return false;
      } else {
         return removeAllSelected;
      }
   }

   public String getSelectedOption() {
      if (removeAllSelected || getSelected() != null) {
         return "";
      }
      return getSelected().getName();
   }

   public boolean isRemoveAllAllowed() {
      return removeAllAllowed;
   }

   public void setRemoveAllAllowed(boolean removeAllAllowed) {
      this.removeAllAllowed = removeAllAllowed;
   }
}
