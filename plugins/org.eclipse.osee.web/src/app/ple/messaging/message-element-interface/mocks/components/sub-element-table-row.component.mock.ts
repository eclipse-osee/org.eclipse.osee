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
import { applic } from "../../../../../types/applicability/applic";
import { element } from "../../../shared/types/element";
import { structure } from "../../../shared/types/structure";
 
 @Component({
     selector: 'app-sub-element-table-row',
     template:'<p>Dummy</p>'
 })
 export class SubElementTableRowComponentMock{
     @Input() header: string = "";
     @Input() editMode: boolean = false;
     @Input() element:element={
         id: "",
         name: "",
         description: "",
         notes: "",
         interfaceElementIndexEnd: 0,
         interfaceElementIndexStart: 0,
         interfaceElementAlterable: false,
         enumLiteral:'',
         units:'Hertz'
     }
     @Input() structure: structure = {
         id: "",
         name: "",
         description: "",
         interfaceMaxSimultaneity: "",
         interfaceMinSimultaneity: "",
         interfaceTaskFileType: 0,
         interfaceStructureCategory: ""
     }
     @Input() filter: string = "";
     @Output() menu = new EventEmitter<{ event: MouseEvent, element: element, field?: string | number | boolean | applic }>();
    @Output() navigate = new EventEmitter<string>(); 
   }