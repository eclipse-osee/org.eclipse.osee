/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ListSelectionDialogNoSave;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxThreeStateDam extends XCheckBoxThreeState implements AttributeWidget, EditorWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;
   private EditorData editorData;
   public static String WIDGET_ID = XCheckBoxThreeStateDam.class.getSimpleName();

   public XCheckBoxThreeStateDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      CheckState state = getCheckState();
      if (state == CheckState.UnSet) {
         artifact.deleteAttributes(attributeType);
      } else if (state == CheckState.Checked) {
         artifact.setSoleAttributeValue(attributeType, true);
      } else if (state == CheckState.UnChecked) {
         artifact.setSoleAttributeValue(attributeType, false);
      } else {
         throw new OseeStateException("UnExpected CheckState " + state.name());
      }
   }

   /**
    * If auto-save, popup dialog instead of toggling through options and saving after each
    */
   @Override
   protected void handleSetCheckState() {
      if (isAutoSave()) {
         List<CheckState> states = new ArrayList<>();
         CheckState currState = getCheckState();
         for (CheckState cState : Arrays.asList(CheckState.Checked, CheckState.UnChecked, CheckState.UnSet)) {
            if (currState != cState) {
               states.add(cState);
            }
         }
         String title = String.format("Select [%s] State", getAttributeType().getUnqualifiedName());
         String message =
            String.format("Select [%s] State\n\nCurrent State: %s\n\nDouble-Click to Select and Close Dialog",
               getAttributeType().getUnqualifiedName(), currState);
         ListSelectionDialogNoSave dialog = new ListSelectionDialogNoSave(Collections.castAll(states),
            Displays.getActiveShell().getShell(), title, null, message, 2, new String[] {"Ok", "Cancel"}, 0);
         if (dialog.open() == 0) {
            Object obj = dialog.getSelected();
            checkState = (CheckState) obj;
            saveToArtifact();
            if (artifact.isDirty()) {
               String comment = null;
               if (editorData != null && Strings.isValid(editorData.getEditorName())) {
                  comment = editorData.getEditorName() + " Auto-Save";
               } else {
                  comment = "XCheckDam Auto-Save";
               }
               getArtifact().persistInThread(comment);
            }
         }
      } else {
         super.handleSetCheckState();
      }
   }

   @Override
   public void revert() {
      setAttributeType(artifact, attributeType);
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         CheckState storedCheckState = getStoredCheckState();
         CheckState checkState = getCheckState();
         if (storedCheckState != checkState) {
            return new Result(true, getAttributeType().toString());
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void refresh() {
      checkState = getStoredCheckState();
      updateCheckWidget();
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
      refresh();
   }

   private CheckState getStoredCheckState() {
      Boolean set = artifact.getSoleAttributeValue(this.attributeType, null);
      if (set == null) {
         return CheckState.UnSet;
      } else if (set) {
         return CheckState.Checked;
      } else {
         return CheckState.UnChecked;
      }
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null && getStoredCheckState() == CheckState.UnSet) {
               status =
                  new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format("Must Select [%s]", getAttributeType()));
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error Validating Artifact/Attribute", ex);
         }
      }
      return status;
   }

   @Override
   public void setEditorData(EditorData editorData) {
      this.editorData = editorData;
   }

}
