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

package org.eclipse.osee.orcs.db.internal.sql;

import org.eclipse.osee.orcs.core.ds.Criteria;

/**
 * @author Roberto E. Escobar
 */
public abstract class SqlHandler<T extends Criteria> {

   private int level;

   public int getLevel() {
      return level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public abstract int getPriority();

   public void setData(T criteria) {
      // do nothing
   }
   
   public void addTables(AbstractSqlWriter writer) {
      // do nothing
   }

   public void addPredicates(AbstractSqlWriter writer) {
      // do nothing
   }

   public boolean hasPredicates() {
      return true;
   }

   public void writeCommonTableExpression(AbstractSqlWriter writer) {
      // Do Nothing
   }

   public void writeSelectFields(AbstractSqlWriter writer) {
      // Do Nothing
   }

   public boolean shouldWriteAnd() {
      return true;
   }

   public boolean criteriaIsValid() {
      return true;
   }

   public void startWithPreSelect(AbstractSqlWriter writer) {

   }

   public void endWithPreSelect(AbstractSqlWriter writer) {

   }
}