/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.xviewer.example;

import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.example.util.MyLib;

public class MyDefaultCustomizations {

   public static CustomizeData getCompletionCustomization() {
      CustomizeData data = new CustomizeData();
      data.setName("Name Status");
      // Each customization must have it's own guid
      data.setGuid(MyLib.generateGuidStr());
      data.setNameSpace(MyXViewerFactory.COLUMN_NAMESPACE);

      // Columns must be copied cause they each store their own manipulation data and can be used
      // across multiple customizations.
      XViewerColumn nameColumn = MyXViewerFactory.Name_Col.copy();
      nameColumn.setSortForward(true);
      nameColumn.setWidth(175);
      nameColumn.setShow(true);
      data.getColumnData().getColumns().add(nameColumn);

      XViewerColumn percentCol = MyXViewerFactory.Completed_Col.copy();
      percentCol.setWidth(150);
      percentCol.setShow(true);

      data.getColumnData().getColumns().add(percentCol);
      return data;
   }

   public static CustomizeData getDescriptionCustomization() {
      CustomizeData data = new CustomizeData();
      data.setName("Name Description");
      data.setGuid(MyLib.generateGuidStr());
      data.setNameSpace(MyXViewerFactory.COLUMN_NAMESPACE);

      XViewerColumn descColumn = MyXViewerFactory.Description.copy();
      descColumn.setShow(true);
      descColumn.setWidth(250);
      data.getColumnData().getColumns().add(descColumn);

      XViewerColumn nameColumn = MyXViewerFactory.Name_Col.copy();
      nameColumn.setSortForward(true);
      nameColumn.setWidth(175);
      nameColumn.setShow(true);
      data.getColumnData().getColumns().add(nameColumn);

      return data;
   }
}
