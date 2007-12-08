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
package org.eclipse.osee.framework.ui.plugin.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Robert A. Fisher
 */
public interface RsetProcessorNonGeneric {
   public Object process(ResultSet set) throws SQLException;

   public boolean validate(Object item);
}
