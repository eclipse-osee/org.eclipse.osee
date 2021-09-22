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
