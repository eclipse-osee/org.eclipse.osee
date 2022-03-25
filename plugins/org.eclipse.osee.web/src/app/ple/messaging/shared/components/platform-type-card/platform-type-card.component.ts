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
import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {  iif, of, OperatorFunction } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { editPlatformTypeDialogData } from '../../types/editPlatformTypeDialogData';
import { editPlatformTypeDialogDataMode } from '../../types/EditPlatformTypeDialogDataMode.enum';
import { enumerationSet } from '../../types/enum';
import { PlatformType } from '../../types/platformType';
import { EditEnumSetDialogComponent } from '../dialogs/edit-enum-set-dialog/edit-enum-set-dialog.component';
import { EditTypeDialogComponent } from '../dialogs/edit-type-dialog/edit-type-dialog.component';
import { enumsetDialogData } from '../../types/EnumSetDialogData';
import { PreferencesUIService } from '../../services/ui/preferences-ui.service';
import { EnumerationUIService } from '../../services/ui/enumeration-ui.service';
import { TypesUIService } from '../../services/ui/types-ui.service';

@Component({
  selector: 'ple-messaging-types-platform-type-card',
  templateUrl: './platform-type-card.component.html',
  styleUrls: ['./platform-type-card.component.sass']
})
export class PlatformTypeCardComponent implements OnInit {

  @Input() typeData!: PlatformType;
  edit: editPlatformTypeDialogDataMode = editPlatformTypeDialogDataMode.edit;
  copy: editPlatformTypeDialogDataMode = editPlatformTypeDialogDataMode.copy;
  inEditMode = this.preferenceService.inEditMode
  constructor(public dialog: MatDialog, private typesService: TypesUIService, private preferenceService: PreferencesUIService,private enumSetService: EnumerationUIService,) { }

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
        filter((val)=>val!==undefined),
        switchMap( ({mode, type})=>iif(()=>mode===copy,this.typesService.copyType(type),this.getEditObservable(copy,{mode,type}))
      )
    ).subscribe()
  }

  /**
   * already shared
   * Gets an observable for updating the attributes of a platform type
   * @param copy Initial values of the platform type PRIOR to the dialog being opened
   * @param result Changed values of the platform type + mode AFTER the dialog is closed
   * @returns @type {Observable<OSEEWriteApiResponse>} observable containing results (see @type {OSEEWriteApiResponse} and @type {Observable})
   */
  getEditObservable(copy: PlatformType, result: editPlatformTypeDialogData) {
    let newType: any = new Object();
    Object.keys(copy).forEach((value) => {
      if (copy[value as keyof PlatformType] !== result.type[value as keyof PlatformType]) {
        newType[value as keyof PlatformType] = result.type[value as keyof PlatformType];
      }
    })
    newType["id"] = copy["id"]
    
    
    return this.typesService.partialUpdate(newType);
  }

  /**
   * already shared
   * @param makeChanges 
   */
  openEnumDialog(makeChanges:boolean) {
    this.dialog.open(EditEnumSetDialogComponent, {
      data: of<enumsetDialogData>(
        {
          id: this.typeData.id || '',
          isOnEditablePage: true
        })
    }).afterClosed().pipe(
      filter(x => x !== undefined) as OperatorFunction<enumerationSet | undefined, enumerationSet>,
      take(1),
      switchMap(({ enumerations,...changes })=>iif(()=>makeChanges,this.enumSetService.changeEnumSet(changes,enumerations)))
    ).subscribe();
  }
  
}
