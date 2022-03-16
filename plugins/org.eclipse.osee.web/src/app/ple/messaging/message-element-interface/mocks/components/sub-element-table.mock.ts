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
import { EventEmitter, Output } from "@angular/core";
import { Component, Input } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { structure } from "../../../shared/types/structure";

@Component({
    selector: 'ple-messaging-message-element-interface-sub-element-table',
    template:'<p>Dummy</p>'
})
export class SubElementTableComponentMock{
  @Input() data: any = {};
  @Input() dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
  @Input() filter: string = "";
  
  @Input() element: any = {};
  @Output() expandRow = new EventEmitter();
  @Input() subMessageHeaders: string[] = [];
  @Input() editMode: boolean = false;
  @Input() structure: structure = {
    id: '',
    name: '',
    description: '',
    interfaceMaxSimultaneity: '',
    interfaceMinSimultaneity: '',
    interfaceTaskFileType: 0,
    interfaceStructureCategory: ''
  };
  }