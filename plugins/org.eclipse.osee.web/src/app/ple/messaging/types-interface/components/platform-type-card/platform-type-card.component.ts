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
import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { defer, iif, Observable, of, OperatorFunction } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { OSEEWriteApiResponse } from '../../../shared/types/ApiWriteResponse';
import { CurrentTypesService } from '../../services/current-types.service';
import { editPlatformTypeDialogData } from '../../types/editPlatformTypeDialogData';
import { editPlatformTypeDialogDataMode } from '../../types/EditPlatformTypeDialogDataMode.enum';
import { enumerationSet } from '../../types/enum';
import { PlatformType } from '../../types/platformType';
import { EditEnumSetDialogComponent } from '../edit-enum-set-dialog/edit-enum-set-dialog.component';
import { EditTypeDialogComponent } from '../edit-type-dialog/edit-type-dialog.component';

@Component({
  selector: 'ple-messaging-types-platform-type-card',
  templateUrl: './platform-type-card.component.html',
  styleUrls: ['./platform-type-card.component.sass']
})
export class PlatformTypeCardComponent implements OnInit {

  @Input() typeData!: PlatformType;
  edit: editPlatformTypeDialogDataMode = editPlatformTypeDialogDataMode.edit;
  copy: editPlatformTypeDialogDataMode = editPlatformTypeDialogDataMode.copy;
  inEditMode = this.typesService.inEditMode;
  constructor(public dialog: MatDialog, private typesService: CurrentTypesService) { }

  ngOnInit(): void {
  }

  /**
   * Opens a Dialog for either editing or creating a new platform type based on the current platform type
   * @param value Whether the dialog should be in edit or copy mode (see @enum {editPlatformTypeDialogDataMode})
   */
  openDialog(value:editPlatformTypeDialogDataMode) {
    let dialogData: editPlatformTypeDialogData = {
      mode: value,
      type:this.typeData
    }
    const copy = JSON.parse(JSON.stringify(this.typeData));
    const dialogRef=this.dialog.open(EditTypeDialogComponent, {
      data: dialogData,
      minWidth:"70%"
    })
    dialogRef.afterClosed()
      .pipe(
       switchMap( ({mode, type})=>iif(()=>mode===copy,this.typesService.copyType(type),this.getEditObservable(copy,{mode,type}))
      )
    ).subscribe()
  }

  /**
   * Gets an observable for updating the attributes of a platform type
   * @param copy Initial values of the platform type PRIOR to the dialog being opened
   * @param result Changed values of the platform type + mode AFTER the dialog is closed
   * @returns @type {Observable<OSEEWriteApiResponse>} observable containing results (see @type {OSEEWriteApiResponse} and @type {Observable})
   */
  getEditObservable(copy: PlatformType, result: editPlatformTypeDialogData): Observable<OSEEWriteApiResponse> {
    let newType: any = new Object();
    Object.keys(copy).forEach((value) => {
      if (copy[value as keyof PlatformType] !== result.type[value as keyof PlatformType]) {
        newType[value as keyof PlatformType] = result.type[value as keyof PlatformType];
      }
    })
    newType["id"] = copy["id"]
    
    
    return this.typesService.partialUpdate(newType);
  }

  openEnumDialog(makeChanges:boolean) {
    this.dialog.open(EditEnumSetDialogComponent, {
      data:of(this.typeData.id)
    }).afterClosed().pipe(
      filter(x => x !== undefined) as OperatorFunction<enumerationSet | undefined, enumerationSet>,
      switchMap((changes)=>iif(()=>makeChanges,this.typesService.changeEnumSet(changes)))
    ).subscribe();
  }
  
}
