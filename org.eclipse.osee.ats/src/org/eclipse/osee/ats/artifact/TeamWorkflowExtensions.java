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
package org.eclipse.osee.ats.artifact;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowExtensions {

   private static TeamWorkflowExtensions instance = new TeamWorkflowExtensions();
   private static Set<IAtsTeamWorkflow> teamWorkflowExtensionItems;

   private TeamWorkflowExtensions() {
      instance = this;
   }

   public static TeamWorkflowExtensions getInstance() {
      return instance;
   }

   public Set<String> getAllTeamWorkflowArtifactNames() throws OseeCoreException {
      Set<String> artifactNames = new HashSet<String>();
      artifactNames.add(TeamWorkFlowArtifact.ARTIFACT_NAME);
      for (IAtsTeamWorkflow ext : getAtsTeamWorkflowExtensions()) {
         artifactNames.addAll(ext.getTeamWorkflowArtifactNames());
      }
      return artifactNames;
   }

   @SuppressWarnings( {"unchecked"})
   public Set<IAtsTeamWorkflow> getAtsTeamWorkflowExtensions() {
      if (teamWorkflowExtensionItems != null) return teamWorkflowExtensionItems;
      teamWorkflowExtensionItems = new HashSet<IAtsTeamWorkflow>();

      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsTeamWorkflow");
      if (point == null) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't access AtsTeamWorkflow extension point");
         return teamWorkflowExtensionItems;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsTeamWorkflow")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     teamWorkflowExtensionItems.add((IAtsTeamWorkflow) obj);
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Error loading AtsTeamWorkflow extension", ex);
                  }
               }
            }
         }
      }
      return teamWorkflowExtensionItems;
   }

}
