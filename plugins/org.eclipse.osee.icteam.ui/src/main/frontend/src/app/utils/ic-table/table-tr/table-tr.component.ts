/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
import { Component, OnInit, ContentChildren, QueryList, forwardRef, Inject, Input } from '@angular/core';
import { IcTableComponent } from '../ic-table.component';

@Component({
  selector: '[app-table-tr]',
  templateUrl: './table-tr.component.html',
  styleUrls: ['./table-tr.component.scss']
})
export class TableTrComponent implements OnInit {


  @Input() item;

  @Input() index;
  // @Inject(forwardRef(() => IcTableComponent))
  constructor(public dataTable: IcTableComponent) { }

  ngOnInit() {
  }

  getProperty(eachItem: any, property: Array<string>) {

    let data = eachItem;
    if (property.length === 1) {
      return property[0];
    }
    switch (property.length) {
      case 2: return eachItem[property[0]][property[1]];
      case 3: return eachItem[property[0]][property[1]][property[2]];
      case 4: return eachItem[property[0]][property[1]][property[2]][property[3]];
      case 5: return eachItem[property[0]][property[1]][property[2]][property[3]][property[4]];
      case 6: return eachItem[property[0]][property[1]][property[2]][property[3]][property[4]][property[5]];
    }
    return eachItem[property[0]];
  }

}
