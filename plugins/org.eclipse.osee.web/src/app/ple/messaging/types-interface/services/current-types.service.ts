import { Injectable } from '@angular/core';
import { iif, Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, repeatWhen, share, switchMap, tap } from 'rxjs/operators';
import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';
import { TypesService } from './types.service';
import { PlatformType } from '../types/platformType'
import { TypesApiResponse } from '../types/ApiResponse';

@Injectable({
  providedIn: 'root'
})
export class CurrentTypesService {

  private _typeData: Observable<PlatformType[]> = this.uiService.filter.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap(x => this.typesService.getFilteredTypes(x,this.uiService.BranchId.getValue()).pipe(
      repeatWhen(_ => this.uiService.typeUpdateRequired),
      share(),
      tap((y) => {
        this.uiService.updateTypes = false;
        if (y.length <= this.uiService.columnCount.getValue()) {
          this.uiService.singleLineAdjustmentNumber = 30;
        } else {
          this.uiService.singleLineAdjustmentNumber = 0;
        }
      })
    )),
  )

  constructor(private typesService: TypesService, private uiService: PlMessagingTypesUIService) { }

  /**
   * Returns a list of platform types based on current branch and filter conditions(debounced).
   * Sets the "single line adjustment" which is used to offset platform type cards in the grid when there is only one line of platform types
   * Also updates when insertions are done via API.
   * @returns @type {Observable<PlatformType[]>} list of platform types
   */
  get typeData() {
    return this._typeData;
  }

  /**
   * Updates the attributes of a platform type using the platform types PATCH API and current branch, id is required
   * @param body @type {Partial<PlatformType>} attributes to update + id of platform type
   * @returns @type {Observable<TypesApiResponse>} observable containing results (see @type {TypesApiResponse} and @type {Observable})
   */
  partialUpdate(body: Partial<PlatformType>) {
    return this.typesService.partialUpdateTypes(body, this.uiService.BranchId.getValue());
  }

  /**
   * Creates a new platform type using the platform types POST API, current branch,but without the id,idIntValue, and idString present
   * @param body @type {PlatformType} platform type to create
   * @returns @type {Observable<TypesApiResponse>} observable containing results (see @type {TypesApiResponse} and @type {Observable})
   */
  createType(body: PlatformType|Partial<PlatformType>) {
    delete body.id;
    return this.typesService.createType(body, this.uiService.BranchId.getValue()).pipe(
      tap((response) => {
        this.uiService.updateTypes = true;
      })
    );
  }

  get logicalTypes() {
    return this.typesService.logicalTypes;
  }

  getLogicalTypeFormDetail(id: string) {
    return this.typesService.getLogicalTypeFormDetail(id);
  }
}
