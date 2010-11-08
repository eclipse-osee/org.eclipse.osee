/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;

/**
 * @author Donald G. Dunne
 */
public final class PromptChangeUtil {

   private PromptChangeUtil() {
      // Utility class
   }

   public static boolean promptChangeAssignees(AbstractWorkflowArtifact sma, boolean persist) throws OseeCoreException {
      return promptChangeAssignees(Arrays.asList(sma), persist);
   }

   public static boolean promptChangeAssignees(final Collection<? extends AbstractWorkflowArtifact> smas, boolean persist) throws OseeCoreException {
      for (AbstractWorkflowArtifact sma : smas) {
         if (sma.isCompleted()) {
            AWorkbench.popup("ERROR",
               "Can't assign completed " + sma.getArtifactTypeName() + " (" + sma.getHumanReadableId() + ")");
            return false;
         } else if (sma.isCancelled()) {
            AWorkbench.popup("ERROR",
               "Can't assign cancelled " + sma.getArtifactTypeName() + " (" + sma.getHumanReadableId() + ")");
            return false;
         }
      }
      UserCheckTreeDialog uld = new UserCheckTreeDialog();
      uld.setMessage("Select to assign.\nDeSelect to un-assign.");
      if (smas.iterator().next().getParentTeamWorkflow() != null) {
         uld.setTeamMembers(smas.iterator().next().getParentTeamWorkflow().getTeamDefinition().getMembersAndLeads());
      }

      if (smas.size() == 1) {
         uld.setInitialSelections(smas.iterator().next().getStateMgr().getAssignees());
      }
      if (uld.open() != 0) {
         return false;
      }
      Collection<User> users = uld.getUsersSelected();
      if (users.isEmpty()) {
         AWorkbench.popup("ERROR", "Must have at least one assignee");
         return false;
      }
      // As a convenience, remove the UnAssigned user if another user is selected
      if (users.size() > 1) {
         users.remove(UserManager.getUser(SystemUser.UnAssigned));
      }
      for (AbstractWorkflowArtifact sma : smas) {
         sma.getStateMgr().setAssignees(users);
      }
      if (persist) {
         Artifacts.persistInTransaction(smas);
      }
      return true;
   }

   public static boolean promptChangePercentAttribute(AbstractWorkflowArtifact sma, IAttributeType attributeType, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(new Artifact[] {sma}), persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeAttribute(final Collection<? extends AbstractWorkflowArtifact> smas, IAttributeType attributeType, boolean persist, boolean multiLine) {
      return ArtifactPromptChange.promptChangeAttribute(attributeType, smas, persist, multiLine);
   }

   public static boolean promptChangeAttribute(final Artifact sma, IAttributeType attributeType, boolean persist, boolean multiLine) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(new Artifact[] {sma}), persist,
            multiLine);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeAttribute(AbstractWorkflowArtifact sma, IAttributeType attributeType, final boolean persist, boolean multiLine) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(sma), persist, multiLine);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeDate(AbstractWorkflowArtifact sma, IAttributeType attributeType, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, java.util.Collections.singleton(sma), persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
            "Can't save " + attributeType.getUnqualifiedName() + " date to artifact " + sma.getHumanReadableId(), ex);
      }
      return false;
   }

}