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
import { Edge } from "@swimlane/ngx-graph";
import { difference } from "src/app/types/change-report/change-report";
import { applic } from "../../../../types/applicability/applic";

export interface connection {
    id?: string,
    name: string,
    dashed?:boolean,
    description?: string,
    transportType: transportType
    applicability?: applic
}
export interface connectionWithChanges extends connection{
    deleted:boolean,
    changes: {
        name?: difference,
        description?: difference,
        transportType?: difference,
        applicability?:difference
    }
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

export interface OseeEdge<T> extends Omit<Edge, 'data'>{
    data:T
}