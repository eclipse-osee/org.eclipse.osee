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
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaAttribute extends CriteriaArtifact {

   private final Collection<AttributeId> ids;
   private final Collection<? extends AttributeTypeId> types;

   public CriteriaAttribute(Collection<AttributeId> ids, Collection<? extends AttributeTypeId> types) {
      super();
      this.ids = ids;
      this.types = types;
   }

   public Collection<AttributeId> getIds() {
      return ids != null ? ids : Collections.<AttributeId> emptyList();
   }

   public Collection<? extends AttributeTypeId> getTypes() {
      return types != null ? types : Collections.emptyList();
   }

   @Override
   public String toString() {
      return "CriteriaAttribute [queryId=" + getQueryId() + ", ids=" + ids + ", types=" + types + "]";
   }

}
