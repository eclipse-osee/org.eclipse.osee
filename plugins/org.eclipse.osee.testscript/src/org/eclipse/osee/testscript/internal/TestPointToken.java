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
public class TestPointToken extends ArtifactAccessorResult {

   public static final TestPointToken SENTINEL = new TestPointToken();

   private Double testNumber;
   private String result;
   private String overallResult;
   private String resultType;
   private boolean interactive;
   private String groupName;
   private String groupType;
   private String groupOperator;
   private String expected;
   private String actual;
   private String requirement;
   private int elapsedTime;
   private int transmissionCount;
   private String notes;
   private List<AttentionLocationToken> locations;
   private List<TestPointToken> subTestPoints;
   private List<InfoGroupToken> infoGroups;

   public TestPointToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public TestPointToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setTestNumber(art.getSoleAttributeValue(CoreAttributeTypes.TestNumber, 0.0));
      this.setResult(art.getSoleAttributeAsString(CoreAttributeTypes.Result, ""));
      this.setOverallResult(art.getSoleAttributeAsString(CoreAttributeTypes.OverallResult, ""));
      this.setResultType(art.getSoleAttributeAsString(CoreAttributeTypes.ResultType, ""));
      this.setInteractive(art.getSoleAttributeValue(CoreAttributeTypes.Interactive, false));
      this.setGroupName(art.getSoleAttributeAsString(CoreAttributeTypes.TestPointGroupName, ""));
      this.setGroupType(art.getSoleAttributeAsString(CoreAttributeTypes.TestPointGroupType, ""));
      this.setGroupOperator(art.getSoleAttributeAsString(CoreAttributeTypes.TestPointGroupOperator, ""));
      this.setExpected(art.getSoleAttributeAsString(CoreAttributeTypes.Expected, ""));
      this.setActual(art.getSoleAttributeAsString(CoreAttributeTypes.Actual, ""));
      this.setRequirement(art.getSoleAttributeAsString(CoreAttributeTypes.TestPointRequirement, ""));
      this.setElapsedTime(art.getSoleAttributeValue(CoreAttributeTypes.ElapsedTime, -1));
      this.setTransmissionCount(art.getSoleAttributeValue(CoreAttributeTypes.TransmissionCount, -1));
      this.setNotes(art.getSoleAttributeAsString(CoreAttributeTypes.Notes, ""));
      this.setLocations(
         art.getRelated(CoreRelationTypes.TestPointToAttentionLocation_AttentionLocation).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new AttentionLocationToken(a)).collect(
               Collectors.toList()));
      this.setSubTestPoints(
         art.getRelated(CoreRelationTypes.TestPointGroupToTestPoint_TestPoint).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new TestPointToken(a)).collect(
               Collectors.toList()));
      this.setInfoGroups(art.getRelated(CoreRelationTypes.TestPointToInfoGroup_InfoGroup).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InfoGroupToken(a)).collect(Collectors.toList()));
   }

   public TestPointToken(Long id, String name) {
      super(id, name);
      this.setTestNumber(0.0);
      this.setResult("");
      this.setOverallResult("");
      this.setResultType("");
      this.setInteractive(false);
      this.setGroupName("");
      this.setGroupType("");
      this.setGroupOperator("");
      this.setExpected("");
      this.setActual("");
      this.setRequirement("");
      this.setElapsedTime(-1);
      this.setTransmissionCount(-1);
      this.setNotes("");
      this.setLocations(new LinkedList<>());
      this.setSubTestPoints(new LinkedList<>());
      this.setInfoGroups(new LinkedList<>());
   }

   public TestPointToken() {
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

   /**
    * @return the result
    */
   public String getResult() {
      return result;
   }

   /**
    * @param result the result to set
    */
   public void setResult(String result) {
      this.result = result;
   }

   public String getOverallResult() {
      return overallResult;
   }

   public void setOverallResult(String overallResult) {
      this.overallResult = overallResult;
   }

   /**
    * @return the resultType
    */
   @JsonIgnore
   public String getResultType() {
      return resultType;
   }

   /**
    * @param resultType the resultType to set
    */
   public void setResultType(String resultType) {
      this.resultType = resultType;
   }

   /**
    * @return the interactive
    */
   public boolean getInteractive() {
      return interactive;
   }

   /**
    * @param interactive the interactive to set
    */
   public void setInteractive(boolean interactive) {
      this.interactive = interactive;
   }

   /**
    * @return the groupName
    */
   public String getGroupName() {
      return groupName;
   }

   /**
    * @param groupName the groupName to set
    */
   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }

   /**
    * @return the groupType
    */
   @JsonIgnore
   public String getGroupType() {
      return groupType;
   }

   /**
    * @param groupType the groupType to set
    */
   public void setGroupType(String groupType) {
      this.groupType = groupType;
   }

   /**
    * @return the groupOperator
    */
   public String getGroupOperator() {
      return groupOperator;
   }

   /**
    * @param groupOperator the groupOperator to set
    */
   public void setGroupOperator(String groupOperator) {
      this.groupOperator = groupOperator;
   }

   /**
    * @return the expected
    */
   public String getExpected() {
      return expected;
   }

   /**
    * @param expected the expected to set
    */
   public void setExpected(String expected) {
      this.expected = expected;
   }

   /**
    * @return the actual
    */
   public String getActual() {
      return actual;
   }

   /**
    * @param actual the actual to set
    */
   public void setActual(String actual) {
      this.actual = actual;
   }

   /**
    * @return the requirement
    */
   public String getRequirement() {
      return requirement;
   }

   /**
    * @param requirement the requirement to set
    */
   public void setRequirement(String requirement) {
      this.requirement = requirement;
   }

   /**
    * @return the elapsedTime
    */
   public int getElapsedTime() {
      return elapsedTime;
   }

   /**
    * @param elapsedTime the elapsedTime to set
    */
   public void setElapsedTime(int elapsedTime) {
      this.elapsedTime = elapsedTime;
   }

   /**
    * @return the transmissionCount
    */
   public int getTransmissionCount() {
      return transmissionCount;
   }

   /**
    * @param transmissionCount the transmissionCount to set
    */
   public void setTransmissionCount(int transmissionCount) {
      this.transmissionCount = transmissionCount;
   }

   /**
    * @return the notes
    */
   public String getNotes() {
      return notes;
   }

   /**
    * @param notes the notes to set
    */
   public void setNotes(String notes) {
      this.notes = notes;
   }

   @JsonIgnore
   public List<TestPointToken> getSubTestPoints() {
      return subTestPoints;
   }

   public void setSubTestPoints(List<TestPointToken> subTestPoints) {
      this.subTestPoints = subTestPoints;
   }

   @JsonIgnore
   public List<AttentionLocationToken> getLocations() {
      return locations;
   }

   public void setLocations(List<AttentionLocationToken> locations) {
      this.locations = locations;
   }

   @JsonIgnore
   public List<InfoGroupToken> getInfoGroups() {
      return infoGroups;
   }

   public void setInfoGroups(List<InfoGroupToken> infoGroups) {
      this.infoGroups = infoGroups;
   }

   public CreateArtifact createArtifact(String key) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.TestNumber, Double.toString(this.getTestNumber()));
      values.put(CoreAttributeTypes.Result, this.getResult());
      values.put(CoreAttributeTypes.ResultType, this.getResultType());
      values.put(CoreAttributeTypes.Interactive, Boolean.toString(this.getInteractive()));
      values.put(CoreAttributeTypes.TestPointGroupName, this.getGroupName());
      values.put(CoreAttributeTypes.TestPointGroupType, this.getGroupType());
      values.put(CoreAttributeTypes.TestPointGroupOperator, this.getGroupOperator());
      values.put(CoreAttributeTypes.Expected, this.getExpected());
      values.put(CoreAttributeTypes.Actual, this.getActual());
      values.put(CoreAttributeTypes.TestPointRequirement, this.getRequirement());
      values.put(CoreAttributeTypes.ElapsedTime, Integer.toString(this.getElapsedTime()));
      values.put(CoreAttributeTypes.TransmissionCount, Integer.toString(this.getTransmissionCount()));
      values.put(CoreAttributeTypes.Notes, this.getNotes());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.TestPoint.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.TestPoint.getValidAttributeTypes()) {
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
