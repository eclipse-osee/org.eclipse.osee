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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Ryan T. Baldwin
 */
public class ScriptBatchToken extends ArtifactAccessorResult {

   public static final ScriptBatchToken SENTINEL = new ScriptBatchToken();

   private String batchId;
   private String testEnvBatchId;
   private Date executionDate;
   private String machineName;
   private String folderUrl;

   public ScriptBatchToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptBatchToken(ArtifactReadable art) {
      super(art);
      this.setBatchId(art.getSoleAttributeAsString(CoreAttributeTypes.BatchId, ""));
      this.setTestEnvBatchId(art.getSoleAttributeAsString(CoreAttributeTypes.TestEnvBatchId, ""));
      this.setExecutionDate(art.getSoleAttributeValue(CoreAttributeTypes.ExecutionDate, new Date()));
      this.setMachineName(art.getSoleAttributeAsString(CoreAttributeTypes.MachineName, ""));
      this.setFolderUrl(art.getSoleAttributeAsString(CoreAttributeTypes.ContentUrl, ""));
   }

   public ScriptBatchToken(Long id, String name) {
      super(id, name);
      this.setBatchId("");
      this.setTestEnvBatchId("");
      this.setExecutionDate(new Date());
      this.setMachineName("");
      this.setFolderUrl("");
   }

   public ScriptBatchToken() {
      super();
   }

   public String getBatchId() {
      return batchId;
   }

   public void setBatchId(String batchId) {
      this.batchId = batchId;
   }

   public String getTestEnvBatchId() {
      return testEnvBatchId;
   }

   public void setTestEnvBatchId(String testEnvBatchId) {
      this.testEnvBatchId = testEnvBatchId;
   }

   public Date getExecutionDate() {
      return executionDate;
   }

   public void setExecutionDate(Date executionDate) {
      this.executionDate = executionDate;
   }

   public String getMachineName() {
      return machineName;
   }

   public void setMachineName(String machineName) {
      this.machineName = machineName;
   }

   @JsonIgnore
   public String getFolderUrl() {
      return folderUrl;
   }

   public void setFolderUrl(String folderUrl) {
      this.folderUrl = folderUrl;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.ExecutionDate, this.getExecutionDate().getTime() + "");
      values.put(CoreAttributeTypes.MachineName, this.getMachineName());
      values.put(CoreAttributeTypes.BatchId, this.getBatchId());
      values.put(CoreAttributeTypes.TestEnvBatchId, this.getTestEnvBatchId());
      values.put(CoreAttributeTypes.ContentUrl, this.getFolderUrl());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.ScriptBatch.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.ScriptBatch.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);

      art.setkey(key);

      return art;
   }

}
