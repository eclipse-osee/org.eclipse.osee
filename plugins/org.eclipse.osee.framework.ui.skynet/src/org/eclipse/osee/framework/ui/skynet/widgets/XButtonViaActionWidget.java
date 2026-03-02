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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XButtonViaActionWidget extends XButtonWidget {

   public static WidgetId ID = WidgetId.XButtonViaActionWidget;
   private Action action;

   public XButtonViaActionWidget() {
      super(ID, "");
   }

   public XButtonViaActionWidget(final Action action) {
      this(action, null);
   }

   public XButtonViaActionWidget(final Action action, OseeImage oseeImage) {
      super(ID, action.getText());
      this.action = action;
      if (Strings.isValid(action.getToolTipText())) {
         setToolTip(action.getToolTipText());
      }
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            action.run();
         }
      });
   }

   @Override
   protected ImageDescriptor getImageOrDefault() {
      if (action.getImageDescriptor() == null) {
         return ImageManager.getImageDescriptor(FrameworkImage.GEAR);
      }
      return action.getImageDescriptor();
   }
}
