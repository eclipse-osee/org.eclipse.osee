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
import { takeUntil, map, share, shareReplay, take, switchMap } from 'rxjs/operators';
import { ColumnPreferencesDialogComponent } from 'src/app/ple/messaging/shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { HeaderService } from 'src/app/ple/messaging/shared/services/ui/header.service';
import { CurrentMessagesService } from '../../../services/current-messages.service';

@Component({
  selector: 'app-usermenu',
  templateUrl: './usermenu.component.html',
  styleUrls: ['./usermenu.component.sass']
})
export class UsermenuComponent implements OnInit {
  preferences = this.messageService.preferences.pipe(takeUntil(this.messageService.done));
  inEditMode = this.preferences.pipe(
    map((r) => r.inEditMode),
    share(),
    shareReplay(1),
    takeUntil(this.messageService.done)
  );
  constructor(private messageService: CurrentMessagesService,public dialog: MatDialog, private headerService:HeaderService,) { }

  ngOnInit(): void {
  }
  openSettingsDialog() {
    combineLatest([this.inEditMode, this.messageService.BranchId]).pipe(
      take(1),
      switchMap(([edit, branch]) => this.dialog.open(ColumnPreferencesDialogComponent, {
        data: {
          branchId: branch,
          allHeaders2: [],
          allowedHeaders2: [],
          allHeaders1: [],
          allowedHeaders1: [],
          editable: edit,
          headers1Label: 'Structure Headers',
          headers2Label: 'Element Headers',
          headersTableActive: false,
        }
      }).afterClosed().pipe(
        take(1),
        switchMap((result) => this.messageService.updatePreferences(result))))
    ).subscribe();
  }
}
