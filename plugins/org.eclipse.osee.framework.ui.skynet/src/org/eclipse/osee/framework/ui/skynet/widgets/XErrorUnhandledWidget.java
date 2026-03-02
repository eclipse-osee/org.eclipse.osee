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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.osgi.service.component.annotations.Component;

/**
 * Widget was not provided through OSGI A Component annotation or XWidget provider. See XTextWidget for example
 * annotation. If annotation exists, Eclipse may not be set to auto-create the OSGI-INF config file, see Preferences >
 * Plugin-Development > DS Annotations.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XErrorUnhandledWidget extends XLabelWidget {

   public static final WidgetId ID = WidgetId.XErrorUnhandledWidget;

   public XErrorUnhandledWidget() {
      this("Error: Unhandled Widget");
   }

   public XErrorUnhandledWidget(String label) {
      super(label);
   }

   @Override
   public void setLabel(String label) {
      super.setLabel(label + " - " + getLabel());
      // don't overwrite label if error
   }

}
