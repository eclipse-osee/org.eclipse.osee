/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.search.branch;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.rest.model.search.branch.BranchQueryOptions;
import org.eclipse.osee.orcs.search.BranchQuery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author John Misinco
 */
@Path("branchQuery")
public class BranchQueryResource {

   private final Gson plainG = new Gson();
   private final Gson prettyG = new GsonBuilder().setPrettyPrinting().create();
   private final OrcsApi orcsApi;

   public BranchQueryResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * Perform a branch query based on a JSON input
    * 
    * @param branchQueryJson JSON of {@link org.eclipse.osee.orcs.rest.model.search.branch.BranchQueryOptions
    * BranchQueryOptions} class
    * @return JSON representation of the branch query results
    */
   @Path("/")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public Response searchBranches(String branchQueryJson) {
      BranchQueryOptions options = plainG.fromJson(branchQueryJson, BranchQueryOptions.class);
      List<BranchReadable> results = getResultsFromOptions(options);
      return Response.ok(plainG.toJson(results)).build();
   }

   /**
    * Perform a branch query based on the input query parameters
    * 
    * @param branchIds comma separated list of branch ids
    * @param branchTypes comma separated list of {@link org.eclipse.osee.framework.core.enums.BranchType BranchType}
    * @param branchStates comma separated list of {@link org.eclipse.osee.framework.core.enums.BranchState BranchState}
    * @param deleted to include deleted branches in the search
    * @param archived to include archived branches in the search
    * @param childOf branch id of the parent to search children of
    * @param ancestorOf branch id of ancestor to search decendents of
    * @param pretty if the returned JSON should be pretty printed
    * @return JSON representation of the branch query results
    */
   @Path("/")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response searchBranchesFromQuery(@QueryParam("branchIds") String branchIds, @QueryParam("branchTypes") String branchTypes, @QueryParam("branchStates") String branchStates, @QueryParam("deleted") boolean deleted, @QueryParam("archived") boolean archived, @QueryParam("nameEquals") String nameEquals, @QueryParam("namePattern") String namePattern, @QueryParam("childOf") Long childOf, @QueryParam("ancestorOf") Long ancestorOf, @QueryParam("pretty") boolean pretty) {
      BranchQueryOptions options = new BranchQueryOptions();
      if (Strings.isValid(branchIds)) {
         List<Integer> branchIdVals = new LinkedList<Integer>();
         for (String branchId : branchIds.split(",")) {
            branchIdVals.add(Integer.parseInt(branchId));
         }
         options.setBranchIds(branchIdVals);
      }

      if (Strings.isValid(branchTypes)) {
         List<BranchType> branchTypeVals = new LinkedList<BranchType>();
         for (String branchType : branchTypes.split(",")) {
            branchTypeVals.add(BranchType.valueOf(branchType.toUpperCase()));
         }
         options.setBranchTypes(branchTypeVals);
      }

      if (Strings.isValid(branchStates)) {
         List<BranchState> branchStateVals = new LinkedList<BranchState>();
         for (String branchState : branchStates.split(",")) {
            branchStateVals.add(BranchState.valueOf(branchState.toUpperCase()));
         }
         options.setBranchStates(branchStateVals);
      }

      options.setIncludeDeleted(deleted);
      options.setIncludeArchived(archived);

      if (Strings.isValid(nameEquals)) {
         options.setNameEquals(nameEquals);
      }

      if (Strings.isValid(namePattern)) {
         options.setNamePattern(namePattern);
      }

      if (childOf != null) {
         options.setIsChildOf(childOf);
      }

      if (ancestorOf != null) {
         options.setIsAncestorOf(ancestorOf);
      }

      List<BranchReadable> results = getResultsFromOptions(options);
      Gson gson = pretty ? prettyG : plainG;
      return Response.ok(gson.toJson(results)).build();
   }

   /**
    * Perform a branch query for all working branches
    * 
    * @param pretty if the returned JSON should be pretty printed
    * @return JSON representation of the branch query results
    */
   @Path("/working")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getWorkingBranches(@QueryParam("pretty") boolean pretty) {
      BranchQueryOptions options = new BranchQueryOptions();
      options.setBranchTypes(Collections.singletonList(BranchType.WORKING));
      List<BranchReadable> results = getResultsFromOptions(options);
      Gson gson = pretty ? prettyG : plainG;
      return Response.ok(gson.toJson(results)).build();
   }

   /**
    * Perform a branch query for all baseline branches
    * 
    * @param pretty if the returned JSON should be pretty printed
    * @return JSON representation of the branch query results
    */
   @Path("/baseline")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getBaselineBranches(@QueryParam("pretty") boolean pretty) throws OseeCoreException {
      BranchQueryOptions options = new BranchQueryOptions();
      options.setBranchTypes(Collections.singletonList(BranchType.BASELINE));
      List<BranchReadable> results = getResultsFromOptions(options);
      Gson gson = pretty ? prettyG : plainG;
      return Response.ok(gson.toJson(results)).build();
   }

   private List<BranchReadable> getResultsFromOptions(BranchQueryOptions options) {
      BranchQuery query = orcsApi.getQueryFactory(null).branchQuery();
      if (Conditions.hasValues(options.getBranchIds())) {
         query.andLocalIds(options.getBranchIds());
      }

      List<BranchState> branchStates = options.getBranchStates();
      if (Conditions.hasValues(branchStates)) {
         query.andStateIs(branchStates.toArray(new BranchState[branchStates.size()]));
      }

      List<BranchType> branchTypes = options.getBranchTypes();
      if (Conditions.hasValues(branchTypes)) {
         query.andIsOfType(branchTypes.toArray(new BranchType[branchTypes.size()]));
      }

      List<Integer> branchIds = options.getBranchIds();
      if (Conditions.hasValues(branchIds)) {
         query.andLocalIds(branchIds);
      }

      if (options.isIncludeArchived()) {
         query.includeArchived();
      }

      if (options.isIncludeDeleted()) {
         query.includeDeleted();
      }

      String nameEquals = options.getNameEquals();
      if (Strings.isValid(nameEquals)) {
         query.andNameEquals(nameEquals);
      }

      String namePattern = options.getNamePattern();
      if (Strings.isValid(namePattern)) {
         query.andNamePattern(namePattern);
      }

      Long ancestorOf = options.getIsAncestorOf();
      if (ancestorOf > 0) {
         IOseeBranch ancestorOfToken = TokenFactory.createBranch(ancestorOf, "queryAncestorOf");
         query.andIsAncestorOf(ancestorOfToken);
      }

      Long childOf = options.getIsChildOf();
      if (childOf > 0) {
         IOseeBranch childOfToken = TokenFactory.createBranch(ancestorOf, "queryChildOf");
         query.andIsAncestorOf(childOfToken);
      }

      List<BranchReadable> toReturn = new LinkedList<BranchReadable>();
      for (BranchReadable branch : query.getResults()) {
         toReturn.add(branch);
      }
      return toReturn;
   }
}
