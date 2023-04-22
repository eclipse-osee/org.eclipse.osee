/*********************************************************************
 * Copyright (c) 2020, 2022 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Interface for the map used to store {@link RendererOption} keys and values for {@link IRenderer} implementations.
 *
 * @author Loren K. Ashley
 */

public interface RendererMap extends ToMessage {

   /**
    * Returns an empty unmodifiable {@link RendererMap}.
    *
    * @return an empty {@link RendererMap}.
    */

   public static RendererMap of() {
      return UnmodifiableRendererMap.emptyRendererMap;
   }

   /**
    * Creates an unmodifiable {@link RendererMap} with the specified keys and values.
    *
    * @param objects any number of key value pairs. The number of arguments must be even.
    * @return an unmodifiable {@link RendererMap}.
    * @throws IllegalArgumentException when:
    * <ul>
    * <li>the number of arguments is odd, or</li>
    * <li>any of the values are not appropriate for the associated keys.</li>
    * </ul>
    */

   public static RendererMap of(Object... objects) {
      //@formatter:off
      return
         ( Objects.isNull( objects ) || ( objects.length == 0 ) )
            ? UnmodifiableRendererMap.emptyRendererMap
            : new UnmodifiableRendererMap(objects);
      //@formatter:on
   }

   /**
    * Creates an unmodifiable {@link RendererMap} with the mappings form the specified <code>rendererMap</code>. Only
    * the entries with {@link RendererOption} keys whose associated {@link OptionType} indicates that the value is
    * copyable will be copied.
    *
    * @param rendererMap the {@link RendererMap} to copy the mappings from.
    * @return an unmodifiable {@link RendererMap}.
    */

   public static RendererMap of(RendererMap rendererMap) {
      //@formatter:off
      return
         Objects.isNull( rendererMap )
            ? UnmodifiableRendererMap.emptyRendererMap
            : new UnmodifiableRendererMap(rendererMap);
      //@formatter:on
   }

   /**
    * Frees the internal storage of the {@link RendererMap}. The {@link RendererMap} will be unusable after this method
    * has been called.
    */

   void free();

   /**
    * Gets a renderer option. If an explicit value has not been set for the renderer, a default value is returned.
    *
    * @implNode Used by {@link AtsOpenWithTaskRenderer}, {@link OpenUsingRenderer}, {@link WordTemplateFileDiffer},
    * {@link AbstractWordCompare}, {@link MSWordTempateClientRenderer}, {@link WordTemplateProcessorClient}
    * @param key the {@link RendererOption} to get.
    * @return the value of the {@link RendererOption} specified by <code>key</code>.
    * @throws NullPointerException when the <code>key</code> is <code>null</code>.
    */

   <T> T getRendererOptionValue(RendererOption key);

   /**
    * A predicate to test if a {@link RendererOption} is set.
    *
    * @param key the {@link RendererOption} to be tested.
    * @return <code>true</code> when the {@link RendererOption} has been set; otherwise, <code>false</code>.
    * @throws NullPointerException when the <code>key</code> is <code>null</code>.
    * @throws IllegalArgumentException when the <code>key</code> is not for a {@link Boolean} {@link RendererOption}.
    */

   boolean isRendererOptionSet(RendererOption key);

   /**
    * A predicate to test if a {@link RendererOption} is set and set to false. An unset {@link RendererOption} will
    * result in a result of false.
    *
    * @param key the {@link RendererOption} to test.
    * @return <code>true</code>, when the {@link RendererOption} is set and set to false; otherwise, <code>false</code>.
    * @throws NullPointerException when the <code>key</code> is <code>null</code>.
    * @throws IllegalArgumentException when the <code>key</code> is not for a {@link Boolean} {@link RendererOption}.
    */

   boolean isRendererOptionSetAndFalse(RendererOption key);

   /**
    * A predicate to test if a {@link RendererOption} is set and set to true. An unset {@link RendererOption} will
    * result in a result of false.
    *
    * @param key the {@link RendererOption} to test.
    * @return <code>true</code>, when the {@link RendererOption} is set and set to true; otherwise, <code>false</code>.
    * @throws NullPointerException when the <code>key</code> is <code>null</code>.
    * @throws IllegalArgumentException when the <code>key</code> is not for a {@link Boolean} {@link RendererOption}.
    */

   boolean isRendererOptionSetAndTrue(RendererOption key);

   /**
    * Gets an unmodifiable {@link Set} view of the {@link RendererOption} keys in the map. The set is backed by the map
    * and changes to the map will be reflected in the set.
    *
    * @return a {@link Set} view of the {@link RendererOption} keys in the map.
    */

   Set<RendererOption> keySet();

   /**
    * Removes the {@link RendererOption} entry specified by <code>key</code> from the {@link RendererMap}.
    *
    * @param key the {@link RendererOption} to remove from the {@link RendererMap}.
    * @return when there is an association with the <code>key</code>, the current value associated with the
    * <code>key</code>; otherwise, <code>null</code>.
    * @throws NullPointerException when the parameter <code>key</code> is <code>null</code>.
    */

   <T> T removeRendererOption(RendererOption key);

   /**
    * Sets the value of a {@link RendererOption} in the map.
    *
    * @param <T> the type of the provided <code>value</code>.
    * @param key the {@link RendererOption} to bet set.
    * @param value the <code>value</code> to associate with the {@link RendererOption}.
    * @return when there is an association with the <code>key</code>, the current value associated with the
    * <code>key</code>; otherwise, <code>null</code>.
    * @throws NullPointerException when either of the parameters <code>key</code> or <code>value</code> are
    * <code>null</code>.
    * @throws IllegalArgumentException when the <code>value</code> is not of the appropriate class for the
    * {@link RendererOption} it is being associated with.
    */

   <T> T setRendererOption(RendererOption key, T value);

   /**
    * Return an unmodifiable view of the {@link RendererMap}. The returned {@link RendererMap} is backed by the original
    * {@link RendererMap} and changes in the original {@link RendererMap} will be reflected in the returned unmodifiable
    * {@link RendererMap}.
    *
    * @return an unmodifiable view of the {@link RendererMap}.
    */

   RendererMap unmodifiableRendererMap();

}

/* EOF */
