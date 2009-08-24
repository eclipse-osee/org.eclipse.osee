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
package org.eclipse.osee.ats.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Megumi Telles
 */
public class PurgeUser extends AbstractBlam {

   public static String FROM_USER = "From User";
   public static String TO_USER = "To User";
   private static int numOfAuthoredTransactions = 0;
   private static int numOfASideRelations = 0;
   private static int numOfBSideRelations = 0;
   private static int numOfUpdatedAuthoredTransactions = 0;
   private static int numOfUpdatedASideRelations = 0;
   private static int numOfUpdatedBSideRelations = 0;

   private static final int defaultUpdateValue = -1;

   private static final String GET_AUTHORED_TRANSACTIONS = "SELECT count(1) from osee_tx_details where author=?";
   private static final String GET_RELATIONS_ASIDE = "SELECT count(1) from osee_relation_link where a_art_id=?";
   private static final String GET_RELATIONS_BSIDE = "SELECT count(1) from osee_relation_link where b_art_id=?";
   private static final String UPDATE_AUTHORED_TRANSACTIONS = "update osee_tx_details set author=? where author=?";
   private static final String UPDATE_RELATIONS_ASIDE = "update osee_relation_link set a_art_id=? where a_art_id=?";
   private static final String UPDATE_RELATIONS_BSIDE = "update osee_relation_link set b_art_id=? where b_art_id=?";

   @Override
   public String getName() {
      return "Purge User";
   }

   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException {
      final IProgressMonitor mon = monitor;
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               //TODO Allow for multiple users to be selected at one time.
               final User fromUser = variableMap.getUser(FROM_USER);
               if (fromUser == null) {
                  AWorkbench.popup("ERROR", "Please select From User");
                  return;
               }

               final User toUser = variableMap.getUser(TO_USER);
               if (toUser == null) {
                  AWorkbench.popup("ERROR", "Please select To User");
                  return;
               }
               //handle roll-backs and exception handling 
               new DbTransaction() {
                  @Override
                  protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
                     // start replacing all transactions, relations, etc.
                     findAndUpdateAuthoredTransactions(connection, fromUser, toUser);
                     findAndUpdateRelations(connection, fromUser, toUser);
                  }
               }.execute();
               // confirm deletion of artifact
               confirmDeletionOfArtifact(fromUser);
               // output results
               displayReport(mon, toUser, fromUser);
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            } finally {
               numOfAuthoredTransactions = 0;
               numOfASideRelations = 0;
               numOfBSideRelations = 0;
               numOfUpdatedAuthoredTransactions = 0;
               numOfUpdatedASideRelations = 0;
               numOfUpdatedBSideRelations = 0;
            }
         };
      });
   }

   private void confirmDeletionOfArtifact(final User fromUser) throws OseeCoreException {
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Persist Confirmation",
            "Do you wish to delete the duplicate User: " + fromUser.getName() + "?")) {
         deleteArtifact(fromUser);
      }
   }

   private void findAndUpdateAuthoredTransactions(OseeConnection connection, final User fromUser, final User toUser) throws OseeDataStoreException {
      numOfAuthoredTransactions =
            ConnectionHandler.runPreparedQueryFetchInt(defaultUpdateValue, GET_AUTHORED_TRANSACTIONS,
                  new Object[] {fromUser.getArtId()});
      numOfUpdatedAuthoredTransactions =
            ConnectionHandler.runPreparedUpdate(connection, UPDATE_AUTHORED_TRANSACTIONS, new Object[] {
                  toUser.getArtId(), fromUser.getArtId()});
   }

   private void findAndUpdateRelations(OseeConnection connection, final User fromUser, final User toUser) throws OseeDataStoreException {
      updateRelationA(connection, fromUser, toUser);
      updateRelationB(connection, fromUser, toUser);
   }

   private void updateRelationA(OseeConnection connection, final User fromUser, final User toUser) throws OseeDataStoreException {
      numOfASideRelations =
            ConnectionHandler.runPreparedQueryFetchInt(defaultUpdateValue, GET_RELATIONS_ASIDE,
                  new Object[] {fromUser.getArtId()});
      numOfUpdatedASideRelations =
            ConnectionHandler.runPreparedUpdate(connection, UPDATE_RELATIONS_ASIDE, new Object[] {toUser.getArtId(),
                  fromUser.getArtId()});
   }

   private void updateRelationB(OseeConnection connection, final User fromUser, final User toUser) throws OseeDataStoreException {
      numOfBSideRelations =
            ConnectionHandler.runPreparedQueryFetchInt(defaultUpdateValue, GET_RELATIONS_BSIDE,
                  new Object[] {fromUser.getArtId()});
      numOfUpdatedBSideRelations =
            ConnectionHandler.runPreparedUpdate(connection, UPDATE_RELATIONS_BSIDE, new Object[] {toUser.getArtId(),
                  fromUser.getArtId()});
   }

   private void deleteArtifact(final User fromUser) throws OseeCoreException {
      Artifact art = ArtifactQuery.getArtifactFromId(fromUser.getArtId(), fromUser.getBranch());
      art.purgeFromBranch();
   }

   private void displayReport(IProgressMonitor monitor, User toUser, User fromUser) throws OseeCoreException {
      XResultData rd = new XResultData();
      try {
         String[] columnHeaders =
               new String[] {"FromUser", "FromUser ArtId", "ToUser", "ToUser ArtId", "Authored Transaction Hits",
                     "Relation ASide Hits", "Relation BSide Hits", "Authored Transaction Updated",
                     "Relation ASide Update", "Relation BSide Updated"};
         rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {fromUser.getName(),
               Integer.toString(fromUser.getArtId()), toUser.getName(), Integer.toString(toUser.getArtId()),
               Integer.toString(numOfAuthoredTransactions), Integer.toString(numOfASideRelations),
               Integer.toString(numOfBSideRelations), Integer.toString(numOfUpdatedAuthoredTransactions),
               Integer.toString(numOfUpdatedASideRelations), Integer.toString(numOfUpdatedBSideRelations)}));
         rd.addRaw(AHTML.endMultiColumnTable());
      } finally {
         rd.report(getName());
      }
   }

   @Override
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XMembersCombo\" displayName=\"" + FROM_USER + "\" />");
      buffer.append("<XWidget xwidgetType=\"XMembersCombo\" displayName=\"" + TO_USER + "\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge the specified User.  You will be prompted to choose which user to re-assign existing transactions and relations.";
   }

   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}