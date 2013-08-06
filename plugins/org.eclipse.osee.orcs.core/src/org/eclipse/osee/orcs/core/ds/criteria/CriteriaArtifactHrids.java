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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaArtifactHrids extends Criteria {
   private final Collection<String> hrids;

   public CriteriaArtifactHrids(Collection<String> hrids) {
      super();
      this.hrids = hrids;
   }

   @Override
   public void checkValid(Options options) throws OseeCoreException {
      super.checkValid(options);
      Conditions.checkNotNullOrEmpty(hrids, "artifact hrids");
      List<String> invalids = new ArrayList<String>();
      for (String hrid : hrids) {
         if (!HumanReadableId.isValid(hrid)) {
            invalids.add(hrid);
         }
      }
      Conditions.checkExpressionFailOnTrue(!invalids.isEmpty(), "Invalid Hrids - %s", invalids);
   }

   public Collection<String> getIds() {
      return hrids;
   }

   @Override
   public String toString() {
      return "CriteriaArtifactHrids [hrids=" + hrids + "]";
   }
}
