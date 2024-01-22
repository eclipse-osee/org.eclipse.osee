/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.world.mini;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsCoreColumnToken;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public abstract class MiniWorldXViewerFactory extends WorldXViewerFactory {

   public GoalArtifact soleGoalArtifact;

   public MiniWorldXViewerFactory(String namespace) {
      super(namespace, null);
   }

   // Return default visible columns in default order.  Override to change defaults.
   @Override
   public List<AtsCoreColumnToken> getDefaultVisibleColumns() {
      return Arrays.asList( //
         AtsColumnTokensDefault.TypeColumn, //
         AtsColumnTokensDefault.StateColumn, //
         AtsColumnTokensDefault.AssigneeColumn, //
         AtsColumnTokensDefault.TitleColumn, //
         AtsColumnTokensDefault.AtsIdColumn, //
         AtsColumnTokensDefault.TargetedVersionColumn, //
         AtsColumnTokensDefault.NotesColumn //
      );
   }

   // Return default visible column widths.  Empty list or missing will use default token width.
   @Override
   public List<Integer> getDefaultColumnWidths() {
      return Arrays.asList(150, 75, 100, 200, 75, 20, 200);
   }

}
