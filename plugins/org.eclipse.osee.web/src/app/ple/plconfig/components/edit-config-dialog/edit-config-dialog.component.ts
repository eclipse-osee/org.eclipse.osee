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
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSelectChange } from '@angular/material/select';
import { Observable } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';
import { cfgGroup } from '../../types/pl-config-branch';
import { PLEditConfigData } from '../../types/pl-edit-config-data';

@Component({
  selector: 'plconfig-edit-configuration-dialog',
  templateUrl: './edit-config-dialog.component.html',
  styleUrls: ['./edit-config-dialog.component.sass']
})
export class EditConfigurationDialogComponent implements OnInit {
  branchApplicability: Observable<PlConfigApplicUIBranchMapping>;
  cfgGroups: Observable<cfgGroup[]>;
  productApplicabilities: Observable<string[]>;
  constructor(private typeService: PlConfigTypesService,public dialogRef: MatDialogRef<EditConfigurationDialogComponent>,@Inject(MAT_DIALOG_DATA) public data: PLEditConfigData,private branchService: PlConfigBranchService,private currentBranchService: PlConfigCurrentBranchService) {
    this.branchApplicability = this.branchService.getBranchApplicability(data.currentBranch);
    this.cfgGroups = this.currentBranchService.cfgGroups;
    this.productApplicabilities = this.typeService.productApplicabilityTypes;
   }

  ngOnInit(): void {
  }
  selectBranch(event: MatSelectChange) {
    this.data.copyFrom = event.value;
  }
  selectCfgGroup(event: MatSelectChange) {
    this.data.group = event.value;
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
  valueTracker(index: any, item: any) {
    return index;
  }
  productTypesSelection(value: any) {
    console.log(value);
  }
}
