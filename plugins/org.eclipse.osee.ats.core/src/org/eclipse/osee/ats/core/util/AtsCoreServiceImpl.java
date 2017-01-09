/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsCoreServiceImpl implements IAtsServices {

   protected IAtsWorkDefinitionAdmin workDefAdmin;
   private static final Object lock = new Object();
   private volatile static IOseeBranch atsBranch;
   private static final String ATS_BRANCH_NAME = "ats.branch.name";
   private static final String ATS_BRANCH_UUID = "ats.branch.uuid";

   @Override
   public String getAtsId(ArtifactId artifact) {
      return getAtsId(getAttributeResolver(), artifact);
   }

   @Override
   public String getAtsId(IAtsObject atsObject) {
      return getAtsId(getAttributeResolver(), atsObject.getStoreObject());
   }

   protected static String getAtsId(IAttributeResolver attrResolver, IAtsObject atsObject) {
      return getAtsId(attrResolver, atsObject.getStoreObject());
   }

   protected static String getAtsId(IAttributeResolver attrResolver, ArtifactId artifact) {
      Conditions.checkNotNull(artifact, "artifact");
      String toReturn = attrResolver.getSoleAttributeValue(artifact, AtsAttributeTypes.AtsId, null);
      if (toReturn == null) {
         toReturn = artifact.getGuid();
      }
      return toReturn;
   }

   public IAtsWorkDefinitionAdmin getWorkDefAdmin() {
      return workDefAdmin;
   }

   @Override
   public <T> T getConfigItem(ArtifactToken artifactToken) {
      return getConfigItem(artifactToken.getId());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(String guid) {
      T atsObject = getCache().getAtsObjectByGuid(guid);
      if (atsObject == null) {
         ArtifactId artifact = getArtifactByGuid(guid);
         if (artifact != null && artifact instanceof IAtsConfigObject) {
            atsObject = (T) getConfigItemFactory().getConfigObject(artifact);
         }
      }
      return atsObject;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(Long uuid) {
      T atsObject = getCache().getAtsObject(uuid);
      if (atsObject == null) {
         ArtifactId artifact = getArtifact(uuid);
         if (artifact != null && artifact instanceof IAtsConfigObject) {
            atsObject = (T) getConfigItemFactory().getConfigObject(artifact);
         }
      }
      return atsObject;
   }

   @Override
   public void setConfigValue(String key, String value) {
      ArtifactId atsConfig = getArtifact(AtsArtifactToken.AtsConfig);
      IAtsChangeSet changes =
         getStoreService().createAtsChangeSet("Set AtsConfig Value", getUserService().getCurrentUser());
      if (atsConfig != null) {
         String keyValue = String.format("%s=%s", key, value);
         boolean found = false;
         Collection<IAttribute<Object>> attributes =
            getAttributeResolver().getAttributes(atsConfig, CoreAttributeTypes.GeneralStringData);
         for (IAttribute<Object> attr : attributes) {
            String str = (String) attr.getValue();
            if (str.startsWith(key)) {
               changes.setAttribute(atsConfig, attr, keyValue);
               found = true;
               break;
            }
         }
         if (!found) {
            changes.addAttribute(atsConfig, CoreAttributeTypes.GeneralStringData, keyValue);
         }
         changes.executeIfNeeded();
      }
   }

   @Override
   public Map<String, String> getWorkDefIdToWorkDef() {
      return getConfigurations().getWorkDefIdToWorkDef();
   }

   @Override
   public IOseeBranch getAtsBranch() {
      synchronized (lock) {
         if (atsBranch == null) {
            // Preference store overrides all
            if (AtsPreferencesService.isAvailable()) {
               try {
                  String atsBranchUuid = AtsPreferencesService.get(ATS_BRANCH_UUID);
                  setConfig(atsBranchUuid, AtsPreferencesService.get(ATS_BRANCH_NAME));
               } catch (Exception ex) {
                  OseeLog.log(AtsUtilCore.class, Level.SEVERE, "Error processing stored ATS Branch.", ex);
               }
            }
            // osee.ini -D option overrides default
            if (atsBranch == null) {
               String atsBranchUuid = System.getProperty(ATS_BRANCH_UUID);
               if (Strings.isValid(atsBranchUuid)) {
                  setConfig(atsBranchUuid, System.getProperty(ATS_BRANCH_NAME));
               }
            }
            // default is always common
            if (atsBranch == null) {
               atsBranch = CoreBranches.COMMON;
            }
         }
      }
      return atsBranch;
   }

   private void setConfig(String branchUuid, String name) {
      if (!Strings.isValid(name)) {
         name = "unknown";
      }
      if (Strings.isValid(branchUuid) && branchUuid.matches("\\d+")) {
         atsBranch = IOseeBranch.create(Long.valueOf(branchUuid), name);
      }
   }

   @Override
   public void storeAtsBranch(BranchId branch, String name) {
      AtsPreferencesService.get().put(ATS_BRANCH_UUID, String.valueOf(branch.getUuid()));
      AtsPreferencesService.get().put(ATS_BRANCH_NAME, name);
   }

}
