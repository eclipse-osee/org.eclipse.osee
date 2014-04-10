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
package org.eclipse.osee.framework.core.dsl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.core.dsl.internal.OseeDslResourceImpl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import com.google.inject.Injector;

/**
 * @author Roberto E. Escobar
 */
public final class OseeDslResourceUtil {

   private OseeDslResourceUtil() {
      // Utility Class
   }

   public static OseeDslResource loadModelUnchecked(String uri, InputStream xTextData) throws Exception {
      xTextData = upConvertTo17(xTextData);
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
      xTextData = upConvertTo17(xTextData);
      return loadModelUnchecked(uri, new ByteArrayInputStream(xTextData.getBytes("UTF-8")));
   }

   public static InputStream upConvertTo17(InputStream inputStream) throws Exception {
      String typesStr = Lib.inputStreamToString(inputStream);
      typesStr = upConvertTo17(typesStr);
      return new ByteArrayInputStream(typesStr.getBytes("UTF-8"));
   }

   private static String upConvertTo17(String typesStr) throws Exception {
      typesStr = typesStr.replaceAll("branchGuid \"AyH_fAj8lhQGmQw2iBAA\"", "branchUuid 423");
      typesStr = typesStr.replaceAll("branchGuid \"AyH_e5wAblOqTdLkxqQA\"", "branchUuid 714");
      typesStr = typesStr.replaceAll("branchGuid \"GyoL_rFqqBYbOcuGYzQA\"", "branchUuid 4312");
      return typesStr;
   }

   public static OseeDslResource loadModel(String uri, String xTextData) throws Exception {
      xTextData = upConvertTo17(xTextData);
      OseeDslResource displayLogicResource = loadModelUnchecked(uri, xTextData);
      checkErrorsEmpty(displayLogicResource.getErrors());
      return displayLogicResource;
   }

   public static OseeDslResource loadModel(String uri, InputStream xTextData) throws Exception {
      OseeDslResource displayLogicResource = loadModelUnchecked(uri, xTextData);
      checkErrorsEmpty(displayLogicResource.getErrors());
      return displayLogicResource;
   }

   public static void saveModel(OseeDslResource dslResource, OutputStream outputStream, boolean compressOnSave) throws Exception {
      if (dslResource instanceof OseeDslResourceImpl) {
         OseeDslResourceImpl resourceImpl = (OseeDslResourceImpl) dslResource;
         Resource resource = resourceImpl.getResource();
         saveResource(resource, outputStream, compressOnSave);
      } else {
         throw new IllegalArgumentException(String.format("Unsupported dslResource class [%s]",
            dslResource != null ? dslResource.getClass() : "null"));
      }
   }

   public static void saveModel(OseeDsl model, String uri, OutputStream outputStream, boolean compressOnSave) throws Exception {
      OseeDslStandaloneSetup.doSetup();

      ResourceSet resourceSet = new ResourceSetImpl();
      Resource resource = resourceSet.createResource(URI.createURI(uri));
      resource.getContents().add(model);

      saveResource(resource, outputStream, compressOnSave);
   }

   private static void saveResource(Resource resource, OutputStream outputStream, boolean compressOnSave) throws Exception {
      Map<String, Boolean> options = new HashMap<String, Boolean>();
      if (compressOnSave) {
         options.put(Resource.OPTION_ZIP, Boolean.TRUE);
      }
      SaveOptions saveOptions = SaveOptions.getOptions(options);
      resource.save(outputStream, saveOptions.toOptionsMap());
   }

   private static void checkErrorsEmpty(Collection<String> errors) throws Exception {
      if (errors != null && !errors.isEmpty()) {
         throw new IllegalStateException(errors.iterator().next());
      }
   }

}
