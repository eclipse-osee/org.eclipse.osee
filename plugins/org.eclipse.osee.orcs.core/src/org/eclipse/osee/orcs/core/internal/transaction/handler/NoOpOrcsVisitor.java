/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction.handler;

import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;

/**
 * @author Roberto E. Escobar
 */
public class NoOpOrcsVisitor implements OrcsVisitor {

   public NoOpOrcsVisitor() {
      super();
   }

   @Override
   public void visit(ArtifactData newData) {
      // Do Nothing
   }

   @Override
   public void visit(AttributeData newData) {
      // Do Nothing
   }

   @Override
   public void visit(RelationData data) {
      // Do Nothing
   }

}
