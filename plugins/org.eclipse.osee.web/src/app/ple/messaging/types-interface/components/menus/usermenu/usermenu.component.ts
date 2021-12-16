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
import { combineLatest } from 'rxjs';
import { take, switchMap } from 'rxjs/operators';
import { ColumnPreferencesDialogComponent } from 'src/app/ple/messaging/shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { CurrentTypesService } from '../../../services/current-types.service';
import { PlMessagingTypesUIService } from '../../../services/pl-messaging-types-ui.service';

@Component({
  selector: 'app-usermenu',
  templateUrl: './usermenu.component.html',
  styleUrls: ['./usermenu.component.sass']
})
export class UsermenuComponent implements OnInit {

  constructor(private typesService: CurrentTypesService, private uiService: PlMessagingTypesUIService,public dialog: MatDialog) { }

  ngOnInit(): void {
  }
  openSettingsDialog() {
    combineLatest([this.typesService.inEditMode, this.uiService.BranchId]).pipe(
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
        switchMap((result) => this.typesService.updatePreferences(result))))
    ).subscribe();
  }
}
