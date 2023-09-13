/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.accessor.types;

import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Luciano T. Vaglienti
 */
public class ArtifactMatch extends ArtifactAccessorResult {

   private String Name; //required

   public ArtifactMatch(ArtifactToken art) {
      super(art);
   }

   public ArtifactMatch(ArtifactReadable art) {
      this(art.getId(), art.getSoleAttributeValue(CoreAttributeTypes.Name));
   }

   public ArtifactMatch(Long id, String name) {
      super(id, name);
   }

   public ArtifactMatch() {
   }

}
