/*********************************************************************
 * Copyright (c) 2012 Boeing
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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * Branch Selection with branch uuid storage as String
 *
 * @author Donald G. Dunne
 */
public class XBranchSelectByWidgetDam extends XBranchSelectWidget implements AttributeWidget {
   public static final String WIDGET_ID = XBranchSelectByWidgetDam.class.getSimpleName();

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XBranchSelectByWidgetDam() {
      super("");
      addXModifiedListener(new DirtyListener());
   }

   public Long getStoredUuid() {
      return Long.valueOf(artifact.getSoleAttributeValue(attributeType, ""));
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      BranchId selection = getSelection();
      if (selection == null) {
         artifact.deleteAttributes(attributeType);
      } else {
         artifact.setSoleAttributeValue(attributeType, selection.getIdString());
      }
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         try {
            BranchId widgetInput = getSelection();
            BranchId widgetUuid = widgetInput == null ? BranchId.SENTINEL : widgetInput;
            if (widgetUuid.notEqual(getStoredUuid())) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (OseeCoreException ex) {
            // Do nothing
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void refresh() {
      setLabel(attributeType.getUnqualifiedName());
      Long storedUuid = getStoredUuid();
      if (storedUuid != null && getStoredUuid() > 0L) {
         setSelection(BranchManager.getBranchToken(storedUuid));
      }
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
      refresh();
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   private class DirtyListener implements XModifiedListener {
      @Override
      public void widgetModified(XWidget widget) {
         isDirty();
      }
   }

}
