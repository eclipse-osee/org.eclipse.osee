/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.common.clientserver.dependent.util;

/**
 * This class is having the constants
 * 
 * @author Ajay Chandrahasan
 */
public class CommonConstants {

  public static final String RELATION_MAP_KEY_SEPARATOR = "##";
  public static final String SUCCESS = "Success";
  public static final String FAILURE = "Failure";
  public static final String STATUS = "Status";

  public static final String MANGLED_NAME = "#__$DORMANT$__#"; // Projects having this at the end of its name is
                                                               // considered dormant
  public static final String PRODUCT_BACKLOG_NAME = "Product_Backlog"; // Release with this name is used as product
                                                                       // backlog for a project
  public static final String PROJECT_SHORTNAME = "pj_sn";

}
