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

package org.eclipse.osee.orcs.db.internal.loader.criteria;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Audrey Denk
 */
public final class CriteriaRelation2 extends CriteriaArtifact {

   private final Collection<? extends RelationTypeToken> types;

   public CriteriaRelation2(Collection<? extends RelationTypeToken> types) {
      this.types = types;
   }

   public Collection<? extends RelationTypeToken> getTypes() {
      return types != null ? types : Collections.<RelationTypeToken> emptyList();
   }

   @Override
   public String toString() {
      return "CriteriaRelation [queryId=" + getQueryId() + ", types=" + types + "]";
   }
}