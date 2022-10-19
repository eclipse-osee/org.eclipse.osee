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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkLabelDateDam extends XHyperlinkLabelValueSelection implements EditorWidget, AttributeWidget {

   public static final String WIDGET_ID = XHyperlinkLabelDateDam.class.getSimpleName();
   private AttributeTypeToken attributeTypeToken;
   private Date dateValue = null;
   private Artifact artifact;
   private EditorData editorData;

   public XHyperlinkLabelDateDam() {
      this("");
   }

   public XHyperlinkLabelDateDam(String label) {
      super(label);
   }

   @Override
   public String getCurrentValue() {
      dateValue = artifact.getSoleAttributeValue(attributeTypeToken, null);
      if (dateValue == null) {
         return Widgets.NOT_SET;
      }
      return DateUtil.getMMDDYY(dateValue);
   }

   @Override
   public boolean handleSelection() {
      try {
         DateSelectionDialog diag = new DateSelectionDialog("Select " + attributeTypeToken.getName(),
            "Select " + attributeTypeToken.getName(), dateValue);
         if (diag.open() == Window.OK) {
            dateValue = diag.getSelectedDate();
            if (isAutoSave()) {
               if (artifact != null && artifact.isValid()) {
                  artifact.setSoleAttributeValue(attributeTypeToken, dateValue);
                  if (artifact.isDirty()) {
                     String comment = null;
                     if (editorData != null && Strings.isValid(editorData.getEditorName())) {
                        comment = editorData.getEditorName() + " Auto-Save";
                     } else {
                        comment = "Date Auto-Save";
                     }
                     getArtifact().persistInThread(comment);
                  }
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

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeTypeToken;
   }

   public Date getDateValue() {
      return dateValue;
   }

   public void setDateValue(Date dateValue) {
      this.dateValue = dateValue;
      refresh();
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeTypeToken) {
      this.artifact = artifact;
      this.attributeTypeToken = attributeTypeToken;
   }

   @Override
   public void setEditorData(EditorData editorData) {
      this.editorData = editorData;
   }

}
