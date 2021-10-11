/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import java.io.InputStream;
import java.lang.ref.Reference;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeContainer;
import org.eclipse.osee.orcs.core.internal.util.OrcsWriteable;

/**
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface Attribute<T> extends OrcsWriteable, AttributeReadable<T>, HasOrcsData<AttributeTypeToken, AttributeData<T>> {

   void setValue(T value);

   boolean setFromString(String value);

   boolean setValueFromInputStream(InputStream value);

   void resetToDefaultValue();

   void clearDirty();

   void setArtifactDeleted();

   /////////

   void internalInitialize(Reference<AttributeContainer> containerReference, AttributeData<T> attributeData, boolean isDirty, boolean setDefaultValue, OrcsTokenService tokenService);

   ArtifactToken getContainer();

   String convertToStorageString(T rawValue);

   /**
    * @param value will be non-null
    */
   T convertStringToValue(String value);
}