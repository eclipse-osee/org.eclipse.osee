/*********************************************************************
 * Copyright (c) 2004, 2007, 2026 Boeing
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXButtonViaActionWidget extends XXButtonHyperWidget {

   public static final WidgetId ID = WidgetId.XXButtonViaActionWidget;

   private final Action action;
   private org.eclipse.swt.graphics.Image cachedActionImage;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXButtonViaActionWidget() {
      this(null);
   }

   public XXButtonViaActionWidget(final Action action) {
      this(action, null);
   }

   public XXButtonViaActionWidget(final Action action, OseeImage oseeImage) {
      super(ID, action == null ? "" : action.getText());
      this.action = action;
      if (oseeImage != null) {
         setOseeImage(oseeImage);
      } else {
         setOseeImage(CoreImage.GEAR);
      }
      if (this.action != null && Strings.isValid(this.action.getToolTipText())) {
         setToolTip(this.action.getToolTipText());
      }
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   protected boolean handleSelection() {
      action.run();
      return false;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   protected org.eclipse.swt.graphics.Image getWidgetIcon() {
      if (action != null) {
         ImageDescriptor desc = action.getImageDescriptor();
         if (desc != null) {
            if (cachedActionImage == null) {
               cachedActionImage = desc.createImage();
            }
            return cachedActionImage;
         }
      }
      return super.getWidgetIcon();
   }

}
