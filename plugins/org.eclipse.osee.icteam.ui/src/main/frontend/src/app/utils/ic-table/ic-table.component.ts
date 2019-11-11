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
import { Component, OnInit, ContentChildren, QueryList, Input } from '@angular/core';
import { TableTrDirective } from './table-tr.directive';
import { DragulaService } from 'ng2-dragula';

@Component({
  selector: 'app-ic-table',
  templateUrl: './ic-table.component.html',
  styleUrls: ['./ic-table.component.scss']
})
export class IcTableComponent implements OnInit {



  private _items: any[] = [];
  selectedFilterType: any;
  filterType: any;
  filterData: any;
  pageSize: number = 15;
  page: any;

  filter: Map<String, Boolean>;
  sortBy: string;
  sortAsc: Boolean = true;
  showlimit: Number;
  pageNumber: Number;


  @Input() pagination: boolean = false;
  @Input("dragulaEnable") dragulaEnable: boolean = false;
  @Input("dragulaId") dragulaId: String = 'table-item';
  // @Input() defaultPageSize:number;

  @Input() get items() {
    return this._items;
  }

  set items(items: any[]) {
    this._items = items;
  }

  @ContentChildren(TableTrDirective) columns: QueryList<TableTrDirective>;

  constructor(private dragulaService: DragulaService) {
    console.log("table constructor");
  }

  ngOnInit() {
    console.log("table ngOnInit");
    if (!this.pagination) {
      this.pageSize = undefined;
    }
  }


  toggleFilter(FieldType: any) {
    console.log("clicked filter", FieldType);
    if (this.selectedFilterType !== '' || this.selectedFilterType != FieldType) {
      this.filterType = FieldType;
      this.selectedFilterType = FieldType;
      // this.filterData = '';

    } else {
      this.filterType = '';
      this.filterData = '';
      this.selectedFilterType = '';
    }


  }
  close(event: Event) {
    if ((<HTMLInputElement>event.target).className !== "fa fa-filter hide active") {
      this.filterType = '';
      if (this.filterData === '') {
        this.selectedFilterType = '';
      }
    }
  }

  sortColumn(column: any) {
    const ascending = this.sortBy === column ? !this.sortAsc : true;
    this.sort(column, ascending);
  }
  sort(column: string, ascending: Boolean) {
    this.sortBy = column;
    this.sortAsc = ascending;
    if (this.sortBy) {
      this.items.sort((a: any, b: any) => {
        if (typeof a[this.sortBy] === 'string') {
          return a[this.sortBy].localeCompare(b[this.sortBy]);
        } else {
          return a[this.sortBy] - b[this.sortBy];
        }
      });
      if (this.sortAsc === false) {
        this.items.reverse();
      }
    }
  }


}
