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

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.computed.ComputedCharacteristicSentinel;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Stephen J. Molaro
 */

public interface ComputedCharacteristicToken<T> extends FullyNamed, HasDescription, NamedId {

   public static final ComputedCharacteristic<?> SENTINEL = valueOf(Id.SENTINEL, Named.SENTINEL, "");

   static final String APPLICATION_ZIP = "application/zip";
   static final String TEXT_CALENDAR = "text/calendar";
   static final String TEXT_URI_LIST = "text/uri-list";
   static final String APPLICATION_MSWORD = "application/msword";
   static final String MODEL_OSEE = "model/osee";
   static final String IMAGE = "image/*";

   boolean isMultiplicityValid(ArtifactTypeToken artifactType);

   T calculate(List<T> computingValues);

   List<AttributeTypeGeneric<T>> getAttributeTypesToCompute();

   String getMediaType();

   Set<DisplayHint> getDisplayHints();

   default NamespaceToken getNamespace() {
      return NamespaceToken.SENTINEL;
   }

   default TaggerTypeToken getTaggerType() {
      return TaggerTypeToken.SENTINEL;
   }

   default boolean isTaggable() {
      return getTaggerType().isValid();
   }

   static @NonNull ComputedCharacteristic<?> valueOf(Long id, String name, String description) {
      List<AttributeTypeGeneric<Object>> SentinalList = new ArrayList<>();
      return new ComputedCharacteristicSentinel(Id.SENTINEL, Named.SENTINEL, TaggerTypeToken.SENTINEL,
         NamespaceToken.OSEE, "", SentinalList);
   }

}