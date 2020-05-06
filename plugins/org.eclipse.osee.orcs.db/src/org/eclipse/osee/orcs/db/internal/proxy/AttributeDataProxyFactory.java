/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.proxy;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataProxy;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDataProxyFactory {
   private final IResourceManager resourceManager;
   private final Log logger;

   public AttributeDataProxyFactory(IResourceManager resourceManager, Log logger) {
      this.resourceManager = resourceManager;
      this.logger = logger;
   }

   public DataProxy createProxy(AttributeTypeToken attributeTypeToken, Object value, String uri) {
      Object checkedValue = intern(attributeTypeToken, value);
      AbstractDataProxy dataProxy;

      if (isUri(attributeTypeToken.getMediaType())) {
         dataProxy = new UriDataProxy();
      } else {
         dataProxy = new VarCharDataProxy();
      }

      dataProxy.setLogger(logger);
      dataProxy.setStorage(new Storage(resourceManager, dataProxy));
      dataProxy.setData(checkedValue, uri);
      return dataProxy;
   }

   private Object intern(AttributeTypeToken attributeType, Object original) {
      Object value = original;
      if (attributeType.isEnumerated() && value instanceof String) {
         value = Strings.intern((String) value);
      }
      return value;
   }

   static final String APPLICATION_ZIP = "application/zip";

   private boolean isUri(String mediaType) {
      if (mediaType.equals(AttributeTypeToken.TEXT_URI_LIST) || mediaType.equals(
         AttributeTypeToken.APPLICATION_MSWORD) || mediaType.equals(AttributeTypeToken.IMAGE) || mediaType.equals(
            AttributeTypeToken.APPLICATION_ZIP) || mediaType.equals(
               MediaType.TEXT_HTML) || mediaType.equals(MediaType.APPLICATION_OCTET_STREAM)) {
         return true;
      }
      return false;
   }
}
