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
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { MatRadioChange } from '@angular/material/radio';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
interface branchMapping {
  [value:string]:string
}
@Component({
  selector: 'plconfig-branch-type-selector',
  templateUrl: './branch-type-selector.component.html',
  styleUrls: ['./branch-type-selector.component.sass']
})
export class BranchTypeSelectorComponent implements OnInit {
  branchTypes: string[] = ['Product Line', 'Working']; //, 'All'
  branchTypeMapping: branchMapping[] = [{
    "product line": 'baseline'
  }, {
    'working':'working'
  }]
  @Output() selectedType = new EventEmitter<string>();
  selectedBranchTypeObservable = this.uiStateService.viewBranchType;
  state:string=""
  constructor(private uiStateService: PlConfigUIStateService) { 
    this.selectedBranchTypeObservable.subscribe((value) => {
      let mapping: any = this.branchTypeMapping.find((element) => Object.values(element)[0] === value);
      if (mapping) {
        let newValue: any = Object.keys(mapping)[0];
        this.state = newValue;
      } else {
        this.state = value; //this whole chain of if statements is an ugly af migration
      }
    })
  }

  ngOnInit(): void {
  }
  selectType(event: MatRadioChange) {
    let mapping: any = this.branchTypeMapping.find((element) => Object.keys(element)[0] === event.value);
    let value: any = Object.values(mapping)[0]
    this.selectedType.emit(value)
  }

}
