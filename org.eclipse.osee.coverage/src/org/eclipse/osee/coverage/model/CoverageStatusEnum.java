/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public enum CoverageStatusEnum {
   None(false), Needs_Work(false), InWork(false), Fix_Code(false), Fix_Test(false), Completed(true);

   private final boolean completed;

   private CoverageStatusEnum(boolean completed) {
      this.completed = completed;
   }

   public boolean isCompleted() {
      return completed;
   }

   public static Collection<CoverageStatusEnum> getCollection() {
      List<CoverageStatusEnum> enums = new ArrayList<CoverageStatusEnum>();
      for (CoverageStatusEnum e : values()) {
         enums.add(e);
      }
      return enums;
   }
}
