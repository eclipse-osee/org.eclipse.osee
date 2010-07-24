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
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.data.HttpBranchExchangeInfo;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class BranchExchangeServlet extends SecureOseeHttpServlet {

   private static final long serialVersionUID = -1642995618810911260L;

   private final IBranchExchange branchExchangeService;
   private final IResourceLocatorManager locatorManager;
   private final IResourceManager resourceManager;

   public BranchExchangeServlet(ISessionManager sessionManager, IBranchExchange branchExchangeService, IResourceLocatorManager locatorManager, IResourceManager resourceManager) {
      super(sessionManager);
      this.branchExchangeService = branchExchangeService;
      this.locatorManager = locatorManager;
      this.resourceManager = resourceManager;
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException {
      try {
         HttpBranchExchangeInfo exchangeInfo = new HttpBranchExchangeInfo(req);
         switch (exchangeInfo.getFunction()) {
            case exportBranch:
               executeExport(exchangeInfo, response);
               break;
            case importBranch:
               executeImport(exchangeInfo, response);
               break;
            case checkExchange:
               executeCheckExchange(exchangeInfo, response);
            default:
               break;
         }
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         response.setContentType("text/plain");
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Error processing [%s]", req.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      }
      response.getWriter().flush();
      response.getWriter().close();
   }

   private void executeCheckExchange(HttpBranchExchangeInfo exchangeInfo, HttpServletResponse response) throws Exception {
      int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      StringBuffer message = new StringBuffer();

      String path = exchangeInfo.getPath();
      IResourceLocator exchangeLocator = locatorManager.getResourceLocator(path);
      IResourceLocator verifyLocator = branchExchangeService.checkIntegrity(exchangeLocator);
      status = HttpServletResponse.SC_ACCEPTED;
      message.append(String.format("Verification at: [%s]", verifyLocator.getLocation().toASCIIString()));

      response.setStatus(status);
      response.setContentType("text/plain");
      response.getWriter().write(message.toString());
   }

   private void executeExport(HttpBranchExchangeInfo exchangeInfo, HttpServletResponse response) throws Exception {
      int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      StringBuffer message = new StringBuffer();
      IResourceLocator exchangeLocator =
         branchExchangeService.exportBranch(exchangeInfo.getExchangeFileName(), exchangeInfo.getOptions(),
            exchangeInfo.getSelectedBranchIds());
      status = HttpServletResponse.SC_ACCEPTED;
      message.append(String.format("Exported: [%s]", exchangeLocator.getLocation().toASCIIString()));

      if (exchangeInfo.isSendExportFile()) {
         InputStream exportFileStream = null;
         try {
            IResource resource = resourceManager.acquire(exchangeLocator, new Options());
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

   private void executeImport(HttpBranchExchangeInfo exchangeInfo, HttpServletResponse response) throws Exception {
      IResourceLocator locator = locatorManager.getResourceLocator(exchangeInfo.getPath());
      branchExchangeService.importBranch(locator, exchangeInfo.getOptions(), exchangeInfo.getSelectedBranchIds());

      response.setStatus(HttpServletResponse.SC_ACCEPTED);
      response.setContentType("text/plain");
      response.getWriter().write(String.format("Successfully imported: [%s]", exchangeInfo.getPath()));
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      super.doGet(req, resp);
      // Get available export files.
      // Get export file metadata for options.
   }
}
