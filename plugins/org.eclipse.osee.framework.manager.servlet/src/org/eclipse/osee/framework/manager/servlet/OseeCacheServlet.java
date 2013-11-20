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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.message.ArtifactTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.ArtifactTypeCacheUpdateResponse.ArtifactTypeRow;
import org.eclipse.osee.framework.core.message.AttributeTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.BranchCacheStoreRequest;
import org.eclipse.osee.framework.core.message.BranchCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.BranchCacheUpdateUtil;
import org.eclipse.osee.framework.core.message.CacheUpdateRequest;
import org.eclipse.osee.framework.core.message.OseeEnumTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.RelationTypeCacheUpdateResponse;
import org.eclipse.osee.framework.core.message.RelationTypeCacheUpdateResponse.RelationTypeRow;
import org.eclipse.osee.framework.core.message.TransactionCacheUpdateResponse;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslatorId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.internal.ApplicationContextFactory;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.EnumEntry;
import org.eclipse.osee.orcs.data.EnumType;
import org.eclipse.osee.orcs.data.EnumTypes;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public class OseeCacheServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = 6693534844874109524L;
   private final IDataTranslationService translationService;
   private final IOseeModelFactoryService factoryService;
   private final BranchCache branchCache;
   private final TransactionCache txCache;
   private final OrcsApi orcsApi;
   private static final StorageState DEFAULT_STORAGE_STATE = StorageState.CREATED;

   public OseeCacheServlet(Log logger, IDataTranslationService translationService, TempCachingService cachingService, OrcsApi orcsApi, IOseeModelFactoryService factoryService) {
      super(logger);
      this.translationService = translationService;
      this.branchCache = cachingService.getBranchCache();
      this.txCache = cachingService.getTransactionCache();
      this.orcsApi = orcsApi;
      this.factoryService = factoryService;
   }

   public IDataTranslationService getTranslationService() {
      return translationService;
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
      OseeCacheEnum cacheId = OseeCacheEnum.valueOf(req.getParameter("cacheId"));
      try {
         IDataTranslationService service = getTranslationService();
         ApplicationContext context = createAppContext(req);
         OrcsTypes orcsTypes = orcsApi.getOrcsTypes(context);
         Pair<Object, ITranslatorId> pair = createResponse(new CacheUpdateRequest(cacheId), orcsTypes);
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/xml");
         resp.setCharacterEncoding("UTF-8");
         InputStream inputStream = service.convertToStream(pair.getFirst(), pair.getSecond());
         OutputStream outputStream = resp.getOutputStream();
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } catch (Exception ex) {
         getLogger().error(ex, "Error acquiring cache [%s]", cacheId);
      }
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      try {
         CacheOperation operation = CacheOperation.fromString(req.getParameter("function"));
         switch (operation) {
            case UPDATE:
               ApplicationContext context = createAppContext(req);
               OrcsTypes orcsTypes = orcsApi.getOrcsTypes(context);
               sendUpdates(req, resp, orcsTypes);
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

   private ApplicationContext createAppContext(HttpServletRequest req) {
      String sessionId = req.getParameter("sessionId");
      return ApplicationContextFactory.createContext(sessionId);
   }

   private void handleError(HttpServletResponse resp, String request, Throwable th) throws IOException {
      getLogger().error(th, "Osee Cache request error: [%s]", request);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.setContentType("text/plain");
      resp.getWriter().write(Lib.exceptionToString(th));
      resp.getWriter().flush();
      resp.getWriter().close();
   }

   private void storeUpdates(HttpServletRequest req, HttpServletResponse resp) throws OseeCoreException {
      IDataTranslationService service = getTranslationService();

      BranchCacheStoreRequest updateRequest = null;
      InputStream inputStream = null;
      try {
         inputStream = req.getInputStream();
         updateRequest = service.convert(inputStream, CoreTranslatorId.BRANCH_CACHE_STORE_REQUEST);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
      Collection<Branch> updated =
         new BranchCacheUpdateUtil(factoryService.getBranchFactory(), txCache).updateCache(updateRequest, branchCache);

      if (updateRequest.isServerUpdateMessage()) {
         for (Branch branch : updated) {
            if (branch.isCreated()) {
               branch.setStorageState(StorageState.MODIFIED);
            }
            branch.clearDirty();
            if (branch.isPurged()) {
               branchCache.decache(branch);
            }
         }
      } else {
         branchCache.storeItems(updated);
      }
      try {
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         resp.setCharacterEncoding("UTF-8");
         resp.getWriter().write("Branch Store Successful");
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private void sendUpdates(HttpServletRequest req, HttpServletResponse resp, OrcsTypes orcsTypes) throws OseeCoreException {
      IDataTranslationService service = getTranslationService();

      CacheUpdateRequest updateRequest = null;
      InputStream inputStream = null;
      try {
         inputStream = req.getInputStream();
         updateRequest = service.convert(inputStream, CoreTranslatorId.OSEE_CACHE_UPDATE_REQUEST);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }

      OutputStream outputStream = null;
      try {
         Pair<Object, ITranslatorId> pair = createResponse(updateRequest, orcsTypes);

         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/xml");
         resp.setCharacterEncoding("UTF-8");

         inputStream = service.convertToStream(pair.getFirst(), pair.getSecond());
         outputStream = resp.getOutputStream();
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private Pair<Object, ITranslatorId> createResponse(CacheUpdateRequest updateRequest, OrcsTypes orcsTypes) throws OseeCoreException {
      Object response = null;
      ITranslatorId transalatorId = null;
      switch (updateRequest.getCacheId()) {
         case BRANCH_CACHE:
            response = BranchCacheUpdateResponse.fromCache(branchCache, branchCache.getAll());
            transalatorId = CoreTranslatorId.BRANCH_CACHE_UPDATE_RESPONSE;
            break;
         case TRANSACTION_CACHE:
            Collection<TransactionRecord> record;

            if (updateRequest.getItemsIds().isEmpty()) {
               record = txCache.getAll();
            } else {
               record = new ArrayList<TransactionRecord>();
               for (Integer item : updateRequest.getItemsIds()) {
                  record.add(txCache.getOrLoad(item));
               }
            }
            response =
               TransactionCacheUpdateResponse.fromCache(factoryService.getTransactionFactory(), record, branchCache);
            transalatorId = CoreTranslatorId.TX_CACHE_UPDATE_RESPONSE;
            break;
         case ARTIFACT_TYPE_CACHE:
            response = createArtifactTypeCacheUpdateResponse(orcsTypes);
            transalatorId = CoreTranslatorId.ARTIFACT_TYPE_CACHE_UPDATE_RESPONSE;
            break;
         case ATTRIBUTE_TYPE_CACHE:
            response = createAttributeTypeCacheUpdateResponse(orcsTypes);
            transalatorId = CoreTranslatorId.ATTRIBUTE_TYPE_CACHE_UPDATE_RESPONSE;
            break;
         case OSEE_ENUM_TYPE_CACHE:
            response = createOseeEnumTypeCacheUpdateResponse(orcsTypes);
            transalatorId = CoreTranslatorId.OSEE_ENUM_TYPE_CACHE_UPDATE_RESPONSE;
            break;
         case RELATION_TYPE_CACHE:
            response = createRelationTypeCacheUpdateResponse(orcsTypes);
            transalatorId = CoreTranslatorId.RELATION_TYPE_CACHE_UPDATE_RESPONSE;
            break;
         default:
            throw new OseeArgumentException("Invalid cacheId [%s]", updateRequest.getCacheId());
      }
      return new Pair<Object, ITranslatorId>(response, transalatorId);
   }

   private ArtifactTypeCacheUpdateResponse createArtifactTypeCacheUpdateResponse(OrcsTypes orcsTypes) throws OseeCoreException {
      List<ArtifactTypeRow> rows = new ArrayList<ArtifactTypeRow>();
      Map<Long, Long[]> baseToSuper = new HashMap<Long, Long[]>();
      List<Triplet<Long, String, Long>> artAttrs = new ArrayList<Triplet<Long, String, Long>>();
      ArtifactTypes artTypes = orcsTypes.getArtifactTypes();
      for (IArtifactType artType : artTypes.getAll()) {
         long artTypeId = artType.getGuid();
         boolean isAbstract = artTypes.isAbstract(artType);
         rows.add(new ArtifactTypeRow(artTypeId, artType.getGuid(), artType.getName(), isAbstract,
            DEFAULT_STORAGE_STATE));

         Collection<? extends IArtifactType> superTypes = artTypes.getSuperArtifactTypes(artType);
         if (!superTypes.isEmpty()) {
            Long[] intSuperTypes = new Long[superTypes.size()];
            int index = 0;
            for (IArtifactType superType : superTypes) {
               intSuperTypes[index++] = superType.getGuid();
            }
            baseToSuper.put(artTypeId, intSuperTypes);
         }

         Map<IOseeBranch, Collection<IAttributeType>> allTypes = artTypes.getAllAttributeTypes(artType);
         for (Entry<IOseeBranch, Collection<IAttributeType>> entry : allTypes.entrySet()) {
            IOseeBranch branch = entry.getKey();
            Collection<IAttributeType> attrTypes = entry.getValue();
            for (IAttributeType type : attrTypes) {
               artAttrs.add(new Triplet<Long, String, Long>(artType.getGuid(), branch.getGuid(), type.getGuid()));
            }

         }
      }
      return new ArtifactTypeCacheUpdateResponse(rows, baseToSuper, artAttrs);
   }

   private AttributeTypeCacheUpdateResponse createAttributeTypeCacheUpdateResponse(OrcsTypes orcsTypes) throws OseeCoreException {
      List<AttributeType> rows = new ArrayList<AttributeType>();
      Map<Long, Long> attrToEnum = new HashMap<Long, Long>();
      AttributeTypes attrTypes = orcsTypes.getAttributeTypes();
      for (IAttributeType item : attrTypes.getAll()) {
         String baseAttributeTypeId = attrTypes.getBaseAttributeTypeId(item);
         String attributeProviderNameId = attrTypes.getAttributeProviderId(item);
         String fileTypeExtension = attrTypes.getFileTypeExtension(item);
         String defaultValue = attrTypes.getDefaultValue(item);
         int minOccurrances = attrTypes.getMinOccurrences(item);
         int maxOccurrences = attrTypes.getMaxOccurrences(item);
         String description = attrTypes.getDescription(item);
         String taggerId = attrTypes.getTaggerId(item);
         String mediaType = attrTypes.getMediaType(item);
         AttributeType type =
            new AttributeType(item.getGuid(), item.getName(), baseAttributeTypeId, attributeProviderNameId,
               fileTypeExtension, defaultValue, minOccurrances, maxOccurrences, description, taggerId, mediaType);

         long typeId = item.getGuid();
         type.setId(typeId);
         rows.add(type);

         if (attrTypes.isEnumerated(item)) {
            EnumType enumType = attrTypes.getEnumType(item);
            attrToEnum.put(typeId, enumType.getGuid());
         }

      }
      return new AttributeTypeCacheUpdateResponse(rows, attrToEnum);
   }

   private RelationTypeCacheUpdateResponse createRelationTypeCacheUpdateResponse(OrcsTypes orcsTypes) throws OseeCoreException {
      List<RelationTypeRow> rows = new ArrayList<RelationTypeRow>();
      RelationTypes relTypes = orcsTypes.getRelationTypes();
      for (IRelationType item : relTypes.getAll()) {
         IArtifactType sideAType = relTypes.getArtifactTypeSideA(item);
         IArtifactType sideBType = relTypes.getArtifactTypeSideB(item);

         String sideAName = relTypes.getSideAName(item);
         String sideBName = relTypes.getSideBName(item);
         RelationTypeMultiplicity multiplicity = relTypes.getMultiplicity(item);
         String defaultOrderTypeGuid = relTypes.getDefaultOrderTypeGuid(item);

         rows.add(new RelationTypeRow(item.getGuid(), item.getName(), item.getGuid(), DEFAULT_STORAGE_STATE, sideAName,
            sideBName, sideAType.getGuid(), sideBType.getGuid(), multiplicity, defaultOrderTypeGuid));
      }
      return new RelationTypeCacheUpdateResponse(rows);
   }

   private OseeEnumTypeCacheUpdateResponse createOseeEnumTypeCacheUpdateResponse(OrcsTypes orcsTypes) throws OseeCoreException {
      List<String[]> enumTypeRows = new ArrayList<String[]>();
      List<String[]> enumEntryRows = new ArrayList<String[]>();
      EnumTypes enumTypes = orcsTypes.getEnumTypes();
      for (EnumType type : enumTypes.getAll()) {
         enumTypeRows.add(new String[] {
            String.valueOf(type.getGuid()),
            DEFAULT_STORAGE_STATE.toString(),
            String.valueOf(type.getGuid()),
            type.getName()});
         for (EnumEntry entry : type.values()) {
            enumEntryRows.add(new String[] {
               String.valueOf(type.getGuid()),
               entry.getGuid(),
               entry.getName(),
               String.valueOf(entry.ordinal()),
               entry.getDescription()});
         }
      }
      return new OseeEnumTypeCacheUpdateResponse(enumTypeRows, enumEntryRows);
   }
}
