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
package org.eclipse.osee.ats.api;

import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * Base class to build all ats config and action objects on
 * 
 * @author Donald G. Dunne
 */
public interface IAtsObject extends Identifiable<String>, HasDescription {

   String toStringWithId();

   Object getStoreObject();

   void setStoreObject(Object object);

   long getId();

}
