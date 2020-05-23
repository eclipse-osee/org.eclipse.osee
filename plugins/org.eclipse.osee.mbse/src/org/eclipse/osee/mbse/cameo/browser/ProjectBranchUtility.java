/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.mbse.cameo.browser;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import java.util.List;

/**
 * @author David W. Miller
 */
public class ProjectBranchUtility {
   public static final String BRANCH_PROPERTY_ID = "OSEEBranch";
   public static final String BRANCH_GROUP_ID = "OSEEGroup";
   private static final String host = ""; // TODO insert host here

   private static ProjectOptions options =
      Application.getInstance().getProjectsManager().getActiveProject().getOptions();
   private static Project project = Application.getInstance().getProjectsManager().getActiveProject();

   public static String listBranchProperties() {
      BranchData data = getCurrentBranch();
      return String.format("Branch id is %d, with name %s confirmed", Long.valueOf(data.getId()), data.getName());
   }

   public static List<BranchData> getBranchData() {
      OSEEHttpClient client = new OSEEHttpClient();
      return client.getBranchData(host + "/orcs/branches");
   }

   public static String getOSEECreationUrl(long artifactType, long parentId) {
      BranchData data = ProjectBranchUtility.getCurrentBranch();
      return String.format("%s/orcs/branch/%d/artifact/type/%d/parent/%d", host, data.getId(), artifactType, parentId);

   }

   public static BranchData getCurrentBranch() {
      BranchData data = null;
      try {
         Property prop = options.getPersonalVisibleProperty(BRANCH_PROPERTY_ID);
         if (prop != null) {
            data = (BranchData) prop.getValue();
         } else {
            Application.getInstance().getGUILog().showMessage(String.format("Branch not found"));
         }
      } catch (Exception e) {
         Application.getInstance().getGUILog().showMessage(String.format("Exception trying to get branch"));
      }
      return data;
   }

   public static void setProjectProperty(BranchData data) {
      Property prop = options.getPersonalVisibleProperty(BRANCH_PROPERTY_ID);
      if (prop != null) {
         prop.setValue(data);
      } else {
         Model m = project.getModel();
         Property toSet = new Property();
         toSet.setID(BRANCH_PROPERTY_ID);
         toSet.setGroup(BRANCH_GROUP_ID);
         toSet.setValue(data);
         options.addPersonalVisibleProperty(toSet);
      }
   }
}
