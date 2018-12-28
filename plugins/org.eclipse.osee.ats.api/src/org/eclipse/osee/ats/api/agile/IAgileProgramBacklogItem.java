/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse  License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

/**
 * @author Donald G. Dunne
 */
public interface IAgileProgramBacklogItem extends IAgileObject {

   @Override
   String getName();

   Long getBacklogId();

   @Override
   Long getId();

}
