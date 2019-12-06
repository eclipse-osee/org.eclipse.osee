/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Ryan D. Brooks
 */
public class OrcsTypeTokenProviderBase implements OrcsTypeTokenProvider {
   private final OrcsTypeTokens orcsTypes;
   private final OrcsTypeTokens[] tokensArray;

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
    * @param tokenClassInits used as a mechanism to cause class loading of token classes to occur prior invocation of
    * instance methods of this class. Simply referencing the static fields of the token as arguments to this method
    * causes the needed class loading to occur
    */
   public void loadClasses(NamedId... tokenClassInits) {
      // class loading already done by method invocation of this method
   }

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      if (orcsTypes == null) {
         for (OrcsTypeTokens orcsTypes : tokensArray) {
            orcsTypes.registerTypes(tokenService);
         }
      } else {
         orcsTypes.registerTypes(tokenService);
      }
   }
}