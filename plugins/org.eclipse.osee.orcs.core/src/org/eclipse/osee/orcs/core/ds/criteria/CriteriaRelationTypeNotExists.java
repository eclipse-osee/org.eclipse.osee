/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.orcs.core.ds.RelationTypeCriteria;

/**
 * @author John Misinco
 */
public final class CriteriaRelationTypeNotExists extends RelationTypeCriteria<RelationTypeToken> {

   public CriteriaRelationTypeNotExists(RelationTypeToken relationType) {
      super(relationType);
   }
}