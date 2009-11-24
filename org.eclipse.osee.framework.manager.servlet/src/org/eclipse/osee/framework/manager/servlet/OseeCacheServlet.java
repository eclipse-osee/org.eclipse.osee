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
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
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
import org.eclipse.osee.framework.core.services.ITranslatorId;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.Pair;
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
         updateRequest = service.convert(inputStream, CoreTranslatorId.OSEE_CACHE_UPDATE_REQUEST);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         Lib.close(inputStream);
      }
      Pair<CacheUpdateResponse<?>, ITranslatorId> reponsePair = createResponse(updateRequest.getCacheId(), caching);

      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/xml");
      resp.setCharacterEncoding("UTF-8");

      OutputStream outputStream = null;
      try {
         inputStream = service.convertToStream(reponsePair.getFirst(), reponsePair.getSecond());
         outputStream = resp.getOutputStream();
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         Lib.close(inputStream);
         Lib.close(outputStream);
      }
   }

   private Pair<CacheUpdateResponse<?>, ITranslatorId> createResponse(OseeCacheEnum cacheId, IOseeCachingService caching) throws OseeCoreException {
      Conditions.checkNotNull(caching, "caching service");
      CacheUpdateResponse<?> response = null;
      ITranslatorId translatorId = null;
      switch (cacheId) {
         case ARTIFACT_TYPE_CACHE:
            response = new CacheUpdateResponse<ArtifactType>(cacheId, caching.getArtifactTypeCache().getAll());
            translatorId = CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__ARTIFACT_TYPE;
            break;
         case ATTRIBUTE_TYPE_CACHE:
            response = new CacheUpdateResponse<AttributeType>(cacheId, caching.getAttributeTypeCache().getAll());
            translatorId = CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__ATTRIBUTE_TYPE;
            break;
         case BRANCH_CACHE:
            response = new CacheUpdateResponse<Branch>(cacheId, caching.getBranchCache().getAll());
            translatorId = CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__BRANCH;
            break;
         case OSEE_ENUM_TYPE_CACHE:
            response = new CacheUpdateResponse<OseeEnumType>(cacheId, caching.getEnumTypeCache().getAll());
            translatorId = CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__OSEE_ENUM_TYPE;
            break;
         case RELATION_TYPE_CACHE:
            response = new CacheUpdateResponse<RelationType>(cacheId, caching.getRelationTypeCache().getAll());
            translatorId = CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__RELATION_TYPE;
            break;
         case TRANSACTION_CACHE:
            response = new CacheUpdateResponse<TransactionRecord>(cacheId, caching.getTransactionCache().getAll());
            translatorId = CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__TRANSACTION_RECORD;
            break;
         default:
            throw new OseeArgumentException(String.format("Invalid cacheId [%s]", cacheId));
      }
      return new Pair<CacheUpdateResponse<?>, ITranslatorId>(response, translatorId);
   }
}
