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

import static org.eclipse.osee.framework.core.data.ApplicabilityId.BASE;
import static org.eclipse.osee.framework.core.data.TransactionToken.SENTINEL;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Command;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Context;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DefaultValue;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Description;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.HttpMethod;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UseValidator;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ValidatorType;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.ContextToCommand;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.ContextToCommand_Artifact;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.UserGroupToContext;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.UserGroupToContext_Context;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.UserToContext_Context;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.UserToHistory_ExecutedCommandHistory;
import static org.eclipse.osee.framework.core.enums.CoreUserGroups.Everyone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.ExecutedCommandHistory;
import org.eclipse.osee.orcs.rest.model.GridCommanderEndpoint;
import org.eclipse.osee.orcs.rest.model.UserContext;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

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
   public TransactionToken createDefaultContext() {
      IUserGroup everyoneUserGroup = orcsApi.userService().getUserGroup(Everyone);
      orcsApi.userService().requireRole(CoreUserGroups.AccountAdmin);

      ArtifactId contextId = orcsApi.getQueryFactory().fromBranch(COMMON).andIsOfType(Context).andNameEquals(
         "Default User Context").asArtifactIdOrSentinel();

      if (contextId.isValid()) {
         return SENTINEL;
      }

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(COMMON, "Create default context");

      ArtifactToken defaultUserContext = tx.createArtifact(CoreArtifactTypes.Context, "Default User Context", BASE);
      tx.setSoleAttributeValue(defaultUserContext, Description,
         "Context containing all default commands available to users");

      ArtifactToken openURLCommand = tx.createArtifact(Command, "Open URL", BASE);
      tx.setSoleAttributeValue(openURLCommand, Description, "Opens a URL provided by user in new tab");

      ArtifactToken helpCommand = tx.createArtifact(Command, "Help", BASE);
      tx.setSoleAttributeValue(helpCommand, Description, "Show available commands");

      ArtifactToken filterCommand = tx.createArtifact(Command, "Filter", BASE);
      tx.setSoleAttributeValue(filterCommand, Description, "Filters elements in data table");

      ArtifactToken hideColumnCommand = tx.createArtifact(Command, "Hide Column", BASE);
      tx.setSoleAttributeValue(hideColumnCommand, Description, "Select columns to hide");

      ArtifactToken findArtifactCommand = tx.createArtifact(Command, "Find Artifact", BASE);
      tx.setSoleAttributeValue(findArtifactCommand, Description, "Search for an artifact");
      tx.setSoleAttributeValue(findArtifactCommand, HttpMethod, "GET");

      //Create Parameters For Commands:
      ArtifactToken urlParameter = tx.createArtifact(CoreArtifactTypes.ParameterString, "URL", BASE);
      tx.setSoleAttributeValue(urlParameter, Description, "URL to open in new browser tab");
      tx.setSoleAttributeValue(urlParameter, DefaultValue, "www.example.com");
      tx.setSoleAttributeValue(urlParameter, UseValidator, true);
      tx.setSoleAttributeValue(urlParameter, ValidatorType,
         "pattern: (?:http[s]:?\\/\\/)?(?:[\\w\\-]+(?::[\\w\\-]+)?@)?(?:[\\w\\-]+\\.)+(?:[a-z]{2,4})(?::[0-9]+)?(?:\\/[\\w\\-\\.%]+)*(?:\\?(?:[\\w\\-\\.%]+=[\\w\\-\\.%!]+&?)+)?(#\\w+\\-\\.%!)?; required: true");

      ArtifactToken searchTermParameter = tx.createArtifact(CoreArtifactTypes.ParameterString, "Search Term", BASE);
      tx.setSoleAttributeValue(searchTermParameter, Description, "Enter a term to search by: ");
      tx.setSoleAttributeValue(searchTermParameter, DefaultValue, "");

      ArtifactToken searchParamParameter =
         tx.createArtifact(CoreArtifactTypes.ParameterString, "Search Parameter", BASE);
      tx.setSoleAttributeValue(searchParamParameter, Description, "Enter parameter to search by: ");
      tx.setSoleAttributeValue(searchParamParameter, DefaultValue, "Search Parameter");

      ArtifactToken columnOptionsParameter =
         tx.createArtifact(CoreArtifactTypes.ParameterMultipleSelect, "Column Options", BASE);
      tx.setSoleAttributeValue(columnOptionsParameter, Description, "Select Column(s) to hide");

      //Establish Command to Parameter Relationship
      tx.relate(openURLCommand, DefaultHierarchical, urlParameter);
      tx.relate(hideColumnCommand, DefaultHierarchical, columnOptionsParameter);
      tx.relate(filterCommand, DefaultHierarchical, searchTermParameter);
      tx.relate(findArtifactCommand, DefaultHierarchical, searchParamParameter);

      //Establish Context to Command Relationship
      tx.relate(defaultUserContext, ContextToCommand, openURLCommand);
      tx.relate(defaultUserContext, ContextToCommand, helpCommand);
      tx.relate(defaultUserContext, ContextToCommand, filterCommand);
      tx.relate(defaultUserContext, ContextToCommand, hideColumnCommand);
      tx.relate(defaultUserContext, ContextToCommand, findArtifactCommand);

      //Establish User Group to Context Relationship
      tx.relate(everyoneUserGroup.getArtifact(), UserGroupToContext, defaultUserContext);
      return tx.commit();
   }

   @Override
   public List<UserContext> getUserCommands() {
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

      List<UserContext> usersContexts =
         contextList.stream().filter(cntxt -> cntxt.isValid()).map(context -> new UserContext(context)).collect(
            Collectors.toList());

      return usersContexts;
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