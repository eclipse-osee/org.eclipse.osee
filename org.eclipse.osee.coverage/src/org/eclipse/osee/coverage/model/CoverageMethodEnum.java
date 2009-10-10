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
public enum CoverageMethodEnum {
   Deactivated_Code, Exception_Handling, Test_Unit, Not_Covered;

   public static Collection<CoverageMethodEnum> getCollection() {
      List<CoverageMethodEnum> enums = new ArrayList<CoverageMethodEnum>();
      for (CoverageMethodEnum e : values()) {
         enums.add(e);
      }
      return enums;
   }
}
