import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { share} from 'rxjs/operators';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { trackableFeature } from '../../types/features/base';
import { defaultBaseFeature } from '../../types/features/feature';
import { modifyFeature, PLAddFeatureData, PLEditFeatureData, writeFeature } from '../../types/pl-config-features';
import { response } from '../../types/pl-config-responses';
import { AddFeatureDialogComponent } from '../add-feature-dialog/add-feature-dialog.component';
import { EditFeatureDialogComponent } from '../edit-feature-dialog/edit-feature-dialog.component';

@Component({
  selector: 'plconfig-feature-dropdown',
  templateUrl: './feature-dropdown.component.html',
  styleUrls: ['./feature-dropdown.component.sass']
})
export class FeatureDropdownComponent implements OnInit {
  selectedBranch: Observable<string> = this.uiStateService.branchId;
  branchApplicability = this.currentBranchService.branchApplicability.pipe(share());
  branchId: string | undefined;
  groups: string[] = [];
  constructor(private uiStateService: PlConfigUIStateService,private currentBranchService: PlConfigCurrentBranchService, public dialog: MatDialog) { 
    this.selectedBranch.subscribe((val) => {
      this.branchId = val;
    });
    this.branchApplicability.subscribe((response) => {
      this.groups = [];
      response.groups.forEach((element) => {
        this.groups.push(element.id);
      })
    });
  }

  ngOnInit(): void {
  }

  deleteFeature(feature: trackableFeature) {
    this.currentBranchService.deleteFeature(feature.id).subscribe((response: response) => {
      if (response.success) {
        this.uiStateService.updateReqConfig = true;
      }
    })
  }

  openEditDialog(feature: trackableFeature) {
    let dialogData = {
      currentBranch: this.branchId,
      editable:true,
      feature: new modifyFeature(feature, "","")
    }
    const dialogRef = this.dialog.open(EditFeatureDialogComponent, {
      data: dialogData,
      minWidth: '60%',
    })
    dialogRef.afterClosed().subscribe((result: PLEditFeatureData) => {
      if (result) {
        this.currentBranchService.modifyFeature(result.feature).subscribe((response: response) => {
        })
      }
    })


  }

  addFeature() {
    let dialogData:PLAddFeatureData ={
      currentBranch: this.branchId,
      feature: new writeFeature(new defaultBaseFeature()),
    }
    const dialogRef = this.dialog.open(AddFeatureDialogComponent, {
      data: dialogData,
      minWidth: '60%'
    });
    dialogRef.afterClosed().subscribe((result: PLAddFeatureData) => {
      if (result) {
        this.currentBranchService.addFeature(result.feature).subscribe((response: response) => {
        }); 
      }
    });
  }

}