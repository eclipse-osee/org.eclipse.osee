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

import java.lang.ref.WeakReference;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XTextDam extends XText implements AttributeWidget, EditorWidget {

   public static final String WIDGET_ID = XTextDam.class.getSimpleName();
   private Artifact artifactStrongRef;
   private AttributeTypeToken attributeType;
   private final boolean isWeakReference;
   private WeakReference<Artifact> artifactRef;
   private EditorData editorData;

   public XTextDam(String displayLabel) {
      this(displayLabel, false);
   }

   public XTextDam(String displayLabel, boolean isWeakReference) {
      super(displayLabel);
      this.isWeakReference = isWeakReference;
   }

   @Override
   public Artifact getArtifact() {
      Artifact toReturn = null;
      if (isWeakReference) {
         if (artifactRef.get() == null) {
            throw new OseeStateException("Artifact has been garbage collected");
         }
         toReturn = artifactRef.get();
      } else {
         toReturn = artifactStrongRef;
      }
      return toReturn;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   private void setArtifact(Artifact artifact) {
      if (isWeakReference) {
         this.artifactRef = new WeakReference<>(artifact);
      } else {
         artifactStrongRef = artifact;
      }
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
      setArtifact(artifact);
      refresh();
   }

   @Override
   public void refresh() {
      if (this.attributeType.notRenderable()) {
         super.set("Attribute not displayable");
         super.setEditable(false);
      } else {
         super.set(getArtifact().getSoleAttributeValue(getAttributeType(), ""));
      }
   }

   @Override
   public void saveToArtifact() {
      if (!this.isEditable()) {
         return;
      }
      String value = get();
      if (!Strings.isValid(value)) {
         getArtifact().deleteSoleAttribute(getAttributeType());
      } else if (!value.equals(getArtifact().getSoleAttributeValue(getAttributeType(), ""))) {
         getArtifact().setSoleAttributeValue(getAttributeType(), value);
      }
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         String enteredValue = get();
         String storedValue = getArtifact().getSoleAttributeValue(getAttributeType(), "");
         if (!enteredValue.equals(storedValue)) {
            return new Result(true, attributeType + " is dirty");
         }
      }
      return Result.FalseResult;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               status =
                  OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(), get());
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      if (isAutoSave()) {
         getStyledText().addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
               if (getArtifact() != null && getArtifact().isValid()) {
                  saveToArtifact();
                  if (getArtifact().isDirty()) {
                     String comment = null;
                     if (editorData != null && Strings.isValid(editorData.getEditorName())) {
                        comment = editorData.getEditorName() + " Auto-Save";
                     } else {
                        comment = "XTextDam Auto-Save";
                     }
                     getArtifact().persistInThread(comment);
                  }
               }
            }

         });
      }
   }

   @Override
   public void setEditorData(EditorData editorData) {
      this.editorData = editorData;
   }

}
