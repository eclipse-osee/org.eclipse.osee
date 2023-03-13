/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.operations.synchronization.publishingdom;

import java.util.Objects;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IncorrectIdentifierTypeException;

/**
 * This class provides a factory for obtaining an implementation of the {@link DocumentMap} interface.
 *
 * @author Loren K. Ashley
 */

public class Factory {

   /**
    * Creates a new {@link DocumentMap} implementation with the provided <code>documentMapIdentifier</code>.
    *
    * @param documentMapIdentifier the {@link Identifier} for the {@link DocumentMap} implementation.
    * @return an implementation of the {@link DocumentMap} interface.
    * @throws NullPointerException when the parameter <code>documentMapIdentifier</code> is <code>null</code>.
    * @throws IncorrectIdentifierTypeException when the parameter <code>documentMapIdentifier</code> is not of the
    * {@link IdentifierType} {@link IdentifierType#FOREST}.
    */

   public static DocumentMap createDocumentMap(Identifier documentMapIdentifier) {

      Objects.requireNonNull(documentMapIdentifier,
         "DocumentMapImpl::createDocument, parameter \"documentIdentifier\" cannot be null.");

      documentMapIdentifier.requireType(IdentifierType.FOREST,
         "Cannot create a DocumentMap with an identifier that is not of the type \"FOREST\".");

      return new DocumentMapImpl(documentMapIdentifier);
   }

}

/* EOF */