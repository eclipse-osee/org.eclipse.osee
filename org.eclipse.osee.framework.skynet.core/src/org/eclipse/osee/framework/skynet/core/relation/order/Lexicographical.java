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

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactNameComparator;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
class Lexicographical implements RelationOrder {

   private final ArtifactNameComparator comparator;
   private final RelationOrderId id;

   Lexicographical(boolean descending, RelationOrderId id) {
      comparator = new ArtifactNameComparator(descending);
      this.id = id;
   }

   @Override
   public RelationOrderId getOrderId() {
      return id;
   }

   @Override
   public void sort(List<Artifact> relatives, List<String> relativeSequence) {
      Collections.sort(relatives, comparator);
   }
}
