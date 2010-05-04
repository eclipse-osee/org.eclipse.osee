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
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.ats.XmlUtil;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class UnsubscribeServlet extends OseeHttpServlet {

   private static final long serialVersionUID = -263648072167664572L;
   private final IOseeDatabaseServiceProvider dbProvider;
   private final IOseeCachingServiceProvider cacheProvider;
   private final BundleContext bundleContext;

   public UnsubscribeServlet(BundleContext bundleContext, IOseeDatabaseServiceProvider dbProvider, IOseeCachingServiceProvider cacheProvider) {
      this.dbProvider = dbProvider;
      this.cacheProvider = cacheProvider;
      this.bundleContext = bundleContext;
   }

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         String requestUri = request.getRequestURL().toString();
         requestUri = requestUri.replace(request.getPathInfo(), "");

         UnsubscribeRequest data = UnsubscribeRequest.createFromURI(request);

         String page = createConfirmationPage(requestUri, data);
         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/html");
         response.setContentLength(page.length());
         response.getWriter().append(page);
      } catch (Exception ex) {
         handleError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during unsubscribe page creation",
               ex);
      }
   }

   private void handleError(HttpServletResponse response, int status, String message, Throwable ex) throws IOException {
      response.setStatus(status);
      response.setContentType("text/plain");
      OseeLog.log(Activator.class, Level.SEVERE, message, ex);
      response.getWriter().write(ex.toString());
   }

   private String createConfirmationPage(String uri, UnsubscribeRequest data) throws IOException {
      URL url = bundleContext.getBundle().getResource("templates/unsubscribeTemplate.html");
      InputStream inputStream = null;
      try {
         inputStream = url.openStream();
         String template = Lib.inputStreamToString(inputStream);
         return String.format(template, uri, data.getGroupId(), data.getUserId());
      } finally {
         Lib.close(inputStream);
      }
   }

   @Override
   protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         UnsubscribeRequest data = UnsubscribeRequest.createFromXML(request);
         IOperation del = new DeleteRelationTransaction(dbProvider, cacheProvider, data);
         Operations.executeWorkAndCheckStatus(del, new LogProgressMonitor(), -1);

         String message =
               String.format("Unsubscribed user [%s] from group [%s] - Success", data.getUserId(), data.getGroupId());
         response.setStatus(HttpServletResponse.SC_ACCEPTED);
         response.setContentType("text/plain");
         response.setContentLength(message.length());
         response.getWriter().append(message);
      } catch (Exception ex) {
         handleError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error unsubscribing", ex);
      }
   }

   private static final class UnsubscribeRequest {
      private static final Matcher URI_PATTERN_MATCHER = Pattern.compile("group/(\\d+)?/user/(\\d+)").matcher("");
      private final String groupId;
      private final String userId;

      public UnsubscribeRequest(String groupId, String userId) {
         super();
         this.groupId = groupId;
         this.userId = userId;
      }

      public int getGroupId() {
         return Integer.parseInt(groupId);
      }

      public int getUserId() {
         return Integer.parseInt(userId);
      }

      public static UnsubscribeRequest createFromXML(HttpServletRequest request) throws IOException, Exception {
         Element rootElement = XmlUtil.readXML(request.getInputStream());
         String groupId = Jaxp.getChildText(rootElement, "groupId");
         String userId = Jaxp.getChildText(rootElement, "userId");
         Conditions.checkNotNullOrEmpty(groupId, "groupId");
         Conditions.checkNotNullOrEmpty(userId, "userId");
         return new UnsubscribeRequest(groupId, userId);
      }

      public static UnsubscribeRequest createFromURI(HttpServletRequest request) throws OseeCoreException {
         String uri = request.getRequestURI();
         String groupId = null;
         String userId = null;
         URI_PATTERN_MATCHER.reset(uri);
         if (URI_PATTERN_MATCHER.find()) {
            groupId = URI_PATTERN_MATCHER.group(1);
            userId = URI_PATTERN_MATCHER.group(2);
         }
         Conditions.checkNotNullOrEmpty(groupId, "groupId");
         Conditions.checkNotNullOrEmpty(userId, "userId");
         return new UnsubscribeRequest(groupId, userId);
      }
   }

   private static final class DeleteRelationTransaction extends AbstractDbTxOperation {
      private final static String SELECT_RELATION_LINK =
            "select txs.gamma_id, rel.rel_link_id, txs.mod_type from osee_relation_link rel, osee_txs txs where rel.a_art_id = ? and rel.b_art_id = ? and rel.rel_link_type_id = ? and rel.gamma_id=txs.gamma_id and txs.branch_id = ? and txs.tx_current = ?";
      private final static String INSERT_INTO_TX_DETAILS =
            "insert into osee_tx_details (branch_id, transaction_id, osee_comment, time, author, tx_type) values (?,?,?,?,?,?)";
      private final static String INSERT_INTO_TXS =
            "insert into osee_txs (mod_type, tx_current, transaction_id, gamma_id, branch_id) values (?, ?, ?, ?, ?)";

      private Branch common;
      private int relationId;
      private int currentGammaId;
      private final UnsubscribeRequest unsubscribeData;
      private final IOseeCachingServiceProvider cacheProvider;

      public DeleteRelationTransaction(IOseeDatabaseServiceProvider provider, IOseeCachingServiceProvider cacheProvider, UnsubscribeRequest unsubscribeData) {
         super(provider, "Delete Relation", Activator.PLUGIN_ID);
         this.unsubscribeData = unsubscribeData;
         this.cacheProvider = cacheProvider;
      }

      @Override
      protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
         getRelationTxData();

         UpdatePreviousTxCurrent txc = new UpdatePreviousTxCurrent(common, connection);
         txc.addRelation(relationId);
         txc.updateTxNotCurrents();

         createNewTxAddressing(connection);
      }

      private void getRelationTxData() throws OseeCoreException {
         IOseeCachingService cacheService = cacheProvider.getOseeCachingService();
         common = cacheService.getBranchCache().getCommonBranch();
         RelationType relationType = cacheService.getRelationTypeCache().get(CoreRelationTypes.Users_Artifact);
         IOseeStatement chStmt = getDatabaseService().getStatement();

         try {
            chStmt.runPreparedQuery(1, SELECT_RELATION_LINK, unsubscribeData.getGroupId(), unsubscribeData.getUserId(),
                  relationType.getId(), common.getId(), TxChange.CURRENT.getValue());
            if (chStmt.next()) {
               currentGammaId = chStmt.getInt("gamma_id");
               relationId = chStmt.getInt("rel_link_id");
               int modType = chStmt.getInt("mod_type");
               ensureNotAlreadyDeleted(modType);
            } else {
               throw new OseeCoreException(String.format("No relation link found for group %s and user %s",
                     unsubscribeData.getGroupId(), unsubscribeData.getUserId()));
            }
         } finally {
            Lib.close(chStmt);
         }
      }

      private void ensureNotAlreadyDeleted(int modType) throws OseeCoreException {
         if (modType == ModificationType.ARTIFACT_DELETED.getValue() || modType == ModificationType.DELETED.getValue()) {
            throw new OseeCoreException("Relation already deleted");
         }
      }

      @SuppressWarnings("unchecked")
      private void createNewTxAddressing(OseeConnection connection) throws OseeDataStoreException {
         int transactionId = getDatabaseService().getSequence().getNextTransactionId();
         String comment =
               String.format("User %s requested unsubscribe from group %s", unsubscribeData.getUserId(),
                     unsubscribeData.getGroupId());
         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         int txType = TransactionDetailsType.NonBaselined.getId();

         getDatabaseService().runPreparedUpdate(connection, INSERT_INTO_TX_DETAILS, common.getId(), transactionId,
               comment, timestamp, unsubscribeData.getUserId(), txType);
         getDatabaseService().runPreparedUpdate(connection, INSERT_INTO_TXS, ModificationType.DELETED.getValue(),
               TxChange.DELETED.getValue(), transactionId, currentGammaId, common.getId());
      }
   }
}
