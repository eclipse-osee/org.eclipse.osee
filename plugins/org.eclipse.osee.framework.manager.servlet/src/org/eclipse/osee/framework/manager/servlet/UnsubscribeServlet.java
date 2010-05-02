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
package org.eclipse.osee.framework.manager.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.commit.UpdatePreviousTxCurrent;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class UnsubscribeServlet extends OseeHttpServlet {

   private static final long serialVersionUID = -1515762009004235783L;
   private final IOseeDatabaseServiceProvider provider;

   public UnsubscribeServlet(IOseeDatabaseServiceProvider p) {
      provider = p;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String uri = request.getRequestURI();
      response.getWriter().append(confirmationPage(uri + "/confirm"));
   }

   private void handleError(HttpServletResponse response, int status, String message, Throwable ex) throws IOException {
      response.setStatus(status);
      response.setContentType("text/plain");
      OseeLog.log(Activator.class, Level.SEVERE, message, ex);
      response.getWriter().write(Lib.exceptionToString(ex));
   }

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
   }

   private static String confirmationPage(String confirmUri) {
      StringBuilder sb = new StringBuilder();
      sb.append("<HTML>");
      sb.append("<HEAD><TITLE>Unsubscribe confirmation</TITLE></HEAD>");
      sb.append("<BODY>");
      sb.append("<P>Are you sure you want to unsubscribe from this group?</P>");
      sb.append("<FORM>");
      sb.append("<BUTTON onclick=\"\">");
      sb.append("Confirm");
      sb.append("</BUTTON>");
      sb.append("</FORM>");
      sb.append("</BODY>");
      sb.append("</HTML>");
      return sb.toString();
   }

   @Override
   protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String uri = req.getRequestURI();
      String[] tokens = uri.split("/");
      int groupId = Integer.parseInt(tokens[tokens.length - 2]);
      int userId = Integer.parseInt(tokens[tokens.length - 1]);

      IOperation del = new DeleteRelationTransaction(provider, "operationName", Activator.PLUGIN_ID, groupId, userId);
      try {
         Operations.executeWorkAndCheckStatus(del, new LogProgressMonitor(), -1);
         resp.getWriter().write(deleteResponse());
      } catch (OseeCoreException ex) {
         handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error unsubscribing", ex);
      }
   }

   private static String deleteResponse() {
      StringBuilder sb = new StringBuilder();
      sb.append("<HTML>");
      sb.append("<HEAD><TITLE>Confirmed unsubscribe</TITLE></HEAD>");
      sb.append("<BODY>");
      sb.append("<P></P>");
      sb.append("</BODY>");
      sb.append("</HTML>");
      return sb.toString();
   }

   class DeleteRelationTransaction extends AbstractDbTxOperation {
      private OseeConnection connection;
      private int relationId;
      private int transactionId;
      private Branch commonBranch;
      private int gammaId;
      private Integer userId;
      private Integer groupId;

      public DeleteRelationTransaction(IOseeDatabaseServiceProvider provider, String operationName, String pluginId, int groupId, int userId) {
         super(provider, operationName, pluginId);
         this.userId = userId;
         this.groupId = groupId;
      }

      @Override
      protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
         this.connection = connection;
         fetchData();

         createTxDetailsRow();
         updateTxsData();
         addTxsData();
      }

      private void fetchData() throws OseeCoreException {
         commonBranch = Activator.getInstance().getOseeCache().getBranchCache().getCommonBranch();
         Integer relationTypeId =
               Activator.getInstance().getOseeCache().getRelationTypeCache().get(CoreRelationTypes.Users_Artifact).getId();
         String query =
               "select txs.gamma_id, rel.rel_link_id from osee_relation_link rel, osee_txs txs where rel.a_art_id = ? and rel.b_art_id=? and rel.rel_link_type_id=? and rel.gamma_id=txs.gamma_id and txs.branch_id=? and txs.tx_current = ?";

         IOseeStatement chStmt = Activator.getInstance().getOseeDatabaseService().getStatement();
         try {
            chStmt.runPreparedQuery(1, query, groupId, userId, relationTypeId, commonBranch.getId(),
                  TxChange.CURRENT.getValue());
            while (chStmt.next()) {
               gammaId = chStmt.getInt("gamma_id");
               relationId = chStmt.getInt("rel_link_id");
            }
         } finally {
            Lib.close(chStmt);
         }
      }

      private void createTxDetailsRow() throws OseeDataStoreException {
         int branchId = commonBranch.getId();
         transactionId = getDatabaseService().getSequence().getNextTransactionId();
         String comment = String.format("User %s requested unsubscribe from group %s", userId, groupId);
         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         int txType = TransactionDetailsType.NonBaselined.getId();
         IOseeStatement stmt = Activator.getInstance().getOseeDatabaseService().getStatement();
         stmt.runCallableStatement(
               "INSERT INTO osee_tx_details (branch_id, transaction_id, osee_comment, time, author, tx_type) VALUES (?,?,?,?,?,?)",
               branchId, transactionId, comment, timestamp, userId, txType);
      }

      private void updateTxsData() throws OseeCoreException {
         UpdatePreviousTxCurrent txc = new UpdatePreviousTxCurrent(commonBranch, connection);
         txc.addRelation(relationId);
         txc.updateTxNotCurrents();
      }

      private void addTxsData() throws OseeDataStoreException {
         IOseeStatement stmt = Activator.getInstance().getOseeDatabaseService().getStatement();
         stmt.runCallableStatement(
               "insert into osee_txs (mod_type, tx_current, transaction_id, gamma_id, branch_id) values (?, ?, ?, ?, ?)",
               ModificationType.DELETED.getValue(), TxChange.DELETED.getValue(), transactionId, gammaId,
               commonBranch.getId());
      }
   }
}
