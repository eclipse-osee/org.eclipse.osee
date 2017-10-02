/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   public void checkValid(Options options)  {
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
