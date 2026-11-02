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

import java.util.Date;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXDateSelWidget extends XXAbstractBaseXWidget<Date> {

   public static final WidgetId ID = WidgetId.XXDateSelWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXDateSelWidget() {
      this(ID, "");
   }

   public XXDateSelWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.CLEAR);
      set(XOption.VAL_SINGLE_SEL);
      setOseeImage(CoreImage.CALENDAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public boolean handleSelection() {
      DateSelectionDialog diag =
         new DateSelectionDialog("Select " + getAttributeType().getUnqualifiedName() + " Date", getSelectedFirst());
      if (diag.open() == Window.OK) {
         if (diag.isNoneSelected()) {
            clear(false);
         } else {
            setSelected(diag.getSelectedDate());
         }
         handleSelectedPersist();
         return true;
      }
      return false;
   }

   @Override
   public Date getSentinel() {
      return null;
   }

   @Override
   public String getDisplayValue() {
      String dateStr = "";
      Date selected = getSelectedFirst();
      if (selected != null) {
         dateStr = DateUtil.get(selected, widData.getDateFormat());
      }
      return dateStr;
   }

   public void setDate(Date date) {
      setSelected(date);
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof Date;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

}
