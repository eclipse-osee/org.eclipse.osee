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

import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Stephen J. Molaro
 */
public class TestPointToken extends ArtifactAccessorResult {

   public static final TestPointToken SENTINEL = new TestPointToken();

   private Double testNumber;
   private String result;
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

   public TestPointToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public TestPointToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setTestNumber(art.getSoleAttributeValue(CoreAttributeTypes.TestNumber, 0.0));
      this.setResult(art.getSoleAttributeAsString(CoreAttributeTypes.Result, ""));
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
   }

   public TestPointToken(Long id, String name) {
      super(id, name);
      this.setTestNumber(0.0);
      this.setResult("");
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

   /**
    * @return the resultType
    */
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
}
