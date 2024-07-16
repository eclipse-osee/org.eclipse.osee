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

package org.eclipse.osee.ats.ide.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.swt.graphics.Image;

/**
 * @author Megumi Telles
 */
public class PurgeUser extends AbstractBlam {

   public final static String FROM_USER = "From User";
   public final static String TO_USER = "To User";
   private static int numOfAuthoredTransactions = 0;
   private static int numOfASideRelations = 0;
   private static int numOfBSideRelations = 0;
   private static int numOfUpdatedAuthoredTransactions = 0;
   private static int numOfUpdatedASideRelations = 0;
   private static int numOfUpdatedBSideRelations = 0;
   private static boolean persist = false;

   private static final int defaultUpdateValue = -1;

   private static final String GET_AUTHORED_TRANSACTIONS = "SELECT count(1) from osee_tx_details where author=?";
   private static final String GET_RELATIONS_ASIDE = "SELECT count(1) from osee_relation_link where a_art_id=?";
   private static final String GET_RELATIONS_BSIDE = "SELECT count(1) from osee_relation_link where b_art_id=?";
   private static final String UPDATE_AUTHORED_TRANSACTIONS = "update osee_tx_details set author=? where author=?";
   private static final String UPDATE_RELATIONS_ASIDE = "update osee_relation_link set a_art_id=? where a_art_id=?";
   private static final String UPDATE_RELATIONS_BSIDE = "update osee_relation_link set b_art_id=? where b_art_id=?";
   private final JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();

   @Override
   public String getName() {
      return "Admin - Purge User";
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            try {
               persist = variableMap.getBoolean("Persist");

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
               jdbcClient.runTransaction(new JdbcTransaction() {
                  @Override
                  public void handleTxWork(JdbcConnection connection) {
                     // start replacing all transactions, relations, etc.
                     findAndUpdateAuthoredTransactions(connection, fromUser, toUser);
                     findAndUpdateRelations(connection, fromUser, toUser);
                  }
               });
               // confirm deletion of artifact
               confirmDeletionOfArtifact(fromUser);
               // output results
               displayReport(toUser, fromUser);
            } catch (Exception ex) {
               log(ex);
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

   private void confirmDeletionOfArtifact(final User fromUser) {
      if (persist) {
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Persist Confirmation",
            "Do you wish to delete the User artifact: " + fromUser.getName() + "?")) {
            deleteArtifact(fromUser);
         }
      }
   }

   private void findAndUpdateAuthoredTransactions(JdbcConnection connection, final User fromUser, final User toUser) {
      numOfAuthoredTransactions = jdbcClient.fetch(-1, GET_AUTHORED_TRANSACTIONS, fromUser);
      if (persist) {
         numOfUpdatedAuthoredTransactions =
            jdbcClient.runPreparedUpdate(connection, UPDATE_AUTHORED_TRANSACTIONS, toUser, fromUser);
      }
   }

   private void findAndUpdateRelations(JdbcConnection connection, final User fromUser, final User toUser) {
      updateRelationA(connection, fromUser, toUser);
      updateRelationB(connection, fromUser, toUser);
   }

   private void updateRelationA(JdbcConnection connection, final User fromUser, final User toUser) {
      numOfASideRelations = jdbcClient.fetch(defaultUpdateValue, GET_RELATIONS_ASIDE, fromUser);
      if (persist) {
         numOfUpdatedASideRelations =
            jdbcClient.runPreparedUpdate(connection, UPDATE_RELATIONS_ASIDE, toUser, fromUser);
      }
   }

   private void updateRelationB(JdbcConnection connection, final User fromUser, final User toUser) {
      numOfBSideRelations = jdbcClient.fetch(defaultUpdateValue, GET_RELATIONS_BSIDE, fromUser);
      if (persist) {
         numOfUpdatedBSideRelations =
            jdbcClient.runPreparedUpdate(connection, UPDATE_RELATIONS_BSIDE, toUser, fromUser);
      }
   }

   private void deleteArtifact(final User fromUser) {
      Artifact art = ArtifactQuery.getArtifactFromToken(fromUser);
      art.purgeFromBranch();
   }

   private void displayReport(User toUser, User fromUser) {
      XResultData rd = new XResultData();
      rd.logf("%s\n\n", getName());
      if (persist) {
         rd.logf("Persisted Changes to DB and Purged User Artifact\n\n", getName());
      } else {
         rd.logf("REPORT ONLY; No changes made\n\n", getName());
      }
      try {
         String[] columnHeaders = new String[] {
            "FromUser",
            "FromUser ArtId",
            "ToUser",
            "ToUser ArtId",
            "Authored Transaction Hits",
            "Relation ASide Hits",
            "Relation BSide Hits",
            "Authored Transaction Updated",
            "Relation ASide Update",
            "Relation BSide Updated"};
         rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {
            fromUser.getName(),
            fromUser.getIdString(),
            toUser.getName(),
            toUser.getIdString(),
            Integer.toString(numOfAuthoredTransactions),
            Integer.toString(numOfASideRelations),
            Integer.toString(numOfBSideRelations),
            Integer.toString(numOfUpdatedAuthoredTransactions),
            Integer.toString(numOfUpdatedASideRelations),
            Integer.toString(numOfUpdatedBSideRelations)}));
         rd.addRaw(AHTML.endMultiColumnTable());
      } finally {
         XResultDataUI.report(rd, getName());
      }
   }

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      XWidgetBuilder wb = new XWidgetBuilder();
      wb.andWidget(FROM_USER, "XMembersComboAll").endWidget();
      wb.andWidget(TO_USER, "XMembersComboAll").endWidget();
      wb.andXCheckbox("Persist").endWidget();
      return wb.getItems();
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge the specified User.  Choose user to purge and user to re-assign existing transactions and relations.  Select Persist to make changes and purge User Artifact.";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.USER_MANAGEMENT_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.X_RED);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.X_RED);
   }

}
