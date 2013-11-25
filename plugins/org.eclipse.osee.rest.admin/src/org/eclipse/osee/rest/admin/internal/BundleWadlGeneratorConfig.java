/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.rest.admin.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.rest.admin.internal.RestResourceConcatenator.InputStreamSupplier;
import org.osgi.framework.Bundle;
import com.sun.jersey.api.wadl.config.WadlGeneratorConfig;
import com.sun.jersey.api.wadl.config.WadlGeneratorDescription;
import com.sun.jersey.server.wadl.generators.WadlGeneratorApplicationDoc;
import com.sun.jersey.server.wadl.generators.WadlGeneratorGrammarsSupport;
import com.sun.jersey.server.wadl.generators.resourcedoc.WadlGeneratorResourceDocSupport;

/**
 * @author Roberto E. Escobar
 */
public class BundleWadlGeneratorConfig extends WadlGeneratorConfig {

   private final Log logger;
   private final Iterable<Bundle> bundles;

   public BundleWadlGeneratorConfig(Log logger, Iterable<Bundle> bundles) {
      this.logger = logger;
      this.bundles = bundles;
   }

   public boolean hasExtendedWadl() {
      return !getWadlBundles().isEmpty();
   }

   public List<Bundle> getWadlBundles() {
      List<Bundle> output = new ArrayList<Bundle>();
      for (Bundle bundle : bundles) {
         if (hasExtendedWadl(bundle)) {
            output.add(bundle);
         }
      }
      return output;
   }

   @Override
   public List<WadlGeneratorDescription> configure() {
      List<WadlGeneratorDescription> toReturn;
      RestResourceConcatenator wadlResource = new RestResourceConcatenator();
      wadlResource.initialize("resourceDoc");
      RestResourceConcatenator wadlApp = new RestResourceConcatenator();
      wadlApp.initialize("applicationDocs");
      RestResourceConcatenator wadlGrammar = new RestResourceConcatenator();
      wadlGrammar.initialize("grammars");
      try {
         toReturn =
            generator(WadlGeneratorApplicationDoc.class).prop("applicationDocsStream",
               getAsInputStream("REST-INF/application-doc.xml", wadlApp)) //
            .generator(WadlGeneratorGrammarsSupport.class).prop("grammarsStream",
               getAsInputStream("REST-INF/application-grammars.xml", wadlGrammar)) //
            .generator(WadlGeneratorResourceDocSupport.class).prop("resourceDocStream",
               getAsInputStream("REST-INF/resourcedoc.xml", wadlResource)) //
            .descriptions();
      } catch (Exception ex) {
         logger.error(ex, "Error generating wadl");
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }

   private static InputStreamSupplier localSupplier(final URL url) {
      return new InputStreamSupplier() {
         @Override
         public InputStream getInputStream() throws IOException {
            return url.openStream();
         }
      };
   }

   private InputStream getAsInputStream(String path, RestResourceConcatenator concat) throws Exception {
      for (Bundle bundle : bundles) {
         if (hasExtendedWadl(bundle)) {
            URL url = bundle.getResource(path);
            concat.addResource(localSupplier(url));
         }
      }
      return concat.getAsInputStream();
   }

   private boolean hasEntries(Bundle bundle, String... paths) {
      // if you don't pass in any paths, this will return true
      for (String path : paths) {
         if (!hasEntry(bundle, path)) {
            return false;
         }
      }
      return true;
   }

   private boolean hasExtendedWadl(Bundle bundle) {
      return hasEntries(bundle, "REST-INF/application-doc.xml", "REST-INF/application-grammars.xml",
         "REST-INF/resourcedoc.xml");
   }

   private boolean hasEntry(Bundle bundle, String path) {
      return bundle.getEntry(path) != null;
   }
}
