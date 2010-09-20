/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import java.util.Collection;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * Single test that can cover multiple Coverage Items
 * 
 * @author Donald G. Dunne
 */
public class CoverageTestUnit extends NamedIdentity implements ICoverage {
   public CoverageTestUnit(String name) {
      super(null, name);
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getAssignees() {
      return "";
   }

   @Override
   public Result isEditable() {
      return Result.FalseResult;
   }

   @Override
   public KeyedImage getOseeImage() {
      if (isCovered()) {
         return CoverageImage.TEST_UNIT_GREEN;
      }
      return CoverageImage.TEST_UNIT_RED;
   }

   @Override
   public boolean isCovered() {
      return false;
   }

   @Override
   public ICoverage getParent() {
      return null;
   }

   @Override
   public boolean isAssignable() {
      return false;
   }

   @Override
   public String getNotes() {
      return null;
   }

   @Override
   public Double getCoveragePercent() {
      return 0.0;
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      return java.util.Collections.emptyList();
   }

   @Override
   public String getCoveragePercentStr() {
      return "";
   }

   @Override
   public boolean isFolder() {
      return false;
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return getChildren(false);
   }

   @Override
   public String getLocation() {
      return "";
   }

   @Override
   public String getNamespace() {
      return "";
   }

   @Override
   public String getFileContents() {
      return "";
   }

   @Override
   public String getOrderNumber() {
      return "";
   }

   @Override
   public String getWorkProductTaskStr() {
      return "";
   }
}