/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.define.api.importing;

import java.net.URI;
import java.util.Collection;

/**
 * @author John R. Misinco
 */
public interface ImportHandler {

   //lower return value mean higher ranking
   int getRank();

   //returns whether or not processing was successful
   boolean process(Collection<URI> resources, Object destination, boolean persistChanges);

}
