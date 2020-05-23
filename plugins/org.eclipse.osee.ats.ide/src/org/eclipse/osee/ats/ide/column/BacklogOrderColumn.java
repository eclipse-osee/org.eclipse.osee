/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.column;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class BacklogOrderColumn extends GoalOrderColumn {

   public static final String COLUMN_ID = WorldXViewerFactory.COLUMN_NAMESPACE + ".backlogOrder";

   static BacklogOrderColumn instance = new BacklogOrderColumn();

   public static BacklogOrderColumn getInstance() {
      return instance;
   }

   public BacklogOrderColumn() {
      super(true, COLUMN_ID, "Backlog Order");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public BacklogOrderColumn copy() {
      BacklogOrderColumn newXCol = new BacklogOrderColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.AgileBacklog;
   }
}
