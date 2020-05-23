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

package org.eclipse.osee.orcs.core.internal.relation.order;

import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Roberto E. Escobar
 */
public interface HasOrderData extends Iterable<Entry<RelationTypeSide, OrderData>> {

   void add(RelationTypeSide typeAndSide, OrderData data);

   void remove(RelationTypeSide typeAndSide);

   void clear();

   boolean isEmpty();

   int size();

}
