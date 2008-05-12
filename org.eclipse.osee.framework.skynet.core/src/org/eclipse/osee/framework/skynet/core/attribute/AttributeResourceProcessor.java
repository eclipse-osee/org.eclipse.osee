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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DataStore;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AbstractResourceProcessor;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;

public class AttributeResourceProcessor extends AbstractResourceProcessor {

   private final Attribute<?> attribute;

   public AttributeResourceProcessor(Attribute<?> attribute) {
      this.attribute = attribute;
   }

   private URL generatePathURL(DataStore dataToStore) throws Exception {
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put("uri", dataToStore.getLocator());
      String urlString = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("resource", parameterMap);
      return new URL(urlString);
   }

   protected URL getAcquireURL(DataStore dataToStore) throws Exception {
      return generatePathURL(dataToStore);
   }

   protected URL getDeleteURL(DataStore dataToStore) throws Exception {
      return generatePathURL(dataToStore);
   }

   protected URL getStorageURL(DataStore dataToStore) throws Exception {
      Artifact artifact = attribute.getArtifact();

      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put("protocol", "attr");
      parameterMap.put("seed", Integer.toString(attribute.getPersistenceMemo().getGammaId()));
      parameterMap.put("name", artifact.getHumanReadableId());
      String extension = dataToStore.getExtension();
      if (Strings.isValid(extension) != false) {
         parameterMap.put("extension", dataToStore.getExtension());
      }
      String urlString = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("resource", parameterMap);
      return new URL(urlString);
   }
}