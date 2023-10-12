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
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Ryan T. Baldwin
 */
public class LoggingSummaryToken extends ArtifactAccessorResult {

   public static final LoggingSummaryToken SENTINEL = new LoggingSummaryToken();

   private int summaryId;
   private int startNumber;
   private int informationalCount;
   private int minorCount;
   private int seriousCount;
   private int criticalCount;
   private int exceptionCount;
   private List<ErrorEntryToken> errorEntries;

   public LoggingSummaryToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public LoggingSummaryToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setSummaryId(art.getSoleAttributeValue(CoreAttributeTypes.SummaryId, -1));
      this.setStartNumber(art.getSoleAttributeValue(CoreAttributeTypes.StartNumber, 0));
      this.setInformationalCount(art.getSoleAttributeValue(CoreAttributeTypes.InformationalCount, 0));
      this.setMinorCount(art.getSoleAttributeValue(CoreAttributeTypes.MinorCount, 0));
      this.setSeriousCount(art.getSoleAttributeValue(CoreAttributeTypes.SeriousCount, 0));
      this.setCriticalCount(art.getSoleAttributeValue(CoreAttributeTypes.CriticalCount, 0));
      this.setExceptionCount(art.getSoleAttributeValue(CoreAttributeTypes.ExceptionCount, 0));
      this.setErrorEntries(
         art.getRelated(CoreRelationTypes.LoggingSummaryToErrorEntry_ErrorEntry).getList().stream().filter(
            a -> a.getExistingAttributeTypes().isEmpty()).map(a -> new ErrorEntryToken(a)).collect(
               Collectors.toList()));
   }

   public LoggingSummaryToken(Long id, String name) {
      super(id, name);
      this.setSummaryId(-1);
      this.setStartNumber(0);
      this.setInformationalCount(0);
      this.setMinorCount(0);
      this.setSeriousCount(0);
      this.setCriticalCount(0);
      this.setExceptionCount(0);
      this.setErrorEntries(new LinkedList<>());
   }

   public LoggingSummaryToken() {
      super();
   }

   public int getSummaryId() {
      return summaryId;
   }

   public void setSummaryId(int summaryId) {
      this.summaryId = summaryId;
   }

   public int getStartNumber() {
      return startNumber;
   }

   public void setStartNumber(int startNumber) {
      this.startNumber = startNumber;
   }

   public int getInformationalCount() {
      return informationalCount;
   }

   public void setInformationalCount(int informationalCount) {
      this.informationalCount = informationalCount;
   }

   public int getMinorCount() {
      return minorCount;
   }

   public void setMinorCount(int minorCount) {
      this.minorCount = minorCount;
   }

   public int getSeriousCount() {
      return seriousCount;
   }

   public void setSeriousCount(int seriousCount) {
      this.seriousCount = seriousCount;
   }

   public int getCriticalCount() {
      return criticalCount;
   }

   public void setCriticalCount(int criticalCount) {
      this.criticalCount = criticalCount;
   }

   public int getExceptionCount() {
      return exceptionCount;
   }

   public void setExceptionCount(int exceptionCount) {
      this.exceptionCount = exceptionCount;
   }

   public List<ErrorEntryToken> getErrorEntries() {
      return errorEntries;
   }

   public void setErrorEntries(List<ErrorEntryToken> errorEntries) {
      this.errorEntries = errorEntries;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.SummaryId, Integer.toString(this.getSummaryId()));
      values.put(CoreAttributeTypes.StartNumber, Integer.toString(this.getStartNumber()));
      values.put(CoreAttributeTypes.InformationalCount, Integer.toString(this.getInformationalCount()));
      values.put(CoreAttributeTypes.MinorCount, Integer.toString(this.getMinorCount()));
      values.put(CoreAttributeTypes.SeriousCount, Integer.toString(this.getSeriousCount()));
      values.put(CoreAttributeTypes.CriticalCount, Integer.toString(this.getCriticalCount()));
      values.put(CoreAttributeTypes.ExceptionCount, Integer.toString(this.getExceptionCount()));

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.LoggingSummary.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.LoggingSummary.getValidAttributeTypes()) {
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
