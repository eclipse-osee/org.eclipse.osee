/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.data;

import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface EnumType extends Id, FullyNamed {

   EnumEntry[] values();

   EnumEntry getEntryByName(String entryName);

   Set<String> valuesAsOrderedStringSet();

   EnumEntry valueOf(int ordinal) throws OseeCoreException;

   EnumEntry valueOf(String entryName) throws OseeCoreException;
}