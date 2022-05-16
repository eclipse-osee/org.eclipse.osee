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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { DiffReportServiceMock } from '../../../shared/mocks/diff-report-service.mock';
import { DiffReportService } from '../../../shared/services/ui/diff-report.service';
import { DiffReportTableComponent } from '../diff-report-table/diff-report-table.component';

import { SubmessageDiffsComponent } from './submessage-diffs.component';

describe('SubmessageDiffsComponent', () => {
  let component: SubmessageDiffsComponent;
  let fixture: ComponentFixture<SubmessageDiffsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: DiffReportService, useValue: DiffReportServiceMock },
      ],
      imports: [MatIconModule, MatTableModule],
      declarations: [ SubmessageDiffsComponent, DiffReportTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubmessageDiffsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
