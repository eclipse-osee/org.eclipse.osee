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
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
import org.eclipse.osee.orcs.core.internal.artifact.RelationContainer;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationRowMapper implements RelationDataHandler {

   private final Map<Integer, ? extends RelationContainer> providersThatWillBeLoaded;

   public RelationRowMapper(Map<Integer, ? extends RelationContainer> providersThatWillBeLoaded) {
      this.providersThatWillBeLoaded = providersThatWillBeLoaded;
   }

   @Override
   public void onData(RelationData data) throws OseeCoreException {
      int parentId = data.getParentId();
      RelationContainer parent = providersThatWillBeLoaded.get(parentId);
      Conditions.checkNotNull(parent, "RelationContainer",
         "We recieved a RelationRow that should be added to a parent that wasn't found [%d]", parentId);
      parent.add(data);
   }
}
