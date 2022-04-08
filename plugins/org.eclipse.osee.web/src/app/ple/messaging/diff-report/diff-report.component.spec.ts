/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatTableModule } from '@angular/material/table';
import { TestScheduler } from 'rxjs/testing';
import { DiffReportServiceMock } from '../shared/mocks/diff-report-service.mock';
import { DiffReportService } from '../shared/services/ui/diff-report.service';
import { ConnectionDiffsComponent } from './connection-diffs/connection-diffs.component';

import { DiffReportComponent } from './diff-report.component';
import { MessageDiffsComponent } from './message-diffs/message-diffs.component';
import { NodeDiffsComponent } from './node-diffs/node-diffs.component';
import { StructureDiffsComponent } from './structure-diffs/structure-diffs.component';
import { SubmessageDiffsComponent } from './submessage-diffs/submessage-diffs.component';

describe('DiffReportComponent', () => {
  let component: DiffReportComponent;
  let fixture: ComponentFixture<DiffReportComponent>;
  let scheduler: TestScheduler;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: DiffReportService, useValue: DiffReportServiceMock },
      ],
      imports: [
        CommonModule,
        MatTableModule],
      declarations: [  
        ConnectionDiffsComponent, 
        DiffReportComponent,
        MessageDiffsComponent, 
        NodeDiffsComponent, 
        StructureDiffsComponent,
        SubmessageDiffsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DiffReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get the header name', () => {
    scheduler.run(() => {
      let expectedObservable = { a: { header: 'description', description: 'Description of the branch', humanReadable: 'Description' } };
      let expectedMarble = '(a)';
      scheduler.expectObservable(component.getHeaderByName('description')).toBe(expectedMarble, expectedObservable);
    })
  })

});
