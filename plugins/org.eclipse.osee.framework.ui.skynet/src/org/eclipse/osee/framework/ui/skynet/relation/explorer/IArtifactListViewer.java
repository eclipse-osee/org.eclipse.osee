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
