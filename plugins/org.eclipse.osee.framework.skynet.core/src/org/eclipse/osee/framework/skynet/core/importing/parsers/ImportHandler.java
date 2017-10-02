/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.parsers;

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
