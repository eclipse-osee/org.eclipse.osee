/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Angel Avila
 */

public class AttributeCheckOperation extends AbstractOperation {

   private final List<Artifact> folders;
   private final AttributeTypeId attribute;
   private boolean changeValues;
   private final boolean multipleValuesRequested;
   private boolean multipleValuesFound;

   public AttributeCheckOperation(OperationLogger logger, List<Artifact> artifacts, AttributeTypeId attribute, boolean changeValues, boolean multipleRequested) {
      super("Attribute Check ", Activator.PLUGIN_ID, logger);
      this.folders = artifacts;
      this.attribute = attribute;
      this.changeValues = changeValues;
      this.multipleValuesRequested = multipleRequested;
   }

   @Override
   protected void doWork(IProgressMonitor monitor)  {
      for (Artifact folder : folders) {
         if (!monitor.isCanceled()) {
            logf("\n\n**************CHECKING IN [%s]***************", folder);
            List<Artifact> allDescendants = folder.getDescendants();
            String popularValue = findMostPopularAttributeValue(allDescendants, monitor);
            compareAllChildrenWithPopularValue(allDescendants, popularValue, folder, monitor);
         }
      }
   }

   private String findMostPopularAttributeValue(List<Artifact> allDescendants, IProgressMonitor monitor)  {
      CountingMap<String> countingMap = new CountingMap<>();

      for (Artifact child : allDescendants) {
         if (!monitor.isCanceled()) {
            List<String> attributeValues = child.getAttributeValues(attribute);
            for (String attributeValue : attributeValues) {
               if (multipleValuesRequested) {
                  if (attributeValues.size() > 1) {
                     multipleValuesFound = true;
                  }
               }
               countingMap.put(attributeValue);
            }
         }
      }

      Set<Entry<String, MutableInteger>> counts = countingMap.getCounts();
      String mostPopular = "";
      int mostPopularCount = 0;

      for (Entry<String, MutableInteger> count : counts) {
         if (!monitor.isCanceled()) {
            int countValueInt = count.getValue().getValue();
            if (countValueInt > mostPopularCount && !mostPopular.equals(count.getKey())) {
               mostPopular = count.getKey();
               mostPopularCount = countValueInt;
            }
         }
      }

      return mostPopular;
   }

   private void compareAllChildrenWithPopularValue(List<Artifact> allDescendants, String popularValue, Artifact folder, IProgressMonitor monitor)  {
      if (!allDescendants.isEmpty()) {
         BranchId branch = allDescendants.get(0).getBranch();
         SkynetTransaction transaction = TransactionManager.createTransaction(branch, "Attribute Check Blam");
         if (!BranchManager.getType(branch).isWorkingBranch()) {
            changeValues = false;
            logf("\n\nCANNOT MAKE ANY CHANGES BECAUSE ARTIFACTS ARE NOT ON A WORKING BRANCH");
         }

         if (multipleValuesRequested != multipleValuesFound) {
            changeValues = false;
            logf("=============WARNING==============");
            logf("NO CHANGES WILL BE MADE\n");
            if (multipleValuesRequested) {
               logf(
                  "No artifacts with multiple values for [%s] were found under the selected folder(s).  All attributes will be expected to have exactly ONE value.",
                  attribute);
               logf(String.format(
                  "To allow this blam to set multiple values, please change an artifact under the folder [%s] to contain multiple values using Artifact Editor.",
                  folder));
            }
            if (multipleValuesFound) {
               logf(String.format(
                  "Artifacts with multiple values for [%s] were found under the selected folder [%s], however, the \'Multiple values allowed for this attribute?\' check box was not selected.",
                  attribute, folder));
            }
         }

         for (Artifact art : allDescendants) {
            if (!monitor.isCanceled()) {
               List<String> attributeValues = art.getAttributeValues(attribute);
               List<String> newValues = attributeValues;
               if (!attributeValues.isEmpty()) {
                  boolean changesNeeded = false;
                  if (multipleValuesRequested && !attributeValues.contains(popularValue)) {
                     logf("[%s] does not contain the value [%s] for [%s]", art, popularValue, attribute);
                     newValues.add(popularValue);
                     changesNeeded = true;
                  } else if (!multipleValuesRequested && !attributeValues.equals(
                     Collections.singletonList(popularValue))) {
                     logf("[%s]'s value for [%s] is NOT equal to [%s]", art, attribute, popularValue);
                     newValues = Arrays.asList(popularValue);
                     changesNeeded = true;
                  }
                  if (changeValues && changesNeeded) {
                     art.setAttributeValues(attribute, newValues);
                     art.persist(transaction);
                     logf("------>Value of attribute [%s] in [%s] changed from [%s] to [%s]", attribute, art,
                        attributeValues, newValues);
                  }
               } else {
                  logf("[%s] has NO values for [%s].  No changes made", art, attribute);
               }
            }
         }
         if (changeValues) {
            transaction.execute();
         }

      }
   }
}
