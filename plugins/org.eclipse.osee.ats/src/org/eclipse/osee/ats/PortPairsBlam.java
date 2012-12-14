/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public class PortPairsBlam extends AbstractBlam {
   private final static String PORT_NAME = "Porting ID Pairs";

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {
      String pairs = variableMap.getString(PORT_NAME);
      return new PortPairsOperation(logger, pairs);
   }

   @Override
   public Collection<String> getCategories() {
      return Collections.singletonList("Admin");
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<xWidgets>");
      sb.append("<XWidget xwidgetType=\"XText\" fill=\"Vertically\" displayName=\"");
      sb.append(PORT_NAME);
      sb.append("\" />");
      sb.append("</xWidgets>");
      return sb.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "Given list of RPCRs, <from>,<to> port the changes. Each pair should be separated by a return. The BLAM can be run multiple times, as the conflicts are resolved on the port branch merges.";
   }

}