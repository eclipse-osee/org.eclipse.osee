/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
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
   private final OrcsApi orcsApi;

   public ActionOperations(IAtsWorkItem workItem, AtsApi atsApi, OrcsApi orcsApi) {
      this.workItem = workItem;
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public Attribute setActionAttributeByType(String id, String attrTypeIdOrKey, List<String> values) {
      Conditions.assertNotNull(values, "values can not be null");
      IAtsChangeSet changes = atsApi.createChangeSet("set attr by type or key " + attrTypeIdOrKey);
      AttributeTypeGeneric<?> attrTypeId = null;
      if (attrTypeIdOrKey.equals(AttributeKey.Title.name())) {
         changes.setSoleAttributeValue(workItem, CoreAttributeTypes.Name, values.iterator().next());
         attrTypeId = CoreAttributeTypes.Name;
      } else if (attrTypeIdOrKey.equals(AttributeKey.Priority.name())) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.Priority, values.iterator().next());
         attrTypeId = AtsAttributeTypes.Priority;
      } else if (attrTypeIdOrKey.equals(AttributeKey.State.name())) {
         String state = values.iterator().next();
         TransitionData transData = new TransitionData("Transition Workflow", Arrays.asList(workItem), state,
            new ArrayList<AtsUser>(), "", changes, TransitionOption.OverrideAssigneeCheck);
         transData.setTransitionUser(atsApi.getUserService().getCurrentUser());
         TransitionManager mgr = new TransitionManager(transData, atsApi);
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
               IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(
                  workItem.getParentTeamWorkflow().getTeamDefinition());
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
               IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefinition(workItem);
               Objects.requireNonNull(teamDef, "teamDef can not be null");
               IAtsTeamDefinition teamDefHoldVer = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
               for (IAtsVersion teamDefVer : atsApi.getVersionService().getVersions(teamDefHoldVer)) {
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
            AtsUser originator = atsApi.getUserService().getUserById(ArtifactId.valueOf(accountId));
            if (originator == null) {
               throw new OseeArgumentException("No user with account id [%s]", accountId);
            }
            atsApi.getActionService().setCreatedBy(workItem, originator, true, workItem.getCreatedDate(), changes);
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
            AtsUser assignee = atsApi.getUserService().getUserById(ArtifactId.valueOf(accountIdOrName));
            if (assignee == null) {
               throw new OseeArgumentException("No user with account id [%s]", accountIdOrName);
            }
            workItem.getStateMgr().addAssignee(assignee);
            changes.add(workItem);
         } else {
            AtsUser assignee = atsApi.getUserService().getUserByName(accountIdOrName);
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

   private AttributeTypeGeneric<?> getAttributeType(String id) {
      return orcsApi.tokenService().getAttributeType(Long.valueOf(id));
   }

   public Attribute getActionAttributeValues(AttributeTypeToken attributeType, IAtsWorkItem workItem) {
      Attribute attribute = new Attribute();
      attribute.setArtId(workItem.getStoreObject());
      attribute.setAttributeType(attributeType);
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(workItem, attributeType)) {
         attribute.addAttribute(attr);
      }
      return attribute;
   }

   public Collection<ArtifactToken> setByArtifactToken(IAtsWorkItem workItem, String changeType,
      Collection<ArtifactToken> artifacts) {
      if (changeType.equals(AttributeKey.Assignee.name())) {
         if (artifacts.isEmpty()) {
            IAtsChangeSet changes = atsApi.createChangeSet("Clear assignees");
            atsApi.getWorkItemService().clearAssignees(workItem, changes);
            changes.executeIfNeeded();
         } else {
            Set<AtsUser> assignees = new HashSet<>();
            for (ArtifactToken userArt : artifacts) {
               AtsUser user = atsApi.getUserService().getUserById(userArt);
               Conditions.assertNotNull(user, "Artifact %s is not a User", userArt.toStringWithId());
               assignees.add(user);
            }
            IAtsChangeSet changes = atsApi.createChangeSet("Clear assignees");
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
            IAtsChangeSet changes = atsApi.createChangeSet("Clear targeted version");
            atsApi.getVersionService().removeTargetedVersion((IAtsTeamWorkflow) workItem, changes);
            changes.executeIfNeeded();
         } else {
            IAtsVersion version = atsApi.getVersionService().getVersionById(artifacts.iterator().next());
            Conditions.assertNotNull(version, "No version found from artifact %s", artifacts.iterator().next());
            IAtsChangeSet changes = atsApi.createChangeSet("Set targeted version");
            atsApi.getVersionService().setTargetedVersion((IAtsTeamWorkflow) workItem, version, changes);
            changes.executeIfNeeded();
         }
      }
      return artifacts;
   }
}