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
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkLabelDate extends XHyperlinkLabelValueSelection {

   public static final String WIDGET_ID = XHyperlinkLabelDate.class.getSimpleName();
   private Date dateValue = null;

   public XHyperlinkLabelDate() {
      this("");
   }

   public XHyperlinkLabelDate(String label) {
      super(label);
   }

   @Override
   public String getCurrentValue() {
      if (dateValue == null) {
         return "Not Set";
      }
      return DateUtil.getMMDDYY(dateValue);
   }

   @Override
   public boolean handleSelection() {
      try {
         DateSelectionDialog diag = new DateSelectionDialog("Select " + label, "Select " + label, dateValue);
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
