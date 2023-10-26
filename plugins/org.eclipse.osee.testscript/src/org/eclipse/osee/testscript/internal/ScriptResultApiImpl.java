/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.testscript.ScriptResultApi;

/**
 * @author Stephen J. Molaro
 */
public class ScriptResultApiImpl implements ScriptResultApi {
   private ArtifactAccessor<ScriptResultToken> accessor;
   private final List<AttributeTypeId> attributes;
   public ScriptResultApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new ScriptResultAccessor(orcsApi));
      attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
   }

   private void setAccessor(ArtifactAccessor<ScriptResultToken> scriptResultTypeAccessor) {
      this.accessor = scriptResultTypeAccessor;
   }

   @Override
   public ScriptResultToken get(BranchId branch, ArtifactId scriptResultTypeId) {
      try {
         return this.accessor.get(branch, scriptResultTypeId);
      } catch (Exception ex) {
         return new ScriptResultToken();
      }
   }

   @Override
   public ScriptResultToken getWithTestPointsAndFilter(BranchId branch, ArtifactId resultId, String filter, int pageNum,
      int count) {
      ScriptResultToken resultToken = ScriptResultToken.SENTINEL;
      try {
         resultToken = this.accessor.get(branch, resultId);
      } catch (Exception ex) {
         System.out.println(ex);
      }
      if (resultToken.isValid()) {
         String url = resultToken.getFileUrl();
         File f = new File(url);
         if (!f.exists()) {
            return resultToken;
         }
         FileInputStream fis;
         try {
            fis = new FileInputStream(f);
            ZipInputStream zis = new ZipInputStream(fis);
            // There should only be one file per zip
            if (zis.getNextEntry() != null) {
               ImportTmoReader reader = new ImportTmoReader();
               ScriptDefToken tmoToken = reader.getScriptDefinition(zis, ArtifactId.SENTINEL);
               resultToken = tmoToken.getScriptResults().get(0);
               zis.close();
               fis.close();
               resultToken.setId(resultToken.getId());
               List<TestPointToken> testPoints = resultToken.getTestPoints();
               if (Strings.isValid(filter)) {
                  testPoints = testPoints.stream().filter(
                     tp -> tp.getName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
               }
               resultToken.setTestPoints(testPoints);
               resultToken.setTotalTestPoints(testPoints.size());
               if (pageNum > 0 && count > 0) {
                  int startIndex = (pageNum - 1) * count;
                  int endIndex = Math.min(testPoints.size(), startIndex + count);
                  if (startIndex > testPoints.size() - 1) {
                     resultToken.setTestPoints(new LinkedList<>());
                  } else {
                     resultToken.setTestPoints(testPoints.subList(startIndex, endIndex));
                  }
               }
               return resultToken;
            } else {
               zis.close();
               fis.close();
            }
         } catch (IOException ex) {
            System.out.println(ex);
         }
      }
      return resultToken;
   }

   @Override
   public Collection<ScriptResultToken> getAllForBatch(BranchId branch, ArtifactId viewId, ArtifactId batchId,
      String filter, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAllByRelationAndFilter(branch,
            CoreRelationTypes.ScriptBatchToTestScriptResult_ScriptBatch, batchId, filter, attributes, pageNum, pageSize,
            orderByAttribute, viewId);
      } catch (Exception ex) {
         System.out.println(ex);
      }
      return new LinkedList<>();
   }

   @Override
   public int getAllForBatchCount(BranchId branch, ArtifactId viewId, ArtifactId batchId, String filter) {
      if (Strings.isValid(filter)) {
         return this.accessor.getAllByRelationAndFilterAndCount(branch,
            CoreRelationTypes.ScriptBatchToTestScriptResult_ScriptBatch, batchId, filter, attributes, viewId);
      }
      return this.accessor.getAllByRelationAndCount(branch, CoreRelationTypes.ScriptBatchToTestScriptResult_ScriptBatch,
         batchId, viewId);
   }

   @Override
   public Collection<ScriptResultToken> getAll(BranchId branch) {
      return this.getAll(branch, ArtifactId.SENTINEL);
   }

   @Override
   public Collection<ScriptResultToken> getAll(BranchId branch, ArtifactId viewId) {
      return this.getAll(branch, viewId, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptResultToken> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, ArtifactId.SENTINEL, orderByAttribute);
   }

   @Override
   public Collection<ScriptResultToken> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, viewId, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<ScriptResultToken> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, ArtifactId.SENTINEL, pageNum, pageSize);
   }

   @Override
   public Collection<ScriptResultToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize) {
      return this.getAll(branch, viewId, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptResultToken> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, ArtifactId.SENTINEL, pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<ScriptResultToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAll(branch, pageNum, pageSize, orderByAttribute);
      } catch (Exception ex) {
         return new LinkedList<ScriptResultToken>();
      }
   }

   @Override
   public Collection<ScriptResultToken> getAllByFilter(BranchId branch, String filter) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter);
   }

   @Override
   public Collection<ScriptResultToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter) {
      return this.getAllByFilter(branch, viewId, filter, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptResultToken> getAllByFilter(BranchId branch, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, orderByAttribute);
   }

   @Override
   public Collection<ScriptResultToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, viewId, filter, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<ScriptResultToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, pageNum, pageSize);
   }

   @Override
   public Collection<ScriptResultToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize) {
      return this.getAllByFilter(branch, viewId, filter, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptResultToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<ScriptResultToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAllByFilter(branch, filter, attributes, pageNum, pageSize, orderByAttribute);
      } catch (Exception ex) {
         return new LinkedList<ScriptResultToken>();
      }
   }

   @Override
   public int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter) {
      return this.accessor.getAllByFilterAndCount(branch, filter, Arrays.asList(CoreAttributeTypes.Name), viewId);
   }

}
