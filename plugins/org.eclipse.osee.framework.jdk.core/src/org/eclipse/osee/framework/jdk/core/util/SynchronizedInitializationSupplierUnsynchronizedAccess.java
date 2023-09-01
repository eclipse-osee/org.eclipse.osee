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

package org.eclipse.osee.framework.jdk.core.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A value that is initialized with a synchronized {@link Supplier} on first access and accessed in an unsynchronized
 * manner. Once the value is initialized it's value cannot be changed.
 *
 * @author Loren K. Ashley
 * @param <T> the type of the encapsulated value.
 */

public class SynchronizedInitializationSupplierUnsynchronizedAccess<T> {

   /**
    * Saves the encapsulated value. Once set it cannot be changed.
    */

   private volatile T value;

   /**
    * A {@link VarHandle} reference for the encapsulated value. Used to access the encapsulated value with different
    * memory semantics.
    */

   private final VarHandle valueVarHandle;

   /**
    * Saves the {@link Supplier} used to get the value on first access.
    */

   private final Supplier<T> initializationAction;

   /**
    * Saves the internal {@link Supplier} implementation used to access the encapsulated value. When a {@link VarHandle}
    * is successfully created for the {@link #value} this is set to the method {@link #accessActionVarHandle};
    * otherwise, this is set to the method {@link #accessActionNoVarHandle}.
    */

   private final Supplier<T> internalAccessAction;

   /**
    * Creates a new {@link SynchronizedInitializationSupplierUnsynchronizedAccess} with an uninitialized value. If
    * creation of the {@link VarHandle} reference fails the implementation reverts to an implementation that relies upon
    * volatile memory semantics for the encapsulated value.
    *
    * @param tClass the class object for the encapsulated value.
    * @param initializationAction the {@link Supplier} implementation used to get the value's initial value.
    */

   public SynchronizedInitializationSupplierUnsynchronizedAccess(Class<T> tClass, Supplier<T> initializationAction) {

      this.value = null;

      VarHandle varHandle;

      try {
         //@formatter:off
         varHandle =
            MethodHandles
               .privateLookupIn
                  (
                     SynchronizedInitializationSupplierUnsynchronizedAccess.class,
                     MethodHandles.lookup()
                  )
               .findVarHandle
                  (
                     SynchronizedInitializationSupplierUnsynchronizedAccess.class,
                     "value",
                     tClass
                  );
         //@formatter:on
      } catch (Exception e) {
         varHandle = null;
      }

      this.valueVarHandle = varHandle;
      this.initializationAction = Objects.requireNonNull(initializationAction);
      this.internalAccessAction =
         Objects.nonNull(this.valueVarHandle) ? this::accessActionVarHandle : this::applyActionNoVarHandle;
   }

   /**
    * Gets the encapsulated value. On first access the <code>initializationAction</code> that was provided to the
    * constructor is called in a synchronized manner to obtain the initial value. When the encapsulated value has
    * already been set, the encapsulated value is returned without synchronization.
    *
    * @return the encapsulated value.
    */

   public T apply() {
      return this.internalAccessAction.get();
   }

   /**
    * The encapsulated value is checked with regular memory semantics to see if it has already been set. If not, the
    * encapsulated value is checked with volatile memory semantics to see if it has already been set. If not set a
    * synchronized block is entered. Upon entry to the synchronized block, the encapsulated value is checked once again
    * to see if it is still unset. When still unset, the <code>initializationAction</code> is invoked to obtain the
    * initial value.
    * <p>
    * When the encapsulated value is found to be set or was just set, the encapsulated value is returned without
    * synchronization.
    *
    * @return the encapsulated value.
    */

   private T accessActionVarHandle() {
      //@formatter:off
      if(    Objects.nonNull( this.valueVarHandle.get( this ) )
          || Objects.nonNull( this.valueVarHandle.getVolatile( this ) ) ) {
         return this.value;
      }
      //@formatter:on
      synchronized (this) {
         if (Objects.nonNull(this.valueVarHandle.getVolatile(this))) {
            return this.value;
         }
         this.value = this.initializationAction.get();
      }
      return this.value;
   }

   /**
    * The encapsulated value is checked with volatile memory semantics to see if it has already been set. If not set a
    * synchronized block is entered. Upon entry to the synchronized block, the encapsulated value is checked once again
    * to see if it is still unset. When still unset, the <code>initializationAction</code> is invoked to obtain the
    * initial value.
    * <p>
    * When the encapsulated value is found to be set or was just set, the encapsulated value is returned without
    * synchronization.
    *
    * @return the encapsulated value.
    */

   private T applyActionNoVarHandle() {
      if (Objects.nonNull(this.value)) {
         return this.value;
      }
      synchronized (this) {
         if (Objects.nonNull(this.value)) {
            return this.value;
         }
         this.value = this.initializationAction.get();
      }
      return this.value;
   }
}

/* EOF */
