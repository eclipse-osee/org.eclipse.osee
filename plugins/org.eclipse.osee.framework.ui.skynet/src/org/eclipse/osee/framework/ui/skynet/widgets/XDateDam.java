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

import java.util.Date;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XDateDam extends XDate implements AttributeWidget, EditorWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;
   private EditorData editorData;

   public XDateDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      if (isAutoSave()) {
         addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (artifact != null && artifact.isValid()) {
                  saveToArtifact();
                  if (artifact.isDirty()) {
                     String comment = null;
                     if (editorData != null && Strings.isValid(editorData.getEditorName())) {
                        comment = editorData.getEditorName() + " Auto-Save";
                     } else {
                        comment = "XDateDam Auto-Save";
                     }
                     getArtifact().persistInThread(comment);
                  }
               }
            }
         });
      }
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void refresh() {
      Date value = artifact.getSoleAttributeValue(getAttributeType(), null);
      if (value != null) {
         super.setDate(value);
      }
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken AttributeTypeId) {
      this.artifact = artifact;
      this.attributeType = AttributeTypeId;
      refresh();
   }

   @Override
   public void saveToArtifact() {
      try {
         if (date == null) {
            getArtifact().deleteSoleAttribute(getAttributeType());
         } else {
            Date enteredValue = getDate();
            getArtifact().setSoleAttributeValue(getAttributeType(), enteredValue);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         Date enteredValue = getDate();
         Date storedValue = getArtifact().getSoleAttributeValue(getAttributeType(), null);
         Result dirty = new Result(true, getAttributeType() + " is dirty");

         if (enteredValue == null) {
            return storedValue == null ? Result.FalseResult : dirty;
         } else {
            if (storedValue == null) {
               return dirty;
            }
            if (enteredValue.getTime() != storedValue.getTime()) {
               return dirty;
            }
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               XResultData rd =
                  OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(), get());
               if (rd.isErrors()) {
                  status = new Status(IStatus.ERROR, getClass().getSimpleName(), rd.toString());
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

   @Override
   public void setEditorData(EditorData editorData) {
      this.editorData = editorData;
   }

}
