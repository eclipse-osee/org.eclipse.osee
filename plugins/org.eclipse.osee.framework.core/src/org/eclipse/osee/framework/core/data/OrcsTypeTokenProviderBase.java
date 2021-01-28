/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Ryan D. Brooks
 */
public class OrcsTypeTokenProviderBase implements OrcsTypeTokenProvider {
   private final OrcsTypeTokens orcsTypes;
   private final OrcsTypeTokens[] tokensArray;
   private Class<?>[] tokenClasses;

   /**
    * @param orcsTypes contains all type tokens for a given namespace that will be used by this provider to register
    * with the token service
    */
   public OrcsTypeTokenProviderBase(OrcsTypeTokens orcsTypes) {
      this.orcsTypes = orcsTypes;
      tokensArray = null;
   }

   public OrcsTypeTokenProviderBase(OrcsTypeTokens... tokensArray) {
      this.tokensArray = tokensArray;
      orcsTypes = null;
   }

   /**
    * @param tokenClasses used to provide dynamic access via getTokenClass
    */
   public void registerTokenClasses(Class<?>... tokenClasses) {
      // class loading already done by method invocation of this method
      this.tokenClasses = tokenClasses;
   }

   /**
    * @param tokenClassInits used as a mechanism to cause class loading of token classes to occur prior invocation of
    * instance methods of this class. Simply referencing the static fields of the token as arguments to this method
    * causes the needed class loading to occur
    */
   public void loadClasses(NamedId... tokenClassInits) {
      // class loading already done by method invocation of this method
   }

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      tokenService.registerTokenClasses(tokenClasses);
      if (orcsTypes == null) {
         for (OrcsTypeTokens orcsTypes : tokensArray) {
            orcsTypes.registerTypes(tokenService);
         }
      } else {
         orcsTypes.registerTypes(tokenService);
      }
   }
}