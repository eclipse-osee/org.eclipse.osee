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

package org.eclipse.osee.framework.core.model.tabledataframework;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Shawn F. Cook
 */
public abstract class ColumnAbstract implements Column {
   private final Collection<ColumnAbstract> requiredCols = new ArrayList<>();
   private final String headerString;
   private final boolean isVisible;

   public ColumnAbstract(boolean isVisible) {
      this(null, "No Name", isVisible);
   }

   public ColumnAbstract(String headerString, boolean isVisible) {
      this(null, headerString, isVisible);
   }

   public ColumnAbstract(Collection<ColumnAbstract> requiredCols, String headerString, boolean isVisible) {
      if (requiredCols != null) {
         this.requiredCols.addAll(requiredCols);
      }
      this.headerString = headerString;
      this.isVisible = isVisible;
   }

   @Override
   public String getHeaderString() {
      return headerString;
   }

   @Override
   public boolean isVisible() {
      return isVisible;
   }
}
