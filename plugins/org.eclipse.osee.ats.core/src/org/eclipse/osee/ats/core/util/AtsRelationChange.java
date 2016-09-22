/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Donald G. Dunne
 */
public class AtsRelationChange {

   public static enum RelationOperation {
      Add,
      Delete;
   }

   private final Object object;
   private final RelationTypeSide relationSide;
   private final Collection<Object> objects;
   private final RelationOperation operation;

   public AtsRelationChange(Object object, RelationTypeSide relationSide, Collection<? extends Object> objects, RelationOperation operation) {
      this.object = object;
      this.relationSide = relationSide;
      this.objects = new ArrayList<>(objects);
      this.operation = operation;
   }

   public Object getObject() {
      return object;
   }

   public RelationTypeSide getRelationSide() {
      return relationSide;
   }

   public Collection<Object> getObjects() {
      return objects;
   }

   public RelationOperation getOperation() {
      return operation;
   }

}
