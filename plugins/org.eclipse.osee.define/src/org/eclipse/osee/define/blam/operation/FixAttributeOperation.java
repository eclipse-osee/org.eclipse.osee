/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Angel Avila
 */

public class FixAttributeOperation extends AbstractOperation {

   public interface Display {

      void displayReport(String reportName, List<String[]> values);

   }

   private final IOseeBranch branch;
   private final boolean commitChangesBool;
   private final Display display;

   public FixAttributeOperation(OperationLogger logger, Display display, IOseeBranch branch, boolean commitChangesBool) {
      super("FixAttributes", Activator.PLUGIN_ID, logger);
      this.branch = branch;
      this.commitChangesBool = commitChangesBool;
      this.display = display;
   }

   private void checkPreConditions() {
      Conditions.checkNotNull(branch, "branch");
      // only allow working branches
      Conditions.checkExpressionFailOnTrue(!BranchManager.getType(branch).isWorkingBranch(),
         "Invalid branch selected [%s]. Only working branches are allowed.", branch);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      checkPreConditions();

      monitor.subTask("Aquiring Artifacts");
      HashCollectionSet<Artifact, AttributeTypeToken> artifactAttributeMap = getArtifactsWithDuplicates(monitor);

      SkynetTransaction transaction = null;
      if (commitChangesBool) {
         transaction = TransactionManager.createTransaction(branch, "Fixing Duplicate Enumerated Types");
      }
      List<String[]> rowData = new ArrayList<>();

      for (Entry<Artifact, Set<AttributeTypeToken>> entry : artifactAttributeMap.entrySet()) {
         Artifact artifact = entry.getKey();
         for (AttributeTypeToken attributeType : entry.getValue()) {
            List<Object> attributeValues = artifact.getAttributeValues(attributeType);
            if (hasDuplicates(attributeValues)) {
               logf("duplicates found art[%s] attrType[%s] values[%s]", artifact, attributeType, attributeValues);

               artifact.setAttributeFromValues(attributeType, attributeValues);
               List<Object> attributeValuesFixed = artifact.getAttributeValues(attributeType);
               if (transaction != null) {
                  transaction.addArtifact(artifact);
               }

               rowData.add(new String[] {
                  branch.getName(),
                  artifact.getIdString(),
                  artifact.getName(),
                  attributeType.getName(),
                  Collections.toString(", ", attributeValues),
                  Collections.toString(", ", attributeValuesFixed)});
            }
         }
      }

      if (rowData.isEmpty()) {
         rowData.add(new String[] {
            "-- no duplicates found --",
            "-- no duplicates found --",
            "-- no duplicates found --",
            "-- no duplicates found --",
            "-- no duplicates found --",
            "-- no duplicates found --"});
      }

      display.displayReport("Fix Duplicate Report", rowData);

      if (transaction != null) {
         transaction.execute();
      } else {
         // Remove dirty artifacts from Cache so we can perform operation again and still get latest artifacts from database
         for (Artifact artifact : artifactAttributeMap.keySet()) {
            ArtifactCache.deCache(artifact);
         }
      }
   }

   private HashCollectionSet<Artifact, AttributeTypeToken> getArtifactsWithDuplicates(IProgressMonitor monitor) {
      HashCollectionSet<Artifact, AttributeTypeToken> artifactAttributeMap = new HashCollectionSet<>(HashSet::new);

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromBranch(branch, DeletionFlag.EXCLUDE_DELETED);
      checkForCancelledStatus(monitor);
      monitor.subTask("Mapping Enumerated Attributes");

      for (Artifact artifact : artifacts) {
         List<Attribute<?>> attributes = artifact.getAttributes();
         for (Attribute<?> attribute : attributes) {
            checkForCancelledStatus(monitor);
            AttributeType attributeType = attribute.getAttributeType();
            if (attributeType.isEnumerated()) {
               artifactAttributeMap.put(artifact, attributeType);
            }
         }
      }
      return artifactAttributeMap;
   }

   private boolean hasDuplicates(List<Object> attributeValues) {
      boolean result = false;
      Set<Object> set = new HashSet<>();
      for (Object object : attributeValues) {
         if (!set.add(object)) {
            result = true;
            break;
         }
      }
      return result;
   }
}