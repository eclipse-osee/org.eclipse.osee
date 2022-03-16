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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { LayoutNotifierService } from 'src/app/layoutNotification/layout-notifier.service';
import { applic } from '../../../../../types/applicability/applic';
import { element } from '../../../shared/types/element';
import { structure } from '../../../shared/types/structure';

@Component({
  selector: 'app-sub-element-table-row',
  templateUrl: './sub-element-table-row.component.html',
  styleUrls: ['./sub-element-table-row.component.sass']
})
export class SubElementTableRowComponent implements OnInit {
  @Input() header: string = "";
  @Input() editMode: boolean = false;

  @Input() element:element={
    id: '',
    name: '',
    description: '',
    notes: '',
    interfaceElementIndexEnd: 0,
    interfaceElementIndexStart: 0,
    applicability: {
      id: '1',
      name:'Base'
    },
    units:'',
    interfaceElementAlterable: false
  }

  @Input() structure: structure={
    id: '',
    name: '',
    description: '',
    interfaceMaxSimultaneity: '',
    interfaceMinSimultaneity: '',
    interfaceTaskFileType: 0,
    interfaceStructureCategory: ''
  }
  editableElementHeaders: string[] = [
    'name',
    'platformTypeName2',
    'interfaceElementAlterable',
    'description',
    'notes',
    'applicability',
    'units',
    'interfaceElementIndexStart',
    'interfaceElementIndexEnd',
  ];
  @Input() filter:string=""
  layout = this.layoutNotifier.layout;

  @Output() menu = new EventEmitter<{ event: MouseEvent, element: element, field?: string | number | boolean | applic }>();
  constructor(private layoutNotifier: LayoutNotifierService) { }

  ngOnInit(): void {
  }

  openGeneralMenu(event: MouseEvent, element: element, field?: string | number | boolean | applic) {
    this.menu.emit({ event, element, field })
  }
}
