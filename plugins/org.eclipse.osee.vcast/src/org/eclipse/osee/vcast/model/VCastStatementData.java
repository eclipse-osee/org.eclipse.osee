/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastStatementData {

   private final int id;
   private final int statementId;
   private final int resultId;
   private final int resultLine;
   private final Boolean hit;

   public VCastStatementData(int id, int statementId, int resultId, int resultLine, Boolean hit) {
      super();
      this.id = id;
      this.statementId = statementId;
      this.resultId = resultId;
      this.resultLine = resultLine;
      this.hit = hit;
   }

   public int getId() {
      return id;
   }

   public int getStatementId() {
      return statementId;
   }

   public int getResultId() {
      return resultId;
   }

   public int getResultLine() {
      return resultLine;
   }

   public Boolean getHit() {
      return hit;
   }

}
