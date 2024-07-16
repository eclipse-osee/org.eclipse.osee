/*********************************************************************
 * Copyright (c) 2018 Boeing
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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Megumi Telles
 */
public class ChangeArtifactName extends AbstractBlam {

   private final static String ID_NAME = "ID to New Name Pairs";
   private static final String BRANCH = "Branch Input";

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      String pairs = variableMap.getString(ID_NAME);
      BranchId branch = variableMap.getBranch(BRANCH);
      return new ChangeArtifactNameOperation(logger, pairs, branch);
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<xWidgets>");
      sb.append("<XWidget xwidgetType=\"XText\" fill=\"Vertically\" displayName=\"");
      sb.append(ID_NAME);
      sb.append("\" />");
      sb.append(String.format("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"%s\"/>", BRANCH));
      sb.append("</xWidgets>");
      return sb.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "Renames artifacts given list of \"art ids, new name\" pairs.  Each rename should be separated by a return.";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

}
