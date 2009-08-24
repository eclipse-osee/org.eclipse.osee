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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DataStore;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AbstractResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeURL;
import org.eclipse.osee.framework.skynet.core.attribute.utils.BinaryContentUtils;

public class AttributeResourceProcessor extends AbstractResourceProcessor {

   private final Attribute<?> attribute;

   public AttributeResourceProcessor(Attribute<?> attribute) {
      this.attribute = attribute;
   }

   @Override
   protected URL getAcquireURL(DataStore dataToStore) throws OseeDataStoreException {
      return AttributeURL.getAcquireURL(dataToStore.getLocator());
   }

   @Override
   protected URL getDeleteURL(DataStore dataToStore) throws OseeDataStoreException {
      return AttributeURL.getDeleteURL(dataToStore.getLocator());
   }

   @Override
   protected URL getStorageURL(int seed, String name, String extension) throws OseeDataStoreException, OseeAuthenticationRequiredException {
      try {
         return AttributeURL.getStorageURL(seed, name, extension);
      } catch (MalformedURLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   @Override
   public String createStorageName() throws OseeDataStoreException {
      try {
         return BinaryContentUtils.getStorageName(attribute);
      } catch (OseeStateException ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}