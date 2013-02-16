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
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class ActivityIdColumn extends WorkPackageColumn {
   public static ActivityIdColumn instance = new ActivityIdColumn();

   public static ActivityIdColumn getInstance() {
      return instance;
   }

   public ActivityIdColumn() {
      super();
      setName("Activity Id");
      setId(WorldXViewerFactory.COLUMN_NAMESPACE + ".activityId");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ActivityIdColumn copy() {
      ActivityIdColumn newXCol = new ActivityIdColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
