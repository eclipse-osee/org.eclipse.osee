/*********************************************************************
 * Copyright (c) 2010 Boeing
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
 * @author Donald G. Dunne
 */
public interface IArtifactEditorEventHandler {

   public AbstractEventArtifactEditor getEditor();

   public boolean isDisposed();

   public void refreshDirtyArtifact();

   public void closeEditor();

   public Artifact getArtifactFromEditorInput();

   public void setMainImage(Image titleImage);

   public void refresh();

}
