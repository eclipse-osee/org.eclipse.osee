import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { defer, Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CurrentTypesService } from '../../services/current-types.service';
import { TypesApiResponse } from '../../types/ApiResponse';
import { editPlatformTypeDialogData } from '../../types/editPlatformTypeDialogData';
import { editPlatformTypeDialogDataMode } from '../../types/EditPlatformTypeDialogDataMode.enum';
import { PlatformType } from '../../types/platformType';
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
    })
    dialogRef.afterClosed()
      .pipe(
      switchMap(
        x => defer(
          () => x.mode ? this.typesService.createType(x.type) : this.getEditObservable(copy,x)
      )
      )
    ).subscribe((result) => {})
  }

  /**
   * Gets an observable for updating the attributes of a platform type
   * @param copy Initial values of the platform type PRIOR to the dialog being opened
   * @param result Changed values of the platform type + mode AFTER the dialog is closed
   * @returns @type {Observable<TypesApiResponse>} observable containing results (see @type {TypesApiResponse} and @type {Observable})
   */
  getEditObservable(copy: PlatformType, result: editPlatformTypeDialogData): Observable<TypesApiResponse> {
    let newType: any = new Object();
    Object.keys(copy).forEach((value) => {
      if (copy[value as keyof PlatformType] !== result.type[value as keyof PlatformType]) {
        newType[value as keyof PlatformType] = result.type[value as keyof PlatformType];
      }
    })
    newType["id"] = copy["id"]
    
    
    return this.typesService.partialUpdate(newType);
  }
}
