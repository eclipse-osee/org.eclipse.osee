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

package org.eclipse.osee.framework.ui.skynet.widgets.cellEditor;

import java.util.Date;
import org.eclipse.osee.framework.ui.skynet.widgets.util.CalendarWidget;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public class DateValue extends UniversalCellEditorValue {
   private Date date;

   public DateValue() {
      super();
   }

   @Override
   public Control prepareControl(UniversalCellEditor universalEditor) {
      CalendarWidget datePicker = universalEditor.getDateControl();
      datePicker.setDate(date);
      return datePicker;
   }

   public void setValue(Date date) {
      this.date = date;
   }
}