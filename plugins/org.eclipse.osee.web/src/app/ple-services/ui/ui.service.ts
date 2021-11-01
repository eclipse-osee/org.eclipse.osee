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
import { BranchUIService } from './branch/branch-ui.service';
import { DiffModeService } from './diff/diff-mode.service';
import { UpdateService } from './update/update.service';

@Injectable({
  providedIn: 'root'
})
export class UiService {

  constructor (private branchService: BranchUIService, private updateService: UpdateService, private diffModeService:DiffModeService) { }
  
  get id() {
    return this.branchService.id;
  }

  get type() {
    return this.branchService.type;
  }

  set idValue(id: string | number) {
    this.branchService.idValue = id;
  }

  set typeValue(branchType: string) {
    this.branchService.typeValue = branchType;
  }

  get update() {
    return this.updateService.update;
  }

  set updated(value: boolean) {
    this.updateService.updated = value;
  }

  get isInDiff() {
    return this.diffModeService.isInDiff;
  }

  set diffMode(value: boolean) {
    this.diffModeService.DiffMode = value;
  }
}
