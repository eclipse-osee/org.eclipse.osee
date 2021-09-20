/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XRadioButtonsBooleanTriState extends XRadioButtons {

   public static final Object WIDGET_ID = XRadioButtonsBooleanTriState.class.getSimpleName();
   protected List<BooleanState> states = Arrays.asList(BooleanState.Yes, BooleanState.No, BooleanState.UnSet);
   protected List<XRadioButton> buttons = new ArrayList<XRadioButton>();
   protected BooleanState selected = BooleanState.UnSet;

   public XRadioButtonsBooleanTriState(String displayLabel) {
      super(displayLabel);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      for (BooleanState state : states) {
         XRadioButton button = addButton(state.name());
         buttons.add(button);
         button.setUseLabelFont(false);
         button.setObject(state);
         button.setSelected(state.equals(selected));
         button.setObject(button);
         button.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               handleSelection(button);
            }

         });
      }
      super.createControls(parent, horizontalSpan);
   }

   protected void handleSelection(XRadioButton button) {
      selected = BooleanState.valueOf(button.getLabel());
   }

   public BooleanState getSelected() {
      return selected;
   }

   public void setSelected(BooleanState state) {
      for (XRadioButton button : buttons) {
         if (button.getLabel().equals(state.name())) {
            button.setSelected(true);
            selected = state;
         } else {
            button.setSelected(false);
         }
      }
      refresh();
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && selected.isUnSet()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must have at least one selection.");
      }
      return Status.OK_STATUS;
   }

   public static enum BooleanState {
      Yes,
      No,
      UnSet;

      public boolean isUnSet() {
         return this == BooleanState.UnSet;
      }

      public boolean isYes() {
         return this == BooleanState.Yes;
      }

      public boolean isNo() {
         return this == BooleanState.No;
      }
   }

}
