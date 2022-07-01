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
import { BehaviorSubject } from 'rxjs';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import {branchType} from '../../types/BranchTypes'


@Injectable({
  providedIn: 'root'
})
export class BranchTypeService {
  private _branchType: BehaviorSubject<string> = new BehaviorSubject<string>("");
  constructor (private uiService: UiService) { }
  
  get BranchType() {
    return this.uiService.type;
  }

  set type(value: branchType) {
    this.uiService.typeValue = value;
  }

  get type() {
    return this.BranchType.getValue() as branchType;
  }
}
