/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.define.api;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Branden W. Phillips
 */
public class PublishingArtifactError {
   private final Long artId;
   private final String artName;
   private final ArtifactTypeToken artType;
   private final String errorDescription;

   public PublishingArtifactError(Long artId, String artName, ArtifactTypeToken artType, String errorDescription) {
      this.artId = artId;
      this.artName = artName;
      this.artType = artType;
      this.errorDescription = errorDescription;
   }

   public Long getArtId() {
      return artId;
   }

   public String getArtName() {
      return artName;
   }

   public ArtifactTypeToken getArtType() {
      return artType;
   }

   public String getErrorDescription() {
      return errorDescription;
   }

}
