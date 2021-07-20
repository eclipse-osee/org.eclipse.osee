/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.mim.InterfaceConnectionModificationEndpoint;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.InterfaceConnection;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceConnectionModificationEndpointImpl implements InterfaceConnectionModificationEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final ArtifactId nodeId;
   private final InterfaceNodeViewApi interfaceNodeApi;
   private final InterfaceConnectionViewApi interfaceConnectionApi;

   public InterfaceConnectionModificationEndpointImpl(BranchId branch, UserId account, ArtifactId nodeId, InterfaceNodeViewApi interfaceNodeApi, InterfaceConnectionViewApi interfaceConnectionViewApi) {
      this.account = account;
      this.branch = branch;
      this.interfaceNodeApi = interfaceNodeApi;
      this.interfaceConnectionApi = interfaceConnectionViewApi;
      this.nodeId = nodeId;
   }

   @Override
   public XResultData createNewConnection(InterfaceConnection ConnectionToCreate, String type) {
      XResultData createResults = interfaceConnectionApi.getInserter().addArtifact(ConnectionToCreate, account, branch);
      if (type.toLowerCase().equals("primary")) {
         createResults.merge(interfaceConnectionApi.getInserter().relateArtifact(nodeId,
            ArtifactId.valueOf(createResults.getIds().get(0)), CoreRelationTypes.InterfaceConnectionPrimary_Connection,
            branch, account));
      } else if (type.toLowerCase().equals("secondary")) {
         createResults.merge(interfaceConnectionApi.getInserter().relateArtifact(nodeId,
            ArtifactId.valueOf(createResults.getIds().get(0)),
            CoreRelationTypes.InterfaceConnectionSecondary_Connection, branch, account));
      }
      return createResults;
   }

   @Override
   public XResultData deleteConnection(ArtifactId ConnectionId) {
      try {
         if (interfaceConnectionApi.getAccessor().getByRelation(branch, nodeId,
            CoreRelationTypes.InterfaceConnectionPrimary_Connection, ConnectionId,
            InterfaceConnection.class).getId() > 0) {
            return interfaceConnectionApi.getInserter().unrelateArtifact(nodeId, ConnectionId,
               CoreRelationTypes.InterfaceConnectionPrimary_Connection, branch, account);
         } else if (interfaceConnectionApi.getAccessor().getByRelation(branch, nodeId,
            CoreRelationTypes.InterfaceConnectionSecondary_Connection, ConnectionId,
            InterfaceConnection.class).getId() > 0) {
            return interfaceConnectionApi.getInserter().unrelateArtifact(nodeId, ConnectionId,
               CoreRelationTypes.InterfaceConnectionSecondary_Connection, branch, account);
         } else {
            XResultData results = new XResultData();
            List<String> idList = new LinkedList<String>();
            idList.add(String.valueOf(nodeId.getId()));
            idList.add(String.valueOf(ConnectionId.getId()));
            results.setIds(idList);
            results.error("Could not find matching Node to unrelate Connection");
            return results;
         }
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         XResultData results = new XResultData();
         List<String> idList = new LinkedList<String>();
         idList.add(String.valueOf(nodeId.getId()));
         idList.add(String.valueOf(ConnectionId.getId()));
         results.setIds(idList);
         results.error("Could not find matching Node to unrelate Connection");
         return results;
      }
   }

   @Override
   public XResultData relateConnection(ArtifactId connectionId, String type) {
      if (type.toLowerCase().equals("primary")) {
         return interfaceConnectionApi.getInserter().relateArtifact(nodeId, connectionId,
            CoreRelationTypes.InterfaceConnectionPrimary_Connection, branch, account);
      } else if (type.toLowerCase().equals("secondary")) {
         return interfaceConnectionApi.getInserter().relateArtifact(nodeId, connectionId,
            CoreRelationTypes.InterfaceConnectionSecondary_Connection, branch, account);
      }
      return null;
   }

}
