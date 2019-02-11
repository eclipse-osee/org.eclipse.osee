/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class ActionOperations {

   private final AtsApi atsApi;
   private IAtsWorkItem workItem;
   private final IAtsUser asUser;
   private final OrcsApi orcsApi;

   public ActionOperations(IAtsUser asUser, IAtsWorkItem workItem, AtsApi atsApi, OrcsApi orcsApi) {
      this.asUser = asUser;
      this.workItem = workItem;
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public Attribute setActionAttributeByType(String id, String attrTypeIdOrKey, List<String> values) {
      Conditions.assertNotNull(values, "values can not be null");
      IAtsChangeSet changes = atsApi.createChangeSet("set attr by type or key " + attrTypeIdOrKey);
      AttributeTypeToken attrTypeId = null;
      if (attrTypeIdOrKey.equals(AttributeKey.Title.name())) {
         changes.setSoleAttributeValue(workItem, CoreAttributeTypes.Name, values.iterator().next());
         attrTypeId = CoreAttributeTypes.Name;
      } else if (attrTypeIdOrKey.equals(AttributeKey.Priority.name())) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.PriorityType, values.iterator().next());
         attrTypeId = AtsAttributeTypes.PriorityType;
      } else if (attrTypeIdOrKey.equals(AttributeKey.ColorTeam.name())) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.ColorTeam, values.iterator().next());
         attrTypeId = AtsAttributeTypes.ColorTeam;
      } else if (attrTypeIdOrKey.equals(AttributeKey.IPT.name())) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.IPT, values.iterator().next());
         attrTypeId = AtsAttributeTypes.IPT;
      } else if (attrTypeIdOrKey.equals(AttributeKey.State.name())) {
         String state = values.iterator().next();
         TransitionHelper helper = new TransitionHelper("Transition Workflow", Arrays.asList(workItem), state,
            new ArrayList<IAtsUser>(), "", changes, atsApi, TransitionOption.OverrideAssigneeCheck);
         helper.setTransitionUser(asUser);
         TransitionManager mgr = new TransitionManager(helper);
         TransitionResults results = new TransitionResults();
         mgr.handleTransitionValidation(results);
         if (!results.isEmpty()) {
            throw new OseeArgumentException("Exception validating transition: " + results.toString());
         }
         mgr.handleTransition(results);
         if (!results.isEmpty()) {
            throw new OseeArgumentException("Exception transitioning: " + results.toString());
         }
         attrTypeId = AtsAttributeTypes.CurrentState;
      } else if (attrTypeIdOrKey.equals(AttributeKey.Version.name())) {
         if (!workItem.isTeamWorkflow()) {
            throw new OseeArgumentException("Not valid to set version for [%s]", workItem.getArtifactTypeName());
         }
         // If values emtpy, clear current version
         IAtsVersion currVersion = atsApi.getVersionService().getTargetedVersion(workItem);
         if (values.isEmpty() && currVersion != null) {
            atsApi.getVersionService().removeTargetedVersion(workItem.getParentTeamWorkflow(), changes);
         }
         // If id, find matching id for this team
         else if (Strings.isNumeric(values.iterator().next())) {
            String version = values.iterator().next();
            if (currVersion == null || !currVersion.getIdString().equals(version)) {
               IAtsVersion newVer = null;
               IAtsTeamDefinition teamDef =
                  workItem.getParentTeamWorkflow().getTeamDefinition().getTeamDefinitionHoldingVersions();
               for (IAtsVersion teamDefVer : atsApi.getVersionService().getVersions(teamDef)) {
                  if (teamDefVer.getIdString().equals(version)) {
                     newVer = teamDefVer;
                     break;
                  }
               }
               if (newVer == null) {
                  throw new OseeArgumentException("Version id [%s] not valid for team ", version,
                     teamDef.toStringWithId());
               }
               atsApi.getVersionService().setTargetedVersion(workItem.getParentTeamWorkflow(), newVer, changes);
            }
         }
         // Else if name, match name with version names for this team
         else if (Strings.isValid(values.iterator().next())) {
            String version = values.iterator().next();
            if (currVersion == null || !currVersion.getName().equals(version)) {
               IAtsVersion newVer = null;
               IAtsTeamDefinition teamDef =
                  workItem.getParentTeamWorkflow().getTeamDefinition().getTeamDefinitionHoldingVersions();
               for (IAtsVersion teamDefVer : atsApi.getVersionService().getVersions(teamDef)) {
                  if (teamDefVer.getName().equals(version)) {
                     newVer = teamDefVer;
                     break;
                  }
               }
               if (newVer == null) {
                  throw new OseeArgumentException("Version name [%s] not valid for team ", version,
                     teamDef.toStringWithId());
               }
               atsApi.getVersionService().setTargetedVersion(workItem.getParentTeamWorkflow(), newVer, changes);
            }
         }
      } else if (attrTypeIdOrKey.equals(AttributeKey.Originator.name())) {
         String accountId = values.iterator().next();
         if (!Strings.isNumeric(accountId)) {
            IAtsUser originator = atsApi.getUserService().getUserByAccountId(Long.valueOf(accountId));
            if (originator == null) {
               throw new OseeArgumentException("No user with account id [%s]", accountId);
            }
            atsApi.getActionFactory().setCreatedBy(workItem, originator, true, workItem.getCreatedDate(), changes);
         }
      } else if (attrTypeIdOrKey.equals(AttributeKey.assocArt.name())) {
         if (Strings.isNumeric(values.get(0))) {
            attrTypeId = getAttributeType(values.get(0));
            if (attrTypeId != null) {
               values.remove(0);
               // check to make sure the rest of the items are valid requirements
               changes.setSoleAttributeValue(workItem, attrTypeId, Collections.toString(",", values));
            }
         }
      } else if (attrTypeIdOrKey.equals(AttributeKey.Assignee.name())) {
         String accountIdOrName = values.iterator().next();
         if (Strings.isNumeric(accountIdOrName)) {
            IAtsUser assignee = atsApi.getUserService().getUserByAccountId(Long.valueOf(accountIdOrName));
            if (assignee == null) {
               throw new OseeArgumentException("No user with account id [%s]", accountIdOrName);
            }
            workItem.getStateMgr().addAssignee(assignee);
            changes.add(workItem);
         } else {
            IAtsUser assignee = atsApi.getUserService().getUserByName(accountIdOrName);
            if (assignee == null) {
               throw new OseeArgumentException("No user with user name [%s]", accountIdOrName);
            }
            workItem.getStateMgr().addAssignee(assignee);
            changes.add(workItem);
         }
      } else {
         attrTypeId = getAttributeType(attrTypeIdOrKey);
         if (attrTypeId != null) {
            changes.setAttributeValuesAsStrings(workItem, attrTypeId, values);
         }
      }
      changes.executeIfNeeded();

      // reload to get latest
      workItem = atsApi.getQueryService().getWorkItem(id);
      if (attrTypeId != null) {
         return getActionAttributeValues(attrTypeId, workItem);
      }
      return null;
   }

   private AttributeTypeToken getAttributeType(String id) {
      return orcsApi.getOrcsTypes().getAttributeTypes().get(Long.valueOf(id));
   }

   public Attribute getActionAttributeValues(String attrTypeId, IAtsWorkItem workItem) {
      AttributeTypeId attrType = atsApi.getStoreService().getAttributeType(Long.valueOf(attrTypeId));
      return getActionAttributeValues(attrType, workItem);
   }

   private Attribute getActionAttributeValues(AttributeTypeId attrType, IAtsWorkItem workItem) {
      Attribute attribute = new Attribute();
      attribute.setArtId(workItem.getStoreObject());
      attribute.setAttrTypeId(attrType);
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(workItem, attrType)) {
         attribute.addAttribute(attr);
      }
      return attribute;
   }

   public Collection<ArtifactToken> setByArtifactToken(IAtsWorkItem workItem, String changeType, Collection<ArtifactToken> artifacts) {
      if (changeType.equals(AttributeKey.Assignee.name())) {
         if (artifacts.isEmpty()) {
            IAtsChangeSet changes = atsApi.createChangeSet("Clear assignees", asUser);
            atsApi.getWorkItemService().clearAssignees(workItem, changes);
            changes.executeIfNeeded();
         } else {
            Set<IAtsUser> assignees = new HashSet<>();
            for (ArtifactToken userArt : artifacts) {
               IAtsUser user = atsApi.getUserService().getUserByArtifactId(userArt);
               Conditions.assertNotNull(user, "Artifact %s is not a User", userArt.toStringWithId());
               assignees.add(user);
            }
            IAtsChangeSet changes = atsApi.createChangeSet("Clear assignees", asUser);
            atsApi.getWorkItemService().setAssignees(workItem, assignees, changes);
            changes.executeIfNeeded();
         }

      }
      if (changeType.equals(AttributeKey.Version.name())) {
         if (workItem.isTeamWorkflow()) {
            throw new OseeArgumentException("WorkItem %s is not a Team Workflow", workItem.toStringWithId());
         }
         if (artifacts.size() > 1) {
            throw new OseeArgumentException("Can not set more than one targeted version for %s",
               workItem.toStringWithId());
         }
         if (artifacts.isEmpty()) {
            IAtsChangeSet changes = atsApi.createChangeSet("Clear targeted version", asUser);
            atsApi.getVersionService().removeTargetedVersion((IAtsTeamWorkflow) workItem, changes);
            changes.executeIfNeeded();
         } else {
            IAtsVersion version = atsApi.getVersionService().getById(artifacts.iterator().next());
            Conditions.assertNotNull(version, "No version found from artifact %s", artifacts.iterator().next());
            IAtsChangeSet changes = atsApi.createChangeSet("Set targeted version", asUser);
            atsApi.getVersionService().setTargetedVersion((IAtsTeamWorkflow) workItem, version, changes);
            changes.executeIfNeeded();
         }
      }
      return artifacts;
   }

}
