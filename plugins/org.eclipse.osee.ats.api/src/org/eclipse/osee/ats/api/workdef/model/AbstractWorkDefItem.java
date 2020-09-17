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

package org.eclipse.osee.ats.api.workdef.model;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class AbstractWorkDefItem extends NamedIdBase {

   protected String description;
   private ArtifactTypeToken artType;

   public AbstractWorkDefItem(Long id, String name) {
      super(id, name);
   }

   public AbstractWorkDefItem(Long id, String name, ArtifactTypeToken artType) {
      super(id, name);
      this.artType = artType;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public ArtifactTypeToken getArtType() {
      return artType;
   }

   public void setArtType(ArtifactTypeToken artType) {
      this.artType = artType;
   }

}
