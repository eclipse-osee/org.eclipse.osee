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
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.Comparator;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class RelationSorterIdComparator implements Comparator<IRelationSorterId> {

   @Override
   public int compare(IRelationSorterId o1, IRelationSorterId o2) {
      return o1.prettyName().compareToIgnoreCase(o2.prettyName());
   }

}
