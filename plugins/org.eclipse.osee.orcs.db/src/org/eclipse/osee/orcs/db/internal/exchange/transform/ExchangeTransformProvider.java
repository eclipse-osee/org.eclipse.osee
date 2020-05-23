/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.osgi.framework.Version;

public class ExchangeTransformProvider implements IExchangeTransformProvider {

   @Override
   public Collection<IOseeExchangeVersionTransformer> getApplicableTransformers(Version versionToCheck) {
      List<IOseeExchangeVersionTransformer> toReturn = new ArrayList<>();

      IOseeExchangeVersionTransformer[] transforms =
         new IOseeExchangeVersionTransformer[] {new V0_9_2Transformer(), new V0_9_4Transformer()};

      for (IOseeExchangeVersionTransformer transformer : transforms) {
         if (isApplicable(transformer.getMaxVersion(), versionToCheck)) {
            toReturn.add(transformer);
         }
      }
      return toReturn;
   }

   private static boolean isApplicable(Version maxVersion, Version versionToCheck) {
      return maxVersion.compareTo(versionToCheck) > 0;
   }
}
