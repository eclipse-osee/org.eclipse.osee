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
import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-sub-element-table-no-edit-field-name',
  templateUrl: './sub-element-table-no-edit-field-name.component.html',
  styleUrls: ['./sub-element-table-no-edit-field-name.component.sass']
})
export class SubElementTableNoEditFieldNameComponent implements OnInit {

  @Input() filter: string = ""
  @Input() name: string = "";
  @Input() end: number = 0;
  @Input() start: number = 0;
  constructor() { }

  ngOnInit(): void {
  }

}
