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
import { Component, Input } from "@angular/core";
import { connection, connectionWithChanges, OseeEdge } from "../../../shared/types/connection";
import { OseeNode, node, nodeData, nodeDataWithChanges } from "../../../shared/types/node";

@Component({
    selector: 'app-graph-node-menu',
    template:'<div>Dummy</div>'
  })
export class GraphNodeMenuDummy{
  @Input() editMode: boolean = false;
  @Input() data: nodeData | nodeDataWithChanges = {
    id: '',
    name: '',
    interfaceNodeAddress: '',
    interfaceNodeBgColor:''
  }
  @Input() sources: OseeEdge<connection | connectionWithChanges>[] = []
  @Input() targets: OseeEdge<connection | connectionWithChanges>[] = []
  }