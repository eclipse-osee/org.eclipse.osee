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
package org.eclipse.osee.ats.ide.operation;

import java.util.ArrayList;
import java.util.List;

public class ChangeReportProviderService {

   private static final List<ChangeReportProvider> providers = new ArrayList<>();

   public static List<ChangeReportProvider> getProviders() {
      return providers;
   }

   public ChangeReportProviderService() {
   }

   public void addChangeReportProvider(ChangeReportProvider provider) {
      providers.add(provider);
   }

}
