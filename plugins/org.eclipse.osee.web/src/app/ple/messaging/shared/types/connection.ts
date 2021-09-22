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
import { applic } from "../../shared/types/NamedId.applic";

export interface connection {
    id?: string,
    name: string,
    description?: string,
    transportType: transportType
    applicability?:applic
}

export enum transportType {
    HSDN = "HSDN",
    Ethernet = "ETHERNET",
    MILSTD1553 ="MILSTD1553_B"
}

export interface newConnection {
    connection: connection,
    nodeId:string
}