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

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.logger.Log;
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
   private final Bundle bundle;

   public BundleWadlGeneratorConfig(Log logger, Bundle bundle) {
      this.logger = logger;
      this.bundle = bundle;
   }

   @Override
   public List<WadlGeneratorDescription> configure() {
      List<WadlGeneratorDescription> toReturn;
      try {
         toReturn =
            generator(WadlGeneratorApplicationDoc.class).prop("applicationDocsFile",
               getAsFile("REST-INF/application-doc.xml")) //
            .generator(WadlGeneratorGrammarsSupport.class).prop("grammarsFile",
               getAsFile("REST-INF/application-grammars.xml")) //
            .generator(WadlGeneratorResourceDocSupport.class).prop("resourceDocFile",
               getAsFile("REST-INF/resourcedoc.xml")) //
            .descriptions();
      } catch (Exception ex) {
         logger.error(ex, "Error generating wadl for [%s]", bundle.getSymbolicName());
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }

   private File getAsFile(String path) throws Exception {
      URL url = bundle.getResource(path);
      url = FileLocator.toFileURL(url);
      return new File(url.toURI());
   }

}
