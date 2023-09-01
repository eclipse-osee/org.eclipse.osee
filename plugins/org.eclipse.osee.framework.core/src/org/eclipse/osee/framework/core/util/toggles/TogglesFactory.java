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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * Creates implementations of the {@link Toggle} interface for either client or server code. The class creates toggles
 * of the following types:
 * <dl>
 * <dt>Data Base Toggles</dt>
 * <dd>The toggle values are read from the configuration area of the data base.</dd>
 * <dt>Manifest Unloaded Configuration Toggles</dt>
 * <dd>The toggle values are obtained from the "Unloaded-Configuration" section of the visible bundle manifest files.
 * Bundles do not have to be activated to have their "unloaded-Configuration" section read.</dd>
 * </dl>
 *
 * @author Loren K. Ashley
 */

public class TogglesFactory {

   /**
    * Enumeration of detected code execution locations.
    */

   private enum Location {

      /**
       * A client specific bundle was detected.
       */

      CLIENT,

      /**
       * A server specific bundle was detected.
       */

      SERVER,

      /**
       * Failed to detect the execution location as client or server.
       */

      UNKNOWN;
   }

   /**
    * A functional interface for a method that produces a new {@link Toggle} instance.
    *
    * @param <T> the type of value returned by the created toggle.
    */

   @FunctionalInterface
   private interface ToggleFactory<T> {

      /**
       * Creates a new {@link Toggle} that gets the value of the toggle specified by <code>name</code> that has been
       * converted to an object of type &lt;T&gt; with the <code>converter</code> {@link Function}.
       *
       * @param name the name associated with the toggle value that is returned by the {@link Toggle}.
       * @param converter a {@link Function} used to convert the {@link String} toggle value into an object of type
       * &lt;T&gt;.
       * @return a {@link Toggle} that returns the {@link String} value of the toggle specified by <code>name</code>
       * that has been converted with the <code>converter</code> {@link Function} into an object of type &lt;T&gt;.
       */

      Toggle<T> create(String name, Function<String, T> converter);
   }

   /**
    * Enumeration used to select the toggle's data source.
    */

   public enum ToggleSource {

      /**
       * The toggle source is key value pairs read from the visible bundle manifest files.
       * {@link TogglesFactory#manifestHeaderName} specifies the name of the manifest header the key value pairs are
       * read from. Each pair is separated with a ',' and the keys are separated from the values with a ';'. Bundle
       * manifest toggles can be obtained as soon as a bundle begins execution.
       */

      BUNDLE_MANIFEST(TogglesFactory.manifestUnloadedConfigurationToggleFactory::apply),

      /**
       * The toggle source is key value pairs read from the configuration area of the data base. Data base toggles are
       * not available on the client until communication with the server has been established. Data base toggles are not
       * available on the server until communication with the data base has been established.
       */

      DATA_BASE(TogglesFactory.dataBaseToggleFactory::apply);

      /**
       * Saves the {@link ToggleFactory} for {@link Toggle} implementations of the kind indicated by the enumeration
       * member.
       */

      private final ToggleFactory<?> toggleFactory;

      /**
       * Creates a new enumeration member and saves the associated {@link ToggleFactory}.
       *
       * @param <T> the type of value returned by the {@link Toggle} implementations created by the factory.
       * @param toggleFactory the {@link ToggleFactory} for that produces the {@link Toggle} implementations of the kind
       * indicated by the enumeration member.
       */

      private <T> ToggleSource(ToggleFactory<T> toggleFactory) {
         this.toggleFactory = toggleFactory;
      }

      /**
       * Uses the {@link ToggleFactory} associated with the enumeration member to create a new {@link Toggle} instance.
       *
       * @param <T> the type of objects returned by the created {@link Toggle}.
       * @param name the key for the toggle value to be retrieved.
       * @param converter the {@link Function} used to convert the toggle's {@link String} value into the {@link Toggle}
       * return type &lt;T&gt;.
       * @return a {@link Toggle} implementation that returns the value of the toggle associated with <code>name</code>
       * using the provided <code>toggleAccessor</code> and <code>converter</code>.
       */

      public <T> Toggle<T> create(String name, Function<String, T> converter) {
         @SuppressWarnings("unchecked")
         var toggle = ((ToggleFactory<T>) this.toggleFactory).create(name, converter);
         return toggle;
      }
   }

   /**
    * A {@link Function} lambda reference to the {@link TogglesFactory#booleanConverter(String)} method.
    */

   public static final Function<String, Boolean> booleanConverter = TogglesFactory::booleanConverter;

   /**
    * The name of the bundle used to detect client operations. The named bundle must contain the class specified by
    * {@link TogglesFactory#clientDataBaseToggleAccessorFactoryClass}.
    */

   private static final String clientBundleName = "org.eclipse.osee.framework.core.client";

   /**
    * The name of the class in the bundle specified by {@link TogglesFactory#clientBundleName} that implements the
    * method {@link TogglesFactory#dataBaseToggleAccessorFactoryMethod}.
    */

   private static final String clientDataBaseToggleAccessorFactoryClass =
      "org.eclipse.osee.framework.core.client.TogglesClientImpl";

   /**
    * The name of the method used to obtain a {@link ToggleAccessor} implementation for access to data base toggles.
    * This method must be implemented by the classes {@link TogglesFactory#clientDataBaseToggleAccessorFactoryClass} and
    * {@link TogglesFactory#serverDataBaseToggleAccessorFactoryClass}.
    */

   private static final String dataBaseToggleAccessorFactoryMethod = "getDataBaseToggleAccessor";

   /**
    * Saves the OSGI {@link Bundle} containing the method used to obtain a {@link ToggleAccessor} implementation for
    * access to data base toggles.
    */

   private static final Bundle databaseToggleAccessorImplementationBundle;

   /**
    * The {@link TogglesFactory#dataBaseToggleFactory} encapsulates the {@link ToggleAccessor} implementation for access
    * to data base toggles. The {@link ToggleAccessor} value is initially <code>null</code>. The following steps are
    * taken when the {@link SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction#apply} function is
    * invoked:
    * <dl>
    * <dt>When the {@link ToggleAccessor} value is <code>null</code>:</dt>
    * <dd>A synchronized block is entered and the <code>initializationAction</code>
    * ({@link TogglesFactory#createDataBaseToggleAccessor}) method is used to create and save a {@link ToggleAccessor}.
    * The synchronized block is exited and the <code>setAction</code> ({@link TogglesFactory#create}) method is invoked
    * to produce a {@link Toggle}.</dd>
    * <dt>When the {@link ToggleAccessor} value is non-<code>null</code>:
    * <dt>
    * <dd>The <code>setAction</code> ({@link TogglesFactory#create}) method is invoked in an un-synchronized manner to
    * produce a {@link Toggle}.</dd>
    * </dl>
    */

   //@formatter:off
   private static final SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction<ToggleAccessor,String,Function<String,?>,Toggle<?>> dataBaseToggleFactory =
      new SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction<>
             (
                ToggleAccessor.class,
                TogglesFactory::create,
                TogglesFactory::createDataBaseToggleAccessor
             );
   //@formatter:on

   /**
    * A default {@link ToggleAccessor} implementation that always returns an empty string for when a toggle is not
    * defined.
    */

   //@formatter:off
   private static final ToggleAccessor defaultToggleAccessor =
      new ToggleAccessor() {

      @Override
      public String getToggle( String name ) {
         return Strings.emptyString();
      }

      @Override
      public String toString() {
         return "defaultToggleAccessor";
      }
   };
   //@formatter:on

   /**
    * Map of implementation class names by bundle names. The map contains an entry for the server bundle that contains
    * the toggle implementation and an entry for the client bundle. Only one or the other bundle is expected to be found
    * by the server or client.
    */

   //@formatter:off
   private static final Map<String,String> implementationClasses =
      Map.of
         (
            TogglesFactory.clientBundleName, TogglesFactory.clientDataBaseToggleAccessorFactoryClass,
            TogglesFactory.serverBundleName, TogglesFactory.serverDataBaseToggleAccessorFactoryClass
         );
    //@formatter:on

   /**
    * Saves the detected location of the executing code.
    */

   //@formatter:off
   private static final TogglesFactory.Location location;

   /**
    * Detection bundle name to {@link Location} map.
    */

   //@formatter:off
   private static final Map<String,Location> locationMap =
      Map.of
      (
         TogglesFactory.clientBundleName, Location.CLIENT,
         TogglesFactory.serverBundleName, Location.SERVER
      );
   //@formatter:on

   /**
    * The name of the manifest header to read the unloaded configuration toggle names and values from.
    */

   private static final String manifestHeaderName = "Unloaded-Configuration";

   /**
    * The {@link TogglesFactory#manifestUnloadedConfigurationToggleFactory} encapsulates the {@link ToggleAccessor}
    * implementation for access to data base toggles. The {@link ToggleAccessor} value is initially <code>null</code>.
    * The following steps are taken when the
    * {@link SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction#apply} function is invoked:
    * <dl>
    * <dt>When the {@link ToggleAccessor} value is <code>null</code>:</dt>
    * <dd>A synchronized block is entered and the <code>initializationAction</code>
    * ({@link TogglesFactory#createManifestUnloadedConfigurationToggleAccessor}) method is used to create and save a
    * {@link ToggleAccessor}. The synchronized block is exited and the <code>setAction</code>
    * ({@link TogglesFactory#create}) method is invoked to produce a {@link Toggle}.</dd>
    * <dt>When the {@link ToggleAccessor} value is non-<code>null</code>:
    * <dt>
    * <dd>The <code>setAction</code> ({@link TogglesFactory#create}) method is invoked in an un-synchronized manner to
    * produce a {@link Toggle}.</dd>
    * </dl>
    */

   //@formatter:off
   private static final SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction<ToggleAccessor,String,Function<String,?>,Toggle<?>> manifestUnloadedConfigurationToggleFactory =
      new SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction<>
             (
                ToggleAccessor.class,
                TogglesFactory::create,
                TogglesFactory::createManifestUnloadedConfigurationToggleAccessor
             );
   //@formatter:on

   /**
    * A {@link Map} of the toggle names and values read from the {@link TogglesFactory#manifestHeaderName} headers.
    */

   private static final Map<String, String> manifestUnloadedConfigurationTogglesMap;

   /**
    * Then name of the bundle used to detect server operations. The named bundle must contain the class specified by
    * {@link TogglesFactory#serverDataBaseToggleAccessorFactoryClass}.
    */

   private static final String serverBundleName = "org.eclipse.osee.define";

   /**
    * The name of the class in the bundle specified by {@link TogglesFactory#serverBundleName} that implements the
    * method {@link TogglesFactory#dataBaseToggleAccessorFactoryMethod}.
    */

   private static final String serverDataBaseToggleAccessorFactoryClass =
      "org.eclipse.osee.define.operations.toggles.TogglesOperationsImpl";

   /**
    * A {@link Function} lambda reference to the {@link TogglesFactory#stringConverter(String)} method.
    */

   public static final Function<String, String> stringConverter = TogglesFactory::stringConverter;

   /**
    * Searches for a bundle amongst all the bundles visible from the context of the bundle containing this class for
    * bundles with names that are keys in the {{@link #implementationClasses} {@link Map}. The manifest files of all
    * visible bundles are scanned for {@link TogglesFactory#manifestHeaderName} headers. When found those header values
    * are parsed into toggle keys and values; and added to the
    * {@link TogglesFactory#manifestUnloadedConfigurationTogglesMap} {@link Map}. Upon completion of the scan the
    * {@link TogglesFactory#manifestUnloadedConfigurationTogglesMap} {@link Map} is locked to prevent further
    * modifications.
    *
    * @param bundleNames the {@link Set} of bundle names to search for.
    * @return on success a {@link List} of bundles with name matching those in the <code>bundleNames</code> set;
    * otherwise, an empty {@link List}.
    */

   static {

      var togglesMap = new HashMap<String, String>();
      var bundleNameSet = TogglesFactory.implementationClasses.keySet();

      Bundle foundBundle = null;

      try {
         //@formatter:off
         var foundBundles =
            Arrays.stream( FrameworkUtil.getBundle(TogglesFactory.class).getBundleContext().getBundles() )
               .peek( ( bundle ) -> TogglesFactory.getUnloadedConfigurationValues( bundle, togglesMap ) )
               .filter( ( bundle ) -> bundleNameSet.contains( bundle.getSymbolicName() ) )
               .collect( Collectors.toList() );
         //@formatter:on

         if (foundBundles.size() == 1) {
            foundBundle = foundBundles.get(0);
         }

      } catch (Exception e) {
         //eat it
      }

      manifestUnloadedConfigurationTogglesMap = Collections.unmodifiableMap(togglesMap);

      databaseToggleAccessorImplementationBundle = foundBundle;

      //@formatter:off
      location =
         Optional
            .ofNullable( foundBundle )
            .map( ( bundle ) -> TogglesFactory.locationMap.get( bundle.getSymbolicName() ) )
            .orElse( TogglesFactory.Location.UNKNOWN );
      //@formatter:on

   }

   /**
    * A converter for getting toggle values as a {@link Boolean}.
    *
    * @param value the toggle's string value.
    * @return when the <code>value</code> contains &quot;true&quot; in any case a <code>true</code> {@link Boolean};
    * otherwise, a <code>false</code> {@link Boolean}.
    */

   private static Boolean booleanConverter(String value) {
      return Boolean.valueOf(value);
   }

   /**
    * Internal factory method to produce new {@link Toggle} implementations.
    *
    * @param <T> the type of objects returned by the {@link Toggle}.
    * @param toggleAccessor the {@link ToggleAccessor} to be used by the {@link Toggle} to get the toggle value.
    * @param name the key for the toggle value to be retrieved.
    * @param converter the {@link Function} implementation used to convert the toggle's string value into the return
    * type &lt;T&gt;.
    * @return a {@link Toggle} implementation that returns the value of the toggle associated with <code>name</code>
    * using the provided <code>toggleAccessor</code> and <code>converter</code>.
    */

   private static <T> Toggle<T> create(ToggleAccessor toggleAccessor, String name, Function<String, T> converter) {
      //@formatter:off
      return
         new Toggle<T>() {

            @Override
            public T get() {
               return converter.apply(toggleAccessor.getToggle(name));
            }

            @Override
            public String toString() {
               //@formatter:off
               return
                  new Message()
                         .title( "Toggle" )
                         .indentInc()
                         .segment( "Name",     name           )
                         .segment( "Accessor", toggleAccessor )
                         .toString();
               //@formatter:on
         }
      };
      //@formatter:on
   }

   /**
    * Loads the client or server class containing the data base {@link ToggleAccessor} factory method and invokes the
    * method.
    *
    * @return on success a {@link ToggleAccessor} for accessing data base toggles; otherwise, the
    * {@link TogglesFactory#defaultToggleAccessor}.
    */

   private static ToggleAccessor createDataBaseToggleAccessor() {

      //@formatter:off
      return
         Optional.ofNullable( TogglesFactory.databaseToggleAccessorImplementationBundle )
            .flatMap( TogglesFactory::loadToggleAccessorFactoryClass )
            .flatMap( TogglesFactory::invokeToggleAccessorFactoryMethod )
            .orElse( TogglesFactory.defaultToggleAccessor );
      //@formatter:on
   }

   /**
    * Creates a {@link ToggleAccessor} for access to the {@link TogglesFactory#manifestUnloadedConfigurationTogglesMap}.
    *
    * @return on success {@link ToggleAccessor} for access to the
    * {@link TogglesFactory#manifestUnloadedConfigurationTogglesMap}; otherwise, the
    * {@link TogglesFactory#defaultToggleAccessor}.
    */

   private static ToggleAccessor createManifestUnloadedConfigurationToggleAccessor() {

      //@formatter:off
      return
         ( TogglesFactory.manifestUnloadedConfigurationTogglesMap.size() >= 0 ) //<-- Triggers static initialization
            ? new ToggleAccessor() {
                 @Override
                 public String getToggle(String name) {
                    return TogglesFactory.manifestUnloadedConfigurationTogglesMap.get(name);
                 }

                 @Override
                 public String toString() {
                    return "manifestUnloadedConfigurationToggleAccessor";
                 }
              }
         : TogglesFactory.defaultToggleAccessor;
      //@formatter:on
   }

   /**
    * Gets the determined location of the running code.
    *
    * @return on success the detected location is either {@link TogglesFactory.Location#CLIENT} or
    * {@link TogglesFactory.Location#SERVER}; otherwise, {@link TogglesFactory.Location#UNKNOWN}.
    */

   public static Location getLocation() {
      return TogglesFactory.location;
   }

   /**
    * Determines if running as a server or as a client and loads the appropriate {@link ToggleAccessor} implementation.
    *
    * @param <T> the type of object returned by the obtained {@link Toggle}.
    * @param name the name of the toggle.
    * @param converter a {@link Function} that takes the toggle string value and converts to a value of type &lt;T&gt;
    * @param toggleSource a {@link ToggleSource} member to specify the source of the toggle.
    * @return an implementation of the {@link ToggleAccessor} interface for the client or the server.
    */

   public static <T> Toggle<T> create(String name, Function<String, T> converter, ToggleSource toggleSource) {
      return toggleSource.create(name, converter);
   }

   /**
    * Scans the manifest file of the <code>bundle</code> for a {@link TogglesFactory#manifestHeaderName} header. When
    * found that header's value is parsed into toggle keys and values; and added to the
    * {@link TogglesFactory#manifestUnloadedConfigurationTogglesMap} {@link Map}.
    *
    * @param bundle the {@link Bundle} to scan the manifest file of.
    */

   private static void getUnloadedConfigurationValues(Bundle bundle, Map<String, String> map) {

      if (Objects.isNull(bundle)) {
         return;
      }

      var manifestHeaders = bundle.getHeaders();

      if (Objects.isNull(manifestHeaders) || manifestHeaders.isEmpty()) {
         return;
      }

      var headerValue = manifestHeaders.get(TogglesFactory.manifestHeaderName);

      if (Strings.isInvalidOrBlank(headerValue)) {
         return;
      }

      var toggles = headerValue.split(",");

      for (var toggle : toggles) {

         if (Strings.isInvalidOrBlank(toggle)) {
            continue;
         }

         var parts = toggle.split(";");

         if (parts.length != 2) {
            continue;
         }

         map.put(parts[0], parts[1]);
      }
   }

   /**
    * Invokes the {@link TogglesFactory#dataBaseToggleAccessorFactoryMethod} on the
    * <code>toggleAccessorFactoryCalss</code> to create a {@link ToggleAccessor} for access to data base toggles.
    *
    * @param toggleAccessorFactoryClass the loaded class containing the {@link ToggleAccessor} factory method.
    * @return on success an {@link Optional} containing a {@link ToggleAccessor} for access to data base toggles;
    * otherwise, an empty {@link Optional}.
    */

   private static Optional<ToggleAccessor> invokeToggleAccessorFactoryMethod(Class<?> toggleAccessorFactoryClass) {
      try {
         //@formatter:off
         return
            Optional.ofNullable
               (
                  (ToggleAccessor) toggleAccessorFactoryClass
                                      .getMethod( TogglesFactory.dataBaseToggleAccessorFactoryMethod )
                                      .invoke( toggleAccessorFactoryClass )
               );
         //@formatter:on
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Loads the {@link ToggleAccessor} implementation class for the bundle it was found in.
    *
    * @param bundle the {@link Bundle} with the {@link ToggleAccessor} implementation class.
    * @param className the name of the {@link ToggleAccessor} implementation class.
    * @return the binary class name of the implementation class.
    * @throws ClassNotFoundException when the class is not found.
    */

   private static Optional<Class<?>> loadClass(Bundle bundle, String className) {

      try {
         return Optional.ofNullable(bundle.adapt(BundleWiring.class).getClassLoader().loadClass(className));
      } catch (Exception e) {
         return Optional.empty();
      }

   }

   /**
    * Loads the class containing the {@link ToggleAccessor} class from the OSGI {@link Bundle} it was found in.
    *
    * @param bundle the OSGI {@link Bundle} the class containing the {@link ToggleAccessor} factory method was found in.
    * @return on success an {@link Optional} containing the loaded class; otherwise, an empty {@link Optional}.
    */

   private static Optional<Class<?>> loadToggleAccessorFactoryClass(Bundle bundle) {

      if (Objects.isNull(bundle)) {
         return Optional.empty();
      }

      var implementationClass = TogglesFactory.implementationClasses.get(bundle.getSymbolicName());

      if (Objects.isNull(implementationClass)) {
         return Optional.empty();
      }

      var toggleAccessorFactoryClassOptional = TogglesFactory.loadClass(bundle, implementationClass);

      return toggleAccessorFactoryClassOptional;

   }

   /**
    * A pass-through converter for getting toggle values as a {@link String}.
    *
    * @param value the toggle's string value.
    * @return the toggle's string value.
    */

   private static String stringConverter(String value) {
      return value;
   }

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private TogglesFactory() {
   }

}

/* EOF */
