import { Injectable } from '@angular/core';
import { branchType } from '../../types/BranchTypes';
import { BranchIdService } from './branch-id.service';
import { BranchTypeService } from './branch-type.service';

@Injectable({
  providedIn: 'root'
})
export class RouterStateService {

  constructor (private idService: BranchIdService, private typeService: BranchTypeService) { }
  
  get BranchId() {
    return this.idService.BranchId;
  }
  get id() {
    return this.idService.id;
  }
  set id(value: string) {
    this.idService.id = value;
  }

  get BranchType() {
    return this.typeService.BranchType;
  }

  get type() {
    return this.typeService.type;
  }

  set type(value: string) {
    this.typeService.type = value as branchType;
  }
}
