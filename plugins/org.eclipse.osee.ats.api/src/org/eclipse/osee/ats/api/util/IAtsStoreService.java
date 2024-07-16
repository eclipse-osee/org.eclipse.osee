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

package org.eclipse.osee.ats.api.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStoreService {

   public static final String ART_TYPE_FROM_ID_QUERY =
      "select art_id, art_type_id from osee_artifact where art_id in (%s)";

   IAtsChangeSet createAtsChangeSet(String comment, AtsUser user);

   List<IAtsWorkItem> reload(Collection<IAtsWorkItem> workItems);

   boolean isDeleted(IAtsObject atsObject);

   boolean isAttributeTypeValid(IAtsObject atsObject, AttributeTypeToken attributeType);

   boolean isAttributeTypeValid(ArtifactId artifact, AttributeTypeToken attributeType);

   ArtifactTypeToken getArtifactType(ArtifactId artifact);

   ArtifactTypeToken getArtifactType(IAtsObject atsObject);

   void executeChangeSet(String comment, IAtsObject atsObject);

   void executeChangeSet(String comment, Collection<? extends IAtsObject> atsObjects);

   Collection<AttributeTypeGeneric<?>> getAttributeTypes();

   boolean isChangedInDb(IAtsWorkItem workItem);

   void clearCaches(IAtsWorkItem workItem);

   Result setTransactionAssociatedArtifact(TransactionId trans, IAtsTeamWorkflow teamWf);

   boolean isDeleted(ArtifactId artifact);

   TransactionId getTransactionId(IAtsWorkItem workItem);

   default boolean isInDb(IAtsWorkItem workItem) {
      return getTransactionId(workItem).isValid();
   }

   CustomizeData getCustomizationByGuid(String customize_guid);

   boolean isProductionDb();

   boolean isHistorical(IAtsObject atsObject);

   JdbcService getJdbcService();

   default Map<ArtifactId, ArtifactTypeToken> getArtifactTypes(Collection<ArtifactId> artIds,
      OrcsTokenService tokenService) {
      String query = String.format(ART_TYPE_FROM_ID_QUERY,
         org.eclipse.osee.framework.jdk.core.util.Collections.toString(artIds, ",", ArtifactId::getIdString));

      Map<ArtifactId, ArtifactTypeToken> artIdToType = new HashMap<>();
      getJdbcService().getClient().runQuery(stmt -> artIdToType.put(ArtifactId.valueOf(stmt.getLong("art_id")),
         tokenService.getArtifactType(stmt.getLong("art_type_id"))), query);
      return artIdToType;
   }

   boolean isHistorical(ArtifactId artifact);

   boolean isReadOnly(IAtsWorkItem workItem);

   boolean isAccessControlWrite(IAtsWorkItem workItem);

   void reloadArts(Collection<ArtifactToken> artifacts);

   boolean isIdeClient();

   default String getArtifactTypeName(ArtifactToken art) {
      ArtifactTypeToken artType = getArtifactType(art);
      if (artType != null && artType.isValid()) {
         return artType.getName();
      }
      return "";
   }

   Collection<ArtifactToken> getDescendants(ArtifactToken art);

   String getSafeName(ArtifactToken art);

   String getSafeName(ArtifactToken chgRptArt, BranchId branch);

   ArtifactTypeToken getArtifactType(ArtifactId artId, BranchId branch);

   IAtsChangeSet createAtsChangeSet(String comment, BranchToken branch, AtsUser asUser);

   boolean isOfType(ArtifactId artifact, ArtifactTypeToken artType);

   boolean isChangedInDb(ArtifactId artifact);

   XResultData clearAtsCachesAllServers();

   void purgeArtifacts(List<ArtifactToken> artifacts);

   void deleteArtifacts(List<ArtifactToken> artifacts);

   XResultData validateTypes();

   Collection<CustomizeData> getCustomizations(String namespace);

   Collection<CustomizeData> getCustomizationsGlobal(String namespace);

}