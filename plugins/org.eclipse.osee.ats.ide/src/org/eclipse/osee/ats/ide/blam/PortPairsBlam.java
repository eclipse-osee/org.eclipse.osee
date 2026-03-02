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

package org.eclipse.osee.ats.ide.blam;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
@Component(service = AbstractBlam.class, immediate = true)
public class PortPairsBlam extends AbstractAtsBlam {
   private final static String PORT_NAME = "Porting ID Pairs";

   private static final String USE_ATSID = "Use Ats IDs instead of RPCRs for Porting Workflows";

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      String pairs = variableMap.getString(PORT_NAME);
      boolean checked = variableMap.getBoolean(USE_ATSID);
      return new PortPairsOperation(logger, pairs, checked);
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<xWidgets>");
      sb.append("<XWidget xwidgetType=\"XText\" fill=\"Vertically\" displayName=\""+PORT_NAME+"\" />");
      sb.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\""+USE_ATSID+"\" horizontalLabel=\"true\"/>");
      sb.append("</xWidgets>");
      return sb.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "Port a given list of workflows represented by either RPCRs or ATS IDs, formated like: <from>,<to>. Each pair should be separated by a return. The BLAM can be run multiple times, as the conflicts are resolved on the port branch merges.";
   }

}