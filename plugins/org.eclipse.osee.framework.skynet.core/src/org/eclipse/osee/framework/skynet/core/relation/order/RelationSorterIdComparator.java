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

import java.io.Serializable;
import java.util.Comparator;
import org.eclipse.osee.framework.core.data.RelationSorter;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationSorterIdComparator implements Serializable, Comparator<RelationSorter> {
   private static final long serialVersionUID = -8364743688821990429L;

   @Override
   public int compare(RelationSorter o1, RelationSorter o2) {
      return o1.getName().compareToIgnoreCase(o2.getName());
   }

}
