/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.core.grammar;

import java.util.ArrayList;

public class ApplicabilityBlock {
   public enum ApplicabilityType {
      Configuration,
      Feature
   };

   private ApplicabilityType applicabilityType;
   private ArrayList<ApplicabilityBlock> embeddedApplicabilityBlocks;
   private ArrayList<String> outerExpressionOperators;
   private String applicabilityExpression;
   private String optionalEndExpression;
   private boolean isInTable;

   private String beforeElseText, afterElseText, beforeEmbeddedBlockText, afterEmbeddedBlockText, fullText;
   private Integer startInsertIndex, endInsertIndex, startTextIndex, endTextIndex;

   public ApplicabilityBlock() {
      embeddedApplicabilityBlocks = new ArrayList<>();
      outerExpressionOperators = new ArrayList<>();
      applicabilityExpression = null;
      optionalEndExpression = null;
      isInTable = false;

      beforeElseText = null;
      afterElseText = null;
      beforeEmbeddedBlockText = null;
      afterEmbeddedBlockText = null;
      fullText = null;

      startInsertIndex = Integer.MAX_VALUE;
      startTextIndex = Integer.MAX_VALUE;
      endInsertIndex = Integer.MIN_VALUE;
      endTextIndex = Integer.MIN_VALUE;
   }

   public Integer getStartInsertIndex() {
      return startInsertIndex;
   }

   public void setStartInsertIndex(Integer startInsertIndex) {
      this.startInsertIndex = startInsertIndex;
   }

   public Integer getEndInsertIndex() {
      return endInsertIndex;
   }

   public void setEndInsertIndex(Integer endInsertIndex) {
      this.endInsertIndex = endInsertIndex;
   }

   public Integer getEndTextIndex() {
      return endTextIndex;
   }

   public void setEndTextIndex(Integer endTextIndex) {
      this.endTextIndex = endTextIndex;
   }

   public Integer getStartTextIndex() {
      return startTextIndex;
   }

   public void setStartTextIndex(Integer startTextIndex) {
      this.startTextIndex = startTextIndex;
   }

   public String getBeforeElseText() {
      return beforeElseText;
   }

   public void setBeforeElseText(String beforeElseText) {
      this.beforeElseText = beforeElseText;
   }

   public String getAfterElseText() {
      return afterElseText;
   }

   public void setAfterElseText(String afterElseText) {
      this.afterElseText = afterElseText;
   }

   public String getBeforeEmbeddedBlockText() {
      return beforeEmbeddedBlockText;
   }

   public void setBeforeEmbeddedBlockText(String beforeEmbeddedBlockText) {
      this.beforeEmbeddedBlockText = beforeEmbeddedBlockText;
   }

   public String getAfterEmbeddedBlockText() {
      return afterEmbeddedBlockText;
   }

   public void setAfterEmbeddedBlockText(String afterEmbeddedBlockText) {
      this.afterEmbeddedBlockText = afterEmbeddedBlockText;
   }

   public String getFullText() {
      return fullText;
   }

   public void setFullText(String fullText) {
      this.fullText = fullText;
   }

   public ApplicabilityType getType() {
      return applicabilityType;
   }

   public void setType(ApplicabilityType applicabilityType) {
      this.applicabilityType = applicabilityType;
   }

   public ArrayList<String> getOuterExpressionOperators() {
      return outerExpressionOperators;
   }

   public void addOuterExpressionOperator(String operator) {
      outerExpressionOperators.add(operator);
   }

   public String getApplicabilityExpression() {
      return applicabilityExpression;
   }

   public void setApplicabilityExpression(String applicabilityExpression) {
      this.applicabilityExpression = applicabilityExpression;
   }

   public void addEmbeddedBlock(ApplicabilityBlock applicBlock) {
      embeddedApplicabilityBlocks.add(applicBlock);
   }

   public ArrayList<ApplicabilityBlock> getEmbeddedApplicabilityBlocks() {
      return embeddedApplicabilityBlocks;
   }

   public String getOptionalEndExpression() {
      return optionalEndExpression;
   }

   public void setOptionalEndExpression(String optionalEndExpression) {
      this.optionalEndExpression = optionalEndExpression;
   }

   public void setIsInTable(boolean isInTable) {
      this.isInTable = isInTable;
   }

   public boolean isInTable() {
      return isInTable;
   }
}
