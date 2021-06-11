import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { share, take } from 'rxjs/operators';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { cfgGroup } from '../../types/pl-config-branch';
import { addCfgGroup } from '../../types/pl-config-cfggroups';
import { AddConfigurationGroupDialogComponent } from '../add-configuration-group-dialog/add-configuration-group-dialog.component';

@Component({
  selector: 'plconfig-configuration-group-dropdown',
  templateUrl: './configuration-group-dropdown.component.html',
  styleUrls: ['./configuration-group-dropdown.component.sass']
})
export class ConfigurationGroupDropdownComponent implements OnInit {
  branchApplicability = this.currentBranchService.branchApplicability.pipe(share());
  cfgGroups = this.currentBranchService.cfgGroups;
  constructor(private currentBranchService: PlConfigCurrentBranchService, public dialog: MatDialog,private uiStateService: PlConfigUIStateService) {
  }

  ngOnInit(): void {
  }
  public addConfigurationGroup() {
    let dialogData: addCfgGroup = {
      title:''
    }
    const dialogRef = this.dialog.open(AddConfigurationGroupDialogComponent, {
      data: dialogData,
      minWidth: '60%'
    });
    dialogRef.afterClosed().subscribe((result:addCfgGroup) => {
      this.currentBranchService.addConfigurationGroup({name:result.title}).pipe(take(1)).subscribe((response) => {
      })
    });
  }
  deleteGroup(id: string) {
    this.currentBranchService.deleteConfigurationGroup(id).pipe(take(1)).subscribe((response) => {
    })
  }
  synchronizeGroups(groups:cfgGroup[]) {
    groups.forEach((value) => {
      this.currentBranchService.synchronizeGroup(value.id).pipe(take(1)).subscribe((response) => {
        if (response.success) {
          this.uiStateService.updateReqConfig = true;
        }
      })
    })
  }
}
