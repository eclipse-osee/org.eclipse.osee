import { Injectable } from '@angular/core';
import { combineLatest, from, iif, Observable, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, reduce, repeatWhen, share, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';
import { TypesService } from './types.service';
import { PlatformType } from '../types/platformType'
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { transaction } from 'src/app/transactions/transaction';
import { settingsDialogData } from '../../shared/types/settingsdialog';

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

  private _preferences = combineLatest([this.uiService.BranchId, this.userService.getUser()]).pipe(
    share(),
    filter(([id, user]) => id !== "" && id !== '-1'),
    switchMap(([id, user]) => this.preferenceService.getUserPrefs(id, user).pipe(
      repeatWhen(_ => this.uiService.typeUpdateRequired),
      share(),
      shareReplay(1)
    )),
    shareReplay(1)
  );

  private _inEditMode = this.preferences.pipe(
    map((x) => x.inEditMode)
  );

  private _branchPrefs = combineLatest([this.uiService.BranchId, this.userService.getUser()]).pipe(
    share(),
    switchMap(([branch, user]) => this.preferenceService.getBranchPrefs(user).pipe(
      repeatWhen(_ => this.uiService.typeUpdateRequired),
      share(),
      switchMap((branchPrefs) => from(branchPrefs).pipe(
        filter((pref) => !pref.includes(branch + ":")),
        reduce((acc, curr) => [...acc, curr], [] as string[])
      )),
      shareReplay(1)
    )),
    shareReplay(1)
  );
  constructor(private typesService: TypesService, private uiService: PlMessagingTypesUIService, private preferenceService: MimPreferencesService,private userService: UserDataAccountService) { }

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
    return this.typesService.changePlatformType(this.uiService.BranchId.getValue(), body).pipe(
      take(1),
      switchMap((transaction) => this.typesService.performMutation(transaction, this.uiService.BranchId.getValue()).pipe(
        tap(() => {
          this.uiService.updateTypes = true;
        })
      ))
    )
  }

  /**
   * Creates a new platform type using the platform types POST API, current branch,but without the id,idIntValue, and idString present
   * @param body @type {PlatformType} platform type to create
   * @returns @type {Observable<TypesApiResponse>} observable containing results (see @type {TypesApiResponse} and @type {Observable})
   */
  createType(body: PlatformType|Partial<PlatformType>) {
    delete body.id;
    return this.typesService.createPlatformType(this.uiService.BranchId.getValue(), body, []).pipe(
      take(1),
      switchMap((transaction) => this.typesService.performMutation(transaction, this.uiService.BranchId.getValue()).pipe(
        tap(() => {
          this.uiService.updateTypes = true;
        })
      ))
    )
  }

  get logicalTypes() {
    return this.typesService.logicalTypes;
  }

  getLogicalTypeFormDetail(id: string) {
    return this.typesService.getLogicalTypeFormDetail(id);
  }

  public get preferences() {
    return this._preferences;
  }
  public get inEditMode() {
    return this._inEditMode;
  }
  public get BranchPrefs() {
    return this._branchPrefs;
  }

  updatePreferences(preferences: settingsDialogData) {
    return this.createUserPreferenceBranchTransaction(preferences.editable).pipe(
      take(1),
      switchMap((transaction) => this.typesService.performMutation(transaction,this.uiService.BranchId.getValue()).pipe(
        take(1),
        tap(() => {
          this.uiService.updateTypes = true;
        })
      )
      )
    )
  }

  private createUserPreferenceBranchTransaction(editMode:boolean) {
    return combineLatest(this.preferences, this.uiService.BranchId, this.BranchPrefs).pipe(
      take(1),
      switchMap(([prefs, branch, branchPrefs]) =>
        iif(
        () => prefs.hasBranchPref,
          of<transaction>(
            {
              branch: "570",
              txComment: 'Updating MIM User Preferences',
              modifyArtifacts:
                [
                  {
                    id: prefs.id,
                    setAttributes:
                      [
                        { typeName: "MIM Branch Preferences", value: [...branchPrefs, `${branch}:${editMode}`] }
                      ],
                  }
                ]
            }
          ),
          of<transaction>(
            {
              branch: "570",
              txComment: "Updating MIM User Preferences",
              modifyArtifacts:
                [
                  {
                    id: prefs.id,
                    addAttributes:
                      [
                        { typeName: "MIM Branch Preferences", value: `${branch}:${editMode}` }
                      ]
                  }
                ]
              }
          ),
        )
      ))
  }
}
