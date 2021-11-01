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
import { MatDialog } from '@angular/material/dialog';
import { ColumnPreferencesDialogComponent } from 'src/app/ple/messaging/shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { RouteStateService } from '../../../services/route-state-service.service';
import { settingsDialogData } from 'src/app/ple/messaging/shared/types/settingsdialog';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { map, share, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { combineLatest } from 'rxjs';
import { transportType } from 'src/app/ple/messaging/shared/types/connection';
import { applic } from 'src/app/types/applicability/applic';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'osee-connectionview-base',
  templateUrl: './base.component.html',
  styleUrls: ['./base.component.sass']
})
export class BaseComponent implements OnInit {

  preferences = this.graphService.preferences;
  inEditMode = this.graphService.preferences.pipe(
    map((r) => r.inEditMode),
    share(),
    shareReplay(1)
  )
  inDiffMode=this.graphService.InDiff;
  sideNav = this.graphService.sideNavContent;
  sideNavOpened = this.sideNav.pipe(
    map((value) => value.opened),
  )
  constructor (private routeState: RouteStateService, public dialog: MatDialog, private graphService: CurrentGraphService, private router:Router, private route: ActivatedRoute) {
   }
  
  ngOnInit(): void {}

  openSettingsDialog() {
    combineLatest([this.inEditMode, this.routeState.id]).pipe(
      take(1),
      switchMap(([edit, id]) => this.dialog.open(ColumnPreferencesDialogComponent, {
        data: {
          branchId: id,
          allHeaders2: [],
          allowedHeaders2: [],
          allHeaders1: [],
          allowedHeaders1: [],
          editable: edit,
          headers1Label: '',
          headers2Label: '',
          headersTableActive: false,
        }
      }).afterClosed().pipe(
        take(1),
        switchMap((result) => this.graphService.updatePreferences(result))))
    ).subscribe();
  }
  viewDiff(open:boolean,value:string|number|applic|transportType, header:string) {
    this.graphService.sideNav = { opened: open,field:header, currentValue: value };
  }

  navigateToDiff() {
    this.router.navigate(['diff'], {
      relativeTo: this.route,
      queryParamsHandling: 'merge',
    });
  }
}
