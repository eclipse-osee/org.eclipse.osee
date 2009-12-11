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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class SetRequirementCategory extends AbstractBlam {
   private final HashMap<String, Artifact> reqs = new HashMap<String, Artifact>();
   private HashMap<String, String> reqPriorities;
   private boolean bulkLoad;
   private Branch branch;

   @Override
   public String getName() {
      return "Set Requirement Category";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Set Categories", 100);

      branch = variableMap.getBranch("Branch");
      String excelMlPath = variableMap.getString("ExcelML Priority File");
      bulkLoad = variableMap.getBoolean("Bulk Load");

      ExtractReqPriority extractor = new ExtractReqPriority(excelMlPath);
      reqPriorities = extractor.getReqPriorities();

      if (bulkLoad) {
         for (ArtifactType artifactType : Requirements.getAllSoftwareRequirementTypes()) {
            for (Artifact req : ArtifactQuery.getArtifactListFromType(artifactType, branch)) {
               reqs.put(req.getName().trim(), req);
            }
         }
      }

      SkynetTransaction transaction = new SkynetTransaction(branch, "set requirement categories");
      for (String requirementName : reqPriorities.keySet()) {
         updateCategory(transaction, requirementName);
      }
      transaction.execute();

      reqs.clear();
   }

   private void updateCategory(SkynetTransaction transaction, String requirementName) {
      try {
         String canonicalRequirementName = requirementName.trim();
         Artifact requirement = getRequirement(requirementName, canonicalRequirementName);
         requirement.setSoleAttributeValue("Category", reqPriorities.get(canonicalRequirementName));
         requirement.persist(transaction);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private Artifact getSoleRequirement(String requirementName, String canonicalRequirementName) throws OseeCoreException {
      Artifact requirement;
      if (bulkLoad) {
         requirement = reqs.get(canonicalRequirementName);
         if (requirement == null) {
            throw new ArtifactDoesNotExist("cant' find " + canonicalRequirementName);
         }
      } else {
         try {
            requirement =
                  ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.NAME.getName(), requirementName, branch);
         } catch (ArtifactDoesNotExist ex) {
            requirement =
                  ArtifactQuery.getArtifactFromAttribute(CoreAttributeTypes.NAME.getName(), canonicalRequirementName,
                        branch);
         }
      }
      return requirement;
   }

   private Artifact getRequirement(String requirementName, String canonicalRequirementName) throws OseeCoreException {
      try {
         Artifact requirement = getSoleRequirement(requirementName, canonicalRequirementName);

         if (requirement.isOrphan()) {
            throw new MultipleArtifactsExist(requirement.getName());
         }
         return requirement;
      } catch (MultipleArtifactsExist ex) {
         List<Artifact> artiafcts =
               ArtifactQuery.getArtifactListFromTypeAndName(Requirements.SOFTWARE_REQUIREMENT,
                     canonicalRequirementName, branch);
         for (Artifact requirement : artiafcts) {
            if (requirement.isOrphan()) {
               OseeLog.log(SkynetGuiPlugin.class, Level.INFO, requirement + " is an orphan");
            }
         }
         throw ex;
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Bulk Load\" /><XWidget xwidgetType=\"XText\" displayName=\"ExcelML Priority File\" defaultValue=\"C:/UserData/RequirementCategories.xml\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Sets the Category attribute on software requirements.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define");
   }
}