/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.define.ide.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.define.ide.blam.operation.FixAttributeOperation.Display;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Angel Avila
 */
@Component(service = AbstractBlam.class, immediate = true)
public class FixDuplicateEnumeratedAttributesBlam extends AbstractBlam {

   public static String SELECT_BRANCH_LABEL = "Branch";
   public static String COMMIT_CHANGES_LABEL = "Commit Changes to Branch?";

   @Override
   public String getName() {
      return "Fix Duplicate Enumerated Attributes";
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andWidget(SELECT_BRANCH_LABEL, WidgetId.XBranchSelectWidget).andSingleSelect();
      wb.andWidget(COMMIT_CHANGES_LABEL, WidgetId.XCheckBoxWidget).andLabelAfter().andHorizLabel();
      return wb.getXWidgetDatas();
   }

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      BranchId branch = variableMap.getBranch(SELECT_BRANCH_LABEL);
      boolean commitChangesBool = variableMap.getBoolean(COMMIT_CHANGES_LABEL);

      Display display = new FixAttributesUIReport();
      return new FixAttributeOperation(logger, display, BranchManager.getBranchToken(branch), commitChangesBool);
   }

   @Override
   public String getDescriptionUsage() {
      return "Remove duplciate enumerated attributes";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

}