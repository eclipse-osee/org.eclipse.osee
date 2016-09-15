/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Ryan D. Brooks
 */
public class ApplicabilityQueryImpl implements ApplicabilityQuery {
   private final TupleQuery tupleQuery;

   public ApplicabilityQueryImpl(TupleQuery tupleQuery) {
      this.tupleQuery = tupleQuery;
   }

   @Override
   public ApplicabilityToken getApplicabilityToken(ArtifactId artId, BranchId branch) {
      List<ApplicabilityToken> result = new ArrayList<>();
      BiConsumer<Long, String> consumer = (id, name) -> result.add(new ApplicabilityToken(id, name));
      tupleQuery.getTupleType2ForArtifactId(artId, branch, consumer);

      return result.get(0);
   }

   @Override
   public List<ApplicabilityToken> getApplicabilityTokens(List<ArtifactId> artIds, BranchId branch) {
      List<ApplicabilityToken> toReturn = new ArrayList<>();
      List<ApplicabilityToken> result = new ArrayList<>();
      BiConsumer<Long, String> consumer = (id, name) -> result.add(new ApplicabilityToken(id, name));
      tupleQuery.getTupleType2ForArtifactIds(artIds, branch, consumer);

      for (ArtifactId artId : artIds) {
         if (result.contains(artId.getId())) {
            toReturn.add(result.get(result.indexOf(artId.getId())));
         } else {
            toReturn.add(ApplicabilityToken.BASE);
         }
      }

      return toReturn;
   }

   @Override
   public HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch) {
      HashMap<Long, ApplicabilityToken> tokens = new HashMap<>();
      BiConsumer<Long, String> consumer = (id, name) -> tokens.put(id, new ApplicabilityToken(id, name));
      tupleQuery.getTuple2UniqueE2Pair(CoreTupleTypes.ViewApplicability, branch, consumer);
      return tokens;
   }

   @Override
   public HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch1, BranchId branch2) {
      HashMap<Long, ApplicabilityToken> tokens = new HashMap<>();
      BiConsumer<Long, String> consumer = (id, name) -> tokens.put(id, new ApplicabilityToken(id, name));
      tupleQuery.getTuple2UniqueE2Pair(CoreTupleTypes.ViewApplicability, branch1, consumer);
      tupleQuery.getTuple2UniqueE2Pair(CoreTupleTypes.ViewApplicability, branch2, consumer);
      return tokens;
   }
}