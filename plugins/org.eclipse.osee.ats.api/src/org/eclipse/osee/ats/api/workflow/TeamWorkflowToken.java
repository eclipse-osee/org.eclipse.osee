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
package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactReadable;

public class TeamWorkflowToken extends ArtifactAccessorResultWithoutGammas {

   private final String atsId;

   public TeamWorkflowToken(ArtifactReadable art) {
      super(art);
      this.atsId = art.getSoleAttributeAsString(AtsAttributeTypes.AtsId, "0");
   }

   public String getAtsId() {
      return atsId;
   }

}
