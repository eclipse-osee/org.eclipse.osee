/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Allow for late loading of large amounts of data that isn't needed often.
 *
 * @author Donald G. Dunne
 */
public interface ICoverageUnitFileContentsLoader {

   public String getText() throws OseeCoreException;

}
