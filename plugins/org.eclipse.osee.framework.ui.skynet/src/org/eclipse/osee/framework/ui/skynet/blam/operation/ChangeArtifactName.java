/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
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
   public Collection<String> getCategories() {
      return Collections.singletonList("Define");
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
   public String getTarget() {
      return TARGET_ALL;
   }

}
