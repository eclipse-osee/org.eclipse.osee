/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class DatastoreStatistics extends AbstractBlam {
   private static final String SELECT_ARTIFACT_COUNTS =
         "select count(1) as total, name FROM osee_artifact_type ary, osee_artifact art where ary.art_type_id = art.art_type_id group by name order by total desc";

   @Override
   public String getName() {
      return "Datastore Statistics";
   }

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(1000, SELECT_ARTIFACT_COUNTS);
         while (chStmt.next()) {
            print(chStmt.getString("name"));
            print(": ");
            print(String.valueOf(chStmt.getInt("total")));
            print("\n");
         }
      } finally {
         chStmt.close();
      }
   }

   @Override
   public String getXWidgetsXml() {
      return AbstractBlam.emptyXWidgetsXml;
   }

   @Override
   public String getDescriptionUsage() {
      return "Reports statistics about the datastore including artifact counts by type.";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Admin.Health");
   }
}