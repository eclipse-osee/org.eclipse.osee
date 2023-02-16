/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.mim;

import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeGeneric;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Audrey E Denk
 */
public interface MimApi {
   OrcsApi getOrcsApi();

   AtsApi getAtsApi();

   InterfaceMessageApi getInterfaceMessageApi();

   InterfaceSubMessageApi getInterfaceSubMessageApi();

   InterfaceStructureApi getInterfaceStructureApi();

   InterfaceElementApi getInterfaceElementApi();

   InterfaceElementArrayApi getInterfaceElementArrayApi();

   InterfacePlatformTypeApi getInterfacePlatformTypeApi();

   InterfaceNodeViewApi getInterfaceNodeViewApi();

   InterfaceConnectionViewApi getInterfaceConnectionViewApi();

   InterfaceEnumerationApi getInterfaceEnumerationApi();

   InterfaceEnumerationSetApi getInterfaceEnumerationSetApi();

   InterfaceDifferenceReportApi getInterfaceDifferenceReportApi();

   MimArtifactsApi getMimArtifactsApi();

   MimReportsApi getMimReportsApi();

   TransportTypeApi getTransportTypeApi();

   ConcurrentHashMap<Long, ? extends InterfaceLogicalTypeGeneric> getLogicalTypes();
}