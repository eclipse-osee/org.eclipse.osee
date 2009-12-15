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
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.ArtifactTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.AttributeTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.BranchCacheStoreRequest;
import org.eclipse.osee.framework.core.data.BranchCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.data.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.data.IArtifactFactory;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.data.OseeEnumTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.RelationTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.data.TransactionCacheUpdateResponse;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeDataTranslationProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.ITranslatorId;
import org.eclipse.osee.framework.core.util.BranchCacheUpdateUtil;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeCacheServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 6693534844874109524L;
   private final IOseeDataTranslationProvider dataTransalatorProvider;

   public OseeCacheServlet(IOseeDataTranslationProvider dataTransalatorProvider) {
      super();
      this.dataTransalatorProvider = dataTransalatorProvider;
   }

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      OseeCacheEnum cacheId = OseeCacheEnum.valueOf(req.getParameter("cacheId"));
      IOseeCachingService caching = MasterServletActivator.getInstance().getOseeCache();
      try {
         IDataTranslationService service = dataTransalatorProvider.getTranslatorService();
         Pair<Object, ITranslatorId> pair = createResponse(new CacheUpdateRequest(cacheId), caching);
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/xml");
         resp.setCharacterEncoding("UTF-8");
         InputStream inputStream = null;
         OutputStream outputStream = null;
         try {
            inputStream = service.convertToStream(pair.getFirst(), pair.getSecond());
            outputStream = resp.getOutputStream();
            Lib.inputStreamToOutputStream(inputStream, outputStream);
         } catch (IOException ex) {
            throw new OseeWrappedException(ex);
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
         CacheOperation operation = CacheOperation.fromString(req.getParameter("function"));
         switch (operation) {
            case UPDATE:
               sendUpdates(req, resp);
               break;
            case STORE:
               storeUpdates(req, resp);
               break;
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

   private void storeUpdates(HttpServletRequest req, HttpServletResponse resp) throws OseeCoreException {
      IDataTranslationService service = dataTransalatorProvider.getTranslatorService();
      IOseeCachingService caching = MasterServletActivator.getInstance().getOseeCache();
      TransactionCache txCache = caching.getTransactionCache();

      BranchCacheStoreRequest updateRequest = null;
      InputStream inputStream = null;
      try {
         inputStream = req.getInputStream();
         updateRequest = service.convert(inputStream, CoreTranslatorId.BRANCH_CACHE_STORE_REQUEST);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         Lib.close(inputStream);
      }
      IOseeModelFactoryService factoryService = MasterServletActivator.getInstance().getOseeFactoryService();
      Collection<Branch> updated =
            new BranchCacheUpdateUtil(factoryService.getBranchFactory(), txCache, new IArtifactFactory<Object>() {
               @Override
               public IBasicArtifact<Object> createArtifact(int artId) {
                  return new DefaultBasicArtifact(artId, null, null);
               }
            }).updateCache(updateRequest, caching.getBranchCache());

      BranchCache cache = caching.getBranchCache();
      if (updateRequest.isServerUpdateMessage()) {
         for (Branch branch : updated) {
            if (branch.getModificationType() == ModificationType.NEW) {
               branch.setModificationType(ModificationType.MODIFIED);
            }
            branch.clearDirty();
            cache.decache(branch);
            cache.cache(branch);
         }
      } else {
         cache.storeItems(updated);
      }
      try {
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         resp.setCharacterEncoding("UTF-8");
         resp.getWriter().write("Branch Store Successful");
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private void sendUpdates(HttpServletRequest req, HttpServletResponse resp) throws OseeCoreException {
      IDataTranslationService service = dataTransalatorProvider.getTranslatorService();
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

      OutputStream outputStream = null;
      try {
         Pair<Object, ITranslatorId> pair = createResponse(updateRequest, caching);

         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/xml");
         resp.setCharacterEncoding("UTF-8");

         inputStream = service.convertToStream(pair.getFirst(), pair.getSecond());
         outputStream = resp.getOutputStream();
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private Pair<Object, ITranslatorId> createResponse(CacheUpdateRequest updateRequest, IOseeCachingService caching) throws OseeCoreException {
      IOseeModelFactoryService factoryService = MasterServletActivator.getInstance().getOseeFactoryService();
      Conditions.checkNotNull(caching, "caching service");
      Object response = null;
      ITranslatorId transalatorId = null;
      switch (updateRequest.getCacheId()) {
         case BRANCH_CACHE:
            response = BranchCacheUpdateResponse.fromCache(caching.getBranchCache(), caching.getBranchCache().getAll());
            transalatorId = CoreTranslatorId.BRANCH_CACHE_UPDATE_RESPONSE;
            break;
         case TRANSACTION_CACHE:
            Collection<TransactionRecord> record;
            TransactionCache txCache = caching.getTransactionCache();

            if (updateRequest.getItemsIds().isEmpty()) {
               record = txCache.getAll();
            } else {
               record = new ArrayList<TransactionRecord>();
               for (Integer item : updateRequest.getItemsIds()) {
                  record.add(txCache.getOrLoad(item));
               }
            }
            response = TransactionCacheUpdateResponse.fromCache(factoryService.getTransactionFactory(), record);
            transalatorId = CoreTranslatorId.TX_CACHE_UPDATE_RESPONSE;
            break;
         case ARTIFACT_TYPE_CACHE:
            response = ArtifactTypeCacheUpdateResponse.fromCache(caching.getArtifactTypeCache().getAll());
            transalatorId = CoreTranslatorId.ARTIFACT_TYPE_CACHE_UPDATE_RESPONSE;
            break;
         case ATTRIBUTE_TYPE_CACHE:
            response =
                  AttributeTypeCacheUpdateResponse.fromCache(factoryService.getAttributeTypeFactory(),
                        caching.getAttributeTypeCache().getAll());
            transalatorId = CoreTranslatorId.ATTRIBUTE_TYPE_CACHE_UPDATE_RESPONSE;
            break;
         case OSEE_ENUM_TYPE_CACHE:
            response = OseeEnumTypeCacheUpdateResponse.fromCache(caching.getEnumTypeCache().getAll());
            transalatorId = CoreTranslatorId.OSEE_ENUM_TYPE_CACHE_UPDATE_RESPONSE;
            break;
         case RELATION_TYPE_CACHE:
            response = RelationTypeCacheUpdateResponse.fromCache(caching.getRelationTypeCache().getAll());
            transalatorId = CoreTranslatorId.RELATION_TYPE_CACHE_UPDATE_RESPONSE;
            break;
         default:
            throw new OseeArgumentException(String.format("Invalid cacheId [%s]", updateRequest.getCacheId()));
      }
      return new Pair<Object, ITranslatorId>(response, transalatorId);
   }
}
