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
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.branch.AbstractBranchCallable;
import org.eclipse.osee.framework.manager.servlet.branch.ArchiveBranchCallable;
import org.eclipse.osee.framework.manager.servlet.branch.ChangeBranchStateCallable;
import org.eclipse.osee.framework.manager.servlet.branch.ChangeBranchTypeCallable;
import org.eclipse.osee.framework.manager.servlet.branch.CommitBranchCallable;
import org.eclipse.osee.framework.manager.servlet.branch.CompareBranchCallable;
import org.eclipse.osee.framework.manager.servlet.branch.CreateBranchCallable;
import org.eclipse.osee.framework.manager.servlet.branch.PurgeBranchCallable;
import org.eclipse.osee.framework.manager.servlet.internal.ApplicationContextFactory;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Andrew M. Finkbeiner
 */
public class BranchManagerServlet extends SecureOseeHttpServlet {

   private static final long serialVersionUID = 226986283540461526L;

   private final OrcsApi orcsApi;
   private final IDataTranslationService translationService;

   public BranchManagerServlet(Log logger, ISessionManager sessionManager, IDataTranslationService translationService, OrcsApi orcsApi) {
      super(logger, sessionManager);
      this.translationService = translationService;
      this.orcsApi = orcsApi;
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      try {
         Callable<?> callable = createCallable(req, resp);
         if (callable != null) {
            callable.call();
         }
      } catch (Exception ex) {
         getLogger().error(ex, "Branch servlet request error: [%s]", req.toString());
         resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         resp.setContentType("text/plain");
         resp.getWriter().write(Lib.exceptionToString(ex));
         resp.getWriter().flush();
         resp.getWriter().close();
      }
   }

   private ApplicationContext getContext(HttpServletRequest req) {
      return ApplicationContextFactory.createContext(getSessionId(req));
   }

   private Callable<?> createCallable(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      String rawFunction = req.getParameter("function");
      Function function = Function.fromString(rawFunction);

      ApplicationContext applicationContext = getContext(req);

      AbstractBranchCallable<?, ?> callable = null;
      switch (function) {
         case BRANCH_COMMIT:
            callable = new CommitBranchCallable(applicationContext, req, resp, translationService, orcsApi);
            break;
         case CREATE_BRANCH:
            callable = new CreateBranchCallable(applicationContext, req, resp, translationService, orcsApi);
            break;
         case CHANGE_REPORT:
            callable = new CompareBranchCallable(applicationContext, req, resp, translationService, orcsApi);
            break;
         case PURGE_BRANCH:
            callable = new PurgeBranchCallable(applicationContext, req, resp, translationService, orcsApi);
            break;
         case UPDATE_ARCHIVE_STATE:
            callable = new ArchiveBranchCallable(applicationContext, req, resp, translationService, orcsApi);
            break;
         case UPDATE_BRANCH_TYPE:
            callable = new ChangeBranchTypeCallable(applicationContext, req, resp, translationService, orcsApi);
            break;
         case UPDATE_BRANCH_STATE:
            callable = new ChangeBranchStateCallable(applicationContext, req, resp, translationService, orcsApi);
            break;
         case RELOAD_BRANCH_CACHE:
            orcsApi.getBranchCache().reloadCache();
            break;
         default:
            throw new UnsupportedOperationException();
      }
      return callable;
   }
}
