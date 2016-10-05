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
package org.eclipse.osee.orcs.rest.internal.writer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifact;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactToken;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttribute;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttributeType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelation;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelationType;

/**
 * @author Donald G. Dunne
 */
public class OrcsCollectorValidator {

   private final OwCollector collector;
   private Map<Long, OwArtifact> uuidToArtifact;
   private final Set<Long> artifactsExist;
   private final IOrcsValidationHelper helper;
   private boolean branchValid;
   private OrcsApi orcsApi;

   public OrcsCollectorValidator(OwCollector collector, IOrcsValidationHelper helper) {
      this.collector = collector;
      this.helper = helper;
      uuidToArtifact = new HashMap<>();
      artifactsExist = new HashSet<>();
   }

   public OrcsCollectorValidator(OrcsApi orcsApi, OwCollector collector2) {
      this(collector2, new OrcsValidationHelperAdapter(orcsApi));
      this.orcsApi = orcsApi;
   }

   public XResultData run() {
      XResultData results = new XResultData(false);
      branchValid = validateBranch(results);
      if (!helper.isUserExists(collector.getAsUserId())) {
         results.errorf("Invalid asUserId [%s].\n", collector.getAsUserId());
      } else if (collector.getAsUserId().equals(SystemUser.OseeSystem.getUserId())) {
         results.errorf("Invalid AS USER ID [%s].  Enter userId of user making change.\n", collector.getAsUserId());
      }
      if (!Strings.isValid(collector.getPersistComment())) {
         results.errorf("Invalid persistComment [%s].\n", collector.getPersistComment());
      } else if (collector.getPersistComment().toLowerCase().contains("enter persist comment")) {
         results.errorf("Invalid persistComment [%s].  Enter why change is being made.\n",
            collector.getPersistComment());
      }
      boolean createEntries = collector.getCreate() != null && !collector.getCreate().isEmpty();
      boolean updateEntries = collector.getUpdate() != null && !collector.getUpdate().isEmpty();
      boolean deleteEntries = collector.getDelete() != null && !collector.getDelete().isEmpty();
      if (!createEntries && !updateEntries && !deleteEntries) {
         results.error("No create, update or delete entries.\n");
      }
      validateCreate(results);
      validateUpdate(results);
      results.log("Completed");
      return results;
   }

   private void validateCreate(XResultData results) {
      for (OwArtifact artifact : collector.getCreate()) {
         validateArtifactType(results, artifact);
         validateArtifactDoesNotExist(results, artifact);
         validateCreateUpdateAttributes(artifact, results);
         validateCreateUpdateRelations(artifact, results);
      }
   }

   private void validateUpdate(XResultData results) {
      for (OwArtifact artifact : collector.getUpdate()) {
         validateArtifactDoesExist(results, artifact);
         validateCreateUpdateAttributes(artifact, results);
         validateCreateUpdateRelations(artifact, results);
      }
   }

   private void validateArtifactType(XResultData results, OwArtifact artifact) {
      OwArtifactType artType = artifact.getType();
      if (artType == null || artType.getUuid() <= 0L) {
         results.errorf("Invalid Artifact Type uuid [%s].\n", artType);
      } else {
         if (!helper.isArtifactTypeExist(artType.getUuid())) {
            results.errorf("Artifact Type [%s] does not exist.\n", artType);
         }
      }
   }

   private void validateArtifactDoesNotExist(XResultData results, OwArtifact artifact) {
      long artifactUuid = artifact.getUuid();
      if (!branchValid) {
         results.errorf("Invalid Branch; can't validate artifact uuid for [%s].\n", artifact);
      } else if (artifactUuid > 0L) {
         if (helper.isArtifactExists(collector.getBranch().getUuid(), artifactUuid)) {
            results.errorf("Artifact with uuid already exists [%s].\n", artifact);
         }
         if (uuidToArtifact == null) {
            uuidToArtifact = new HashMap<>();
         }
         uuidToArtifact.put(artifactUuid, artifact);
      }
   }

   private void validateArtifactDoesExist(XResultData results, OwArtifact artifact) {
      long artifactUuid = artifact.getUuid();
      if (!branchValid) {
         results.errorf("Invalid Branch; can't validate artifact uuid for [%s].\n", artifact);
      } else if (artifactUuid > 0L) {
         if (!helper.isArtifactExists(collector.getBranch().getUuid(), artifactUuid)) {
            results.errorf("Artifact with uuid does not exist [%s].\n", artifact);
         } else {
            if (uuidToArtifact == null) {
               uuidToArtifact = new HashMap<>();
            }
            uuidToArtifact.put(artifactUuid, artifact);
         }
      }
   }

   private void validateCreateUpdateRelations(OwArtifact artifact, XResultData results) {
      for (OwRelation relation : artifact.getRelations()) {
         if (!branchValid) {
            results.errorf("Invalid Branch; can't validate artifact uuid for artifact [%s] and relation [%s].\n",
               artifact, relation);
         } else {
            OwRelationType relType = relation.getType();
            try {
               if (relType == null || relType.getUuid() <= 0L || !helper.isRelationTypeExist(relType.getUuid())) {
                  results.errorf("Invalid Relation Type [%s] for artifact [%s].\n", relType, artifact);
               } else {
                  OwArtifactToken artToken = relation.getArtToken();
                  long branchUuid = collector.getBranch().getUuid();
                  if (artToken == null) {
                     results.errorf("Invalid artifact token [%s] for artifact [%s] and relation [%s].\n", artToken,
                        artifact, relation);
                  }
                  // for performance, check to see if this artifact token was validated
                  else if (!artifactsExist.contains(artToken.getUuid())) {
                     // check to see if token is one of the artifacts to create
                     if (!uuidToArtifact.containsKey(artToken.getUuid())) {
                        // else, check to see if token exists in db
                        if (!helper.isArtifactExists(branchUuid, artToken.getUuid())) {
                           results.errorf(
                              "Artifact from token [%s] does not exist to relate to artifact [%s] for relation [%s].\n",
                              artToken, artifact, relation);
                        } else {
                           artifactsExist.add(artToken.getUuid());
                        }
                     }
                  }
               }
            } catch (Exception ex) {
               results.errorf("Exception [%s] processing relation [%s] for relType [%s].\n", ex.getLocalizedMessage(),
                  relation, relType);
            }
         }
      }
   }

   private void validateCreateUpdateAttributes(OwArtifact artifact, XResultData results) {
      String name = artifact.getName();
      if (!Strings.isValid(name)) {
         results.errorf("Artifact [%s] does not have Name attribute.\n", artifact);
      }
      for (OwAttribute attribute : artifact.getAttributes()) {
         OwAttributeType owAttrType = attribute.getType();

         IAttributeType attrType = OrcsCollectorWriter.getAttributeType(orcsApi, owAttrType);
         if (attrType == null) {
            results.errorf("Invalid Attribute Type [%s] for artifact [%s].\n", owAttrType, artifact);
         }

         if (orcsApi.getOrcsTypes().getAttributeTypes().isBooleanType(attrType)) {
            if (attribute.values.size() > 1) {
               results.errorf("Cannot have multiple values for Boolean Attribute [%s] for artifact [%s].\n",
                  attribute, artifact);
            }
         } else if (orcsApi.getOrcsTypes().getAttributeTypes().isDateType(attrType)) {
            if (attribute.values.size() > 1) {
               results.errorf("Cannot have multiple attributes Date Attribute [%s] for artifact [%s]", attribute,
                  artifact);
            }
         }

      }
   }

   private boolean validateBranch(XResultData results) {
      boolean valid = true;
      if (collector.getBranch() == null || collector.getBranch().getUuid() <= 0L || !helper.isBranchExists(
         collector.getBranch().getUuid())) {
         results.errorf("Branch [%s] not valid.\n", collector.getBranch());
         valid = false;
      }
      return valid;
   }

}
