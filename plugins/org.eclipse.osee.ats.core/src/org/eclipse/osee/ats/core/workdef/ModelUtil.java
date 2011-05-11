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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.osee.ats.dsl.AtsDslStandaloneSetup;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import com.google.inject.Injector;

/**
 * @author Donald G. Dunne
 */
public final class ModelUtil {

   private ModelUtil() {
      // Utility Class
   }

   public static AtsDsl loadModel(InputStream inputStream, boolean isZipped) throws OseeCoreException {
      Injector injector = new AtsDslStandaloneSetup().createInjectorAndDoEMFRegistration();
      XtextResource resource = injector.getInstance(XtextResource.class);

      Map<String, Boolean> options = new HashMap<String, Boolean>();
      options.put(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
      if (isZipped) {
         options.put(Resource.OPTION_ZIP, Boolean.TRUE);
      }
      try {
         resource.setURI(URI.createURI("http://www.eclipse.org/osee/ats/dsl/AtsDsl"));
         resource.load(inputStream, options);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      AtsDsl model = (AtsDsl) resource.getContents().get(0);
      for (Diagnostic diagnostic : resource.getErrors()) {
         throw new OseeStateException(diagnostic.toString());
      }
      return model;
   }

   public static AtsDsl loadModel(String uri, String xTextData) throws OseeCoreException {
      try {
         AtsDslStandaloneSetup setup = new AtsDslStandaloneSetup();
         Injector injector = setup.createInjectorAndDoEMFRegistration();
         XtextResourceSet set = injector.getInstance(XtextResourceSet.class);

         //         set.setClasspathURIContext(ModelUtil.class);
         set.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

         Resource resource = set.createResource(URI.createURI(uri));
         resource.load(new ByteArrayInputStream(xTextData.getBytes("UTF-8")), set.getLoadOptions());
         AtsDsl model = (AtsDsl) resource.getContents().get(0);
         for (Diagnostic diagnostic : resource.getErrors()) {
            throw new OseeStateException(diagnostic.toString());
         }
         return model;
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   public static void saveModel(AtsDsl model, String uri, OutputStream outputStream, boolean isZipped) throws IOException {
      AtsDslStandaloneSetup.doSetup();

      ResourceSet resourceSet = new ResourceSetImpl();
      Resource resource = resourceSet.createResource(URI.createURI(uri));
      resource.getContents().add(model);

      Map<String, Boolean> options = new HashMap<String, Boolean>();
      //		options.put(XtextResource.OPTION_FORMAT, Boolean.TRUE);
      if (isZipped) {
         options.put(Resource.OPTION_ZIP, Boolean.TRUE);
      }
      SaveOptions saveOptions = SaveOptions.getOptions(options);
      resource.save(outputStream, saveOptions.toOptionsMap());
   }

   private static void storeModel(Resource resource, OutputStream outputStream, EObject object, String uri, Map<String, Boolean> options) throws OseeCoreException {
      try {
         resource.setURI(URI.createURI(uri));
         resource.getContents().add(object);
         resource.save(outputStream, options);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public static String modelToStringXML(EObject object, String uri, Map<String, Boolean> options) throws OseeCoreException {
      return modelToString(new XMLResourceImpl(), object, uri, options);
   }

   public static String modelToStringXText(EObject object, String uri, Map<String, Boolean> options) throws OseeCoreException {
      AtsDslStandaloneSetup setup = new AtsDslStandaloneSetup();
      Injector injector = setup.createInjectorAndDoEMFRegistration();
      Resource resource = injector.getInstance(XtextResource.class);
      Map<String, Boolean> options2 = new HashMap<String, Boolean>();
      options2.put(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
      return modelToString(resource, object, uri, options2);
   }

   private static String modelToString(Resource resource, EObject object, String uri, Map<String, Boolean> options) throws OseeCoreException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      storeModel(resource, outputStream, object, uri, options);
      try {
         return outputStream.toString("UTF-8");
      } catch (UnsupportedEncodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

}
