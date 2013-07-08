/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.message;

import java.util.List;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class OseeEnumTypeCacheUpdateResponse {
   private final List<String[]> enumTypeRows;
   private final List<String[]> enumEntryRows;

   public OseeEnumTypeCacheUpdateResponse(List<String[]> enumTypeRows, List<String[]> enumEntryRows) {
      this.enumTypeRows = enumTypeRows;
      this.enumEntryRows = enumEntryRows;
   }

   public List<String[]> getEnumTypeRows() {
      return enumTypeRows;
   }

   public List<String[]> getEnumEntryRows() {
      return enumEntryRows;
   }

}
