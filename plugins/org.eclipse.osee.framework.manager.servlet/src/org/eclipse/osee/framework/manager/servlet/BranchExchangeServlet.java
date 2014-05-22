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
import java.net.URI;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.data.HttpBranchExchangeInfo;
import org.eclipse.osee.framework.manager.servlet.internal.ApplicationContextFactory;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.search.BranchQuery;
import com.google.common.collect.Lists;

/**
 * @author Roberto E. Escobar
 */
public class BranchExchangeServlet extends SecureOseeHttpServlet {

   private static final long serialVersionUID = -1642995618810911260L;

   private final IResourceManager resourceManager;
   private final OrcsApi orcsApi;

   public BranchExchangeServlet(Log logger, ISessionManager sessionManager, IResourceManager resourceManager, OrcsApi orcsApi) {
      super(logger, sessionManager);
      this.orcsApi = orcsApi;
      this.resourceManager = resourceManager;
   }

   private ApplicationContext getContext(HttpServletRequest req) {
      return ApplicationContextFactory.createContext(getSessionId(req));
   }

   private OrcsBranch getBranchOps(ApplicationContext context) {
      return orcsApi.getBranchOps(context);
   }

   private BranchQuery getBranchQuery(ApplicationContext context) {
      return orcsApi.getQueryFactory(context).branchQuery();
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException {
      try {
         HttpBranchExchangeInfo exchangeInfo = new HttpBranchExchangeInfo(req);
         ApplicationContext context = getContext(req);
         OrcsBranch branchOps = getBranchOps(context);
         switch (exchangeInfo.getFunction()) {
            case exportBranch:
               executeExport(branchOps, getBranchQuery(context), exchangeInfo, response);
               break;
            case importBranch:
               executeImport(branchOps, getBranchQuery(context), exchangeInfo, response);
               break;
            case checkExchange:
               executeCheckExchange(branchOps, exchangeInfo, response);
            default:
               break;
         }
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         response.setContentType("text/plain");
         getLogger().error(ex, "Error processing [%s]", req);
         response.getWriter().write(Lib.exceptionToString(ex));
      }
      response.getWriter().flush();
      response.getWriter().close();
   }

   private void executeCheckExchange(OrcsBranch orcsBranch, HttpBranchExchangeInfo exchangeInfo, HttpServletResponse response) throws Exception {
      int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      StringBuffer message = new StringBuffer();

      String path = exchangeInfo.getPath();
      IResourceLocator exchangeLocator = resourceManager.getResourceLocator(path);
      Callable<URI> callable = orcsBranch.checkBranchExchangeIntegrity(exchangeLocator.getLocation());
      URI verifyUri = callable.call();
      status = HttpServletResponse.SC_ACCEPTED;
      message.append(String.format("Verification at: [%s]", verifyUri.toASCIIString()));

      response.setStatus(status);
      response.setContentType("text/plain");
      response.getWriter().write(message.toString());
   }

   private void executeExport(OrcsBranch orcsBranch, BranchQuery branchQuery, HttpBranchExchangeInfo exchangeInfo, HttpServletResponse response) throws Exception {
      int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      StringBuffer message = new StringBuffer();

      branchQuery.andUuids(exchangeInfo.getSelectedBranchUuids()).includeArchived().includeDeleted();
      LinkedList<IOseeBranch> branches = Lists.newLinkedList(branchQuery.getResultsAsId());

      Callable<URI> callable =
         orcsBranch.exportBranch(branches, exchangeInfo.getOptions(), exchangeInfo.getExchangeFileName());
      URI exportURI = callable.call();

      IResourceLocator exchangeLocator = resourceManager.getResourceLocator(exportURI.toASCIIString());
      status = HttpServletResponse.SC_ACCEPTED;
      message.append(String.format("Exported: [%s]", exchangeLocator.getLocation().toASCIIString()));

      if (exchangeInfo.isSendExportFile()) {
         InputStream exportFileStream = null;
         try {
            IResource resource = resourceManager.acquire(exchangeLocator, new PropertyStore());
            exportFileStream = resource.getContent();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentLength(exportFileStream.available());
            response.setCharacterEncoding("ISO-8859-1");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=" + resource.getName());
            Lib.inputStreamToOutputStream(exportFileStream, response.getOutputStream());
         } finally {
            if (exportFileStream != null) {
               exportFileStream.close();
            }
         }
      }

      if (exchangeInfo.isDeleteExportFile()) {
         int deleteResult = resourceManager.delete(exchangeLocator);
         if (deleteResult == IResourceManager.OK) {
            status = HttpServletResponse.SC_ACCEPTED;
         } else {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            message.append(String.format("Error during deletion of: [%s]",
               exchangeLocator.getLocation().toASCIIString()));
         }
      }
      response.setStatus(status);
      response.setContentType("text/plain");
      response.getWriter().write(message.toString());
   }

   private void executeImport(OrcsBranch orcsBranch, BranchQuery branchQuery, HttpBranchExchangeInfo exchangeInfo, HttpServletResponse response) throws Exception {

      branchQuery.andUuids(exchangeInfo.getSelectedBranchUuids()).includeArchived().includeDeleted();
      LinkedList<IOseeBranch> branches = Lists.newLinkedList(branchQuery.getResultsAsId());

      IResourceLocator locator = resourceManager.getResourceLocator(exchangeInfo.getPath());
      Callable<URI> callable = orcsBranch.importBranch(locator.getLocation(), branches, exchangeInfo.getOptions());
      URI importURI = callable.call();
      response.setStatus(HttpServletResponse.SC_ACCEPTED);
      response.setContentType("text/plain");
      response.getWriter().write(String.format("Successfully imported: [%s]", importURI.toASCIIString()));
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      super.doGet(req, resp);
      // Get available export files.
      // Get export file metadata for options.
   }
}
