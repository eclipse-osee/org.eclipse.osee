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
import { ActivatedRoute, Router } from '@angular/router';
import { RouterStateService } from './router-state.service';

@Injectable({
  providedIn: 'root'
})
export class RoutingService {

  constructor (private state: RouterStateService, private router: Router) { }
  
  get id() {
    return this.state.id
  }

  set id(value: string) {
    let baseUrl;
    if (this.state.type != '') {
      baseUrl = this.router.url.split(this.state.type.replace('baseline', 'product%20line').replace(/ /g, "%20"))[0];
    } else {
      baseUrl = this.router.url;
    }
    this.router.navigate([baseUrl,this.state.type.replace('baseline', 'product line'),value,'typeSearch'])
    this.state.id = value;
  }

  get type() {
    return this.state.type;
  }

  set type(value: string) {
    let baseUrl;
    if (this.state.type != '') {
      baseUrl = this.router.url.split(this.state.type.replace('baseline','product%20line').replace(/ /g, "%20"))[0];
    } else {
      baseUrl = this.router.url.split('typeSearch')[0];
    }
    this.state.type = value;
    this.router.navigate([baseUrl,value,'typeSearch'])
  }

  get BranchId() {
    return this.state.BranchId;
  }

  get BranchType() {
    return this.state.BranchType;
  }
}
