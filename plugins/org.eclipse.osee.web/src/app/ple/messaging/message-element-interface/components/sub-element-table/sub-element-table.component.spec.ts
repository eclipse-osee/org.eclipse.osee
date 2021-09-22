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
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { ConvertMessageInterfaceTitlesToStringPipe } from '../../../shared/pipes/convert-message-interface-titles-to-string.pipe';
import { SharedMessagingModule } from '../../../shared/shared-messaging.module';
import { EditElementFieldComponent } from './edit-element-field/edit-element-field.component';

import { SubElementTableComponent } from './sub-element-table.component';

describe('SubElementTableComponent', () => {
  let component: SubElementTableComponent;
  let fixture: ComponentFixture<SubElementTableComponent>;
  let expectedData = [
    {
      beginWord: 'BEGIN',
      endWord: "END",
      BeginByte: '0',
      EndByte: '32',
      Sequence: 'Sequence',
      ElementName: 'name1',
      Units: 'N/A',
      MinValue: '0',
      MaxValue: '1',
      AlterableAfterCreationValid: false,
      Description: "A description",
      EnumLiteralsDesc: "Description of enum literals",
      Notes: "Notes go here",
      DefaultValue: "0",
      isArray: false,
    },
    {
      beginWord: 'Hello',
      endWord: "World",
      BeginByte: '0',
      EndByte: '32',
      Sequence: 'Sequence',
      ElementName: 'name2',
      Units: 'N/A',
      MinValue: '0',
      MaxValue: '1',
      AlterableAfterCreationValid: false,
      Description: "A description",
      EnumLiteralsDesc: "Description of enum literals",
      Notes: "Notes go here",
      DefaultValue: "0",
      isArray: false,
    }
  ];
  let router: any;

  beforeEach(async () => {
    router = jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl'],['paramMap']);
    await TestBed.configureTestingModule({
      imports:[CommonModule,MatDialogModule,MatTableModule,MatMenuModule,MatFormFieldModule,MatInputModule,FormsModule,NoopAnimationsModule, OseeStringUtilsDirectivesModule, OseeStringUtilsPipesModule, RouterTestingModule,SharedMessagingModule, HttpClientTestingModule],
      declarations: [SubElementTableComponent, ConvertMessageInterfaceTitlesToStringPipe, EditElementFieldComponent],
      providers: [{ provide: Router, useValue: router }, {
        provide: ActivatedRoute, useValue: {
          paramMap: of(convertToParamMap({ branchId: "10",branchType:"working" }))
      }}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubElementTableComponent);
    component = fixture.componentInstance;
    component.data = expectedData;
    component.dataSource.filter="name1"
    component.filter="element: name1"
    //fixture.detectChanges();
  });

  it('should create',async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    expect(component).toBeTruthy();
    expect(component.data === expectedData).toBeTruthy();
    expect(component.filter === 'element: name1').toBeTruthy();
    expect(component.dataSource.filter === 'name1').toBeTruthy();
  });
  it('should update filter on changes',async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    component.filter = "element: name2";
    component.ngOnChanges({
      data: new SimpleChange(expectedData, expectedData, false),
      filter: new SimpleChange('element: name1', 'element: name2', false)
    });
    await fixture.whenStable();
    expect(component.dataSource.filter === 'name2').toBeTruthy();
    expect(component).toBeTruthy();
  });

  it('should navigate to //types/10', () => {
    component.navigateTo("10");
    expect(router.navigate).toHaveBeenCalledWith(['','','types','10'],{relativeTo: undefined, queryParamsHandling:'merge'});
  });

});
