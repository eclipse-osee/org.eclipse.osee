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
import { connection, connectionWithChanges, transportType } from "../../../shared/types/connection";
import { OseeNode, node, nodeData, nodeDataWithChanges } from "../../../shared/types/node";

@Component({
    selector: 'app-graph-link-menu',
    template:'<div>Dummy</div>'
  })
export class GraphLinkMenuDummy{
@Input() editMode: boolean = false;
  @Input() data: connection | connectionWithChanges = {
    name: '',
    transportType: transportType.Ethernet
  };

  @Input()
  source!: OseeNode<node | nodeData | nodeDataWithChanges>;
  @Input()
  target!: OseeNode<node | nodeData | nodeDataWithChanges>;
  }