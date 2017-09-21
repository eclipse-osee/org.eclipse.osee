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
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class MatchComparator implements Comparator<Match<ArtifactReadable, AttributeReadable<?>>> {

   private final NamedComparator comparator;

   public MatchComparator(SortOrder orderType) {
      comparator = new NamedComparator(orderType);
   }

   private Named getNamed(Match<ArtifactReadable, AttributeReadable<?>> match) {
      return match != null ? match.getItem() : null;
   }

   @Override
   public int compare(Match<ArtifactReadable, AttributeReadable<?>> o1, Match<ArtifactReadable, AttributeReadable<?>> o2) {
      return comparator.compare(getNamed(o1), getNamed(o2));
   }
}