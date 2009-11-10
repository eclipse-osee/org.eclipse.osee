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
package org.eclipse.osee.framework.resource.management.test.mocks;

import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceListener;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class MockResourceListener implements IResourceListener {
   private boolean postAcquire;
   private boolean preAcquire;
   private boolean preSave;
   private boolean postSave;
   private boolean preDelete;
   private boolean postDelete;
   private IResource resource;
   private IResourceLocator locator;

   public MockResourceListener() {
      reset();
   }

   public void reset() {
      postAcquire = false;
      preAcquire = false;
      preSave = false;
      postSave = false;
      preDelete = false;
      postDelete = false;
      this.resource = null;
      this.locator = null;
   }

   @Override
   public void onPostAcquire(IResource resource) {
      postAcquire = true;
      this.resource = resource;
   }

   @Override
   public void onPreAcquire(IResourceLocator locator) {
      preAcquire = true;
      this.locator = locator;
   }

   @Override
   public void onPostDelete(IResourceLocator locator) {
      postDelete = true;
      this.locator = locator;
   }

   @Override
   public void onPostSave(IResourceLocator locator, IResource resource, Options options) {
      postSave = true;
      this.resource = resource;
   }

   @Override
   public void onPreDelete(IResourceLocator locator) {
      preDelete = true;
      this.locator = locator;
   }

   @Override
   public void onPreSave(IResourceLocator locator, IResource resource, Options options) {
      preSave = true;
      this.locator = locator;
   }

   public boolean isPostAcquire() {
      return postAcquire;
   }

   public boolean isPreAcquire() {
      return preAcquire;
   }

   public boolean isPreSave() {
      return preSave;
   }

   public boolean isPostSave() {
      return postSave;
   }

   public boolean isPreDelete() {
      return preDelete;
   }

   public boolean isPostDelete() {
      return postDelete;
   }

   public IResource getResource() {
      return resource;
   }

   public IResourceLocator getLocator() {
      return locator;
   }
}
