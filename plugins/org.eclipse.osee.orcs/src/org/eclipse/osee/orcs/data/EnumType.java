/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

/**
 * @author Roberto E. Escobar
 */
@JsonSerialize(using = IdSerializer.class)
public interface EnumType extends Id, FullyNamed {

   EnumEntry[] values();

   EnumEntry getEntryByName(String entryName);

   Set<String> valuesAsOrderedStringSet();

}