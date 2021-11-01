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
  selector: 'app-sub-element-table-no-edit-field-dynamic-width',
  templateUrl: './sub-element-table-no-edit-field-dynamic-width.component.html',
  styleUrls: ['./sub-element-table-no-edit-field-dynamic-width.component.sass']
})
export class SubElementTableNoEditFieldDynamicWidthComponent implements OnInit {

  @Input() field: string = "";
  @Input() width: string = "";
  @Input() filter: string = "";
  constructor() { }

  ngOnInit(): void {
  }

}
