/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.rest.api;

import java.util.Comparator;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;

/**
 * @author Loren K. Ashley
 */

public class ArtifactDescendingIdentifierComparator implements Comparator<PublishingArtifact> {

   public ArtifactDescendingIdentifierComparator() {

   }

   @Override
   public int compare(PublishingArtifact lhs, PublishingArtifact rhs) {
      if (lhs == null && rhs == null) {
         return 0;
      } else if (lhs == null) {
         return 1;
      } else if (rhs == null) {
         return -1;
      } else {
         long lhsId = lhs.getId();
         long rhsId = rhs.getId();
         return lhsId == rhsId ? 0 : lhsId > rhsId ? 1 : -1;
      }

   }
}
