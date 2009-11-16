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
package org.eclipse.osee.framework.skynet.core.artifact.search;


/**
 * Provides the ability to build the tables and sequences necessary for the Define tools to be able to work. This class
 * is only intended for installation purposes, and is not to support general runtime needs.
 * 
 * @author Robert A. Fisher
 */
@Deprecated
public class SkynetDatabase {
   public static final Table ARTIFACT_TABLE = new Table("OSEE_ARTIFACT");
   public static final Table ARTIFACT_VERSION_TABLE = new Table("OSEE_ARTIFACT_VERSION");
   public static final Table TRANSACTIONS_TABLE = new Table("OSEE_TXS");

   public static final Table TRANSACTION_DETAIL_TABLE = new Table("OSEE_TX_DETAILS");
   public static final Table ARTIFACT_TYPE_TABLE = new Table("OSEE_ARTIFACT_TYPE");

   public static final Table ATTRIBUTE_VERSION_TABLE = new Table("OSEE_ATTRIBUTE");
   public static final Table ATTRIBUTE_TYPE_TABLE = new Table("OSEE_ATTRIBUTE_TYPE");
   public static final Table RELATION_LINK_VERSION_TABLE = new Table("OSEE_RELATION_LINK");

}
