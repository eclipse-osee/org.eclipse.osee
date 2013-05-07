/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.accessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.orcs.db.internal.accessor.ArtifactTypeDataAccessor.TypeLoader;
import org.eclipse.osee.orcs.db.internal.types.IOseeModelingService;

public class TypeLoaderImpl implements TypeLoader {
   private static final String LOAD_OSEE_TYPE_DEF_URIS =
      "select attr.uri from osee_txs txs1, osee_artifact art, osee_attribute attr, osee_txs txs2 where txs1.branch_id = ? and txs1.tx_current = ? and txs1.gamma_id = art.gamma_id and txs2.branch_id = ? and txs2.tx_current = ? and txs2.gamma_id = attr.gamma_id and art.art_type_id = ? and art.art_id = attr.art_id and attr.attr_type_id = ?";

   private final boolean needsPriming;
   private final IOseeModelingService modelingService;
   private final IdentityService identityService;
   private final IOseeDatabaseService dbService;
   private final IResourceManager resourceManager;
   private final BranchCache branchCache;
   private volatile boolean loading = false;

   public TypeLoaderImpl(IOseeModelingService modelingService, IdentityService identityService, IOseeDatabaseService dbService, IResourceManager resourceManager, BranchCache branchCache, boolean needsPriming) {
      super();
      this.modelingService = modelingService;
      this.identityService = identityService;
      this.dbService = dbService;
      this.resourceManager = resourceManager;
      this.branchCache = branchCache;
      this.needsPriming = needsPriming;
   }

   @Override
   public void load() throws OseeCoreException {
      if (needsPriming && !loading) {
         loading = true;
         try {
            loadTypes();
         } finally {
            loading = false;
         }
      }
   }

   private void loadTypes() throws OseeCoreException {
      Collection<String> uriPaths = findOseeTypeData();
      if (!uriPaths.isEmpty()) {
         List<IResource> resources = getTypeData(uriPaths);
         String modelData = createCombinedFile(resources);
         String modelName = String.format("osee.types.%s.osee", Lib.getDateTimeString());
         OseeImportModelRequest request = new OseeImportModelRequest(modelName, modelData, false, false, true);
         OseeImportModelResponse response = new OseeImportModelResponse();
         modelingService.importOseeTypes(true, request, response);
      }
   }

   private Collection<String> findOseeTypeData() throws OseeCoreException {
      Collection<String> paths = new ArrayList<String>();

      Integer artifactTypeId = identityService.getLocalId(CoreArtifactTypes.OseeTypeDefinition);
      Integer attributeTypeId = identityService.getLocalId(CoreAttributeTypes.UriGeneralStringData);

      Branch commonBranch = branchCache.get(CoreBranches.COMMON);

      if (commonBranch != null) {
         IOseeStatement chStmt = null;
         try {
            chStmt = dbService.getStatement();
            chStmt.runPreparedQuery(LOAD_OSEE_TYPE_DEF_URIS, commonBranch.getId(), TxChange.CURRENT.getValue(),
               commonBranch.getId(), TxChange.CURRENT.getValue(), artifactTypeId, attributeTypeId);
            while (chStmt.next()) {
               String uri = chStmt.getString("uri");
               paths.add(uri);
            }
         } finally {
            Lib.close(chStmt);
         }
      }
      return paths;
   }

   private List<IResource> getTypeData(Collection<String> paths) throws OseeCoreException {
      List<IResource> toReturn = new ArrayList<IResource>();

      PropertyStore options = new PropertyStore();
      options.put(StandardOptions.DecompressOnAquire.name(), "true");
      for (String path : paths) {
         IResourceLocator locator = resourceManager.getResourceLocator(path);
         IResource resource = resourceManager.acquire(locator, options);
         toReturn.add(resource);
      }
      return toReturn;
   }

   private String createCombinedFile(List<IResource> resources) throws OseeCoreException {
      StringWriter writer = new StringWriter();
      for (IResource resource : resources) {
         InputStream inputStream = null;
         try {
            inputStream = resource.getContent();
            String oseeTypeFragment = Lib.inputStreamToString(inputStream);
            oseeTypeFragment = oseeTypeFragment.replaceAll("import\\s+\"", "// import \"");
            writer.write("\n");
            writer.write("//////////////     ");
            writer.write(resource.getName());
            writer.write("\n");
            writer.write("\n");
            writer.write(oseeTypeFragment);
         } catch (IOException ex) {
            OseeExceptions.wrapAndThrow(ex);
         } finally {
            Lib.close(inputStream);
         }
      }
      return writer.toString();
   }

}
