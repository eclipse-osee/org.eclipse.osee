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
import { Component,OnInit } from '@angular/core';
import { MatRadioChange } from '@angular/material/radio';
import { BranchRoutedUIService } from '../../../services/branch-routed-ui.service';

@Component({
  selector: 'osee-branch-type-selector',
  templateUrl: './branch-type-selector.component.html',
  styleUrls: ['./branch-type-selector.component.sass']
})
export class BranchTypeSelectorComponent implements OnInit {

  branchTypes: string[] = ['Product Line', 'Working'];
  branchType = ""
  constructor(private routerState: BranchRoutedUIService) { }

  ngOnInit(): void {
    this.routerState.type.subscribe((value) => {
      this.branchType = value;
    })
  }

  changeBranchType(value: string) {
    this.routerState.branchType = value;
  }

  selectType(event: MatRadioChange) {
    this.changeBranchType(event.value as string)
  }
  normalizeType(type: string) {
    if (type === 'product line') {
      return 'baseline';
    }
    return type;
  }
}
