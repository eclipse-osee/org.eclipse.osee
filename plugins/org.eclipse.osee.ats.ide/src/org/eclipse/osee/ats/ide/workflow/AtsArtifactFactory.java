/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Action;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileBacklog;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileFeatureGroup;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileSprint;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileTeam;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.DecisionReview;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Goal;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.PeerToPeerReview;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Task;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Version;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.WorkDefinition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.IAtsClient;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class AtsArtifactFactory extends ArtifactFactory {
   private final List<ArtifactTypeId> disabledUserCreationTypes = new ArrayList<>(20);
   private List<ArtifactTypeId> supportedTypes = null;

   private Collection<ArtifactTypeId> getSupportedTypes() {
      if (supportedTypes == null) {
         supportedTypes = new LinkedList<>();
         supportedTypes.addAll(Arrays.asList(Action, PeerToPeerReview, DecisionReview, Task, TeamWorkflow, Goal,
            AgileSprint, AgileBacklog));
         IAtsClient atsClient = AtsClientService.get();
         IAtsStoreService storeService = atsClient.getStoreService();
         supportedTypes.addAll(storeService.getTeamWorkflowArtifactTypes());
      }
      return supportedTypes;
   }

   @Override
   public boolean isResponsibleFor(ArtifactTypeId artifactType) {
      return getSupportedTypes().contains(artifactType);
   }

   @Override
   public Artifact getArtifactInstance(Long id, String guid, BranchId branch, ArtifactTypeToken artifactType, boolean inDataStore) {
      Artifact toReturn;
      if (artifactType.equals(Task)) {
         toReturn = new TaskArtifact(id, guid, branch, artifactType);
      } else if (artifactType.inheritsFrom(TeamWorkflow)) {
         toReturn = new TeamWorkFlowArtifact(id, guid, branch, artifactType);
      } else if (artifactType.equals(DecisionReview)) {
         toReturn = new DecisionReviewArtifact(id, guid, branch, artifactType);
      } else if (artifactType.equals(PeerToPeerReview)) {
         toReturn = new PeerToPeerReviewArtifact(id, guid, branch, artifactType);
      } else if (artifactType.matches(Goal, AgileBacklog)) {
         toReturn = new GoalArtifact(id, guid, branch, artifactType);
      } else if (artifactType.equals(AgileSprint)) {
         toReturn = new SprintArtifact(id, guid, branch, artifactType);
      } else if (artifactType.equals(Action)) {
         toReturn = new ActionArtifact(id, branch);
      } else {
         throw new OseeArgumentException("AtsArtifactFactory did not recognize the artifact type [%s]", artifactType);
      }
      return toReturn;
   }

   @Override
   public Collection<ArtifactTypeId> getEternalArtifactTypes() {
      return Arrays.asList(WorkDefinition, Version, TeamDefinition, ActionableItem);
   }

   @Override
   public boolean isUserCreationEnabled(ArtifactTypeToken artifactType) {
      if (disabledUserCreationTypes.isEmpty()) {
         setupDisabledUserCreationArtifactTypes();
      }
      if (disabledUserCreationTypes.contains(artifactType)) {
         return false;
      } else if (artifactType.inheritsFrom(AtsArtifactTypes.TeamWorkflow)) {
         return false;
      }
      return true;
   }

   public void setupDisabledUserCreationArtifactTypes() {
      disabledUserCreationTypes.addAll(getSupportedTypes());
      disabledUserCreationTypes.add(AgileTeam);
      disabledUserCreationTypes.add(AgileFeatureGroup);
      disabledUserCreationTypes.add(AgileSprint);
      String configValue = AtsClientService.get().getConfigValue(AtsUtil.USER_CREATION_DISABLED);
      if (Strings.isValid(configValue)) {
         for (String artifactTypeNamedIdStr : configValue.split(";")) {
            ArtifactTypeId artifactTypeId = createArtifactTypeIdFromToken(artifactTypeNamedIdStr);
            if (artifactTypeId == null) {
               OseeLog.logf(Activator.class, Level.SEVERE,
                  "Artifact Type Name [%s] specified in AtsConfig.[%s] is invalid", AtsUtil.USER_CREATION_DISABLED);
            } else {
               disabledUserCreationTypes.add(artifactTypeId);
            }
         }
      }
   }

   private static final Pattern nameIdPattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]");

   /**
    * @param token as [name]-[uuid]
    */
   private static ArtifactTypeId createArtifactTypeIdFromToken(String token) {
      Matcher matcher = nameIdPattern.matcher(token);
      if (matcher.find()) {
         long id = Long.valueOf(matcher.group(2));
         String name = matcher.group(1);
         return ArtifactTypeToken.valueOf(id, name);
      }
      return null;
   }
}