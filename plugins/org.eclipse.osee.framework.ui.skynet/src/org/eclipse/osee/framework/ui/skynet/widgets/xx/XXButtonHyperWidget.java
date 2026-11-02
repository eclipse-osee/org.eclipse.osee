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
import org.osgi.service.component.annotations.Component;

/**
 * A clickable hyperlink-label action widget (BUTTON_HYPER) that may also display a value (e.g. sign-off widgets, the
 * header ID-by-title widget). Extends XXLabelValueWidget so it can render a label + value; the primary left-click
 * action belongs in an overridden handleSelection().
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXButtonHyperWidget extends XXLabelValueWidget {

   public static final WidgetId ID = WidgetId.XXButtonHyperWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXButtonHyperWidget() {
      this(ID, "");
   }

   public XXButtonHyperWidget(WidgetId widgetId, String label) {
      super(widgetId, label, "");
      remove(XOption.VAL_SINGLE_SEL);
      set(XOption.VAL_NOT_APPLICABLE, XOption.WRITE, XOption.BUTTON_HYPER);
   }

}
