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
package org.eclipse.osee.framework.core.model.tabledataframework;

import java.util.Collection;

/**
 * @author Shawn F. Cook
 */
public interface TableData extends Iterable<Collection<Object>> {

   public int getColumnCount();

   public Collection<Object> getHeaderStrings();

   public int getRowCount();

   public String getName();

}
