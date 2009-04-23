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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;

/**
 * @author Roberto E. Escobar
 */
public class NewArtifactEditorInput extends BaseArtifactEditorInput {

   public NewArtifactEditorInput(Artifact artifact) {
      super(artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NewArtifactEditorInput) {
         return (getArtifact() == ((NewArtifactEditorInput) obj).getArtifact());
      }
      return false;
   }
}
