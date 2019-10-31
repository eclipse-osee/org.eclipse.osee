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

/**
 * @author Ryan D. Brooks
 */
public class OrcsTypeTokenProviderBase implements OrcsTypeTokenProvider {
   private final OrcsTypeTokens tokens;
   private final OrcsTypeTokens[] tokensArray;

   public OrcsTypeTokenProviderBase(OrcsTypeTokens tokens) {
      this.tokens = tokens;
      tokensArray = null;
   }

   public OrcsTypeTokenProviderBase(OrcsTypeTokens... tokensArray) {
      this.tokensArray = tokensArray;
      tokens = null;
   }

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      if (tokens == null) {
         for (OrcsTypeTokens token : tokensArray) {
            token.registerTypes(tokenService);
         }
      } else {
         tokens.registerTypes(tokenService);
      }
   }
}