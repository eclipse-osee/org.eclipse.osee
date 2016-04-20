/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
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
public class XArtifactSelectWidgetWithSave extends XArtifactSelectWidget implements IAttributeWidget {

   private Artifact artifact;
   private IAttributeType attributeType;

   public XArtifactSelectWidgetWithSave(String label) {
      super(label);
      addXModifiedListener(new DirtyListener());
   }

   public Artifact getStored() throws OseeCoreException {
      Artifact stored = null;
      if (artifact != null) {
         Integer uuid = artifact.getSoleAttributeValue(attributeType, 0);
         if (uuid > 0) {
            stored = ArtifactQuery.getArtifactFromId(uuid, artifact.getBranch());
         }
      }
      return stored;
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
      artifact.setSoleAttributeValue(attributeType, getSelection().getUuid().intValue());
   }

   @Override
   public void revert() throws OseeCoreException {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         try {
            Artifact storedArt = getStored();
            Artifact widgetArt = getSelection();
            if (storedArt == null && widgetArt == null) {
               return Result.FalseResult;
            } else if (storedArt != null && widgetArt == null) {
               return new Result(true, getAttributeType() + " is dirty");
            } else if (storedArt == null && widgetArt != null) {
               return new Result(true, getAttributeType() + " is dirty");
            } else if (!storedArt.equals(widgetArt)) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (OseeCoreException ex) {
            // Do nothing
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, IAttributeType attributeTypeName) throws OseeCoreException {
      this.artifact = artifact;
      this.attributeType = attributeTypeName;
      Artifact storedArt = getStored();
      if (storedArt != null) {
         setSelection(storedArt);
      }
   }

   @Override
   public IAttributeType getAttributeType() {
      return attributeType;
   }

   private class DirtyListener implements XModifiedListener {
      @Override
      public void widgetModified(XWidget widget) {
         isDirty();
      }
   }

   @Override
   public Collection<Artifact> getArtifacts() {
      return java.util.Collections.emptyList();
   }

}
