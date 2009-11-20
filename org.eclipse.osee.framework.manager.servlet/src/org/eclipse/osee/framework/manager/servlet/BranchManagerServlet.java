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
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.function.ChangeReportFunction;
import org.eclipse.osee.framework.manager.servlet.function.CreateBranchFunction;
import org.eclipse.osee.framework.manager.servlet.function.CreateCommitFunction;

/**
 * @author Andrew M Finkbeiner
 */
public class BranchManagerServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 226986283540461526L;

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
      //      super.checkAccessControl(request);
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String rawFunction = req.getParameter("function");
      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
      //      ArrayList<ChangeItem> changes = new ArrayList<ChangeItem>();
      //      changes.add(new ArtifactChangeItem(1L, ModificationType.MODIFIED, 45, 1));
      //      changes.add(new ArtifactChangeItem(2L, ModificationType.NEW, 77, 2));
      //      changes.add(new ArtifactChangeItem(3L, ModificationType.DELETED, 66, 3));
      //
      //      ChangeReportResponse changeReportResponseData = new ChangeReportResponse(changes);
      BranchCommitResponse responseData = new BranchCommitResponse();
      responseData.setTransaction(new TransactionRecord(45, null, "A comment", new Date(), 45, 12,
            TransactionDetailsType.NonBaselined));

      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/xml");
      InputStream inputStream;
      try {
         inputStream = service.convertToStream(responseData);
         try {
            Lib.inputStreamToOutputStream(inputStream, resp.getOutputStream());
         } finally {
            Lib.close(inputStream);
         }
      } catch (OseeCoreException e) {
      }

   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
         String rawFunction = req.getParameter("function");
         Function function = Function.fromString(rawFunction);
         switch (function) {
            case BRANCH_COMMIT:
               new CreateCommitFunction().commitBranch(req, resp);
               break;
            case CREATEFULLBRANCH:
               new CreateBranchFunction().createBranch(req, resp);
               break;
            case CHANGE_REPORT:
               new ChangeReportFunction().getChanges(req, resp);
               break;
            default:
               throw new UnsupportedOperationException();
         }
      } catch (Exception ex) {
         OseeLog.log(MasterServletActivator.class, Level.SEVERE, String.format("Branch servlet request error: [%s]",
               req.toString()), ex);
         resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         resp.setContentType("text/plain");
         resp.getWriter().write(Lib.exceptionToString(ex));
      }
      resp.getWriter().flush();
      resp.getWriter().close();
   }

}
