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
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { PlatformType } from '../../../shared/types/platformType';

@Injectable({
  providedIn: 'root'
})
export class PlatformTypesService {

  constructor (private http: HttpClient) { }
  
  getFilteredTypes(filter: string, branchId: string): Observable<PlatformType[]> {
    return this.http.get<PlatformType[]>(apiURL + "/mim/branch/" + branchId + "/types/filter/" + filter);
  }
}
