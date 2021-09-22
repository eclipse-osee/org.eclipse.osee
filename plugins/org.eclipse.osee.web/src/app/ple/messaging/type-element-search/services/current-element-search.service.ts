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
import { Injectable } from '@angular/core';
import { combineLatest, from } from 'rxjs';
import { concatMap, distinct, filter, map, mergeMap, scan, switchMap, toArray } from 'rxjs/operators';
import { element } from '../types/element';
import { ElementSearchService } from './http/element-search.service';
import { PlatformTypesService } from './http/platform-types.service';
import { BranchIdService } from './router/branch-id.service';
import { SearchService } from './router/search.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentElementSearchService {

  private _elements = combineLatest([this.idService.BranchId,this.searchService.searchTerm]).pipe(
    filter(latest => latest[0] !== '' && !isNaN(Number(latest[0])) && Number(latest[0]) > 0),
    switchMap((searchAndId) => this.platformTypesService.getFilteredTypes(searchAndId[1], searchAndId[0]).pipe(
      concatMap((platformTypes) => from(platformTypes).pipe(
        distinct((platformType) => { return platformType.id }),
        concatMap((value) => this.elementSearch.getFilteredElements(searchAndId[0], value.id??'').pipe(
          concatMap((elements) => from(elements).pipe(
            distinct((element) => { return element.id })
          ))
        )),
        distinct((val) => { return val.id }),
        scan((acc, curr) => [...acc, curr], [] as element[])))
    )),
  )
  constructor (private idService: BranchIdService, private platformTypesService: PlatformTypesService, private elementSearch: ElementSearchService, private searchService: SearchService) { }
  
  get elements() {
    return this._elements;
  }
}
