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

/**
 * @author Andrew M. Finkbeiner
 */
public class UserDefinedOrder implements RelationOrder {

   @Override
   public RelationOrderId getOrderId() {
      return RelationOrderBaseTypes.USER_DEFINED;
   }

   @Override
   public void sort(List<Artifact> relatives, List<String> relativeSequence) {
      if (relatives.size() > 1) {
         Collections.sort(relatives, new UserDefinedOrderComparator(relativeSequence));
      }
   }
}
