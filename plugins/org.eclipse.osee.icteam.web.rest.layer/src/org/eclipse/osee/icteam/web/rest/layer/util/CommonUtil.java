/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.rest.layer.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.util.RestUtil;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.icteam.web.rest.data.write.TranferableArtifactLoader;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Operator;

/**
 * Util class for All Rest resources
 *
 * @author Ajay Chandrahasan 
 */

public class CommonUtil {

	/**
	 * 
	 * @param orcsApi 
	 * @return branch ID of common branch 
	 * @throws OseeCoreException
	 */
  public static BranchId getCommonBranch(final OrcsApi orcsApi) throws OseeCoreException {
	  
	  return CoreBranches.COMMON;
  }
/**
 * 
 * @return branch ID of common branch 
 * @throws OseeCoreException
 */
  public static BranchId getCommonBranch() throws OseeCoreException {
    return getCommonBranch(OseeCoreData.getOrcsApi());
  }
  
/**
 * 
 * @param orcsApi to query the database
 * @param currentLoggedInUser userID of current logged in user
 * @return Artifact data of current logged in user from database
 * @throws OseeCoreException
 */
  public static ArtifactReadable getCurrentUser(final OrcsApi orcsApi, final String currentLoggedInUser)
      throws OseeCoreException {
    if ((currentLoggedInUser != null) && !currentLoggedInUser.isEmpty()) {
      ArtifactReadable exactlyOne =
          orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User)
              .and(CoreAttributeTypes.UserId, currentLoggedInUser, QueryOption.EXACT_MATCH_OPTIONS).getResults().getExactlyOne();
      return exactlyOne;
    }
    return orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults()
        .getExactlyOne();
  }
  
/**
 * 
 * @param userId of user
 * @return Artifact Data of given userID from database
 * @throws OseeCoreException
 */

  public static ArtifactReadable getUserFromGivenUserId(final String userId) throws OseeCoreException {
    return getCurrentUser(OseeCoreData.getOrcsApi(), userId);
  }

  /**
   * 
   * @param guidOrHrid  guid or hrid to get Artifact data for
   * @param branchId branch ID to get artifact data from
   * @param orcsApi to query database
   * @return Artifact Data for given Guid or Hrid and for given branch ID from database and this also filters out deleted data
   * @throws OseeCoreException
   */
  public static ArtifactReadable getArtifactFromIdExcludingDeleted(final String guidOrHrid, final BranchId branchId,
      final OrcsApi orcsApi) throws OseeCoreException {
	  ArtifactReadable ai = null;
 	//  if(guidOrHrid.matches("[0-9]+")){
     ai =  orcsApi.getQueryFactory().fromBranch(branchId).andUuid(Long.valueOf(guidOrHrid)).excludeDeleted().getResults()
            .getExactlyOne();
    return ai;
  }
/**
 * 
 * @param guidOrHrid guid or hrid to get Artifact data for
 * @param branch branch to get artifact data from
 * @return Artifact Data for given Guid or Hrid and for given branch from database and this also filters out deleted data
 * @throws OseeCoreException
 */
  public static ArtifactReadable getArtifactFromIdExcludingDeleted(final String guidOrHrid, final IOseeBranch branch)
      throws OseeCoreException {

    return getArtifactFromIdExcludingDeleted(guidOrHrid, branch, OseeCoreData.getOrcsApi());
  }
/**
 * 
 * @param guidOrHrid guid or hrid to get Artifact deatils
 * @return Artifact details for given guid or hrid
 * @throws OseeCoreException
 */
  public static ArtifactReadable getArtifactFromIdExcludingDeleted(final String guidOrHrid) throws OseeCoreException {

    return getArtifactFromIdExcludingDeleted(guidOrHrid, getCommonBranch(), OseeCoreData.getOrcsApi());
  }
/**
 * 
 * @param dateString Date information in String format
 * @return Date object format from string format
 */

	public static Date getDate(final String dateString) {
    try {
    	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd hh:mm:ss zzz yyyy", Locale.getDefault());
      Date parse = simpleDateFormat.parse(dateString);
      return parse;
    }
    catch (ParseException e) {
    	try {
    		  
    	   	 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
    	     Date parse = simpleDateFormat.parse(dateString);
    	     return parse;
    	   }
    	   catch (ParseException e2) {
    	     e2.printStackTrace();
    	   }
    }
    
    return null;

  }
/**
 * 
 * @param xml xml string containing userIDs
 * @return List of Transferable Artifact containing UserIDs from xml string
 */
  
  public static List<ITransferableArtifact> getBasicAssignees(final String xml) {

	    Set<String> users = new HashSet<String>();
	    OrcsApi orcsApi = OseeCoreData.getOrcsApi();

	    List<ITransferableArtifact> assignees = new ArrayList<ITransferableArtifact>();

	    StringTokenizer tok = new StringTokenizer(xml, ";");
	    int countTokens = tok.countTokens();

	    if (countTokens >= 2) {
	      tok.nextElement();
	      String assiStr = tok.nextToken();

	      Matcher m2 = Pattern.compile("<(.+?)>").matcher(assiStr);
	      while (m2.find()) {
	        assiStr = m2.group(1);
	        if(!(assiStr.equalsIgnoreCase("undefined"))){
	        	users.add(assiStr);
	        }
	        

	      }

	    }

	    for (String userId : users) {


	      try {
	        ArtifactReadable exactlyOne =
	            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
	                .andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, userId)
	                .getResults().getExactlyOne();
	        TransferableArtifact user = new TransferableArtifact();
	        TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(exactlyOne, user);
	        
	        AttributeReadable<Object> userIDAttr = exactlyOne.getAttributes(CoreAttributeTypes.UserId).getExactlyOne();
		      String userIDStr = (String) userIDAttr.getValue();
		      user.putAttributes("User Id", Arrays.asList(userIDStr));
	        
	        assignees.add(user);
	      }
	      catch (OseeCoreException e) {
	        e.printStackTrace();
	      }
	    }

	    return assignees;

	  }
/**
 * 
 * @param xml xml string containing userIDs
 * @return List of Transferable Artifact containing UserIDs from xml string
 */
  public static List<ITransferableArtifact> getAssignees(final String xml) {

    Set<String> users = new HashSet<String>();
    OrcsApi orcsApi = OseeCoreData.getOrcsApi();

    List<ITransferableArtifact> assignees = new ArrayList<ITransferableArtifact>();

    StringTokenizer tok = new StringTokenizer(xml, ";");
    int countTokens = tok.countTokens();

    if (countTokens >= 2) {
      tok.nextElement();
      String assiStr = tok.nextToken();

      Matcher m2 = Pattern.compile("<(.+?)>").matcher(assiStr);
      while (m2.find()) {
        assiStr = m2.group(1);
        if(!(assiStr.equalsIgnoreCase("undefined"))){
        users.add(assiStr);
        }
      }

    }

    for (String userId : users) {


      try {
        ArtifactReadable exactlyOne =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
                .andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, userId,QueryOption.EXACT_MATCH_OPTIONS )
                .getResults().getExactlyOne();
        TransferableArtifact user = new TransferableArtifact();
        TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(exactlyOne, user);
        assignees.add(user);
      }
      catch (OseeCoreException e) {
        e.printStackTrace();
      }
    }

    return assignees;

  }
/**
 * 
 * @param readable  Artifact
 * @return Transferable Artifact containing user details having completedby attribute
 */
  public static TransferableArtifact getCompletedByUser(final ArtifactReadable readable) {

    ResultSet<? extends AttributeReadable<Object>> attrCompletedBy =
        readable.getAttributes(AtsAttributeTypes.CompletedBy);
    if (attrCompletedBy.size() > 0) {
      String string = attrCompletedBy.getExactlyOne().getValue().toString();
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      ArtifactReadable exactlyOne =
          orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
              .andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, Operator.EQUAL.toString(), QueryOption.valueOf(string)).getResults()
              .getExactlyOne();
      TransferableArtifact user = new TransferableArtifact();
      TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(exactlyOne, user);
      return user;
    }
    return null;

  }
/**
 *   
 * @param readable Artifact
 * @return Transferable Artifact containing user details having completedby attribute
 */
  
  public static TransferableArtifact getBasicCompletedByUser(final ArtifactReadable readable) {

	    ResultSet<? extends AttributeReadable<Object>> attrCompletedBy =
	        readable.getAttributes(AtsAttributeTypes.CompletedBy);
	    if (attrCompletedBy.size() > 0) {
	      String string = attrCompletedBy.getExactlyOne().getValue().toString();
	      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
	      ArtifactReadable exactlyOne =
	          orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
	              .andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, string).getResults()
	              .getExactlyOne();
	      TransferableArtifact user = new TransferableArtifact();
	      TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(exactlyOne, user);
	      
	      AttributeReadable<Object> userIDAttr = exactlyOne.getAttributes(CoreAttributeTypes.UserId).getExactlyOne();
	      String userIDStr = (String) userIDAttr.getValue();
	      user.putAttributes("User Id", Arrays.asList(userIDStr));
	      
	      return user;
	    }
	    return null;

	  }
  
  /**
   * 
   * @param readable  Artifact
   * @return Transferable Artifact containing user details having CancelledBy attribute
   */

  public static TransferableArtifact getCancelledBy(final ArtifactReadable readable) {

    ResultSet<? extends AttributeReadable<Object>> attrCompletedBy =
        readable.getAttributes(AtsAttributeTypes.CancelledBy);
    if (attrCompletedBy.size() > 0) {
      String string = attrCompletedBy.getExactlyOne().getValue().toString();
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      ArtifactReadable exactlyOne =
          orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
              .andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, Operator.EQUAL.toString(), QueryOption.valueOf(string)).getResults()
              .getExactlyOne();
      TransferableArtifact user = new TransferableArtifact();
      TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(exactlyOne, user);
      return user;
    }
    return null;

  }
  /**
   * 
   * @param readable  Artifact
   * @return Transferable Artifact containing user details having cancelledBy attribute
   */
  public static TransferableArtifact getBasicCancelledBy(final ArtifactReadable readable) {

	    ResultSet<? extends AttributeReadable<Object>> attrCompletedBy =
	        readable.getAttributes(AtsAttributeTypes.CancelledBy);
	    if (attrCompletedBy.size() > 0) {
	      String string = attrCompletedBy.getExactlyOne().getValue().toString();
	      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
	      ArtifactReadable exactlyOne =
	          orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
	              .andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, string).getResults()
	              .getExactlyOne();
	      TransferableArtifact user = new TransferableArtifact();
	      TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(exactlyOne, user);
	      
	      
	      AttributeReadable<Object> userIDAttr = exactlyOne.getAttributes(CoreAttributeTypes.UserId).getExactlyOne();
	      String userIDStr = (String) userIDAttr.getValue();
	      user.putAttributes("User Id", Arrays.asList(userIDStr));
	      
	      return user;
	    }
	    return null;

	  }

/**
 * 
 * @param artifact from which to get related artifacts
 * @param relationName used to get related artifacts
 * @return List of artifacts having given relationtype
 */

  public static List<ArtifactReadable> getRelatedArtifact(final ArtifactReadable artifact, final String relationName) {
    List<ArtifactReadable> listTeamArtifact = new ArrayList<ArtifactReadable>();
    try {

      Collection<RelationTypeToken> existingRelationTypes = artifact.getExistingRelationTypes();
      for (RelationTypeToken iRelationType : existingRelationTypes) {

        if (((RelationTypeToken) iRelationType).getName().equals(relationName)) {
        	RelationTypeSide createRelationTypeSide =
                    RelationTypeSide
                        .create(((RelationTypeToken)iRelationType), RelationSide.SIDE_B);
          ResultSet<ArtifactReadable> relatedArtifactsTemp1 = artifact.getRelated(createRelationTypeSide);
          if (relatedArtifactsTemp1.size() > 0) {
            Iterator<ArtifactReadable> iterator = relatedArtifactsTemp1.iterator();
            while (iterator.hasNext()) {
              listTeamArtifact.add(iterator.next());
            }
          }

          RelationTypeSide createRelationTypeSide1 =
                  RelationTypeSide
                      .create(((RelationTypeToken)iRelationType), RelationSide.SIDE_A);
          ResultSet<ArtifactReadable> relatedArtifactsTemp2 = artifact.getRelated(createRelationTypeSide1);
          if (relatedArtifactsTemp2.size() > 0) {
            Iterator<ArtifactReadable> iterator = relatedArtifactsTemp2.iterator();
            while (iterator.hasNext()) {
              listTeamArtifact.add(iterator.next());
            }

          }
          break;
        }


      }

    }
    catch (OseeCoreException e) {
      e.printStackTrace();
    }
    return listTeamArtifact;
  }

/**
 * 
 * @param readable from which to get actual hours worked
 * @return Actual hours worked from Artifact and its sub tasks and reviews
 */
  public static double getActualWorkedHours(final ArtifactReadable readable) {

    double actualHours = 0.0;
    ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(AtsAttributeTypes.CurrentState);
    for (AttributeReadable<Object> attributeReadable : attributes) {
      String totalHoursString = RestUtil.getTotalHoursString(attributeReadable.getValue().toString());
      if (totalHoursString.length() > 0) {
        actualHours = Double.parseDouble(totalHoursString);
      }
    }

    ResultSet<? extends AttributeReadable<Object>> attributes1 = readable.getAttributes(AtsAttributeTypes.State);
    for (AttributeReadable<Object> attributeReadable : attributes1) {
      String totalHoursString = RestUtil.getTotalHoursString(attributeReadable.getValue().toString());
      if (totalHoursString.length() > 0) {
        actualHours = actualHours + Double.parseDouble(totalHoursString);
      }
    }

    // Sub task
    ResultSet<ArtifactReadable> relatedArtifacts =
        OseeCoreData.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON)
            .andIsOfType(AtsArtifactTypes.Task).andRelatedTo(AtsRelationTypes.TeamWfToTask_TeamWorkflow, readable)
            .getResults();
    for (ArtifactReadable subtask : relatedArtifacts) {
      ResultSet<? extends AttributeReadable<Object>> attributes2 = subtask.getAttributes(AtsAttributeTypes.State);
      for (AttributeReadable<Object> attributeReadable : attributes2) {
        String totalHoursString = RestUtil.getTotalHoursString(attributeReadable.getValue().toString());
        if (totalHoursString.length() > 0) {
          actualHours = actualHours + Double.parseDouble(totalHoursString);
        }
      }
      ResultSet<? extends AttributeReadable<Object>> attributes3 =
          subtask.getAttributes(AtsAttributeTypes.CurrentState);
      for (AttributeReadable<Object> attributeReadable : attributes3) {
        String totalHoursString = RestUtil.getTotalHoursString(attributeReadable.getValue().toString());
        if (totalHoursString.length() > 0) {
          actualHours = actualHours + Double.parseDouble(totalHoursString);
        }
      }

    }

    // Reviews
    ResultSet<ArtifactReadable> relatedArtifacts1 =
        OseeCoreData.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON)
            .andIsOfType(AtsArtifactTypes.PeerToPeerReview)
            .andRelatedTo(AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow, readable).getResults();
    for (ArtifactReadable subtask : relatedArtifacts1) {
      ResultSet<? extends AttributeReadable<Object>> attributes2 = subtask.getAttributes(AtsAttributeTypes.State);
      for (AttributeReadable<Object> attributeReadable : attributes2) {
        String totalHoursString = RestUtil.getTotalHoursString(attributeReadable.getValue().toString());
        if (totalHoursString.length() > 0) {
          actualHours = actualHours + Double.parseDouble(totalHoursString);
        }
      }
      ResultSet<? extends AttributeReadable<Object>> attributes3 =
          subtask.getAttributes(AtsAttributeTypes.CurrentState);
      for (AttributeReadable<Object> attributeReadable : attributes3) {
        String totalHoursString = RestUtil.getTotalHoursString(attributeReadable.getValue().toString());
        if (totalHoursString.length() > 0) {
          actualHours = actualHours + Double.parseDouble(totalHoursString);
        }
      }

    }
    return actualHours;
  }

/**
 * Gets all Projects as Artifacts
 * @return List of projects as artifact readable
 */

  public static ResultSet<ArtifactReadable> getAllProjectsAsArtifactReadable() {
    OrcsApi orcsApi = OseeCoreData.getOrcsApi();
    ResultSet<ArtifactReadable> list =
        orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.Project)
            .getResults();
    return list;
  }




  /**
   * @param artifactReadable ArtiartifactReadable from which the value of @param attributeType is extracted
   * @param attributeType type of attribute for which the value is required
   * @return the value of attribute if found else empty string value
   */
  public static String getAttributeValue(final ArtifactReadable artifactReadable, final AttributeTypeToken attributeType) {
    return getAttributeValue(artifactReadable, attributeType, "");
  }

  /**
   * @param artifactReadable ArtiartifactReadable from which the value of @param attributeType is extracted
   * @param attributeType type of attribute for which the value is required
   * @param defaultValue default value to be returned
   * @return the value of attribute if found else default value
   */
  private static <T> T getAttributeValue(final ArtifactReadable artifactReadable, final AttributeTypeToken attributeType,
      final T defaultValue) {
    AttributeReadable<T> readable = artifactReadable.<T> getAttributes(attributeType).getOneOrNull();
    if (readable != null) {
      return readable.getValue();
    }
    return defaultValue;
  }

  /**
   * @param attribute project GuID for which Release versions have to be returned
   * @return Artifact Readable having project release versions 
   */
  public static ResultSet<ArtifactReadable> getReleasesForProject(final String projectGuid) {
    ArtifactReadable project = CommonUtil.getArtifactFromIdExcludingDeleted(projectGuid);

    return OseeCoreData.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON)
        .andIsOfType(AtsArtifactTypes.Version).andRelatedTo(AtsRelationTypes.ProjectToVersion_Project, project)
        .getResults();
  }
/**
 * @param xml  string containing userIDs
 * @return List of Transferable Artifact containing UserIDs from xml string
 */
  public static List<ITransferableArtifact> getBasicAssigneesInfo(final String xml) {

	    Set<String> users = new HashSet<String>();
	    OrcsApi orcsApi = OseeCoreData.getOrcsApi();

	    List<ITransferableArtifact> assignees = new ArrayList<ITransferableArtifact>();

	    StringTokenizer tok = new StringTokenizer(xml, ";");
	    int countTokens = tok.countTokens();

	    if (countTokens >= 2) {
	      tok.nextElement();
	      String assiStr = tok.nextToken();

	      Matcher m2 = Pattern.compile("<(.+?)>").matcher(assiStr);
	      while (m2.find()) {
	        assiStr = m2.group(1);
	        users.add(assiStr);

	      }

	    }

	    for (String userId : users) {


	      try {
	        ArtifactReadable exactlyOne =
	            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
	                .andIsOfType(CoreArtifactTypes.User).and(CoreAttributeTypes.UserId, userId)
	                .getResults().getExactlyOne();
	        TransferableArtifact user = new TransferableArtifact();
	        TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(exactlyOne, user);
	        user.putAttributes(CoreAttributeTypes.UserId.toString(), Arrays.asList(exactlyOne.getAttributes(CoreAttributeTypes.UserId).getExactlyOne().toString()));
	        assignees.add(user);
	      }
	      catch (OseeCoreException e) {
	        e.printStackTrace();
	      }
	    }

	    return assignees;

	  }
}
