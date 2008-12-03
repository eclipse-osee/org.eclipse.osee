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

import java.util.ArrayList;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public interface IFavoriteableArtifact {

   public void addFavorite(User u, SkynetTransaction transaction) throws OseeCoreException;

   public void removeFavorite(User u, SkynetTransaction transaction) throws OseeCoreException;

   public boolean isFavorite(User u) throws OseeCoreException;

   public ArrayList<User> getFavorites() throws OseeCoreException;

   public boolean amIFavorite();

}
