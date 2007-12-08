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
package org.eclipse.osee.framework.ui.skynet.relation.explorer;

public interface IArtifactListViewer {

   /**
    * Update the view to reflect the fact that a ArtifactModel was added to the ArtifactModel list
    * 
    * @param artifact -
    */
   public void addArtifact(ArtifactModel artifact);

   /**
    * Update the view to reflect the fact that a ArtifactModel was removed from the ArtifactModel list
    * 
    * @param artifact -
    */
   public void removeArtifact(ArtifactModel artifact);

   /**
    * Update the view to reflect the fact that one of the ArtifactModels was modified
    * 
    * @param artifact -
    */
   public void updateArtifact(ArtifactModel artifact);
}
