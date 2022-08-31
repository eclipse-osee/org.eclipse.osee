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
import { applic } from '../../../../../types/applicability/applic';

@Injectable({
  providedIn: 'root'
})
export class ApplicabilityListService {

  constructor (private http: HttpClient) { }
  
  getApplicabilities(branchId: string | number) {
    return this.http.get<applic[]>(apiURL+'/orcs/branch/'+branchId+'/applic')
  }

  getViews(branchId: string | number) {
    return this.http.get<applic[]>(apiURL+'/orcs/branch/'+branchId+'/applic/views')
  }
}
