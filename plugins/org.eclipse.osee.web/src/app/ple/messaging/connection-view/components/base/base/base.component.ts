import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ColumnPreferencesDialogComponent } from 'src/app/ple/messaging/shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { RouteStateService } from '../../../services/route-state-service.service';
import { branchStorage} from '../../../../shared/types/branchstorage'
import { settingsDialogData } from 'src/app/ple/messaging/shared/types/settingsdialog';

@Component({
  selector: 'osee-connectionview-base',
  templateUrl: './base.component.html',
  styleUrls: ['./base.component.sass']
})
export class BaseComponent implements OnInit {

  editMode: boolean = false;
  constructor (private routeState: RouteStateService, public dialog: MatDialog) { }
  

  ngOnInit(): void {
    let branchStorage = JSON.parse(
      localStorage.getItem(this.routeState.id.getValue()) || '{}'
    ) as branchStorage;
    if (branchStorage?.mim?.editMode) {
      this.editMode = branchStorage.mim.editMode;
    }
  }

  openSettingsDialog() {
    let dialogData:settingsDialogData = {
      branchId: this.routeState.id.getValue(),
      allHeaders2: [],
      allowedHeaders2: [],
      allHeaders1: [],
      allowedHeaders1: [],
      editable: this.editMode,
      headers1Label: '',
      headers2Label: '',
      headersTableActive: false,
    };
    const dialogRef = this.dialog.open(ColumnPreferencesDialogComponent, {
      data: dialogData,
    });
    dialogRef.afterClosed().subscribe((result:settingsDialogData) => {
      this.editMode = result.editable;
      //@todo: remove when user preferences are available on backend
      if (localStorage.getItem(this.routeState.id.getValue())) {
        let branchStorage = JSON.parse(
          localStorage.getItem(this.routeState.id.getValue()) ||
            '{}'
        ) as branchStorage;
        branchStorage.mim['editMode'] = result.editable;
        localStorage.setItem(
          this.routeState.id.getValue(),
          JSON.stringify(branchStorage)
        );
      } else {
        localStorage.setItem(
          this.routeState.id.getValue(),
          JSON.stringify({
            mim: {
              editMode: result.editable,
            },
          })
        );
      }
    });
  }

}
