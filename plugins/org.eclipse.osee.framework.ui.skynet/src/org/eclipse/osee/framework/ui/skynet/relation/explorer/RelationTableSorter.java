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
package org.eclipse.osee.framework.ui.skynet.relation.explorer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Michael S. Rodgers
 */
@SuppressWarnings("deprecation")
public class RelationTableSorter extends ViewerSorter {

   /**
    * Constructor argument values that indicate to sort items by name or type.
    */
   public final static int ARTIFACT_NAME = 1;
   public final static int ARTIFACT_TYPE = 2;

   // Criteria that the instance uses
   private final int criteria;

   /**
    * Creates a resource sorter that will use the given sort criteria.
    *
    * @param criteria the sort criterion to use: one of <code>NAME</code> or <code>TYPE</code>
    */
   public RelationTableSorter(int criteria) {
      super();
      this.criteria = criteria;
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {

      ArtifactModel model1 = (ArtifactModel) o1;
      ArtifactModel model2 = (ArtifactModel) o2;

      switch (criteria) {
         case ARTIFACT_NAME:
            return compareNames(model1, model2);
         case ARTIFACT_TYPE:
            return compareTypes(model1, model2);
         default:
            return 0;
      }
   }

   /**
    * Returns a number reflecting the collation order of the given names based on the description.
    *
    * @param model1 the first task element to be ordered
    * @param model2 the second task element to be ordered
    * @return a negative number if the first element is less than the second element; the value <code>0</code> if the
    * first element is equal to the second element; and a positive number if the first element is greater than the
    * second element
    */
   protected int compareNames(ArtifactModel model1, ArtifactModel model2) {
      return getComparator().compare(model1.getName(), model1.getName());
   }

   /**
    * Returns a number reflecting the collation order of the given tasks based on their owner.
    *
    * @param model1 the first resource element to be ordered
    * @param model2 the second resource element to be ordered
    * @return a negative number if the first element is less than the second element; the value <code>0</code> if the
    * first element is equal to the second element; and a positive number if the first element is greater than the
    * second element
    */
   protected int compareTypes(ArtifactModel model1, ArtifactModel model2) {
      return getComparator().compare(model1.getDescriptor().getName(), model2.getDescriptor().getName());
   }

   /**
    * Returns the sort criteria of this this sorter.
    *
    * @return the sort criterion
    */
   public int getCriteria() {
      return criteria;
   }
}
