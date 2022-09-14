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
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { affectedArtifactWarning } from '../../../types/affectedArtifact';

@Component({
  selector: 'osee-affected-artifact-dialog',
  templateUrl: './affected-artifact-dialog.component.html',
  styleUrls: ['./affected-artifact-dialog.component.sass']
})
export class AffectedArtifactDialogComponent<T=unknown> implements OnInit {

  constructor(public dialogRef: MatDialogRef<AffectedArtifactDialogComponent<T>>, @Inject(MAT_DIALOG_DATA) public data: affectedArtifactWarning<T>,) { }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }
}
