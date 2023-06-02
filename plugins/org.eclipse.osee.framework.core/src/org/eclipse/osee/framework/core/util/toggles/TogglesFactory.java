/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.util.toggles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * Loads an implementation of the {@link Toggles} interface appropriate for whether the code is running on the server or
 * the client.
 *
 * @author Loren K. Ashley
 */

public class TogglesFactory {

   /**
    * Map of implementation class names by bundle names. The map contains an entry for the server bundle that contains
    * the toggle implementation and an entry for the client bundle. Only one or the other bundle is expected to be found
    * by the server or client.
    */

   //@formatter:off
   private static Map<String,String> implementationClasses =
      Map.of
      (
         "org.eclipse.osee.framework.skynet.core", "org.eclipse.osee.framework.skynet.core.httpRequests.TogglesClientImpl",
         "org.eclipse.osee.define",                "org.eclipse.osee.define.operations.toggles.TogglesOperationsImpl"
      );

   //@formatter:on

   /**
    * A default toggle implementation that always returns <code>false</code> for when a toggle is not defined.
    */

   //@formatter:off
   private static Toggles defaultToggles =
      new Toggles() {

      @Override
      public Boolean apply( String name ) {
         return false;
      }
   };
   //@formatter:on

   /**
    * Once the {@link Toggles} implementation has been found and loaded it is saved for future requests.
    */

   private static Toggles togglesImpl = null;

   /**
    * Searches for a bundle amongst all the bundles visible from the context of the bundle containing this class for
    * bundles with names that are keys in the {{@link #implementationClasses} {@link Map}.
    *
    * @param bundleNames the {@link Set} of bundle names to search for.
    * @return a {@link List} of bundles with name matching those in the <code>bundleNames</code> set.
    */

   private static List<Bundle> getBundles(Set<String> bundleNames) {

      //@formatter:off
      try {
      return
         Arrays.stream( FrameworkUtil.getBundle(TogglesFactory.class).getBundleContext().getBundles() )
            .filter
               (
                  ( bundle ) -> bundleNames.contains( bundle.getSymbolicName() )
               )
            .collect( Collectors.toList() );
      }
      catch(NullPointerException ext) {
         return null;
      }
      //@formatter:on

   }

   /**
    * Loads the {@link Toggles} implementation class for the bundle it was found in.
    *
    * @param bundle the {@link Bundle} with the {@link Toggles} implementation class.
    * @param className the name of the {@link Toggles} implementation class.
    * @return the binary class name of the implementation class.
    * @throws ClassNotFoundException when the class is not found.
    */
   private static Class<?> loadClass(Bundle bundle, String className) throws ClassNotFoundException {

      return bundle.adapt(BundleWiring.class).getClassLoader().loadClass(className);

   }

   /**
    * Determines if running as a server or as a client and loads the appropriate {@link Toggles} implementation.
    *
    * @return an implementation of the {@link Toggles} interface for the client or the server.
    */

   public static synchronized Toggles getTogglesImpl() {

      if (Objects.nonNull(TogglesFactory.togglesImpl)) {
         return TogglesFactory.togglesImpl;
      }

      try {

         var bundles = TogglesFactory.getBundles(TogglesFactory.implementationClasses.keySet());

         if (bundles.size() != 1) {
            return TogglesFactory.defaultToggles;
         }

         var bundle = bundles.get(0);
         var implementationClass = TogglesFactory.implementationClasses.get(bundle.getSymbolicName());

         var aClass = TogglesFactory.loadClass(bundle, implementationClass);

         TogglesFactory.togglesImpl = (Toggles) aClass.getMethod("create").invoke(null);

         return TogglesFactory.togglesImpl;

      } catch (Exception e) {
         return TogglesFactory.defaultToggles;
      }
   }
}

/* EOF */
