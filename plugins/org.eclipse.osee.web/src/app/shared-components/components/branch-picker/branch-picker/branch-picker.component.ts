/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { BranchCategoryService } from '../../../../shared-services/ui/branch-category.service';

@Component({
  selector: 'osee-branch-picker',
  templateUrl: './branch-picker.component.html',
  styleUrls: ['./branch-picker.component.sass']
})
export class BranchPickerComponent implements OnInit,OnChanges {

  @Input() category: string="0";
  constructor (private branchCategoryService: BranchCategoryService) { }
  ngOnChanges(changes: SimpleChanges): void {
    this.branchCategoryService.category = this.category;
  }

  ngOnInit(): void {
  }

}
