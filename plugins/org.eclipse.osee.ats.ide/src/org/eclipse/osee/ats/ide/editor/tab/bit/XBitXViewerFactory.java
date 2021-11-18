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
package org.eclipse.osee.ats.ide.editor.tab.bit;

import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

/**
 * @author Donald G. Dunne
 */
public class XBitXViewerFactory extends XViewerFactory {

   public final static String NAMESPACE = "BitXViewer";

   // @formatter:off

   public static XViewerColumn Program_Col = new XViewerColumn("ats.bit.program", "Program", 150, XViewerAlign.Left, true, SortDataType.String, false, "");
   public static XViewerColumn Build_Col = new XViewerColumn("ats.bit.build", "Build", 110, XViewerAlign.Left, true, SortDataType.String, false, "");
   public static XViewerColumn Config_Col = new XViewerColumn("ats.bit.config", "Config", 60, XViewerAlign.Left, true, SortDataType.String, false, "");
   public static XViewerColumn State_Col = new XViewerColumn("ats.bibit.state", "State", 80, XViewerAlign.Left, true, SortDataType.String, false, "");
   public static XViewerColumn Id_Col = new XViewerColumn("ats.bibit.cr.id", "Id", 55, XViewerAlign.Left, true, SortDataType.String, false, "");
   public static XViewerColumn Cr_State_Col = new XViewerColumn("ats.bibit.cr.state", "CR State", 100, XViewerAlign.Left, true, SortDataType.String, false, "Show related Team Workflows, if created");
   public static XViewerColumn Cr_Type_Col = new XViewerColumn("ats.bibit.cr.type", "CR Type", 100, XViewerAlign.Left, true, SortDataType.String, false, "Show related Team Workflows, if created");
   public static XViewerColumn Cr_Title_Col = new XViewerColumn("ats.bibit.cr.title", "CR Title", 480, XViewerAlign.Left, true, SortDataType.String, false, "Show related Team Workflows, if created");

   // @formatter:on

   public XBitXViewerFactory() {
      super(NAMESPACE);
      registerColumns(Program_Col, Build_Col, Config_Col, State_Col, Id_Col, Cr_State_Col, Cr_Type_Col, Cr_Title_Col);
   }

   @Override
   public boolean isAdmin() {
      return true;
   }

}
