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
package org.eclipse.osee.account.rest.client.internal;

import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.framework.core.services.URIProvider;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * @author Roberto E. Escobar
 */
public class AccountClientModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(AccountClient.class).to(AccountClientImpl.class);
      TypeListener listener = new TypeListener() {

         @Override
         public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            encounter.register(new InjectionListener<I>() {

               @Override
               public void afterInjection(I injectee) {
                  AccountClientImpl client = (AccountClientImpl) injectee;
                  client.start();
               }
            });
         }
      };
      bindListener(subtypeOf(AccountClientImpl.class), listener);
      bind(URIProvider.class).to(StandadloneUriProviderImpl.class);
   }

   private static Matcher<? super TypeLiteral<?>> subtypeOf(Class<?> superclass) {
      return new SubTypeOfMatcher(TypeLiteral.get(AccountClientImpl.class));
   }

   private static final class SubTypeOfMatcher extends AbstractMatcher<TypeLiteral<?>> {
      private final TypeLiteral<AccountClientImpl> superType;

      public SubTypeOfMatcher(TypeLiteral<AccountClientImpl> superType) {
         super();
         this.superType = superType;
      }

      @Override
      public boolean matches(TypeLiteral<?> subType) {
         return subType.equals(superType) || superType.getRawType().isAssignableFrom(subType.getRawType());
      }
   }

}
