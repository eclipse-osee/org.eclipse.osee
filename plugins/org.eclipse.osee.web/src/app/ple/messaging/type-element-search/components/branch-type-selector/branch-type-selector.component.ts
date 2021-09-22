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
import { Component, OnInit } from '@angular/core';
import { MatRadioChange } from '@angular/material/radio';
import { iif, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BranchTypeService } from '../../services/router/branch-type.service';
import { RoutingService } from '../../services/router/routing.service';

@Component({
  selector: 'osee-typesearch-branch-type-selector',
  templateUrl: './branch-type-selector.component.html',
  styleUrls: ['./branch-type-selector.component.sass']
})
export class BranchTypeSelectorComponent implements OnInit {
  branchTypes: string[] = ['Product Line', 'Working'];
  branchType = ""
  constructor (private typeService: RoutingService) {
   }

  ngOnInit(): void {
    this.typeService.BranchType.pipe(
      switchMap((branchType) => iif(() => branchType === 'baseline', of('product line'), of(branchType)).pipe())).subscribe((value) => {
      this.branchType = value;
    })
  }
  changeBranchType(value: string) {
    this.typeService.type = value;
  }

  selectType(event: string) {
    this.changeBranchType(event as string)
  }

}
