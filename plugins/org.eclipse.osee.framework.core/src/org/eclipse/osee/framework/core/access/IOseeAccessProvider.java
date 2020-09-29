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

package org.eclipse.osee.framework.core.access;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public interface IOseeAccessProvider {

   default boolean isApplicable(ArtifactToken user, Object object) {
      return false;
   }

   default Collection<ArtifactCheck> getArtifactChecks() {
      return Collections.emptyList();
   }

   // Convience method for only single artifact check
   default XResultData hasArtifactContextWriteAccess(ArtifactToken subject, ArtifactToken artifact, XResultData rd) {
      return hasArtifactContextWriteAccess(subject, Collections.singleton(artifact), rd);
   }

   /**
    * Only need to check access based on context ids. All other checks have already been done
    *
    * @return XResultData with errors if not allowed
    */
   default XResultData hasArtifactContextWriteAccess(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts, XResultData rd) {
      return XResultData.EMPTY_RD;
   }

   /**
    * Only need to check access based on context ids. All other checks have already been done
    *
    * @return XResultData with errors if not allowed
    */
   default XResultData hasAttributeTypeContextWriteAccess(ArtifactToken subject, Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, XResultData rd) {
      return XResultData.EMPTY_RD;
   }

   /**
    * Only need to check access based on context ids. All other checks have already been done
    *
    * @return XResultData with errors if not allowed
    */
   default XResultData hasRelationContextWriteAccess(ArtifactToken subject, ArtifactToken artifact, RelationTypeToken relationType, XResultData rd) {
      return XResultData.EMPTY_RD;
   }

}
