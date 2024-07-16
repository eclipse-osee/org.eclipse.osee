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
import org.eclipse.osee.define.ide.blam.operation.FixAttributeOperation.Display;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Angel Avila
 */
public class FixDuplicateEnumeratedAttributes extends AbstractBlam {

   public static String SELECT_BRANCH_LABEL = "Branch";
   public static String COMMIT_CHANGES_LABEL = "Commit Changes to Branch?";

   @Override
   public String getName() {
      return "Fix Duplicate Enumerated Attributes";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidgets>");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" multiSelect=\"false\" displayName=\"");
      builder.append(SELECT_BRANCH_LABEL);
      builder.append("\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(COMMIT_CHANGES_LABEL);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("</XWidgets>");
      return builder.toString();
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