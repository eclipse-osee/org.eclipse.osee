/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.user.UserGroupAuthorization;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXCheckBoxWidget extends XXAbstractBaseXWidget<Boolean> {

   public static final WidgetId ID = WidgetId.XXCheckBoxWidget;

   protected Button checkButton;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXCheckBoxWidget() {
      this(ID, "");
   }

   public XXCheckBoxWidget(String label) {
      this(ID, label);
   }

   public XXCheckBoxWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.LABEL_AFTER, XOption.VAL_SINGLE_SEL, XOption.NO_FILL, XOption.NO_LABEL_BOLD, XOption.WRITE);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (!isVerticalLabel() && horizontalSpan < 2) {
         horizontalSpan = 2;
      }

      boolean labelAfter = is(XOption.LABEL_AFTER);
      if (isFillVertically()) {
         comp = new Composite(parent, SWT.NONE);
         GridLayout layout = ALayout.getZeroMarginLayout(1, false);
         comp.setLayout(layout);
         comp.setLayoutData(new GridData());
      } else {
         comp = new Composite(parent, SWT.NONE);
         GridLayout layout = ALayout.getZeroMarginLayout(horizontalSpan, false);
         comp.setLayout(layout);
         GridData gd = new GridData();
         gd.horizontalSpan = horizontalSpan;
         comp.setLayoutData(gd);
      }
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }

      // Create Text Widgets
      if (!labelAfter) {
         String label = getLabel() + ":";
         if (getToolkit() == null) {
            labelWidget = new Label(comp, SWT.NONE);
            labelWidget.setText(label);
         } else {
            labelWidget = getToolkit().createLabel(comp, label, SWT.NONE);
         }
      }

      checkButton = new Button(comp, SWT.CHECK);
      GridData gd2 = new GridData(GridData.BEGINNING);
      checkButton.setLayoutData(gd2);

      // set checked before adding listener
      boolean initialSelected = false;
      if (isAttributeStorage()) {
         initialSelected = getSelectedFirst();
      } else {
         if (widData.getDefaultValue() instanceof Boolean) {
            initialSelected = (Boolean) widData.getDefaultValue();
         }
      }
      setSelected(initialSelected, false);

      checkButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent event) {
            handleSelection();
         }

      });
      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      gd.horizontalSpan = horizontalSpan - 1;

      if (labelAfter) {
         String label = getLabel();
         if (getToolkit() == null) {
            labelWidget = new Label(comp, SWT.NONE);
            labelWidget.setText(label);
         } else {
            labelWidget = getToolkit().createLabel(comp, label, SWT.NONE);
         }
      }
      if (getToolTip() != null) {
         labelWidget.setToolTipText(getToolTip());
      }

      checkButton.setLayoutData(gd);
      refresh();
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Boolean getSentinel() {
      return false;
   }

   @Override
   protected boolean handleSelection() {
      boolean selected = checkButton.getSelection();

      IUserGroupArtifactToken userGroup = widData.getUserGroup();
      if (userGroup.isValid()) {
         XResultData rd = UserGroupAuthorization.check(userGroup, getLabel(), new XResultData());
         if (rd.isErrors()) {
            AWorkbench.popup(getLabel() + " - Error", rd.toString());
            checkButton.setSelection(!selected);
            return false;
         }
      }

      setSelected(selected, false);
      handleSelectedPersist();
      refresh();
      return true;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   /**
    * Synchronizes the SWT checkbox from the selected model value. This widget creates {@link #checkButton} instead of
    * the base value Text control, so the base refresh cannot display a builder default, restored search value, or
    * programmatic {@code setSelected(...)} change unless this method updates the Button explicitly.
    */
   @Override
   public void refresh() {
      if (Widgets.isAccessible(checkButton)) {
         boolean selected = getSelectedFirst();
         if (checkButton.getSelection() != selected) {
            checkButton.setSelection(selected);
         }
      }
      super.refresh();
   }

   public boolean isChecked() {
      return getSelectedFirst();
   }

   public Button getCheckButton() {
      return checkButton;
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      if (Widgets.isAccessible(checkButton)) {
         checkButton.addSelectionListener(selectionListener);
      }
   }

}
