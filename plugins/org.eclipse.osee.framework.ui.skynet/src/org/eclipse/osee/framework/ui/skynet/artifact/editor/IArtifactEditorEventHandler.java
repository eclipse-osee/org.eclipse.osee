/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
 * @author Donald G. Dunne
 */
public interface IArtifactEditorEventHandler {

   public AbstractEventArtifactEditor getEditor();

   public boolean isDisposed();

   public void refreshDirtyArtifact();

   public void closeEditor();

   public void refreshRelations();

   public Artifact getArtifactFromEditorInput();

   public void setMainImage(Image titleImage);

}
