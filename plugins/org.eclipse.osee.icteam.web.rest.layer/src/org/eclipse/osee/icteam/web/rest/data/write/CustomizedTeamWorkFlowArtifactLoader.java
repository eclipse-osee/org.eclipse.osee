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
package org.eclipse.osee.icteam.web.rest.data.write;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.CustomizedTeamWorkFlowArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.util.CommonConstants;
import org.eclipse.osee.icteam.common.clientserver.util.RestUtil;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.icteam.web.rest.layer.util.CommonUtil;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * This class is used to populate the CustomizedTeamWorkFlowArtifact from the ArtifactReadable object
 * 
 * @author Ajay Chandrahasan
 */
public class CustomizedTeamWorkFlowArtifactLoader {

	/**
	   * This function sets the values in the transferable artifact from artifact readable object
	   * 
	   * @param readable ArtifactReadable Object holding the values
	   * @param transArtifact is the CustomizedTeamWorkFlowArtifact object in which the values will be set.
	   */
  public static void copyArtifactReadbleToTransferableArtifact(final ArtifactReadable readable,
      final CustomizedTeamWorkFlowArtifact transArtifact) throws OseeCoreException {

	    Collection<? extends AttributeTypeToken> existingAttributeTypes = readable.getExistingAttributeTypes();
	    for (AttributeTypeToken iAttributeType : existingAttributeTypes) {
	      ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(iAttributeType);
	      List<String> listTemp = new ArrayList<String>();
	      for (AttributeReadable<Object> attributeReadable : attributes) {
	        if (null != attributeReadable.getValue()) {
	          listTemp.add(attributeReadable.getValue().toString());
	        }
	        else {
	          listTemp.add("");
	        }
	      }
	      transArtifact.putAttributes(iAttributeType.toString(), listTemp);
	    }

	    List<String> list2 = new ArrayList<String>();
	    list2.add(readable.getGuid());
	    transArtifact.putAttributes("guid", list2);

	   
	    transArtifact.setGuid(readable.getGuid());
	    transArtifact.setName(readable.getName());


	    Collection<RelationTypeToken> existingRelationTypes = readable.getExistingRelationTypes();
	    try {
	      for (RelationTypeToken iRelationType : existingRelationTypes) {
	    	  RelationTypeSide createRelationTypeSide =
	  	            RelationTypeSide.create(((RelationTypeToken)iRelationType), RelationSide.SIDE_B);
	        ResultSet<ArtifactReadable> relatedArtifacts = readable.getRelated(createRelationTypeSide);
	        if (relatedArtifacts.size() > 0) {
	          fillRelations(relatedArtifacts, createRelationTypeSide, iRelationType, transArtifact);
	        }

	        RelationTypeSide createRelationTypeSide1 =
	        		RelationTypeSide.create(((RelationTypeToken)iRelationType), RelationSide.SIDE_A);
	        ResultSet<ArtifactReadable> relatedArtifacts1 = readable.getRelated(createRelationTypeSide1);
	        if (relatedArtifacts1.size() > 0) {
	          fillRelations(relatedArtifacts1, createRelationTypeSide1, iRelationType, transArtifact);
	        }

	      }
	    }
	    catch (Exception e) {
	      // TODO Auto-generated catch block
	      OseeLog.log(org.eclipse.osee.icteam.web.rest.layer.Activator.class, Level.WARNING,
	          " Exception while copying relations . Some relation will not be copied ");
	    }
	  }

  
  /**
   * This function sets the values in the transferable artifact object from artifact readable object but
   * the Relations will not be filled in this method
   * 
   * @param readable ArtifactReadable Object holding the values
   * @param transArtifact is the CustomizedTeamWorkFlowArtifact object in which the values will be set.
   */
  public static void copyArtifactReadbleToTransferableArtifactWithoutRelation(final ArtifactReadable readable,
	      final CustomizedTeamWorkFlowArtifact transArtifact) throws OseeCoreException {


		    List<String> list2 = new ArrayList<String>();
		    list2.add(readable.getGuid());
		    transArtifact.putAttributes("guid", list2);

		   
		    transArtifact.setGuid(readable.getGuid());
		    transArtifact.setName(readable.getName());
		    transArtifact.setArtifactType(readable.getArtifactType().toString());

		    ResultSet<ArtifactReadable> related = readable.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
		    for (ArtifactReadable artifactReadable : related) {
		      TransferableArtifact ar = new TransferableArtifact();
		      TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, ar);
		      transArtifact.setVersion(ar);
		    }
		    
		    
		    ResultSet<? extends AttributeReadable<Object>> attributes1 = readable.getAttributes(AtsAttributeTypes.ActionableItem);
		    if(attributes1!=null && attributes1.size()>0)
		    {
		    	for (AttributeReadable<Object> attributeReadable : attributes1) {
					String actionableGuid = (String) attributeReadable.getValue();
					
					OrcsApi orcsApi = OseeCoreData.getOrcsApi();
				      ResultSet<ArtifactReadable> list =
				          orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.ActionableItem).andUuid(Long.valueOf(actionableGuid))
				              .getResults();
					if(list!=null && list.size()>0)
					{
						for (ArtifactReadable artifactReadable : list) {
							
							
							ArtifactReadable teamArtifact = artifactReadable.getRelated(AtsRelationTypes.TeamActionableItem_TeamDefinition).getExactlyOne();
							
							fillTeamMembersTeamLeads(teamArtifact,transArtifact);
							
							 TransferableArtifact ar = new TransferableArtifact();
					         TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, ar);
					          transArtifact.setActionableItem(ar);
						}
					}
					
				}
		    }
		    ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(AtsAttributeTypes.CurrentState);
		    
		    if(attributes!=null && attributes.size()>0)
		    {
		    	for (AttributeReadable<Object> attributeReadable : attributes) {
					String currentStateString = (String) attributeReadable.getValue();
					 String currentState = RestUtil.getCurrentState(currentStateString);
//				        if (currentState.equals("Completed")) {
//				          ITransferableArtifact completedByUser = CommonUtil.getBasicCompletedByUser(readable);
//				          transArtifact.setAssignee(Arrays.asList(completedByUser));
//				        }
//				        else if (currentState.equals("Cancelled")) {
//				          ITransferableArtifact completedByUser = CommonUtil.getBasicCancelledBy(readable);
//				          transArtifact.setAssignee(Arrays.asList(completedByUser));
//				        }
//				        else {
				          List<ITransferableArtifact> assignee = CommonUtil.getBasicAssignees(attributeReadable.getValue().toString());
				          transArtifact.setAssignee(assignee);
//				        }
				        transArtifact.setCurrentState(currentState);
				
		    }
		    }
		    	
		    	//Get Description
		    	 ResultSet<? extends AttributeReadable<Object>> attributesDes = readable.getAttributes(AtsAttributeTypes.Description);
		    	 if(attributesDes!=null && attributesDes.size()>0)
				    {
			    	 for (AttributeReadable<Object> attributeReadable : attributesDes) {
							String des = (String) attributeReadable.getValue();
							transArtifact.setDescription(des);
							
			    	 }
				    }
		    	 
		    	//Get prority
		    	 ResultSet<? extends AttributeReadable<Object>> attributesprority = readable.getAttributes(AtsAttributeTypes.Priority);
		    	 if(attributesprority!=null && attributesprority.size()>0)
				    {
		    	 for (AttributeReadable<Object> attributeReadable : attributesprority) {
						String pro = (String) attributeReadable.getValue();
						transArtifact.setPriority(pro);
		    	 }
		    	 }
		    	 
		    	//Get change type
		    	 ResultSet<? extends AttributeReadable<Object>> attributesChange = readable.getAttributes(AtsAttributeTypes.ChangeType);
		    	 if(attributesChange!=null && attributesChange.size()>0)
				    {
		    	 for (AttributeReadable<Object> attributeReadable : attributesChange) {
						String change = (String) attributeReadable.getValue();
						transArtifact.setChangeType(change);
		    	 }
		    	 }
		    	 
		    	//Get createdBy
		    	 ResultSet<? extends AttributeReadable<Object>> attributesCreatedBy = readable.getAttributes(AtsAttributeTypes.CreatedBy);
		    	 if(attributesCreatedBy!=null && attributesCreatedBy.size()>0)
				    {
		    	 for (AttributeReadable<Object> attributeReadable : attributesCreatedBy) {
						String createdBy = (String) attributeReadable.getValue();
						transArtifact.setCreatedBy(createdBy);
		    	 }
		    	 }
		    	 
		    		//Get rank
		    	 ResultSet<? extends AttributeReadable<Object>> attributesRank = readable.getAttributes(AtsAttributeTypes.Rank);
		    	 if(attributesRank!=null && attributesRank.size()>0)
				    {
		    	 for (AttributeReadable<Object> attributeReadable : attributesRank) {
						String rank = (String) attributeReadable.getValue();
						transArtifact.setRank(rank);
		    	 }
		    	 }
		    	 
		    	 
		    	//Get createdDate
		    	 ResultSet<? extends AttributeReadable<Object>> attributesCreatedDate = readable.getAttributes(AtsAttributeTypes.CreatedDate);
		    	 if(attributesCreatedDate!=null && attributesCreatedDate.size()>0)
				    {
		    	 for (AttributeReadable<Object> attributeReadable : attributesCreatedDate) {
						fillCreatedDate(attributeReadable.getValue().toString(), transArtifact);
		    	 }
		    	 }
		    	 
		    	 //Get Story point
		    	 ResultSet<? extends AttributeReadable<Object>> attributesStory = readable.getAttributes(AtsAttributeTypes.PointsAttributeType);
		    	 if(attributesStory!=null && attributesStory.size()>0)
				    {
		    	 for (AttributeReadable<Object> attributeReadable : attributesStory) {
						String story = (String) attributeReadable.getValue();
						transArtifact.setStory(story);
		    	 }
		    	 }
		    	//Get createdDate
		    	 ResultSet<? extends AttributeReadable<Object>> attributeEstimateComp = readable.getAttributes(AtsAttributeTypes.EstimatedCompletionDate);
		    	 if(attributeEstimateComp!=null && attributeEstimateComp.size()>0)
				    {
		    	 for (AttributeReadable<Object> attributeReadable : attributeEstimateComp) {
		    		 if(attributeReadable.getValue()!=null){
		    		 fillEstimatedCompletionDate(attributeReadable.getValue().toString(), transArtifact);
		    		 }
		    	 }
		    	 }
		    	 
		    	//Get createdDate
		    	 ResultSet<? extends AttributeReadable<Object>> attributesCompletionDate = readable.getAttributes(AtsAttributeTypes.CompletedDate);
		    	 if(attributesCompletionDate!=null && attributesCompletionDate.size()>0)
				    {
		    	 for (AttributeReadable<Object> attributeReadable : attributesCompletionDate) {
		    		 fillCompletionDate(attributeReadable.getValue().toString(), transArtifact);
		    	 }
		    	 }
		    	 
		    	 
		    	//Get Estimated Hours
		    	 ResultSet<? extends AttributeReadable<Object>> attributesEstimatedHours = readable.getAttributes(AtsAttributeTypes.EstimatedHours);
		    	 if(attributesEstimatedHours!=null && attributesEstimatedHours.size()>0)
				    {
			    	 for (AttributeReadable<Object> attributeReadable : attributesEstimatedHours) {
							double estimatedHours = (Double) attributeReadable.getValue();
							transArtifact.setEstimatedHours(estimatedHours+"");
							
			    	 }
				    }
		    	 
		    	//Get Remaining Hours
		    	 ResultSet<? extends AttributeReadable<Object>> attributesRemainingHours = readable.getAttributes(AtsAttributeTypes.Category1);
		    	 if(attributesRemainingHours!=null && attributesRemainingHours.size()>0)
				    {
			    	 for (AttributeReadable<Object> attributeReadable : attributesRemainingHours) {
							String des = (String) attributeReadable.getValue();
							transArtifact.setRemainingHours(des);
							
			    	 }
				    }
		    	
		  }
  
  /**
   * This function sets the estimated completion date of the task in the 
   * CustomizedTeamWorkFlowArtifact object 
   * 
   * @param string String object holding the estimated completion date information
   * @param transArtifact is the CustomizedTeamWorkFlowArtifact object in which the 
   * ExpectedDate will be set.
   */
  private static void fillEstimatedCompletionDate(final String string, final CustomizedTeamWorkFlowArtifact transArtifact) {
	    Date expDate = null;

	    if (string.length() > 0) {
	      try {
	        expDate = new SimpleDateFormat("E MMM dd hh:mm:ss zzz yyyy", Locale.getDefault()).parse(string);
	        transArtifact.setExpectedDate(expDate);
	      }
	      catch (ParseException e) {
	        e.printStackTrace();
	      }
	    }

	  }

  /**
   * This function sets the completion date of the task in the CustomizedTeamWorkFlowArtifact object 
   * 
   * @param string String object holding the completion date information
   * @param transArtifact is the CustomizedTeamWorkFlowArtifact object in which the 
   * CompletionDate will be set.
   */
	  private static void fillCompletionDate(final String string, final CustomizedTeamWorkFlowArtifact transArtifact) {
	    Date compDate = null;

	    if (string.length() > 0) {
	      try {
	    	  compDate = new SimpleDateFormat("E MMM dd hh:mm:ss zzz yyyy", Locale.getDefault()).parse(string);
	        transArtifact.setCompletionDate(compDate);
	      }
	      catch (ParseException e) {
	        e.printStackTrace();
	      }
	    }

	  }

  /**
	 * This function sets the created date of the task in the CustomizedTeamWorkFlowArtifact object 
	 * 
	 * @param string String object holding the created date information
	 * @param transArtifact is the CustomizedTeamWorkFlowArtifact object in which 
	 * the CreatedDate will be set.
	 */
  public static void fillCreatedDate(final String string, final CustomizedTeamWorkFlowArtifact transArtifact) {
    Date createdDate = null;

    if (string.length() > 0) {
      try {
    	  createdDate = new SimpleDateFormat("E MMM dd hh:mm:ss zzz yyyy", Locale.getDefault()).parse(string);
        transArtifact.setCreatedDate(createdDate);
      }
      catch (ParseException e) {
        e.printStackTrace();
      }
    }

  }

  /**
   * This function sets the team members and team leads information
   * of the task in the CustomizedTeamWorkFlowArtifact object 
   * @param teamArtifact ArtifactReadable object holding the team member and team lead information
   * @param transArtifact is the CustomizedTeamWorkFlowArtifact object in which the team member and team lead
   * information will be set.
   */
  
  private static void fillTeamMembersTeamLeads(ArtifactReadable teamArtifact,
		CustomizedTeamWorkFlowArtifact transArtifact) {

	   

	          List<ArtifactReadable> relatedArtifacts = CommonUtil.getRelatedArtifact(teamArtifact, "TeamLead");
	          List<ArtifactReadable> relatedArtifacts2 = CommonUtil.getRelatedArtifact(teamArtifact, "TeamMember");

	          for (ArtifactReadable artifactReadable2 : relatedArtifacts) {
	            ArtifactReadable removeArt = null;
	            String userId = artifactReadable2.getSoleAttributeAsString(CoreAttributeTypes.UserId);
	            for (ArtifactReadable artifactReadable3 : relatedArtifacts2) {
	              String UserIdTmp = artifactReadable3.getSoleAttributeAsString(CoreAttributeTypes.UserId);
	              if (UserIdTmp.equals(userId)) {
	                removeArt = artifactReadable3;
	                break;
	              }
	            }
	            if (removeArt != null) {
	              relatedArtifacts2.remove(removeArt);
	            }

	          }

	          for (ArtifactReadable art : relatedArtifacts2) {
	            TransferableArtifact ar = new TransferableArtifact();
	            TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(art, ar);
	            ResultSet<? extends AttributeReadable<Object>> attributes = art.getAttributes(CoreAttributeTypes.UserId);
	            for (AttributeReadable<Object> attributeReadable : attributes) {
	              List<String> l = new ArrayList<String>();
	              l.add(attributeReadable.getValue().toString());
	              ar.putAttributes(CoreAttributeTypes.UserId.toString(), l);
	            }

	            transArtifact.getListTeamMembersTeamLeads().add(ar);
	          }
	          for (ArtifactReadable art : relatedArtifacts) {
	            TransferableArtifact ar = new TransferableArtifact();
	            TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(art, ar);
	            ResultSet<? extends AttributeReadable<Object>> attributes = art.getAttributes(CoreAttributeTypes.UserId);
	            for (AttributeReadable<Object> attributeReadable : attributes) {
	              List<String> l = new ArrayList<String>();
	              l.add(attributeReadable.getValue().toString());
	              ar.putAttributes(CoreAttributeTypes.UserId.toString(), l);
	            }
	            transArtifact.getListTeamMembersTeamLeads().add(ar);
	          }


}

/**
 * This function sets the relations in the CustomizedTeamWorkFlowArtifact object 
 * 
 * @param relatedArtifacts ResultSet<ArtifactReadable> object which holds the relations 
 * @param side RelationTypeSide
 * @param iRelationType the key for the relation map, set in CustomizedTeamWorkFlowArtifact object
 * @param ar CustomizedTeamWorkFlowArtifact object in which the relations will be set from
 * the ArtifactReadable object
 * @throws OseeCoreException
 */
public static void fillRelations(final ResultSet<ArtifactReadable> relatedArtifacts, final RelationTypeSide side,
	      final RelationTypeToken iRelationType, final CustomizedTeamWorkFlowArtifact ar) throws OseeCoreException {
	    List<ITransferableArtifact> list1 = new ArrayList<ITransferableArtifact>();
	    for (ArtifactReadable artifactReadable : relatedArtifacts) {

	      if (artifactReadable.getArtifactType().equals(AtsArtifactTypes.Version)) {
	        ArtifactReadable user =
	            OseeCoreData.getOrcsApi().getQueryFactory().fromBranch(artifactReadable.getBranch())
	                .andGuid(artifactReadable.getGuid()).getResults().getExactlyOne();
	        TransferableArtifact art = new TransferableArtifact();
	        TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifactDup(user, art);
	        list1.add(art);
	      }
	      else {
	        ArtifactReadable user =
	            OseeCoreData.getOrcsApi().getQueryFactory().fromBranch(artifactReadable.getBranch())
	                .andGuid(artifactReadable.getGuid()).getResults().getExactlyOne();
	        TransferableArtifact art = new TransferableArtifact();
	        TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(user, art);
	        list1.add(art);
	      }
	    }
	    ar.putRelations(((RelationTypeToken)iRelationType).getName() + CommonConstants.RELATION_MAP_KEY_SEPARATOR + side.getSide().name(), list1);
	  }
  
}