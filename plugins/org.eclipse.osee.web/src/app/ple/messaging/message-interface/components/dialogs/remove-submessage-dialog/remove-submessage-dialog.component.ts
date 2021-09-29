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
import { RemoveSubMessageDialogData } from '../../../types/RemoveSubMessageDialog';

@Component({
  selector: 'app-remove-submessage-dialog',
  templateUrl: './remove-submessage-dialog.component.html',
  styleUrls: ['./remove-submessage-dialog.component.sass']
})
export class RemoveSubmessageDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<RemoveSubmessageDialogComponent>, @Inject(MAT_DIALOG_DATA) public data:RemoveSubMessageDialogData) { }

  ngOnInit(): void {
  }

}
