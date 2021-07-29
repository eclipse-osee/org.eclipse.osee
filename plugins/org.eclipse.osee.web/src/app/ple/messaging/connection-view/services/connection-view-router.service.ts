import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { RouteStateService } from './route-state-service.service';

@Injectable({
  providedIn: 'root'
})
export class ConnectionViewRouterService {

  constructor (private routerState: RouteStateService, private router: Router) { }
  
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
    let baseUrl = this.router.url.split("connections")[0];
    let url = this.router.serializeUrl(this.router.createUrlTree([baseUrl,this.routerState.type.getValue(),this.routerState.id.getValue(),value,"messages"]))
    window.open(url, '_blank');
  }
}
