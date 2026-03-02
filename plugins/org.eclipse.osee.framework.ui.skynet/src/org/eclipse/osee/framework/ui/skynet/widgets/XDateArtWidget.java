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
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XDateArtWidget extends XDateWidget implements EditorWidget {

   public static WidgetId ID = WidgetId.XDateArtWidget;

   private EditorData editorData;

   public XDateArtWidget() {
      this(ID, "");
   }

   public XDateArtWidget(WidgetId widgetId, String displayLabel) {
      super(widgetId, displayLabel);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      if (isAutoSave()) {
         addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (getArtifact() != null && getArtifact().isValid()) {
                  //                  saveToArtifact();
                  if (getArtifact().isDirty()) {
                     String comment = null;
                     if (editorData != null && Strings.isValid(editorData.getEditorName())) {
                        comment = editorData.getEditorName() + " XDateArtWidget Auto-Save";
                     } else {
                        comment = "XDateArtWidget Auto-Save";
                     }
                     getArtifact().persistInThread(comment);
                  }
               }
            }
         });
      }
   }

   @Override
   public void refresh() {
      Date value = getArtifact().getSoleAttributeValue(getAttributeType(), null);
      if (value != null) {
         super.setDate(value);
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      refresh();
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
