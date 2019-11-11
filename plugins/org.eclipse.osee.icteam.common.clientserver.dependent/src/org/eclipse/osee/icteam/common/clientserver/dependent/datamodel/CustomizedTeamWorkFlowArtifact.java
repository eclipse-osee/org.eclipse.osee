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
package org.eclipse.osee.icteam.common.clientserver.dependent.datamodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.util.CommonConstants;

/**
 * This class is for adding customized or user defined TeamWorkFlow Artifact. Its is used while creating task.
 *
 * @author Ajay Chandrahasan
 */
public class CustomizedTeamWorkFlowArtifact {


  public String name;
  public String guid;
  public final Map<String, List<String>> attributeMap = new ConcurrentHashMap<String, List<String>>();
  public final Map<String, List<ITransferableArtifact>> relationMap =
      new ConcurrentHashMap<String, List<ITransferableArtifact>>();


  private List<CommentArtifact> commentArtifactList = new ArrayList<CommentArtifact>();
  private List<ITransferableArtifact> listVersionsDropDown = new ArrayList<ITransferableArtifact>();
  private List<ITransferableArtifact> listCompoentsDropDown = new ArrayList<ITransferableArtifact>();
  private final List<ITransferableArtifact> listTeamMembersTeamLeads = new ArrayList<ITransferableArtifact>();
  private TransferableArtifact actionableItem;
  private TransferableArtifact version;
  private List<ITransferableArtifact> assignee;
  private String description;
  private String estimatedHours;
  private String priority;
  private String changeType;
  private String rank;
  private String createdBy;
  private Date createdDate;
  private Date expectedDate;
  private Date completionDate;
  public String artifactType;
  public String currentState;
  public String remainingHours;
  public String workPackage;
  public String story;


  /**
   * get remaining hours
   *
   * @return
   */
  public String getRemainingHours() {
    return this.remainingHours;
  }

  /**
   * set remaining hours
   *
   * @param remainingHours
   */
  public void setRemainingHours(final String remainingHours) {
    this.remainingHours = remainingHours;
  }

  /**
   * get current state like new, completed
   *
   * @return
   */
  public String getCurrentState() {
    return this.currentState;
  }

  /**
   * set current state
   *
   * @param currentState
   */
  public void setCurrentState(final String currentState) {
    this.currentState = currentState;
  }

  /**
   * get artifact type
   *
   * @return
   */
  public String getArtifactType() {
    return this.artifactType;
  }

  /**
   * set artifact type
   *
   * @param artifactType
   */
  public void setArtifactType(final String artifactType) {
    this.artifactType = artifactType;
  }

  /**
   * get description
   *
   * @return
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * set desciption
   *
   * @param description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * get estimated hours
   *
   * @return
   */
  public String getEstimatedHours() {
    return this.estimatedHours;
  }

  /**
   * set estimated hours
   *
   * @param estimatedHours
   */
  public void setEstimatedHours(final String estimatedHours) {
    this.estimatedHours = estimatedHours;
  }

  /**
   * get priority
   *
   * @return
   */
  public String getPriority() {
    return this.priority;
  }

  /**
   * set priority
   *
   * @param priority
   */
  public void setPriority(final String priority) {
    this.priority = priority;
  }

  /**
   * get change type
   *
   * @return
   */
  public String getChangeType() {
    return this.changeType;
  }

  /**
   * set change type
   *
   * @param changeType
   */
  public void setChangeType(final String changeType) {
    this.changeType = changeType;
  }

  /**
   * get rank
   *
   * @return
   */
  public String getRank() {
    return this.rank;
  }

  /**
   * set rank
   *
   * @param rank
   */
  public void setRank(final String rank) {
    this.rank = rank;
  }

  /**
   * get created by
   *
   * @return
   */
  public String getCreatedBy() {
    return this.createdBy;
  }

  /**
   * set created by
   *
   * @param createdBy
   */
  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * get created date
   *
   * @return
   */
  public Date getCreatedDate() {
    return this.createdDate;
  }

  /**
   * set created date
   *
   * @param createdDate
   */
  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * get expected date
   *
   * @return
   */
  public Date getExpectedDate() {
    return this.expectedDate;
  }

  /**
   * set expected date
   *
   * @param expectedDate
   */
  public void setExpectedDate(final Date expectedDate) {
    this.expectedDate = expectedDate;
  }

  /**
   * get completion date
   *
   * @return
   */
  public Date getCompletionDate() {
    return this.completionDate;
  }

  /**
   * set completion date
   *
   * @param completionDate
   */
  public void setCompletionDate(final Date completionDate) {
    this.completionDate = completionDate;
  }


  /**
   * get list od assignee artifact
   *
   * @return
   */
  public List<ITransferableArtifact> getAssignee() {
    return this.assignee;
  }

  /**
   * Sets list of assignee artifact
   *
   * @param assignee
   */
  public void setAssignee(final List<ITransferableArtifact> assignee) {
    this.assignee = assignee;
  }

  /**
   * get actionable item
   *
   * @return
   */
  public TransferableArtifact getActionableItem() {
    return this.actionableItem;
  }

  /**
   * sets actionable items
   *
   * @param actionableItem
   */
  public void setActionableItem(final TransferableArtifact actionableItem) {
    this.actionableItem = actionableItem;
  }

  /**
   * get version artifact
   *
   * @return
   */
  public TransferableArtifact getVersion() {
    return this.version;
  }

  /**
   * sets version
   *
   * @param version
   */
  public void setVersion(final TransferableArtifact version) {
    this.version = version;
  }

  /**
   * get name
   *
   * @return
   */
  public String getName() {
    return this.name;
  }

  /**
   * sets name
   *
   * @param name
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * get guid
   *
   * @return
   */
  public String getGuid() {
    return this.guid;
  }

  /**
   * sets guid
   *
   * @param guid
   */
  public void setGuid(final String guid) {
    this.guid = guid;
  }

  /**
   * get attributeMap
   *
   * @return
   */
  public Map<String, List<String>> getAttributeMap() {
    return this.attributeMap;
  }

  /**
   * puts data to the attribute Map
   *
   * @param key
   * @param value
   */
  public void putAttributes(final String key, final List<String> value) {
    this.attributeMap.put(key, value);
  }

  /**
   * adds a tuple to key value {@inheritDoc}
   */
  public void putRelations(final String key, final List<ITransferableArtifact> value) {
    this.relationMap.put(key, value);
  }


  /**
   * get relation map
   *
   * @return
   */
  public Map<String, List<ITransferableArtifact>> getRelationMap() {
    Map<String, List<ITransferableArtifact>> relations = new HashMap<>();
    Set<Entry<String, List<ITransferableArtifact>>> entrySet = this.relationMap.entrySet();
    for (Entry<String, List<ITransferableArtifact>> entry : entrySet) {
      String[] splitKey = splitKey(entry.getKey());
      relations.put(splitKey[0], entry.getValue());
    }
    return relations;
  }


  /**
   * get related artifact
   *
   * @param key
   * @return
   */
  public List<ITransferableArtifact> getRelatedArtifacts(final String key) {
    Map<String, List<ITransferableArtifact>> relationMap2 = getRelationMap();
    if (relationMap2.containsKey(key)) {
      return relationMap2.get(key);
    }
    // do not change the return value here
    return null;
  }

  /**
   * get split key
   *
   * @param key
   * @return
   */
  public String[] splitKey(final String key) {
    return key.split(CommonConstants.RELATION_MAP_KEY_SEPARATOR);
  }


  /**
   * @param listVersionsDropDown
   */
  public void setListVersionsDropDown(final List<ITransferableArtifact> listVersionsDropDown) {
    this.listVersionsDropDown = listVersionsDropDown;
  }


  /**
   * get components
   *
   * @return
   */
  public List<ITransferableArtifact> getListCompoenntsDropDown() {
    return this.listCompoentsDropDown;
  }


  /**
   * Set components
   *
   * @param listCompoenntsDropDown
   */
  public void setListCompoenntsDropDown(final List<ITransferableArtifact> listCompoenntsDropDown) {
    this.listCompoentsDropDown = listCompoenntsDropDown;
  }


  /**
   * get list of comment artifact
   *
   * @return
   */
  public List<CommentArtifact> getCommentArtifactList() {
    return this.commentArtifactList;
  }


  /**
   * getlist of common artifact
   *
   * @param commentArtifactList
   */
  public void setCommentArtifactList(final List<CommentArtifact> commentArtifactList) {
    this.commentArtifactList = commentArtifactList;
  }

  /**
   * get list of versions
   *
   * @return
   */
  public List<ITransferableArtifact> getListVersionsDropDown() {
    return this.listVersionsDropDown;
  }

  /**
   * get list of leads of team members
   *
   * @return
   */
  public List<ITransferableArtifact> getListTeamMembersTeamLeads() {
    return this.listTeamMembersTeamLeads;
  }

  /**
   * get work package
   *
   * @return
   */
  public String getWorkPackage() {
    return this.workPackage;
  }

  /**
   * set workpackage
   *
   * @param workPackage
   */
  public void setWorkPackage(final String workPackage) {
    this.workPackage = workPackage;
  }

  /**
   * set story
   *
   * @param story
   */
  public void setStory(final String story) {
    this.story = story;

  }

}
