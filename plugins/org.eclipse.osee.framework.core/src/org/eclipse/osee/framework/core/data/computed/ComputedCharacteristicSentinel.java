
/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.framework.core.data.computed;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.ComputedCharacteristic;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;

/**
 * @author Stephen J. Molaro
 */
public final class ComputedCharacteristicSentinel extends ComputedCharacteristic<Object> {
   public ComputedCharacteristicSentinel(Long id, String name, TaggerTypeToken taggerType, NamespaceToken namespace, String description, List<AttributeTypeGeneric<Object>> typesToCompute) {
      super(id, name, taggerType, namespace, description, typesToCompute);
   }

   @Override
   public boolean isMultiplicityValid(ArtifactTypeToken artifactType) {
      return true;
   }

   @Override
   public Object calculate(List<Object> computingValues) {
      return 0;
   }
}
