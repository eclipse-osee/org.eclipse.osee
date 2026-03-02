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

import java.util.Date;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkLabelDateWidget extends XAbstractHyperlinkLabelValueSelWidget {

   public static final WidgetId ID = WidgetId.XHyperlinkLabelDateWidget;

   private Date dateValue = null;

   public XHyperlinkLabelDateWidget() {
      this("");
   }

   public XHyperlinkLabelDateWidget(String label) {
      super(ID, label);
   }

   @Override
   public String getCurrentValue() {
      if (dateValue == null) {
         return Widgets.NOT_SET;
      }
      return DateUtil.getMMDDYY(dateValue);
   }

   @Override
   public boolean handleSelection() {
      try {
         DateSelectionDialog diag = new DateSelectionDialog("Select " + getLabel(), "Select " + getLabel(), dateValue);
         if (diag.open() == Window.OK) {
            dateValue = diag.getSelectedDate();
            refresh();
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public Date getDateValue() {
      return dateValue;
   }

   public void setDateValue(Date dateValue) {
      this.dateValue = dateValue;
      refresh();
   }

   @Override
   public Object getData() {
      return dateValue;
   }

}
