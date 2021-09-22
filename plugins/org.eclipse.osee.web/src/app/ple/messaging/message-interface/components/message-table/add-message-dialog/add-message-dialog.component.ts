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
import { EnumsService } from 'src/app/ple/messaging/shared/services/http/enums.service';
import { AddMessageDialog } from '../../../types/AddMessageDialog';
import { AddSubMessageDialogComponent } from '../../sub-message-table/add-sub-message-dialog/add-sub-message-dialog.component';

@Component({
  selector: 'app-add-message-dialog',
  templateUrl: './add-message-dialog.component.html',
  styleUrls: ['./add-message-dialog.component.sass']
})
export class AddMessageDialogComponent implements OnInit {

  rates = this.enumService.rates;
  types = this.enumService.types;
  periodicities = this.enumService.periodicities;
  constructor(public dialogRef: MatDialogRef<AddSubMessageDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: AddMessageDialog,private enumService: EnumsService) { }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }
}
