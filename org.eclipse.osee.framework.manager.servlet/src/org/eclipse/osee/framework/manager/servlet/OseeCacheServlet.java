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
import java.io.OutputStream;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeCacheServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 6693534844874109524L;

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

      try {
         CacheOperation operation = CacheOperation.fromString(req.getParameter("function"));
         switch (operation) {
            case UPDATE:
               sendUpdates(req, resp);
               break;
            case STORE:
               throw new UnsupportedOperationException();
            default:
               throw new UnsupportedOperationException();
         }
      } catch (Exception ex) {
         handleError(resp, req.toString(), ex);
      }
   }

   private void handleError(HttpServletResponse resp, String request, Throwable th) throws IOException {
      OseeLog.log(MasterServletActivator.class, Level.SEVERE, String.format("Osee Cache request error: [%s]", request),
            th);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.setContentType("text/plain");
      resp.getWriter().write(Lib.exceptionToString(th));
      resp.getWriter().flush();
      resp.getWriter().close();
   }

   private void sendUpdates(HttpServletRequest req, HttpServletResponse resp) throws OseeCoreException {
      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
      IOseeCachingService caching = MasterServletActivator.getInstance().getOseeCache();

      CacheUpdateRequest updateRequest = null;
      InputStream inputStream = null;
      try {
         inputStream = req.getInputStream();
         updateRequest = service.convert(inputStream, CacheUpdateRequest.class);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         Lib.close(inputStream);
      }

      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/xml");
      resp.setCharacterEncoding("UTF-8");

      CacheUpdateResponse<?> response = null;
      switch (updateRequest.getCacheId()) {
         case ARTIFACT_TYPE_CACHE:
            response =
                  new CacheUpdateResponse<ArtifactType>(updateRequest.getCacheId(),
                        caching.getArtifactTypeCache().getAll());
            break;
         case ATTRIBUTE_TYPE_CACHE:
            response =
                  new CacheUpdateResponse<AttributeType>(updateRequest.getCacheId(),
                        caching.getAttributeTypeCache().getAll());
            break;
         case BRANCH_CACHE:
            response = new CacheUpdateResponse<Branch>(updateRequest.getCacheId(), caching.getBranchCache().getAll());
            break;
         case OSEE_ENUM_TYPE_CACHE:
            response =
                  new CacheUpdateResponse<OseeEnumType>(updateRequest.getCacheId(), caching.getEnumTypeCache().getAll());
            break;
         case RELATION_TYPE_CACHE:
            response =
                  new CacheUpdateResponse<RelationType>(updateRequest.getCacheId(),
                        caching.getRelationTypeCache().getAll());
            break;
         case TRANSACTION_CACHE:
            response =
                  new CacheUpdateResponse<TransactionRecord>(updateRequest.getCacheId(),
                        caching.getTransactionCache().getAll());
            break;
      }

      OutputStream outputStream = null;
      try {
         inputStream = service.convertToStream(response);
         outputStream = resp.getOutputStream();
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         Lib.close(inputStream);
         Lib.close(outputStream);
      }
   }
}
