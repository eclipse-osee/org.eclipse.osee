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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Stephen J. Molaro
 */
public class TestCaseToken extends ArtifactAccessorResult {

   public static final TestCaseToken SENTINEL = new TestCaseToken();

   private Double testNumber;
   private List<TestPointToken> testPoints;
   private List<AttentionLocationToken> attentionMessages;
   private List<ScriptLogToken> logs;
   private List<TraceToken> trace;

   private boolean isInitial = false;
   private boolean isCleanup = false;

   public TestCaseToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public TestCaseToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setTestNumber(art.getSoleAttributeValue(CoreAttributeTypes.TestNumber, 0.0));
      this.setInitial(art.getArtifactType().equals(CoreArtifactTypes.TestCaseInitial));
      this.setCleanup(art.getArtifactType().equals(CoreArtifactTypes.TestCaseCleanup));
      this.setTestPoints(art.getRelated(CoreRelationTypes.TestCaseToTestPoint_TestPoint).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new TestPointToken(a)).collect(Collectors.toList()));
      this.setAttentionMessages(
         art.getRelated(CoreRelationTypes.TestCaseToAttentionLocation_AttentionLocation).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new AttentionLocationToken(a)).collect(
               Collectors.toList()));
      this.setLogs(art.getRelated(CoreRelationTypes.TestCaseToScriptLog_ScriptLog).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new ScriptLogToken(a)).collect(Collectors.toList()));
      this.setTrace(art.getRelated(CoreRelationTypes.TestCaseToTrace_Trace).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new TraceToken(a)).collect(Collectors.toList()));
   }

   public TestCaseToken(Long id, String name) {
      super(id, name);
      this.setTestNumber(0.0);
      this.setTestPoints(new LinkedList<>());
      this.setAttentionMessages(new LinkedList<>());
      this.setLogs(new LinkedList<>());
      this.setTrace(new LinkedList<>());
   }

   public TestCaseToken() {
      super();
   }

   /**
    * @return the testNumber
    */
   public Double getTestNumber() {
      return testNumber;
   }

   /**
    * @param testNumber the testNumber to set
    */
   public void setTestNumber(Double testNumber) {
      this.testNumber = testNumber;
   }

   public List<TestPointToken> getTestPoints() {
      return testPoints;
   }

   public void setTestPoints(List<TestPointToken> testPoints) {
      this.testPoints = testPoints;
   }

   @JsonIgnore
   public List<AttentionLocationToken> getAttentionMessages() {
      return attentionMessages;
   }

   public void setAttentionMessages(List<AttentionLocationToken> attentionMessages) {
      this.attentionMessages = attentionMessages;
   }

   @JsonIgnore
   public List<ScriptLogToken> getLogs() {
      return logs;
   }

   public void setLogs(List<ScriptLogToken> logs) {
      this.logs = logs;
   }

   @JsonIgnore
   public List<TraceToken> getTrace() {
      return trace;
   }

   public void setTrace(List<TraceToken> trace) {
      this.trace = trace;
   }

   @JsonIgnore
   public boolean isInitial() {
      return isInitial;
   }

   public void setInitial(boolean isInitial) {
      this.isInitial = isInitial;
   }

   @JsonIgnore
   public boolean isCleanup() {
      return isCleanup;
   }

   public void setCleanup(boolean isCleanup) {
      this.isCleanup = isCleanup;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.TestNumber, Double.toString(this.getTestNumber()));

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());

      ArtifactTypeToken artType = CoreArtifactTypes.ScriptTestCase;

      if (isInitial()) {
         artType = CoreArtifactTypes.TestCaseInitial;
      }
      if (isCleanup()) {
         artType = CoreArtifactTypes.TestCaseCleanup;
      }

      art.setTypeId(artType.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : artType.getValidAttributeTypes()) {
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