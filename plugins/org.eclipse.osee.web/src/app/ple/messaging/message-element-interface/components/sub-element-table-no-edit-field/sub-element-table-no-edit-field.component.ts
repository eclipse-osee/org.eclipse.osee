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
import { ActivatedRoute } from '@angular/router';
import { applic } from '../../../../../types/applicability/applic';
import { element } from '../../types/element';

@Component({
  selector: 'app-sub-element-table-no-edit-field',
  templateUrl: './sub-element-table-no-edit-field.component.html',
  styleUrls: ['./sub-element-table-no-edit-field.component.sass']
})
export class SubElementTableNoEditFieldComponent implements OnInit {

  @Input() filter: string = "";
  @Input() element: element= {
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
  };
  @Input() header: string = "";
  @Input() width: string = "";
  _branchId: string = "";
  _branchType: string = "";


  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this._branchId = values.get("branchId") || '';
      this._branchType = values.get("branchType") || '';
    })
  }

}
