/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.criteria;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Roberto E. Escobar
 */
public final class CriteriaRelation extends CriteriaArtifact {

   private final Collection<RelationId> ids;
   private final Collection<? extends RelationTypeToken> types;

   public CriteriaRelation(Collection<RelationId> ids, Collection<? extends RelationTypeToken> types) {
      this.ids = ids;
      this.types = types;
   }

   public Collection<RelationId> getIds() {
      return ids != null ? ids : Collections.<RelationId> emptyList();
   }

   public Collection<? extends RelationTypeToken> getTypes() {
      return types != null ? types : Collections.<RelationTypeToken> emptyList();
   }

   @Override
   public String toString() {
      return "CriteriaRelation [queryId=" + getQueryId() + ", ids=" + ids + ", types=" + types + "]";
   }
}