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
   public void setMainImage(Image titleImage) {
      super.setTitleImage(titleImage);
   }

   @Override
   public AbstractEventArtifactEditor getEditor() {
      return this;
   }

}
