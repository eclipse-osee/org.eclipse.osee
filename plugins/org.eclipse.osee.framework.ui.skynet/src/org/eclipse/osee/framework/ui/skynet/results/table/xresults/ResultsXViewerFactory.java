/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.results.table.xresults;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;

/**
 * @author Donald G. Dunne
 */
public class ResultsXViewerFactory extends XViewerFactory {

   private static String COLUMN_NAMESPACE = "org.eclipse.osee.table";

   public ResultsXViewerFactory(List<XViewerColumn> columns) {
      super("xviewer.test");
      for (XViewerColumn xCol : columns) {
         registerColumns(xCol);
      }
   }

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return new XViewerCustomizations();
   }

   @Override
   public boolean isAdmin() {
      return true;
   }

   @Override
   public String getNamespace() {
      return COLUMN_NAMESPACE;
   }

}
