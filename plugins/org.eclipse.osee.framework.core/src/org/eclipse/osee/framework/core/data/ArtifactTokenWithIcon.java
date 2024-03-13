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
package org.eclipse.osee.framework.core.data;

/**
 * Used as a data transfer object for cases where just a name, id, and icon are needed.
 *
 * @author Ryan T. Baldwin
 */
public class ArtifactTokenWithIcon {

   private final ArtifactToken token;

   public ArtifactTokenWithIcon(ArtifactToken artifactToken) {
      this.token = artifactToken;
   }

   public String getName() {
      return token.getName();
   }

   public String getId() {
      return token.getIdString();
   }

   public MaterialIcon getIcon() {
      return token.getArtifactType().getIcon();
   }
}
