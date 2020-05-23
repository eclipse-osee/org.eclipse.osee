/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.template.engine.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.jaxrs.mvc.AbstractViewResolver;
import org.eclipse.osee.template.engine.AppendableRule;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;

/**
 * @author Roberto E. Escobar
 */
public class PageFactoryViewResolver extends AbstractViewResolver<ResourceToken> {

   private TemplateRegistry registry;

   public void setTemplateRegistry(TemplateRegistry registry) {
      this.registry = registry;
   }

   @Override
   public ResourceToken resolve(String viewId, MediaType mediaType) {
      ResourceToken toReturn = null;
      if (MediaType.TEXT_HTML_TYPE.equals(mediaType) || MediaType.WILDCARD_TYPE.equals(mediaType)) {
         toReturn = registry.resolveTemplate(viewId, mediaType);
      }
      return toReturn;
   }

   @Override
   public void write(ViewModel model, ResourceToken view, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream output, Charset encoding) throws IOException {
      PageCreator pageCreator = PageFactory.newPageCreator(registry.getResourceRegistry());
      for (Entry<String, Object> entry : model.asMap().entrySet()) {
         String key = entry.getKey();
         Object value = entry.getValue();
         if (value instanceof AppendableRule) {
            pageCreator.addSubstitution((AppendableRule<?>) value);
         } else {
            pageCreator.addKeyValuePair(key, String.valueOf(value));
         }
      }
      OutputStreamWriter writer = new OutputStreamWriter(output, encoding);
      try {
         pageCreator.realizePage(view, writer);
      } finally {
         writer.flush();
      }
   }

}