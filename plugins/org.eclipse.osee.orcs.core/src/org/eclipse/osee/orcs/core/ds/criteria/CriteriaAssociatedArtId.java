/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author John Misinco
 */
public class CriteriaAssociatedArtId extends Criteria implements BranchCriteria {

   private final Integer associatedArtId;

   public CriteriaAssociatedArtId(Integer associatedArtId) {
      super();
      this.associatedArtId = associatedArtId;
   }

   public Integer getAssociatedArtId() {
      return associatedArtId;
   }

   @Override
   public void checkValid(Options options) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(associatedArtId == null, "Associated artifact id cannot be null");
   }

   @Override
   public String toString() {
      return "CriteriaAssociatedArtId [associatedArtId=" + associatedArtId + "]";
   }
}
