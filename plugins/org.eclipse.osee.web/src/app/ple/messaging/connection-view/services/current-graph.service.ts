import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { filter, share, switchMap } from 'rxjs/operators';
import { GraphService } from './graph.service';
import { RouteStateService } from './route-state-service.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentGraphService {

  private _nodes = this.routeStateService.id.pipe(
    share(),
    filter((val)=>val!=="" && val!=='-1'),
    switchMap((val) => this.graphService.getNodes(val).pipe(
      share()
    )),
  )
  private _update = new Subject<boolean>();
  constructor (private graphService: GraphService, private routeStateService: RouteStateService) { }
  
  get nodes() {
    return this._nodes;
  }

  get updated() {
    return this._update;
  }

  set update(value: boolean) {
    this._update.next(true);
  }
}
