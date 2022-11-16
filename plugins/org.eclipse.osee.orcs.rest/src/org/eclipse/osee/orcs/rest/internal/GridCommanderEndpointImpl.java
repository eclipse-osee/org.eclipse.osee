/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.ContextToCommand_Artifact;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.UserGroupToContext_Context;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.UserToContext_Context;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.UserToHistory_ExecutedCommandHistory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.ExecutedCommandHistory;
import org.eclipse.osee.orcs.rest.model.GridCommanderEndpoint;
import org.eclipse.osee.orcs.rest.model.UserWithContexts;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * A new instance of this REST endpoint is created for each REST call so this class does not require a thread-safe
 * design
 *
 * @author Christopher Rebuck
 */

public class GridCommanderEndpointImpl implements GridCommanderEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;

   public GridCommanderEndpointImpl(OrcsApi orcsApi, BranchId branch, UriInfo uriInfo) {
      this.orcsApi = orcsApi;
      this.branch = branch;
   }

   @Override
   public UserWithContexts getUserCommands() {
      //get user, branch, and roles
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      UserToken user = orcsApi.userService().getUser();
      List<IUserGroupArtifactToken> roles = user.getRoles();

      //user and group contexts queries
      ArtifactReadable userWithContextsReadable =
         query.andId(user).follow(UserToContext_Context).follow(ContextToCommand_Artifact).follow(
            DefaultHierarchical_Child).asArtifact();

      List<ArtifactReadable> groups =
         query.andIds(roles).follow(UserGroupToContext_Context).follow(ContextToCommand_Artifact).follow(
            DefaultHierarchical_Child).asArtifacts();

      //want to get the contexts for the user
      Set<ArtifactReadable> contexts = new HashSet<>();
      //merge into array and pass in as an array into constructor
      contexts.addAll(userWithContextsReadable.getRelatedList(UserToContext_Context));
      groups.forEach(group -> contexts.addAll(group.getRelatedList(UserGroupToContext_Context)));
      ArrayList<ArtifactReadable> contextList = new ArrayList<>(contexts);

      return new UserWithContexts(user, contextList);
   }

   @Override
   public ExecutedCommandHistory asExecutedCommandHistoryTable() {
      //get user, branch
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      UserToken user = orcsApi.userService().getUser();
      List<String> columnHeaders = Arrays.asList("Artifact Id", "Name", "Parameters", "Timestamp",
         "Execution Frequency", "Validated", "Favorite");

      //get users command history relationship
      ArtifactReadable userWithExecutedCmdHistoryReadable =
         query.andId(user).follow(UserToHistory_ExecutedCommandHistory).follow(DefaultHierarchical_Child).asArtifact();

      if (!userWithExecutedCmdHistoryReadable.getRelated(UserToHistory_ExecutedCommandHistory,
         ArtifactTypeToken.SENTINEL).isEmpty()) {
         ArtifactReadable cmdHistory =
            userWithExecutedCmdHistoryReadable.getRelated(UserToHistory_ExecutedCommandHistory,
               ArtifactTypeToken.SENTINEL).get(0);

         return new ExecutedCommandHistory(user, cmdHistory, columnHeaders);
      } else {
         return new ExecutedCommandHistory(user);
      }

   }

}