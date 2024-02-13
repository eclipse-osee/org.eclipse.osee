/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.core.action;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.action.ICreateNewActionFieldsProvider;

/**
 * @author Ryan T. Baldwin
 */
public class CreateNewActionFieldsProviderService {

   private static final List<ICreateNewActionFieldsProvider> createNewActionProviders = new LinkedList<>();

   public CreateNewActionFieldsProviderService() {
      // for jax-rs
   }

   public void addCreateNewActionFieldsProvider(ICreateNewActionFieldsProvider provider) {
      createNewActionProviders.add(provider);
   }

   public static List<ICreateNewActionFieldsProvider> getCreateNewActionProviders() {
      return createNewActionProviders;
   }

}
