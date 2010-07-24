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
import java.util.Collections;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class MessageCoverageItem implements ICoverage {

   private final String message;

   public MessageCoverageItem(String message) {
      this.message = message;
   }

   @Override
   public String getName() {
      return message;
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return Collections.emptyList();
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      return Collections.emptyList();
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   @Override
   public Double getCoveragePercent() {
      return 0.0;
   }

   @Override
   public String getCoveragePercentStr() {
      return "";
   }

   @Override
   public String getFileContents() throws OseeCoreException {
      return null;
   }

   @Override
   public String getGuid() {
      return null;
   }

   @Override
   public String getLocation() {
      return null;
   }

   @Override
   public String getNamespace() {
      return null;
   }

   @Override
   public String getNotes() {
      return null;
   }

   @Override
   public KeyedImage getOseeImage() {
      return FrameworkImage.WARNING;
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
   public boolean isCovered() {
      return false;
   }

   @Override
   public Result isEditable() {
      return null;
   }

   @Override
   public boolean isFolder() {
      return false;
   }

   @Override
   public String getOrderNumber() {
      return "";
   }

}
