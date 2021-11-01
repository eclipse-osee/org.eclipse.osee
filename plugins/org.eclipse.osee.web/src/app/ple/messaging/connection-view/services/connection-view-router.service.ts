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
import { Router } from '@angular/router';
import { RouteStateService } from './route-state-service.service';
import { LocationStrategy } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ConnectionViewRouterService {

  constructor (private routerState: RouteStateService, private router: Router , private angLocationStrategy:LocationStrategy) { }
  
  set branchType(value: string) {
    let baseUrl;
    if (this.routerState.type.getValue() != "") {
      baseUrl=this.router.url.split(this.routerState.type.getValue().replace(/ /g,"%20"))[0]
    } else {
      baseUrl = this.router.url;
    }
    this.routerState.branchType = value;
    this.router.navigate([baseUrl,value])
  }
  get type() {
    return this.routerState.type;
  }

  get id() {
    return this.routerState.id;
  }

  set branchId(value: string) {
    let baseUrl;
    if (this.routerState.type.getValue() != "") {
      baseUrl=this.router.url.split(this.routerState.type.getValue().replace(/ /g,"%20"))[0]
    } else {
      baseUrl = this.router.url;
    }
    this.routerState.branchId = value;
    this.router.navigate([baseUrl,this.routerState.type.getValue(),value])
  }

  set connection(value: string) {
    let baseUrl = this.router.url.split("connections")[0];
    this.router.navigate([baseUrl,this.routerState.type.getValue(),this.routerState.id.getValue(),value,"messages"])
  }

  set connectionInNewTab(value: string) {
    let currentUrl = this.router.url.split("/");
    currentUrl.shift()
    currentUrl.splice(currentUrl.indexOf('connections'),1)
    let url = this.router.serializeUrl(this.router.createUrlTree([this.angLocationStrategy.getBaseHref(),...currentUrl,value,"messages"]))
    window.open(url, '_blank');
  }
  get inDiffMode() {
    return this.routerState.isInDiff;
  }
}
