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

package org.eclipse.osee.orcs.script.dsl;

import static org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants.TIMESTAMP_FORMAT;
import static org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants.TIMEZONE_ID;
import com.google.inject.Injector;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;
import org.eclipse.osee.orcs.script.dsl.resource.OrcsScriptResourceImpl;
import org.eclipse.osee.orcs.script.dsl.typesystem.TemplateBinding;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.SaveOptions.Builder;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

/**
 * @author Roberto E. Escobar
 */
public final class OrcsScriptUtil {

   private OrcsScriptUtil() {
      // Utility Class
   }

   private static final int BINDINGS_DATA__RESOURCE_INDEX = 1;
   private static Injector INJECTOR_INSTANCE;

   public static enum OsStorageOption {
      FORMAT_ON_SAVE,
      NO_VALIDATION_ON_SAVE;
   }

   private static Injector getInjector() {
      if (INJECTOR_INSTANCE == null) {
         OrcsScriptDslStandaloneSetup setup = new OrcsScriptDslStandaloneSetup();
         INJECTOR_INSTANCE = setup.createInjectorAndDoEMFRegistration();
      }
      return INJECTOR_INSTANCE;
   }

   private static void initializeXtextRuntime() {
      getInjector();
   }

   public static IExpressionResolver getExpressionResolver() {
      return getInjector().getInstance(IExpressionResolver.class);
   }

   public static IFieldResolver getFieldResolver() {
      return getInjector().getInstance(IFieldResolver.class);
   }

   public static Map<String, Object> getBinding(EObject element) {
      Resource eResource = element.eResource();
      EList<EObject> contents = eResource.getContents();
      Map<String, Object> toReturn = null;
      if (BINDINGS_DATA__RESOURCE_INDEX < contents.size()) {
         EObject eObject = contents.get(BINDINGS_DATA__RESOURCE_INDEX);
         if (eObject != null) {
            TemplateBinding container = (TemplateBinding) eObject;
            toReturn = container.map();
         }
      }
      if (toReturn == null) {
         toReturn = Collections.emptyMap();
      }
      return toReturn;
   }

   public static void bind(EObject element, Map<String, Object> binding) {
      TemplateBinding container = new TemplateBinding();
      container.setValue(binding);

      Resource eResource = element.eResource();
      EList<EObject> contents = eResource.getContents();
      contents.add(BINDINGS_DATA__RESOURCE_INDEX, container);
   }

   public static void unbind(EObject element) {
      Resource eResource = element.eResource();
      EList<EObject> contents = eResource.getContents();
      if (BINDINGS_DATA__RESOURCE_INDEX < contents.size()) {
         contents.remove(BINDINGS_DATA__RESOURCE_INDEX);
      }
   }

   public static OrcsScriptDslResource loadModel(InputStream inputStream, String uri) {
      OrcsScriptDslResource resource = loadModelSafely(inputStream, uri);
      if (resource == null) {
         throw new OseeCoreException("Error loading resource [%s] - resource was null", uri);
      }
      for (String error : resource.getErrors()) {
         throw new OseeCoreException("Error loading resource [%s] - %s", uri, error);
      }
      return resource;
   }

   public static OrcsScriptDslResource loadModelSafely(InputStream inputStream, String uri) {
      OrcsScriptResourceImpl toReturn = null;
      try {
         XtextResourceSet set = getInjector().getInstance(XtextResourceSet.class);

         set.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
         Resource resource = set.createResource(URI.createURI(uri));
         Map<Object, Object> loadOptions = set.getLoadOptions();

         resource.load(inputStream, loadOptions);
         toReturn = new OrcsScriptResourceImpl(resource);
      } catch (Exception ex) {
         toReturn = new OrcsScriptResourceImpl(null);
         toReturn.error(ex, "Error loading [%s]", uri);
      }
      return toReturn;
   }

   public static void saveModel(OrcsScriptDslResource dslResource, OutputStream outputStream, OsStorageOption... options) {
      if (dslResource instanceof OrcsScriptResourceImpl) {
         OrcsScriptResourceImpl resourceImpl = (OrcsScriptResourceImpl) dslResource;
         Resource resource = resourceImpl.getResource();
         saveResource(resource, outputStream, options);
      } else {
         throw new OseeCoreException("Unsupported dslResource class [%s]",
            dslResource != null ? dslResource.getClass() : null);
      }
   }

   public static void saveModel(OrcsScript model, String uri, OutputStream outputStream, OsStorageOption... options) {
      if (model != null && model.eAllContents().hasNext()) {
         initializeXtextRuntime();
         URI storageUri = URI.createURI(uri);
         Resource resource = model.eResource();
         if (resource != null) {
            resource.setURI(storageUri);
         } else {
            ResourceSet resourceSet = new XtextResourceSet();
            resource = resourceSet.createResource(storageUri);
            resource.getContents().add(model);
         }
         saveResource(resource, outputStream, options);
      } else {
         // Nothing to save
      }
   }

   private static void saveResource(Resource resource, OutputStream outputStream, OsStorageOption... options) {
      Builder builder = SaveOptions.newBuilder();
      for (OsStorageOption option : options) {
         switch (option) {
            case FORMAT_ON_SAVE:
               builder.format();
               break;
            case NO_VALIDATION_ON_SAVE:
               builder.noValidation();
               break;
            default:
               break;
         }
      }
      SaveOptions saveOptions = builder.getOptions();
      try {
         resource.save(outputStream, saveOptions.toOptionsMap());
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Error saving resource [%s]", resource.getURI());
      }
   }

   public static Date parseDate(String rawValue) throws ParseException {
      Date toReturn = null;
      String unQuotedValue = unquote(rawValue);
      try {
         SimpleDateFormat fmt = new SimpleDateFormat(TIMESTAMP_FORMAT);
         fmt.setTimeZone(TimeZone.getTimeZone(TIMEZONE_ID));
         toReturn = fmt.parse(unQuotedValue);
      } catch (ParseException ex) {
         // Do nothing - try locale specific format
      }
      if (toReturn == null) {
         toReturn = DateFormat.getDateTimeInstance().parse(unQuotedValue);
      }
      return toReturn;
   }

   public static String asDateString(Date date) {
      SimpleDateFormat fmt = new SimpleDateFormat(TIMESTAMP_FORMAT);
      fmt.setTimeZone(TimeZone.getTimeZone(TIMEZONE_ID));
      return fmt.format(date);
   }

   public static String asQuotedDateString(Date date) {
      return quote(asDateString(date));
   }

   public static String quote(String value) {
      String toReturn = value;
      if (value != null) {
         StringBuilder builder = new StringBuilder();
         builder.append("\"");
         builder.append(value);
         builder.append("\"");
         toReturn = builder.toString();
      }
      return toReturn;
   }

   public static String unquote(String value) {
      String toReturn = value;
      if (toReturn != null) {
         int startAt = 0;
         int endAt = value.length();
         if (toReturn.startsWith("\"") || toReturn.startsWith("'")) {
            startAt = 1;
         }
         if (toReturn.endsWith("\"") || toReturn.endsWith("'")) {
            endAt = toReturn.length() - 1;
         }
         toReturn = toReturn.substring(startAt, endAt);
      }
      return toReturn;
   }

}
