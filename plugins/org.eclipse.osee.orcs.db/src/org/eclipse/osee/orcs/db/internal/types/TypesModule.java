/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.db.internal.callable.PurgeArtifactTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeAttributeTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeRelationTypeDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class TypesModule {

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory joinFactory;
   private final IResourceManager resourceManager;

   public TypesModule(Log logger, JdbcClient jdbcClient, SqlJoinFactory joinFactory, IResourceManager resourceManager) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
      this.resourceManager = resourceManager;
   }

   public OrcsTypesDataStore createTypesDataStore() {
      return new OrcsTypesDataStore() {

         @Override
         public Callable<Void> purgeArtifactsByArtifactType(OrcsSession session, Collection<? extends ArtifactTypeToken> typesToPurge) {
            return new PurgeArtifactTypeDatabaseTxCallable(logger, session, jdbcClient, typesToPurge);
         }

         @Override
         public Callable<Void> purgeAttributesByAttributeType(OrcsSession session, Collection<? extends AttributeTypeId> typesToPurge) {
            return new PurgeAttributeTypeDatabaseTxCallable(logger, session, jdbcClient, joinFactory, typesToPurge);
         }

         @Override
         public Callable<Void> purgeRelationsByRelationType(OrcsSession session, Collection<? extends RelationTypeToken> typesToPurge) {
            return new PurgeRelationTypeDatabaseTxCallable(logger, session, jdbcClient, typesToPurge);
         }

         @Override
         public InputStream getAccessInputStream() {
            List<String> uriPaths = new ArrayList<>();

            jdbcClient.runQuery(stmt -> uriPaths.add(stmt.getString("uri")), OrcsTypes.LOAD_OSEE_TYPE_DEF_URIS,
               CoreTupleTypes.OseeTypeDef, CoreBranches.COMMON, TxCurrent.CURRENT, OrcsTypesData.OSEE_TYPE_VERSION,
               TxCurrent.CURRENT);

            return asInputStream(uriPaths);
         }

         private InputStream asInputStream(List<String> resources) {
            PropertyStore options = new PropertyStore();
            options.put(StandardOptions.DecompressOnAquire.name(), "true");
            StringBuilder builder = new StringBuilder();
            for (String path : resources) {
               IResourceLocator locator = resourceManager.getResourceLocator(path);
               IResource resource = resourceManager.acquire(locator, options);
               if (resource == null) {
                  throw new OseeStateException("Types resource can not be null for %s", path);
               }

               InputStream inputStream = null;
               try {
                  inputStream = resource.getContent();
                  String oseeTypeFragment = Lib.inputStreamToString(inputStream);
                  oseeTypeFragment = oseeTypeFragment.replaceAll("import\\s+\"", "// import \"");
                  builder.append("\n//////////////     ");
                  builder.append(resource.getName());
                  builder.append("\n\n");
                  builder.append(oseeTypeFragment);
               } catch (IOException ex) {
                  OseeCoreException.wrapAndThrow(ex);
               } finally {
                  Lib.close(inputStream);
               }
            }
            InputStream toReturn = null;
            try {
               toReturn = Lib.stringToInputStream(builder.toString());
            } catch (UnsupportedEncodingException ex) {
               OseeCoreException.wrapAndThrow(ex);
            }
            return toReturn;
         }
      };
   }
}