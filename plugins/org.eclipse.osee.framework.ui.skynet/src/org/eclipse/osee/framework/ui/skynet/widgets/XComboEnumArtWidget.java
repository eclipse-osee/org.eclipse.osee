/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

/**
 * XComboDam that takes it's options from valid Enum values.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XComboEnumArtWidget extends XComboWidget implements EditorWidget {

   public static WidgetId ID = WidgetId.XComboEnumArtWidget;

   private EditorData editorData;

   public XComboEnumArtWidget() {
      this(ID, "");
   }

   public XComboEnumArtWidget(WidgetId widgetId, String displayLabel) {
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
                        comment = editorData.getEditorName() + " XComboEnumArtWidget Auto-Save";
                     } else {
                        comment = "XComboEnumDam Auto-Save";
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
      if (getAttributeType() != null) {
         try {
            String value = getArtifact().getSoleAttributeValue(getAttributeType());
            super.set(value.toString());
         } catch (AttributeDoesNotExist ex) {
            super.set("");
         }
      } else {
         super.set("");
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      setComboOptions();
      refresh();
   }

   private void setComboOptions() {
      setDataStrings(Named.getNames(getAttributeType().toEnum().getEnumValues()));
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null && Strings.isValid(get())) {
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
