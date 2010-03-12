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

/**
 * @author Ryan D. Brooks
 */
public class ArtifactEditorInput extends BaseArtifactEditorInput {

   public ArtifactEditorInput(Artifact artifact) {
      super(artifact);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ArtifactEditorInput) {
         return super.equals(obj);
      }
      return false;
   }
}
