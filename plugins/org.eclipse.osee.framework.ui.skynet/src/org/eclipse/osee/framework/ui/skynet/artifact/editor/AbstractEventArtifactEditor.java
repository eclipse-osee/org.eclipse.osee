/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public abstract class AbstractEventArtifactEditor extends AbstractArtifactEditor implements IArtifactEditorEventHandler {

   public AbstractEventArtifactEditor() {
      ArtifactEditorEventManager.add(this);
   }

   @Override
   protected void addPages() {
      // do nothing
   }

   @Override
   public boolean isDirty() {
      boolean wasDirty = false;
      Artifact artifact = getArtifactFromEditorInput();
      if (artifact != null) {
         wasDirty = super.isDirty() || artifact.isDirty();
      }
      return wasDirty;
   }

   protected abstract void checkEnabledTooltems();

   @Override
   public abstract void refreshDirtyArtifact();

   @Override
   public void closeEditor() {
      ArtifactEditorEventManager.remove(this);
   }

   @Override
   public abstract void refreshRelations();

   @Override
   public void setMainImage(Image titleImage) {
      super.setTitleImage(titleImage);
   }

   @Override
   public AbstractEventArtifactEditor getEditor() {
      return this;
   }

}
