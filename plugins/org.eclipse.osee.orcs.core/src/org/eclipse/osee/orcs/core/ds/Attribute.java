/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import java.io.InputStream;
import java.lang.ref.Reference;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeContainer;
import org.eclipse.osee.orcs.core.internal.util.OrcsWriteable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface Attribute<T> extends OrcsWriteable, AttributeReadable<T>, HasOrcsData<AttributeData> {

   void setValue(T value) throws OseeCoreException;

   boolean setFromString(String value) throws OseeCoreException;

   boolean setValueFromInputStream(InputStream value) throws OseeCoreException;

   void resetToDefaultValue() throws OseeCoreException;

   void clearDirty();

   void setArtifactDeleted();

   /////////

   void internalInitialize(AttributeTypes attributeTypeCache, Reference<AttributeContainer> containerReference, AttributeData attributeData, boolean isDirty, boolean setDefaultValue) throws OseeCoreException;

   Identifiable<String> getContainer() throws OseeStateException;

}