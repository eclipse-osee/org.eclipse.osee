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
package org.eclipse.osee.framework.jdk.core.util;

/**
 * Implemented by items that store persistence layer specific information. These items can then be attached to
 * persistable items to the persistence manager can place implementation specific data on the object that it needs to
 * function properly.<br/><br/> One example of information stored in such an item would be unique database id's that
 * the persistence layer will use for identifying the item in the database. Storing this in the memo allows the
 * persisted object class to remain unchanged if the persistence manager is changed out.<br/><br/> Only the
 * persistence manager that places a particular memo on an object should ever read it off the object and use it since
 * the persistence memos are only intended to remove the implementation details that are specific to a particular
 * persistence manager design.
 * 
 * @author Robert A. Fisher
 */
public interface PersistenceMemo {

}
