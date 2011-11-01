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
package org.eclipse.osee.orcs.utility;

import java.util.Comparator;
import org.eclipse.osee.framework.core.data.Named;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class MatchComparator implements Comparator<Match<ReadableArtifact, ReadableAttribute<?>>> {

   private final NameComparator comparator;

   public MatchComparator(SortOrder orderType) {
      comparator = new NameComparator(orderType);
   }

   private Named getNamed(Match<ReadableArtifact, ReadableAttribute<?>> match) {
      return match != null ? match.getItem() : null;
   }

   @Override
   public int compare(Match<ReadableArtifact, ReadableAttribute<?>> o1, Match<ReadableArtifact, ReadableAttribute<?>> o2) {
      return comparator.compare(getNamed(o1), getNamed(o2));
   }
}