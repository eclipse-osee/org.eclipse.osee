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
package org.eclipse.osee.disposition.rest.util;

import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.framework.core.data.IOseeBranch;

/**
 * @author Donald G. Dunne
 */
public interface DispoFactory {

   public DispoProgram createProgram(String name);

   public DispoProgram createProgram(String name, String guid, Long uuid);

   public DispoProgram createProgram(IOseeBranch branch);

}