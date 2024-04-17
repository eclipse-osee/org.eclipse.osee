/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import static org.eclipse.osee.framework.core.enums.DispoTypeTokenProvider.dispo;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeString;

/**
 * @author Angel Avila
 */
public interface DispoOseeTypes {

   // @formatter:off
   AttributeTypeString DispoCiSet = dispo.createString(5225296359986133054L, "dispo.CI Set", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CoverageConfig = osee.createString(1152921504606847893L, "Config", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean DispoItemAborted = dispo.createBoolean(3458764513820541448L, "dispo.item.Aborted", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CoverageItemCategory = osee.createString(3458764513820541442L, "item.Category", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemElapsedTime = dispo.createString(3458764513820541447L, "dispo.item.Elapsed Time", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemMachine = dispo.createString(3458764513820541446L, "dispo.item.Machine", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean DispoItemNeedsReview = osee.createBoolean(2903020690286924090L, "item.Needs Review", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemVersion = osee.createString(3458764513820541440L, "item.Version", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoMultiEnvSettings = osee.createString(3587660131047940387L, "Multi-Env Settings", MediaType.APPLICATION_JSON, ""); //Not Used in Code
   AttributeTypeBoolean DispoIsMultiEnv = osee.createBoolean(3587620131443940337L, "Is Multi-Env", MediaType.TEXT_PLAIN, ""); //Not Used in Code
   // @formatter:on

}