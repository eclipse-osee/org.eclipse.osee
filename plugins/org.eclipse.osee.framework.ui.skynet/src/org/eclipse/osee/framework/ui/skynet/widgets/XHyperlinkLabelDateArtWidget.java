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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * AUTO_SAVE by default
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkLabelDateArtWidget extends XAbstractHyperlinkLabelValueSelWidget implements EditorWidget {

   public static final WidgetId ID = WidgetId.XHyperlinkLabelDateArtWidget;

   private Date dateValue = null;

   private EditorData editorData;

   public XHyperlinkLabelDateArtWidget() {
      this("");
   }

   public XHyperlinkLabelDateArtWidget(String label) {
      super(ID, label);
   }

   @Override
   public String getCurrentValue() {
      dateValue = getArtifact().getSoleAttributeValue(getAttributeType(), null);
      if (dateValue == null) {
         return Widgets.NOT_SET;
      }
      return DateUtil.getMMDDYY(dateValue);
   }

   @Override
   public boolean handleSelection() {
      try {
         DateSelectionDialog diag = new DateSelectionDialog("Select " + getAttributeType().getName(),
            "Select " + getAttributeType().getName(), dateValue);
         if (diag.open() == Window.OK) {
            dateValue = diag.getSelectedDate();
            if (getArtifact() != null && getArtifact().isValid()) {
               getArtifact().setSoleAttributeValue(getAttributeType(), dateValue);
               if (getArtifact().isDirty()) {
                  String comment = null;
                  if (editorData != null && Strings.isValid(editorData.getEditorName())) {
                     comment = editorData.getEditorName() + " HLDateDam Auto-Save";
                  } else {
                     comment = "HLDateDam Auto-Save";
                  }
                  getArtifact().persistInThread(comment);
               }
            }
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
   public void setEditorData(EditorData editorData) {
      this.editorData = editorData;
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
      } else if (isRequiredEntry() && getCurrentValue().equals(Widgets.NOT_SET)) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " is not set.");
      }
      return Status.OK_STATUS;
   }

}
