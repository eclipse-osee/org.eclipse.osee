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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from 'src/environments/environment';
import { element } from '../../types/element';

@Injectable({
  providedIn: 'root'
})
export class ElementSearchService {

  constructor (private http: HttpClient) { }
  
  getFilteredElements(branchId:string, typeId:string ) {
    return this.http.get<element[]>(apiURL + '/mim/branch/' + branchId + '/elements/getType/' + typeId);
  }
}
