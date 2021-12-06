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
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { combineLatest, iif, Observable, of } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { PlConfigUIStateService } from './services/pl-config-uistate.service';

@Component({
  selector: 'app-plconfig',
  templateUrl: './plconfig.component.html',
  styleUrls: ['./plconfig.component.sass']
})
export class PlconfigComponent implements OnInit {
  _updateRequired: Observable<boolean>= this.uiStateService.updateReq;
  _branchType: string = '';
  _loading: Observable<string>=this.uiStateService.loading
  isAllowedToDiff = combineLatest([this.uiStateService.viewBranchType, this.uiStateService.branchId, this.uiStateService.isInDiff]).pipe(
    //invalid conditions equals false
    switchMap(([branchType,branchId,inDiff])=>iif(()=>inDiff===false && branchId.length!==0&&branchId!=='-1'&&branchId!==undefined,of(true),of(false)))
  );
  diff = "./diff"
  currentRoute = this.route;
  constructor (private uiStateService: PlConfigUIStateService, private route: ActivatedRoute, private router: Router) {
    this.uiStateService.branchIdNum = '';
    this.uiStateService.viewBranchTypeString='';
    this.uiStateService.viewBranchType.subscribe((id) => {
      this._branchType = id;
    })
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map(() => this.route),
      switchMap(route => {
        while (route.firstChild) {
          route = route.firstChild
        }
        return of(route);
      }),
      filter((activatedRoute) => activatedRoute.outlet === 'primary'),
      switchMap((route) => combineLatest([route.paramMap, route.data]).pipe(
        map(([paramMap, data]) => {
          this.uiStateService.viewBranchTypeString = paramMap.get('branchType') || '';
          this.uiStateService.branchIdNum = paramMap.get('branchId') || '';
          return data;
        }),
        switchMap((data) => iif(() => data.diff !== undefined, of(data).pipe(
          map(data => {
            this.uiStateService.difference = data.diff;
            return data.diff;
          })
        ), of(data).pipe(
          map(data => {
            this.uiStateService.difference = [];
            return data;
          })
        )))
      ))
    ).subscribe();
   }

  ngOnInit(): void {
  }
  branchTypeSelected(branchType:string): void {
    this.uiStateService.viewBranchTypeString = branchType;
    this.uiStateService.branchIdNum = '';
    this.router.navigate([branchType], {
      relativeTo: this.route.parent,
      queryParamsHandling: 'merge',
    })
  }
  branchSelected(branch: number): void{
    this.uiStateService.branchIdNum = branch.toString();
    this.router.navigate([this._branchType,branch], {
      relativeTo: this.route.parent,
      queryParamsHandling: 'merge',
    })
  }
  navigateToDiff() {
    this.router.navigate(['diff'], {
      relativeTo: this.route.firstChild?.firstChild,
      queryParamsHandling: 'merge',
    });
  }
}
