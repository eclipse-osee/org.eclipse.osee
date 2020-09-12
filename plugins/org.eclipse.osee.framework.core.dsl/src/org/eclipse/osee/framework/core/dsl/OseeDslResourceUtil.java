/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.core.dsl;

import com.google.inject.Injector;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.osee.framework.core.dsl.internal.OseeDslResourceImpl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

/**
 * @author Roberto E. Escobar
 */
public final class OseeDslResourceUtil {

   private OseeDslResourceUtil() {
      // Utility Class
   }

   public static OseeDslResource loadModelUnchecked(String uri, InputStream xTextData) throws Exception {
      OseeDslStandaloneSetup setup = new OseeDslStandaloneSetup();
      Injector injector = setup.createInjectorAndDoEMFRegistration();
      XtextResourceSet set = injector.getInstance(XtextResourceSet.class);

      set.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
      Resource resource = set.createResource(URI.createURI(uri));
      Map<Object, Object> loadOptions = set.getLoadOptions();
      resource.load(xTextData, loadOptions);

      OseeDslResource displayLogicResource = new OseeDslResourceImpl(resource);
      return displayLogicResource;
   }

   public static OseeDslResource loadModelUnchecked(String uri, String xTextData) throws Exception {
      return loadModelUnchecked(uri, new ByteArrayInputStream(xTextData.getBytes("UTF-8")));
   }

   public static OseeDslResource loadModel(String uri, String xTextData) throws Exception {
      OseeDslResource displayLogicResource = loadModelUnchecked(uri, xTextData);
      checkErrorsEmpty(uri, displayLogicResource.getErrors());
      return displayLogicResource;
   }

   public static OseeDslResource loadModel(String uri, InputStream xTextData) throws Exception {
      OseeDslResource displayLogicResource = loadModelUnchecked(uri, xTextData);
      checkErrorsEmpty(uri, displayLogicResource.getErrors());
      return displayLogicResource;
   }

   public static void saveModel(OseeDslResource dslResource, OutputStream outputStream, boolean compressOnSave) throws Exception {
      if (dslResource instanceof OseeDslResourceImpl) {
         OseeDslResourceImpl resourceImpl = (OseeDslResourceImpl) dslResource;
         Resource resource = resourceImpl.getResource();
         saveResource(resource, outputStream, compressOnSave);
      } else {
         throw new IllegalArgumentException(
            String.format("Unsupported dslResource class [%s]", dslResource != null ? dslResource.getClass() : "null"));
      }
   }

   public static void saveModel(OseeDsl model, String uri, OutputStream outputStream, boolean compressOnSave) throws Exception {
      OseeDslStandaloneSetup.doSetup();

      ResourceSet resourceSet = new XtextResourceSet();
      Resource resource = resourceSet.createResource(URI.createURI(uri));
      resource.getContents().add(model);

      saveResource(resource, outputStream, compressOnSave);
   }

   private static void saveResource(Resource resource, OutputStream outputStream, boolean compressOnSave) throws Exception {
      Map<String, Boolean> options = new HashMap<>();
      if (compressOnSave) {
         options.put(Resource.OPTION_ZIP, Boolean.TRUE);
      }
      SaveOptions saveOptions = SaveOptions.getOptions(options);
      resource.save(outputStream, saveOptions.toOptionsMap());
   }

   private static void checkErrorsEmpty(String uri, Collection<String> errors) throws Exception {
      if (errors != null && !errors.isEmpty()) {
         throw new IllegalStateException(
            String.format("Error loading Orcs Types Model from uri [%s]\n\n%s", uri, errors.iterator().next()));
      }
   }

}
