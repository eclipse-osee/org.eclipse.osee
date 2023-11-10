/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.taskest;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsCoreColumnToken;
import org.eclipse.osee.ats.ide.workflow.task.mini.MiniTaskXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstXViewerFactory extends MiniTaskXViewerFactory {

   public final static String NAMESPACE = "TaskEstXViewer";

   public XTaskEstXViewerFactory() {
      super(NAMESPACE);
   }

   // Return default visible columns in default order.  Override to change defaults.
   @Override
   public List<AtsCoreColumnToken> getDefaultVisibleColumns() {
      List<AtsCoreColumnToken> cols = new ArrayList<>();
      cols.add(AtsColumnTokensDefault.CheckColumn);
      cols.add(AtsColumnTokensDefault.AttachmentsCountColumn);
      cols.addAll(super.getDefaultVisibleColumns());
      cols.add(AtsColumnTokensDefault.NotesColumn);
      return cols;
   }

   // Return default visible column widths.  Empty list or missing will use default token width.
   @Override
   public List<Integer> getDefaultColumnWidths() {
      List<Integer> widths = new ArrayList<>();
      widths.add(53);
      widths.add(20);
      widths.addAll(super.getDefaultColumnWidths());
      widths.add(200);
      return widths;
   }

}
