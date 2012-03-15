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
package org.eclipse.osee.ats.core.workdef;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.ats.dsl.AtsDslStandaloneSetup;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.xtext.resource.SaveOptions;

/**
 * @author Donald G. Dunne
 */
public final class ModelUtil {

   protected ModelUtil() {
      // Utility Class
   }

   public static AtsDsl loadModel(String uri, String xTextData) throws OseeCoreException {
      return loadModel(uri, xTextData, new AtsDslResourceProvider());

   }

   public static AtsDsl loadModel(String uri, String xTextData, IResourceProvider resourceProvider) throws OseeCoreException {
      AtsDsl atsDsl = resourceProvider.getContents(uri, xTextData);
      for (String error : resourceProvider.getErrors()) {
         throw new OseeStateException(error);
      }
      return atsDsl;
   }

   public static void saveModel(AtsDsl model, String uri, OutputStream outputStream) throws IOException {
      AtsDslStandaloneSetup.doSetup();

      ResourceSet resourceSet = new ResourceSetImpl();
      Resource resource = resourceSet.createResource(URI.createURI(uri));
      resource.getContents().add(model);

      Map<String, Boolean> options = new HashMap<String, Boolean>();
      SaveOptions saveOptions = SaveOptions.getOptions(options);
      resource.save(outputStream, saveOptions.toOptionsMap());
   }

}
