import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSelectChange } from '@angular/material/select';
import { from, Observable, Subject } from 'rxjs';
import {
  concatMap,
  debounceTime,
  distinctUntilChanged,
  share,
  switchMap,
  tap,
} from 'rxjs/operators';
import { CurrentTypesService } from '../../services/current-types.service';
import { logicalType, logicalTypeFieldInfo, logicalTypeFormDetail } from '../../types/logicaltype';
import { logicalTypefieldValue } from '../../types/newTypeDialogDialogData';
import { PlatformType } from '../../types/platformType';

@Component({
  selector: 'app-new-type-dialog',
  templateUrl: './new-type-dialog.component.html',
  styleUrls: ['./new-type-dialog.component.sass'],
})
export class NewTypeDialogComponent implements OnInit {
  returnObj: Partial<PlatformType> = {};
  type: string = "";
  private _typeName:string =""
  private _typeSubject: Subject<string> = new Subject();
  logicalTypes: Observable<logicalType[]> = this.typesService.logicalTypes;
  fields: logicalTypefieldValue[] = [];
  formInfo = this._typeSubject.pipe(
    debounceTime(500),
    distinctUntilChanged(),
    switchMap((x) =>
      this.typesService.getLogicalTypeFormDetail(x).pipe(share())
    ),
    share()
  );
  formDetail!: logicalTypeFormDetail;
  private _fieldObs = this.formInfo.pipe(
    tap((c) => {
      this.fields = [];
      this._typeName = c.name;
    }),
    concatMap((a) => from(a.fields)),
    tap((x) => {
      this.fields.push({
        name: x.attributeType,
        value: '',
      });
    }),
    debounceTime(200),
    tap((z) => {
      this.fields.push({
        name: "interfaceLogicalType",
        value: this._typeName
      })
    })
  );
  constructor(
    public dialogRef: MatDialogRef<NewTypeDialogComponent>,
    private typesService: CurrentTypesService
  ) {
    this._fieldObs.subscribe();
    this.formInfo.subscribe((value) => {
      this.formDetail = value;
    })
  }

  ngOnInit(): void { }
  /**
   * sets the current type to query
   * @param id id of type
   */
  setType(id: string) {
    this._typeSubject.next(id);
  }
  /**
   * Validates the second step of the form
   * @returns true if all required fields have a filled in value
   */
  isStep2Complete() {
    let result: boolean = true;
    if (this.formDetail !== undefined) {
      this.formDetail.fields.forEach((value, index) => {
        if (value.required && this.fields[index].value.length === 0) {
          if (value.editable) {
            result = false;
          }
          if (!value.editable && this.fields[index].value !== value.defaultValue) {
            result = false; 
          }
        }
      })
    }
    return result;
  }
  setDefaultValue(form: logicalTypeFieldInfo, index: number) {
    if (this.fields[index].name === form.attributeType) {
      if (this.fields[index].value === null || this.fields[index].value === undefined || this.fields[index].value === "") {
        this.fields[index].value = form.defaultValue;
      }
    }
  }
}
