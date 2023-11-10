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

import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class BacklogOrderColumnUI extends GoalOrderColumnUI {

   public static final String COLUMN_ID = WorldXViewerFactory.COLUMN_NAMESPACE + ".backlogOrder";

   static BacklogOrderColumnUI instance = new BacklogOrderColumnUI();

   public static BacklogOrderColumnUI getInstance() {
      return instance;
   }

   public BacklogOrderColumnUI() {
      super(true, AtsColumnTokensDefault.BacklogOrderColumn);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public BacklogOrderColumnUI copy() {
      BacklogOrderColumnUI newXCol = new BacklogOrderColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.AgileBacklog;
   }
}
