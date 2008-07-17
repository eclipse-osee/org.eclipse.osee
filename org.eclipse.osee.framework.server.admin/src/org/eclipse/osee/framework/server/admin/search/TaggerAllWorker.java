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
package org.eclipse.osee.framework.server.admin.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.server.admin.Activator;

/**
 * @author Roberto E. Escobar
 */
class TaggerAllWorker extends BaseCmdWorker {

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES =
         "SELECT attr1.gamma_id FROM osee_define_attribute attr1, osee_define_attribute_type type1 WHERE attr1.attr_type_id = type1.attr_type_id AND type1.tagger_id IS NOT NULL AND NOT type1.tagger_id = ''";

   private ISearchEngineTagger searchTagger;

   TaggerAllWorker() {
      super();
      this.searchTagger = Activator.getInstance().getSearchTagger();
   }

   protected void doWork(long startTime) throws Exception {
      Connection connection = null;
      ResultSet resultSet = null;
      Statement statement = null;
      try {
         connection = ConnectionHandler.getConnection();
         statement = connection.createStatement();
         resultSet = statement.executeQuery(FIND_ALL_TAGGABLE_ATTRIBUTES);
         int count = 0;
         while (resultSet.next() && isExecutionAllowed()) {
            long gammaId = resultSet.getLong("gamma_id");
            searchTagger.tagAttribute(gammaId);

            count++;
            if (count % 100 == 0) {
               if (isVerbose()) {
                  println(String.format("%d processed, Elapsed Time = %s.", count, getElapsedTime(startTime)));
               }
            }
         }
         while (searchTagger.getWorkersInQueue() > 0 && isExecutionAllowed()) {
         }
         if (isVerbose()) {
            println(String.format("Waiting in Queue: " + searchTagger.getWorkersInQueue()));
            println(String.format("%d processed, Elapsed Time = %s.", count, getElapsedTime(startTime)));
         }
      } finally {
         try {
            if (resultSet != null) {
               resultSet.close();
            }
         } catch (SQLException ex) {
            printStackTrace(ex);
         }
         try {
            if (statement != null) {
               statement.close();
            }
         } catch (SQLException ex) {
            printStackTrace(ex);
         }
         try {
            if (connection != null) {
               connection.close();
            }
         } catch (SQLException ex) {
            printStackTrace(ex);
         }
      }
   }
}
