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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.osgi.service.component.annotations.Component;

/**
 * Specialized StyledText where in-line edit is disabled and can only edit through label hyperlink or double-click.
 * Tooltip is provided to notify user.<br/>
 * <br/>
 * Use-case is when editing needs to complete before persist.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXStringSTextHEditWidget extends XXAbstractStringStyledTextWidget {

   public static final WidgetId ID = WidgetId.XXStringSTextHEditWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXStringSTextHEditWidget() {
      this("", "");
   }

   public XXStringSTextHEditWidget(String label) {
      this(label, "");
   }

   public XXStringSTextHEditWidget(String label, String value) {
      this(ID, label, value);
   }

   public XXStringSTextHEditWidget(WidgetId widgetId, String label, String value) {
      super(widgetId, label, value);
      set(XOption.NO_INLINE_EDIT);
      set(XOption.VAL_SINGLE_TEXT);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createValueWidget() {
      super.createValueWidget();
      // Add convenience Alt-Left-Click
      valueSText.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            if ((e.stateMask & org.eclipse.swt.SWT.ALT) != 0 && e.button == 1) {
               boolean changed = handleSelection();
               if (changed) {
                  refresh();
                  notifyXModifiedListeners();
               }
            }
         }

      });

   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   protected String getSelectToModifyMsg() {
      return "Select to Modify or Alt-Left-Click";
   }

}
