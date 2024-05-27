/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.orcs.rest.model;

import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan T. Baldwin
 */
public class ArtifactExplorerUserPreferences extends ArtifactAccessorResultWithoutGammas {

   private final boolean artifactExplorerPanelLocation;

   public ArtifactExplorerUserPreferences(ArtifactReadable art) {
      super(art);
      this.artifactExplorerPanelLocation =
         art.getSoleAttributeValue(CoreAttributeTypes.ArtifactExplorerPanelLocation, false);
   }

   public ArtifactExplorerUserPreferences() {
      this.artifactExplorerPanelLocation = false;
   }

   public boolean isArtifactExplorerPanelLocation() {
      return artifactExplorerPanelLocation;
   }

}
