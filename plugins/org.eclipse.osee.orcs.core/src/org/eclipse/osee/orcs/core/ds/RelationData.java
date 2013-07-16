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
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Andrew M. Finkbeiner
 */
public interface RelationData extends OrcsData {

   int getArtIdA();

   void setArtIdA(int artIdA);

   int getArtIdB();

   void setArtIdB(int artIdB);

   int getArtIdOn(RelationSide side);

   String getRationale();

   void setRationale(String rationale);

}
