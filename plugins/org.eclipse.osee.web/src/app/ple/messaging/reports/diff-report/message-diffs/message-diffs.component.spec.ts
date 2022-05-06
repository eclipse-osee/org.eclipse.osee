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
import { DiffReportServiceMock } from '../../../shared/mocks/diff-report-service.mock';
import { DiffReportService } from '../../../shared/services/ui/diff-report.service';
import { DiffReportTableComponent } from '../diff-report-table/diff-report-table.component';

import { MessageDiffsComponent } from './message-diffs.component';

describe('MessageDiffsComponent', () => {
  let component: MessageDiffsComponent;
  let fixture: ComponentFixture<MessageDiffsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: DiffReportService, useValue: DiffReportServiceMock },
      ],
      declarations: [ MessageDiffsComponent, DiffReportTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageDiffsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
