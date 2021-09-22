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
import { MatSelectChange } from '@angular/material/select';
import { CurrentBranchTypeService } from '../../services/current-branch-type.service';
import { BranchIdService } from '../../services/router/branch-id.service';
import { BranchTypeService } from '../../services/router/branch-type.service';
import { RoutingService } from '../../services/router/routing.service';

@Component({
  selector: 'osee-typesearch-branch-selector',
  templateUrl: './branch-selector.component.html',
  styleUrls: ['./branch-selector.component.sass']
})
export class BranchSelectorComponent implements OnInit {
  selectedBranchType = this.branchTypeService.BranchType;
  selectedBranchId = "";
  options = this.branchesService.branches;
  constructor (private branchesService: CurrentBranchTypeService, private branchTypeService: BranchTypeService, private branchIdService: RoutingService) {
    this.branchIdService.BranchId.subscribe((val) => {
      this.selectedBranchId = val;
    })
  }

  ngOnInit(): void {
  }

  selectBranch(event:MatSelectChange) {
    this.branchIdService.id = event.value;
  }

}
