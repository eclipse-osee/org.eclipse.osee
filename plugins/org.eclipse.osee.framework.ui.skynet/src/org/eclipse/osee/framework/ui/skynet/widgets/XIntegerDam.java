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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XIntegerDam extends XInteger implements AttributeWidget, EditorWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;
   public static final String WIDGET_ID = XIntegerDam.class.getSimpleName();
   private EditorData editorData;

   public XIntegerDam(String displayLabel) {
      super(displayLabel);
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
      // getSoleAttributeValue may return a value of type other than Integer such as ArtifactId
      Object value = artifact.getSoleAttributeValue(attributeType, null);
      set(value == null ? "" : value.toString());
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
      refresh();
   }

   @Override
   public void saveToArtifact() {
      try {
         if (!Strings.isValid(text)) {
            getArtifact().deleteSoleAttribute(getAttributeType());
         } else {
            Integer enteredValue = getInteger();
            if (getAttributeType().isArtifactId()) {
               getArtifact().setSoleAttributeValue(getAttributeType(), ArtifactId.valueOf(enteredValue));
            } else {
               getArtifact().setSoleAttributeValue(getAttributeType(), enteredValue);
            }
         }
      } catch (NumberFormatException ex) {
         // do nothing
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         try {
            Integer enteredValue = getInteger();
            Integer storedValue = 0;
            if (getAttributeType().isArtifactId()) {
               ArtifactId artId = getArtifact().getSoleAttributeValue(getAttributeType());
               if (artId != null && artId.isValid()) {
                  storedValue = artId.getIdIntValue();
               }
            } else {
               storedValue = getArtifact().getSoleAttributeValue(getAttributeType(), 0);
            }
            if (enteredValue.doubleValue() != storedValue.doubleValue()) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (AttributeDoesNotExist ex) {
            if (!get().equals("")) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (NumberFormatException ex) {
            // do nothing
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public void setEditorData(EditorData editorData) {
      this.editorData = editorData;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      if (isAutoSave()) {
         getStyledText().addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
               if (artifact != null && artifact.isValid()) {
                  saveToArtifact();
                  if (artifact.isDirty()) {
                     String comment = null;
                     if (editorData != null && Strings.isValid(editorData.getEditorName())) {
                        comment = editorData.getEditorName() + " Auto-Save";
                     } else {
                        comment = "XIntegerDam Auto-Save";
                     }
                     getArtifact().persistInThread(comment);
                  }
               }
            }

         });
      }
   }

}
