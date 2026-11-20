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
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.component.annotations.Component;

/**
 * Horizontal separator line spanning the available width; no label or value. Use XWidgetBuilder.andLineWidget() so
 * the line gets its own row.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXLineWidget extends XXLabelWidget {

   public static final WidgetId ID = WidgetId.XXLineWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXLineWidget() {
      this(ID, "");
   }

   public XXLineWidget(WidgetId widgetId, String label) {
      super(widgetId, label, "");
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   /**
    * Native separator in place of the label, so it follows the platform theme (light and dark).
    */
   @Override
   protected void createLabelWidget() {
      Label line = new Label(comp, SWT.SEPARATOR | SWT.HORIZONTAL);
      line.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      if (getToolkit() != null) {
         getToolkit().adapt(line, false, false);
      }
   }

}
