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

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXButtonPushWidget extends XXLabelWidget {

   public static final WidgetId ID = WidgetId.XXButtonPushWidget;

   private Button button;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXButtonPushWidget() {
      this(ID, "");
   }

   public XXButtonPushWidget(WidgetId widgetId, String label) {
      super(widgetId, label, "");
      set(XOption.READ, XOption.BUTTON_PUSH);
      remove(XOption.VAL_SINGLE_SEL);
      set(XOption.VAL_NOT_APPLICABLE);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createLabelWidget() {
      if (getToolkit() != null) {
         button = getToolkit().createButton(comp, "", SWT.PUSH);
      } else {
         button = new Button(comp, SWT.PUSH);
      }
      GridData gd2 = new GridData(GridData.BEGINNING);
      button.setText(getLabel());
      if (Widgets.isValidSelection(getToolTip())) {
         button.setToolTipText(getToolTip());
      }
      button.setLayoutData(gd2);
      button.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            notifyXModifiedListeners();
         }
      });
   }

   @Override
   protected void createValueWidget() {
      // do nothing
   }

}
