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
import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { apiURL } from 'src/environments/environment';
import { ConvertMessageInterfaceTitlesToStringPipe } from '../../../pipes/convert-message-interface-titles-to-string.pipe';
import { branchApplicability } from '../../../types/branch.applic';
import { settingsDialogData } from '../../../types/settingsdialog';

import { ColumnPreferencesDialogComponent } from './column-preferences-dialog.component';

describe('ColumnPreferencesDialogComponent', () => {
  let component: ColumnPreferencesDialogComponent;
  let fixture: ComponentFixture<ColumnPreferencesDialogComponent>;
  let dialogData:settingsDialogData= {
    allowedHeaders1: ['s1','s2'],
    allHeaders1: ['s1','s2','s3'],
    allowedHeaders2: ['e1','e2'],
    allHeaders2: ['e1', 'e2', 'e3'],
    branchId: '10',
    editable: false,
    headers1Label: "Headers1 Label",
    headers2Label: "Headers2 Label",
    headersTableActive:true
  };
  let httpClient :HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatDialogModule, MatFormFieldModule,NoopAnimationsModule, MatListModule, FormsModule,MatButtonModule,MatTableModule,MatCheckboxModule, HttpClientTestingModule],
      declarations: [ColumnPreferencesDialogComponent, ConvertMessageInterfaceTitlesToStringPipe],
      providers: [{ provide: MatDialogRef, useValue: {} },
      {provide:MAT_DIALOG_DATA,useValue:dialogData}]
    })
      .compileComponents();
      httpClient = TestBed.inject(HttpClient);
      httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ColumnPreferencesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the backend to get whether a branch is editable', () => {
    let testData: branchApplicability = {
      associatedArtifactId: '-1',
      branch: {
        id: '-1',
        viewId: '-1',
        idIntValue: -1,
        name:''
      },
      editable: true,
      features: [],
      groups: [],
      parentBranch: {
        id: '-1',
        viewId: '-1',
        idIntValue: -1,
        name:''
      },
      views:[]
    }
    const req = httpTestingController.expectOne(apiURL + '/orcs/applicui/branch/' + 10);
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  });
});
