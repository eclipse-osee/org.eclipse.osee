/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.util;

import java.lang.annotation.Annotation;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * Class of methods for finding classes in an OSGI bundle.
 *
 * @author Loren K. Ashley
 */

public class OsgiUtils {

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private OsgiUtils() {
   }

   /**
    * Gets the {@link String} value of an {@link Annotation} parameter. <code>null</code> is returned when the
    * {@link Annotation} method, <code>annotationKeyMethodName</code> does not exist or returns a non-{@link String}
    * value.
    *
    * @param annotationKeyMethodName the name of the {@link Annotation} method to use.
    * @param theAnnotation the {@link Annotation} to get the parameter value from.
    * @return on success, the value of the {@link Annotation} parameter; otherwise, <code>null</code>.
    */

   private static String getAnnotationKey(String annotationKeyMethodName, Annotation theAnnotation) {
      try {
         var key = theAnnotation.annotationType().getMethod(annotationKeyMethodName).invoke(theAnnotation);
         return (key instanceof String) ? (String) key : null;
      } catch (Exception e) {
         return null;
      }
   }

   /**
    * Finds all classes in the OSGI bundle that implement the specified interface that also have the specified
    * annotation.
    *
    * @param <A> The expected {@link Annotation} class that also implements the
    * {@link KeyedInterfaceImplementationAnnotation} interface.
    * @param <I> The expected interface implemented by implementations.
    * @param packagePath the class path to search for classes under.
    * @param annotationClass the {@link Class} object representing the expected annotation on implementors.
    * @param interfaceClass the {@link Class} object representing the expected interface implemented by implementors.
    * @return an immutable {@link Map} of the implementors by their annotation keys.
    */

   //@formatter:off
   public static <A extends Annotation, I>
      Map<String, Class<I>>
         findImplementations
            (
               String   packagePath,
               Class<A> annotationClass,
               String   annotationKeyMethodName,
               Class<I> interfaceClass
            )
   //@formatter:on
   {
      var implementationList = new ArrayList<Map.Entry<String, Class<I>>>();

      var bundleContext = FrameworkUtil.getBundle(OsgiUtils.class).getBundleContext();
      var bundle = bundleContext.getBundle();
      var bundleWiring = bundle.adapt(BundleWiring.class);
      var classLoader = bundleWiring.getClassLoader();
      var resources = bundleWiring.listResources(packagePath, "*.class", BundleWiring.LISTRESOURCES_RECURSE);

      resources.forEach(resource -> {
         try {
            var className = resource.substring(0, resource.indexOf('.')).replace('/', '.');
            var aClass = classLoader.loadClass(className);
            if (interfaceClass.isAssignableFrom(aClass)) {
               @SuppressWarnings("unchecked")
               var theImplementationClass = (Class<I>) aClass;
               var theAnnotation = theImplementationClass.getAnnotation(annotationClass);
               if (Objects.nonNull(theAnnotation)) {
                  var key = OsgiUtils.getAnnotationKey(annotationKeyMethodName, theAnnotation);
                  if (Objects.nonNull(key)) {
                     implementationList.add(new AbstractMap.SimpleImmutableEntry<>(key, theImplementationClass));
                  }
               }
            }
         } catch (Exception e) {
            /*
             * Eat exceptions. If the implementations are not found, an empty map will be returned.
             */
         }
      });

      return implementationList.stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
   }

}

/* EOF */
