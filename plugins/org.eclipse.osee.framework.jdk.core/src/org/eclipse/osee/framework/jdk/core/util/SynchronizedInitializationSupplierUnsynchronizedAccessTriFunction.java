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
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.eclipse.osee.framework.jdk.core.type.TriFunction;

/**
 * A value that is initialized with a synchronized {@link Supplier} on first access and accessed with an unsynchronized
 * {@link TriFunction} where the first parameter is the encapsulated value. Once the value is initialized it's value
 * cannot be changed.
 *
 * @author Loren K. Ashley
 * @param <T> the type of the encapsulated value.
 * @param <X> the type of the second <code>setAction</code> {@link TriFunction} parameter.
 * @param <Y> the type of the third <code>setAction</code> {@link TriFunction} parameter.
 * @param <R> the return type of the <code>setAction</code> {@link TriFunction}.
 */

public class SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction<T, X, Y, R> {

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
    * Saves the {@link TriFunction} implementation used to access the encapsulated value.
    */

   private final TriFunction<T, X, Y, R> accessAction;

   /**
    * Saves the {@link Supplier} used to get the value on first access.
    */

   private final Supplier<T> initializationAction;

   /**
    * Saves the internal {@link BiFunction} implementation used to access the encapsulated value. When a
    * {@link VarHandle} is successfully created for the {@link #value} this is set to the method
    * {@link #accessActionVarHandle}; otherwise, this is set to the method {@link #accessActionNoVarHandle}.
    */

   private final BiFunction<X, Y, R> internalAccessAction;

   /**
    * Creates a new {@link SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction} with an uninitialized
    * value. If creation of the {@link VarHandle} reference fails the implementation reverts to an implementation that
    * relies upon volatile memory semantics for the encapsulated value.
    *
    * @param tClass the class object for the encapsulated value.
    * @param accessAction the {@link TriFucntion} implementation used to access the value.
    * @param initializationAction the {@link Supplier} implementation used to get the value's initial value.
    */

   public SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction(Class<T> tClass, TriFunction<T, X, Y, R> accessAction, Supplier<T> initializationAction) {

      this.value = null;

      VarHandle varHandle;

      try {
         //@formatter:off
         varHandle =
            MethodHandles
               .privateLookupIn
                  (
                     SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction.class,
                     MethodHandles.lookup()
                  )
               .findVarHandle
                  (
                     SynchronizedInitializationSupplierUnsynchronizedAccessTriFunction.class,
                     "value",
                     tClass
                  );
         //@formatter:on
      } catch (Exception e) {
         varHandle = null;
      }

      this.valueVarHandle = varHandle;
      this.accessAction = Objects.requireNonNull(accessAction);
      this.initializationAction = Objects.requireNonNull(initializationAction);
      this.internalAccessAction =
         Objects.nonNull(this.valueVarHandle) ? this::accessActionVarHandle : this::accessActionNoVarHandle;
   }

   /**
    * Applies the <code>accessAction</code> that was provided to the constructor to the encapsulated value along with
    * the parameters <code>x</code> and <code>y</code>. On first access the <code>initializationAction</code> that was
    * provided to the constructor is called in a synchronized manner to obtain the initial value. When the encapsulated
    * value has already been set, the <code>accessAction</code> is applied without synchronization.
    *
    * @param x the value of the second parameter passed to the <code>accessAction</code>.
    * @param y the value of the third parameter passed to the <code>accessAction</code>.
    * @return the result of the <code>accessAction</code>.
    */

   public R apply(X x, Y y) {
      return this.internalAccessAction.apply(x, y);
   }

   /**
    * The encapsulated value is checked with regular memory semantics to see if it has already been set. If not, the
    * encapsulated value is checked with volatile memory semantics to see if it has already been set. If not set a
    * synchronized block is entered. Upon entry to the synchronized block, the encapsulated value is checked once again
    * to see if it is still unset. When still unset, the <code>initializationAction</code> is invoked to obtain the
    * initial value.
    * <p>
    * When the encapsulated value is found to be set or was just set, the <code>accessAction</code> is applied to the
    * encapsulated value and the parameters <code>x</code> and <code>y</code>.
    *
    * @param x the value of the second parameter passed to the <code>accessAction</code>.
    * @param y the value of the third parameter passed to the <code>accessAction</code>.
    * @return the result of the <code>accessAction</code>.
    */

   private R accessActionVarHandle(X x, Y y) {
      //@formatter:off
      if(    Objects.nonNull( this.valueVarHandle.get( this ) )
          || Objects.nonNull( this.valueVarHandle.getVolatile( this ) ) ) {
         return this.accessAction.apply(this.value, x, y);
      }
      //@formatter:on
      synchronized (this) {
         if (Objects.isNull(this.valueVarHandle.getVolatile(this))) {
            this.value = this.initializationAction.get();
         }
      }
      return this.accessAction.apply(this.value, x, y);
   }

   /**
    * The encapsulated value is checked with volatile memory semantics to see if it has already been set. If not set a
    * synchronized block is entered. Upon entry to the synchronized block, the encapsulated value is checked once again
    * to see if it is still unset. When still unset, the <code>initializationAction</code> is invoked to obtain the
    * initial value.
    * <p>
    * When the encapsulated value is found to be set or was just set, the <code>accessAction</code> is applied to the
    * encapsulated value and the parameters <code>x</code> and <code>y</code>.
    *
    * @param x the value of the second parameter passed to the <code>accessAction</code>.
    * @param y the value of the third parameter passed to the <code>accessAction</code>.
    * @return the result of the <code>accessAction</code>.
    */

   private R accessActionNoVarHandle(X x, Y y) {
      if (Objects.nonNull(this.value)) {
         return this.accessAction.apply(this.value, x, y);
      }
      synchronized (this) {
         if (Objects.isNull(this.value)) {
            this.value = this.initializationAction.get();
         }
      }
      return this.accessAction.apply(this.value, x, y);
   }
}

/* EOF */
