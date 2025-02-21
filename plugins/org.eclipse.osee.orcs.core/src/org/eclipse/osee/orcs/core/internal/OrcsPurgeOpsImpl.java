/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.orcs.core.internal;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TupleTypeImpl;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsPurgeOps;
import org.eclipse.osee.orcs.data.OrcsPurgeResult;

public class OrcsPurgeOpsImpl implements OrcsPurgeOps {

   private final OrcsApi orcsApi;

   public OrcsPurgeOpsImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * Purge the artifact from the database, deleting the artifact, its attributes, relations, and tuples. A backup is
    * not created for this purge.
    */
   @Override
   public OrcsPurgeResult purgeArtifact(ArtifactId artifact) {
      orcsApi.userService().requireRole(CoreUserGroups.AccountAdmin);

      //First delete resources on server before removing associated attribute rows
      String uriQuery = "select distinct uri from osee_attribute where art_id = ? and uri is not null";
      List<String> filesToDelete = new ArrayList<>();

      orcsApi.getJdbcService().getClient().runQuery(stmt -> filesToDelete.add(stmt.getString("uri")), uriQuery,
         artifact.getId());

      for (String loc : filesToDelete) {
         if (loc.startsWith("attr:")) {
            IResourceLocator resourceLocator = orcsApi.getAdminOps().getResourceManager().getResourceLocator(loc);
            orcsApi.getAdminOps().getResourceManager().delete(resourceLocator);
            if (orcsApi.getAdminOps().getResourceManager().exists(resourceLocator)) {
               return new OrcsPurgeResult("Failed to delete resource: " + loc, true);
            }
         }
      }

      //get all gammas associated with artifact to use in batch statement to delete rows
      List<Object[]> gammaIds = new ArrayList<>();
      List<Object> parameters = new ArrayList<>();
      String gammaQuery = "select gamma_id from osee_artifact where art_id = ? union all " + //
         "select gamma_id from osee_attribute where art_id = ? union all " + //"
         "select gamma_id from osee_relation_link where a_art_id = ? union all " + //"
         "select gamma_id from osee_relation_link where b_art_id = ? union all " + //"
         "select gamma_id from osee_relation where a_art_id = ? union all " + //"
         "select gamma_id from osee_relation where b_art_id = ? ";
      parameters.add(artifact.getId());
      parameters.add(artifact.getId());
      parameters.add(artifact.getId());
      parameters.add(artifact.getId());
      parameters.add(artifact.getId());
      parameters.add(artifact.getId());

      for (Field field : CoreTupleTypes.class.getDeclaredFields()) {
         TupleTypeImpl tupleImpl = null;
         try {
            tupleImpl = (TupleTypeImpl) field.get(Object.class);
         } catch (IllegalArgumentException | IllegalAccessException ex) {
            return new OrcsPurgeResult("Error parsing TupleTypes", true);
         }
         if (tupleImpl != null) {
            ParameterizedType pt = (ParameterizedType) field.getAnnotatedType().getType();
            for (int i = 0; i < pt.getActualTypeArguments().length; i++) {
               Type type = pt.getActualTypeArguments()[i];
               if (type == (ArtifactId.class)) {
                  String table_name = tupleImpl.getClass().getSimpleName().equals(
                     "Tuple2TypeImpl") ? "osee_tuple2" : tupleImpl.getClass().getSimpleName().equals(
                        "Tuple3TypeImpl") ? "osee_tuple3" : "osee_tuple4";
                  gammaQuery =
                     gammaQuery + " union all select gamma_id from " + table_name + " where tuple_type = " + tupleImpl.getIdString() + " and e" + Integer.toString(
                        i + 1) + " = ? ";

                  parameters.add(artifact.getId());
               }
            }
         }

      }
      return DeleteFromAllTablesWithGammaId.deleteAllGammas(orcsApi.getJdbcService().getClient(), gammaQuery,
         parameters, gammaIds);
   }

}
