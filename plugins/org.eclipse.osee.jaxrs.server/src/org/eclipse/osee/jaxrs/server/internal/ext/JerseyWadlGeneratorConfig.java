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
package org.eclipse.osee.jaxrs.server.internal.ext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.Application;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitable;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitor;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import com.google.common.io.InputSupplier;
import com.sun.jersey.api.wadl.config.WadlGeneratorConfig;
import com.sun.jersey.api.wadl.config.WadlGeneratorDescription;
import com.sun.jersey.server.wadl.generators.WadlGeneratorApplicationDoc;
import com.sun.jersey.server.wadl.generators.WadlGeneratorGrammarsSupport;
import com.sun.jersey.server.wadl.generators.resourcedoc.WadlGeneratorResourceDocSupport;

/**
 * @author Roberto E. Escobar
 */
public class JerseyWadlGeneratorConfig extends WadlGeneratorConfig {

   private final Log logger;
   private final JaxRsVisitable visitable;

   public JerseyWadlGeneratorConfig(Log logger, JaxRsVisitable visitable) {
      this.logger = logger;
      this.visitable = visitable;
   }

   private Iterable<Bundle> getBundles() {
      final List<Bundle> bundles = new ArrayList<Bundle>();
      visitable.accept(new JaxRsVisitor() {

         @Override
         public void onApplication(String applicationContext, String componentName, Bundle bundle, Application application) {
            bundles.add(bundle);
         }
      });
      return bundles;
   }

   public boolean hasExtendedWadl() {
      boolean result = false;
      Iterable<Bundle> bundles = getBundles();
      for (Bundle bundle : bundles) {
         result = hasExtendedWadl(bundle);
         if (result) {
            break;
         }
      }
      return result;
   }

   @Override
   public List<WadlGeneratorDescription> configure() {
      List<WadlGeneratorDescription> toReturn;

      InputStream appDocsStream = null;
      InputStream grammarsStream = null;
      InputStream resourceDocStream = null;
      try {
         appDocsStream = getAsInputStream("REST-INF/application-doc.xml", "applicationDocs");
         grammarsStream = getAsInputStream("REST-INF/application-grammars.xml", "grammars");
         resourceDocStream = getAsInputStream("REST-INF/resourcedoc.xml", "resourceDoc");

         toReturn = generator(WadlGeneratorApplicationDoc.class) //
         .prop("applicationDocsStream", appDocsStream) //
         .generator(WadlGeneratorGrammarsSupport.class) //
         .prop("grammarsStream", grammarsStream) //
         .generator(WadlGeneratorResourceDocSupport.class) //
         .prop("resourceDocStream", resourceDocStream) //
         .descriptions();
      } catch (Exception ex) {
         logger.error(ex, "Error generating wadl");
         toReturn = Collections.emptyList();
      } finally {
         Lib.close(appDocsStream);
         Lib.close(grammarsStream);
         Lib.close(resourceDocStream);
      }
      return toReturn;
   }

   private static InputSupplier<InputStream> newSupplier(final URL url) {
      return new InputSupplier<InputStream>() {
         @Override
         public InputStream getInput() throws IOException {
            return url.openStream();
         }
      };
   }

   private InputStream getAsInputStream(String path, String xmlRoot) throws Exception {
      RestResourceConcatenator concat = new RestResourceConcatenator();
      concat.initialize(xmlRoot);
      Iterable<Bundle> bundles = getBundles();
      for (Bundle bundle : bundles) {
         if (hasExtendedWadl(bundle)) {
            URL url = bundle.getResource(path);
            concat.addResource(newSupplier(url));
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
