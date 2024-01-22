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

package org.eclipse.osee.ats.ide.workflow.task.mini;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsCoreColumnToken;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public abstract class MiniTaskXViewerFactory extends WorldXViewerFactory {

   public MiniTaskXViewerFactory(String namespace) {
      super(namespace, null);
   }

   // Return default visible columns in default order.  Override to change defaults.
   @Override
   public List<AtsCoreColumnToken> getDefaultVisibleColumns() {
      return Arrays.asList( //
         AtsColumnTokensDefault.TitleColumn, //
         AtsColumnTokensDefault.StateColumn, //
         AtsColumnTokensDefault.AgileTeamPointsColumn, //
         AtsColumnTokensDefault.ReviewedByAndDateColumn, //
         AtsColumnTokensDefault.RiskFactorColumn, //
         AtsColumnTokensDefault.DescriptionColumn, //
         AtsColumnTokensDefault.AssumptionsColumn, //
         AtsColumnTokensDefault.AtsIdColumn //
      );
   }

   // Return default visible column widths.  Empty list or missing will use default token width.
   @Override
   public List<Integer> getDefaultColumnWidths() {
      return Arrays.asList(200, 75, 20, 30, 100, 40, 150, 150, 40);
   }

}
