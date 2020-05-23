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

package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaArtifactGuids extends Criteria {

   private final Collection<String> artifactGuids;

   public CriteriaArtifactGuids(Collection<String> artifactGuids) {
      super();
      this.artifactGuids = artifactGuids;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNullOrEmpty(artifactGuids, "artifact guids");
      List<String> invalids = new ArrayList<>();
      for (String guid : artifactGuids) {
         if (!GUID.isValid(guid)) {
            invalids.add(guid);
         }
      }
      Conditions.checkExpressionFailOnTrue(!invalids.isEmpty(), "Invalid Guids - %s", invalids);
   }

   public Collection<String> getIds() {
      return artifactGuids;
   }

   @Override
   public String toString() {
      return "CriteriaArtifactGuids [artifactGuids=" + artifactGuids + "]";
   }

}
