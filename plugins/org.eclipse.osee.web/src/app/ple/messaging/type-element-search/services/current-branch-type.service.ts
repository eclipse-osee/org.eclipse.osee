import { Injectable } from '@angular/core';
import { iif, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BranchListing } from '../types/BranchListing';
import { BranchService } from './http/branch.service';
import { BranchTypeService } from './router/branch-type.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentBranchTypeService {
  private _branches = this.typeService.BranchType.pipe(
    switchMap((val)=>iif(()=>val!==''&& (val === 'baseline' || val === 'working'),this.branchService.getBranches(val),of<BranchListing[]>([])))
  )
  constructor (private branchService: BranchService, private typeService: BranchTypeService) { }
  
  get branches() {
    return this._branches;
  }
}
