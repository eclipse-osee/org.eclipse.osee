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
package org.eclipse.osee.framework.types.bridge.operations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.OseeTypesStandaloneSetup;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import com.google.inject.Injector;

/**
 * @author Roberto E. Escobar
 */
public final class OseeTypeModelUtil {

   private OseeTypeModelUtil() {
   }

   public static OseeTypeModel loadModel(Object context, java.net.URI target) throws OseeCoreException {
      String uri = target.toASCIIString();
      Injector injector = new OseeTypesStandaloneSetup().createInjectorAndDoEMFRegistration();
      XtextResourceSet set = injector.getInstance(XtextResourceSet.class);

      set.setClasspathURIContext(context);
      set.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
      Resource resource = set.getResource(URI.createURI(uri), true);
      OseeTypeModel model = (OseeTypeModel) resource.getContents().get(0);
      for (Diagnostic diagnostic : resource.getErrors()) {
         throw new OseeStateException(diagnostic.toString());
      }
      return model;
   }

   public static void saveModel(java.net.URI uri, OseeTypeModel model) throws IOException {
      OseeTypesStandaloneSetup.doSetup();

      ResourceSet resourceSet = new ResourceSetImpl();
      Resource resource = resourceSet.createResource(URI.createURI(uri.toASCIIString()));
      resource.getContents().add(model);

      Map<String, Boolean> options = new HashMap<String, Boolean>();
      options.put(XtextResource.OPTION_FORMAT, Boolean.TRUE);
      resource.save(options);
   }

   public static void saveModel(URI uri, OseeTypeModel model, OutputStream outputStream) throws IOException {
      OseeTypesStandaloneSetup.doSetup();
      ResourceSet resourceSet = new ResourceSetImpl();
      Resource resource = resourceSet.createResource(uri);
      resource.getContents().add(model);

      Map<String, Boolean> options = new HashMap<String, Boolean>();
      options.put(XtextResource.OPTION_FORMAT, Boolean.TRUE);
      resource.save(outputStream, options);
   }
}
