import { Injectable } from '@angular/core';
import { iif } from 'rxjs';
import { map, share, switchMap, tap } from 'rxjs/operators';
import { BranchService } from './branch.service';
import { RouteStateService } from './route-state-service.service';

@Injectable({
  providedIn: 'root'
})
export class BranchListService {

  private _branches=this.routeService.type.pipe(
    map(value => this.updateType(value)),
    switchMap(viewBranchType => iif(() => viewBranchType === 'all' || viewBranchType === 'working' || viewBranchType === 'baseline',
    this.branchService.getBranches(viewBranchType)
    )),
    share()
  )
  constructor (private branchService: BranchService, private routeService: RouteStateService) { }
  
  private updateType(value: string) {
    if (value === 'product line') {
      return 'baseline'
    } else if (value === 'working') {
      return 'working'
    } else {
      return value;
    }
    //"product line , working"
  }
  get branches() {
    return this._branches;
  }
}
