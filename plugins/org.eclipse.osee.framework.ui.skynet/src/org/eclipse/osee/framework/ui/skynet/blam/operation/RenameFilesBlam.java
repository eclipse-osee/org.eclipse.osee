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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.ops.RenameFilesOperation;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan D. Brooks
 */
@Component(service = AbstractBlam.class, immediate = true)
public class RenameFilesBlam extends AbstractBlam {
   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      String parentFolder = variableMap.getString("Parent Folder");
      String pathPattern = variableMap.getString("Full Path Pattern");
      String replacement = variableMap.getString("Replacement");
      return new RenameFilesOperation(logger, parentFolder, pathPattern, replacement);
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andWidget("Parent Folder", WidgetId.XTextWidget);
      wb.andWidget("Full Path Pattern", WidgetId.XTextWidget);
      wb.andWidget("Replacement", WidgetId.XTextWidget);
      return wb.getXWidgetDatas();
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE);
   }

}