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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Widget providing attribute label, select button with filterable list and readonly name of selected artifact and saves
 * to artifact.
 *
 * @author Donald G. Dunne
 */
public class XArtifactSelectWidgetWithSave extends XArtifactSelectWidget implements AttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XArtifactSelectWidgetWithSave() {
      this("");
   }

   public XArtifactSelectWidgetWithSave(String label) {
      super(label);
      addXModifiedListener(new DirtyListener());
   }

   public Artifact getStored() {
      Object obj = artifact.getSoleAttributeValue(attributeType, null);
      if (obj instanceof Integer) {
         return ArtifactQuery.getArtifactFromId((Integer) obj, artifact.getBranch());
      } else if (obj instanceof Artifact) {
         return (Artifact) obj;
      }
      return null;
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      artifact.setSoleAttributeValue(attributeType, getSelection());
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         try {
            Artifact storedArt = getStored();
            Artifact widgetArt = getSelection();

            Result dirty = new Result(true, getAttributeType() + " is dirty");
            if (widgetArt == null) {
               return storedArt == null ? Result.FalseResult : dirty;
            } else {
               if (storedArt == null) {
                  return dirty;
               }
               if (!widgetArt.equals(storedArt)) {
                  return dirty;
               }
            }
         } catch (OseeCoreException ex) {
            // Do nothing
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void refresh() {
      Artifact storedArt = getStored();
      if (storedArt != null) {
         setSelection(storedArt);
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

   @Override
   public Collection<Artifact> getSelectableArtifacts() {
      return java.util.Collections.emptyList();
   }

}
