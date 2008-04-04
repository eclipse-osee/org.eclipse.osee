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
package org.eclipse.osee.framework.database.data;

/**
 * @author Roberto E. Escobar
 */
public class ConstraintFactory {

   public static ConstraintElement getConstraint(ConstraintTypes constraintType, String schema, String id, boolean deferrable) {
      ConstraintElement element = null;
      if (constraintType != null) {
         switch (constraintType) {
            case FOREIGN_KEY:
               element = new ForeignKey(constraintType, schema, id, deferrable);
               break;
            case PRIMARY_KEY:
            case UNIQUE:
            case CHECK:
            default:
               element = new ConstraintElement(constraintType, schema, id, deferrable);
               break;
         }
      }
      return element;
   }
}
