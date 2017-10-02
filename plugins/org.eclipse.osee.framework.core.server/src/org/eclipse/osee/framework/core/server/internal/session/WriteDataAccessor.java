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
package org.eclipse.osee.framework.core.server.internal.session;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public interface WriteDataAccessor<D> {

   void create(Iterable<D> sessions) ;

   void delete(Iterable<D> sessions) ;

}