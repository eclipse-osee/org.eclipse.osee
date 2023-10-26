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
   private Date executionDate;
   private String machineName;

   public ScriptBatchToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptBatchToken(ArtifactReadable art) {
      super(art);
      this.setBatchId(art.getSoleAttributeAsString(CoreAttributeTypes.BatchId, ""));
      this.setExecutionDate(art.getSoleAttributeValue(CoreAttributeTypes.ExecutionDate, new Date()));
      this.setMachineName(art.getSoleAttributeAsString(CoreAttributeTypes.MachineName, ""));
   }

   public ScriptBatchToken(Long id, String name) {
      super(id, name);
      this.setBatchId("");
      this.setExecutionDate(new Date());
      this.setMachineName("");
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

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.ExecutionDate, this.getExecutionDate().getTime() + "");
      values.put(CoreAttributeTypes.MachineName, this.getMachineName());
      values.put(CoreAttributeTypes.BatchId, this.getBatchId());

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
