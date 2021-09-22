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
import { messageBranch } from '../../shared/types/branches';

@Injectable({
  providedIn: 'root'
})
export class BranchService {

  constructor (private http: HttpClient) { }
  
  public getBranches(type: string): Observable<messageBranch[]> {
    return this.http.get<messageBranch[]>(apiURL+'/orcs/branches/'+type);
  }
}
