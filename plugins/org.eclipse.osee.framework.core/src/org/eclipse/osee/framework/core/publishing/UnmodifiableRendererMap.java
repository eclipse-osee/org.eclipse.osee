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

package org.eclipse.osee.framework.core.publishing;

import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * Package private class provide an unmodifiable implementation of the {@link RendererMap} interface.
 *
 * @author Loren K. Ashley
 */

final class UnmodifiableRendererMap extends EnumRendererMap {

   /**
    * Saves an empty unmodifiable {@link RendererMap} to be returned by the {@link #empty} method. The write methods all
    * throw an {@link UnsupportedOperationException}.
    */

   static RendererMap emptyRendererMap = new RendererMap() {

      @Override
      public void free() {
         throw new UnsupportedOperationException();
      }

      @Override
      public <T> T getRendererOptionValue(RendererOption key) {
         return null;
      }

      @Override
      public boolean isRendererOptionSet(RendererOption key) {
         return false;
      }

      @Override
      public boolean isRendererOptionSetAndFalse(RendererOption key) {
         return false;
      }

      @Override
      public boolean isRendererOptionSetAndTrue(RendererOption key) {
         return false;
      }

      @Override
      public Set<RendererOption> keySet() {
         return Set.of();
      }

      @Override
      public <T> T removeRendererOption(RendererOption key) {
         throw new UnsupportedOperationException();
      }

      @Override
      public <T> T setRendererOption(RendererOption key, T value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Message toMessage(int indent, Message message) {
         return new Message().title("Unmodifiable Empty RendererMap");
      }

      @Override
      public RendererMap unmodifiableRendererMap() {
         return this;
      }

   };

   /**
    * Creates a new {@link UnmodifiableRendererMap} with the specified keys and values.
    *
    * @param objects any number of key value pairs. The number of arguments must be even.
    * @return the new {@link UnmodifiableRendererMap}.
    * @throws IllegalArgumentException when:
    * <ul>
    * <li>the number of arguments is odd, or</li>
    * <li>any of the values are not appropriate for the associated keys.</li>
    * </ul>
    */

   UnmodifiableRendererMap(Object... objects) {
      super(objects);
   }

   /**
    * Creates a new {@link UnmodifiableRendererMap} with the entries from <code>rendererMap</code> that are allowed to
    * be copied. The {@link OptionType} associated with each {@link RendererOption} indicates if the value for that
    * {@link RendererOption} is allowed to be copied.
    *
    * @param rendererMap the map of {@link RendererOption} and value to be copied.
    * @throws IllegalArgumentException if any values in the <code>rendererMap</code> are not allowed for the
    * {@link RendererOption} they are associated with.
    */

   UnmodifiableRendererMap(RendererMap rendererMap) {
      super(rendererMap);
   }

   /**
    * Overloaded method to prevent modification of the map.
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public void free() {
      throw new UnsupportedOperationException();
   }

   /**
    * Overloaded method to prevent modification of the map.
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public <T> T setRendererOption(RendererOption key, T value) {
      throw new UnsupportedOperationException();
   }

}

/* EOF */
