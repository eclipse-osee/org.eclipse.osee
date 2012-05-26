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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.WritableArtifact;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class UnsubscribeServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = -263648072167664572L;
   private final BundleContext bundleContext;
   private final OrcsApi orcsApi;

   public UnsubscribeServlet(Log logger, BundleContext bundleContext, OrcsApi orcsApi) {
      super(logger);
      this.bundleContext = bundleContext;
      this.orcsApi = orcsApi;
   }

   private OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
      getLogger().error(ex, message);
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
   protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         UnsubscribeRequest data = UnsubscribeRequest.createFromXML(request);
         int groupId = data.getGroupId();
         int userId = data.getUserId();

         QueryFactory queryFactory = getOrcsApi().getQueryFactory(null);

         ReadableArtifact authorArtifact = getArtifactById(queryFactory, data.getUserId());
         ReadableArtifact groupArtifact = getArtifactById(queryFactory, data.getGroupId());

         String comment =
            String.format("UserId [%s] requested unsubscribe from group [%s]",
               authorArtifact.getSoleAttributeAsString(CoreAttributeTypes.UserId), groupArtifact.getName());

         TransactionFactory factory = orcsApi.getTransactionFactory(null);
         OrcsTransaction txn = factory.createTransaction(CoreBranches.COMMON, authorArtifact, comment);

         WritableArtifact group = txn.asWritable(groupArtifact);

         // TODO
         group.deleteRelation(CoreRelationTypes.Users_Artifact, authorArtifact);
         //         txn.deleteRelation(CoreRelationTypes.Users_Artifact, groupId, userId);

         txn.commit();

         String message = String.format("<br/>You have been successfully unsubscribed.");

         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType("text/plain");
         response.setContentLength(message.length());
         response.getWriter().append(message);
      } catch (Exception ex) {
         handleError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error unsubscribing", ex);
      }
   }

   protected ReadableArtifact getArtifactById(QueryFactory queryFactory, int id) throws OseeCoreException {
      ReadableArtifact artifact = null;
      if (id > 0) {
         artifact = queryFactory.fromBranch(CoreBranches.COMMON).andLocalId(id).getResults().getExactlyOne();
      }
      return artifact;
   }
}