import { Component,OnInit } from '@angular/core';
import { MatRadioChange } from '@angular/material/radio';
import { ConnectionViewRouterService } from '../../../services/connection-view-router.service';

@Component({
  selector: 'osee-connectionview-branch-type-selector',
  templateUrl: './branch-type-selector.component.html',
  styleUrls: ['./branch-type-selector.component.sass']
})
export class BranchTypeSelectorComponent implements OnInit {

  branchTypes: string[] = ['Product Line', 'Working'];
  branchType = ""
  constructor(private routerState: ConnectionViewRouterService) { }

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
}
