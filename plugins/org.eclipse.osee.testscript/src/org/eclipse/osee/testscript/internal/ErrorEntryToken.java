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
public class ErrorEntryToken extends ArtifactAccessorResult {

   public static final ErrorEntryToken SENTINEL = new ErrorEntryToken();

   private int summaryId;
   private String errorSeverity;
   private String errorVersion;
   private int errorCount;

   public ErrorEntryToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ErrorEntryToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setSummaryId(art.getSoleAttributeValue(CoreAttributeTypes.SummaryId, -1));
      this.setErrorSeverity(art.getSoleAttributeAsString(CoreAttributeTypes.ErrorSeverity, ""));
      this.setErrorVersion(art.getSoleAttributeAsString(CoreAttributeTypes.ErrorVersion, ""));
      this.setErrorCount(art.getSoleAttributeValue(CoreAttributeTypes.ErrorCount, 0));
   }

   public ErrorEntryToken(Long id, String name) {
      super(id, name);
      this.setSummaryId(-1);
      this.setErrorSeverity("");
      this.setErrorVersion("");
      this.setErrorCount(0);
   }

   public ErrorEntryToken() {
      super();
   }

   public int getSummaryId() {
      return summaryId;
   }

   public void setSummaryId(int summaryId) {
      this.summaryId = summaryId;
   }

   public String getErrorSeverity() {
      return errorSeverity;
   }

   public void setErrorSeverity(String errorSeverity) {
      this.errorSeverity = errorSeverity;
   }

   public String getErrorVersion() {
      return errorVersion;
   }

   public void setErrorVersion(String errorVersion) {
      this.errorVersion = errorVersion;
   }

   public int getErrorCount() {
      return errorCount;
   }

   public void setErrorCount(int errorCount) {
      this.errorCount = errorCount;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.SummaryId, Integer.toString(this.getSummaryId()));
      values.put(CoreAttributeTypes.ErrorSeverity, this.getErrorSeverity());
      values.put(CoreAttributeTypes.ErrorVersion, this.getErrorVersion());
      values.put(CoreAttributeTypes.ErrorCount, Integer.toString(this.getErrorCount()));

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.ErrorEntry.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.ErrorEntry.getValidAttributeTypes()) {
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
