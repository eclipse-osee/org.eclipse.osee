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
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { map } from 'rxjs/operators';
import { HeaderService } from '../../../shared/services/ui/header.service';
import { CurrentElementSearchService } from '../../services/current-element-search.service';
import { element } from '../../types/element';

@Component({
  selector: 'osee-typesearch-element-table',
  templateUrl: './element-table.component.html',
  styleUrls: ['./element-table.component.sass']
})
export class ElementTableComponent implements OnInit {
  dataSource = new MatTableDataSource<element>();
  headers:(keyof element)[] = [
    'name',
    'platformTypeName2',
    'interfaceElementAlterable',
    'description',
    'notes'];
  constructor (private elementService: CurrentElementSearchService, private headerService: HeaderService) {
    this.elementService.elements.subscribe((val) => {
      this.dataSource.data = val;
    })
  }

  ngOnInit(): void {
  }

  valueTracker(index: any, item: any) {
    return index;
  }

  getHumanReadable(value:string) {
    return this.headerService.getHeaderByName(value, 'element').pipe(
      map((v)=>v.humanReadable)
    )
  }
}
