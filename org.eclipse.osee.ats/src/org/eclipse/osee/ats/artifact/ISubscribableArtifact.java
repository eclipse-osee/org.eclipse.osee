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
package org.eclipse.osee.ats.artifact;

import java.sql.SQLException;
import java.util.ArrayList;
import org.eclipse.osee.framework.skynet.core.User;

/**
 * @author Donald G. Dunne
 */
public interface ISubscribableArtifact {

   public void addSubscribed(User u) throws SQLException;

   public void removeSubscribed(User u) throws SQLException;

   public boolean isSubscribed(User u) throws SQLException;

   public ArrayList<User> getSubscribed() throws SQLException;

   public boolean amISubscribed();

}
