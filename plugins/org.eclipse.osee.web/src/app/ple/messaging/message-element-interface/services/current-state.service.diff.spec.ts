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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { changeReportMock } from 'src/app/ple-services/http/change-report.mock';
import { DiffReportBranchService } from 'src/app/ple-services/ui/diff/diff-report-branch.service';
import { diffReportBranchServiceMock } from 'src/app/ple-services/ui/diff/diff-report-branch.service.mock';
import { applicabilityListServiceMock } from '../../shared/mocks/ApplicabilityListService.mock';
import { MimPreferencesServiceMock } from '../../shared/mocks/MimPreferencesService.mock';
import { typesServiceMock } from '../../shared/mocks/types.service.mock';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { TypesService } from '../../shared/services/http/types.service';
import { structureRepeatingWithChanges, structuresMockWithChangesMulti, structuresPreChanges } from '../mocks/ReturnObjects/Structures.mock';
import { elementServiceMock } from '../mocks/services/element.service.mock';
import { messageServiceMock } from '../mocks/services/messages.service.mock';
import { platformTypeServiceMock } from '../mocks/services/platform-type.service.mock';
import { CurrentStateService } from './current-state.service';
import { ElementService } from './element.service';
import { MessagesService } from './messages.service';
import { PlatformTypeService } from './platform-type.service';
import { StructuresService } from './structures.service';
import { ElementUiService } from './ui.service';


describe('CurrentStateService', () => {
  let service: CurrentStateService;
  let ui: ElementUiService;
  let scheduler: TestScheduler;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: ElementService, useValue: elementServiceMock },
        {
          provide: StructuresService, useValue: {
            getFilteredStructures(v1: string, v2: string, v3: string, v4: string, v5: string) { return of(structuresPreChanges) },
            getStructure(branchId: string, messageId: string, subMessageId: string, structureId: string, connectionId: string){return of (structuresPreChanges[0])}
          }
        },
        { provide: MessagesService, useValue: messageServiceMock },
        { provide: TypesService, useValue: typesServiceMock },
        { provide: MimPreferencesService, useValue: MimPreferencesServiceMock },
        { provide: ApplicabilityListService, useValue: applicabilityListServiceMock },
        { provide: ElementUiService },
        { provide: DiffReportBranchService, useValue: diffReportBranchServiceMock },
        CurrentStateService
      ],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(CurrentStateService);
    ui = TestBed.inject(ElementUiService);
    httpTestingController = TestBed.inject(HttpTestingController);
    ui.DiffMode = false;
    ui.difference = [];
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch structures array with diff', () => {
    service.DiffMode = true;
    service.difference = changeReportMock;
    service.subMessageId="201301"
    scheduler.run(({ expectObservable, cold }) => {
      service.branchId = '10';
      const values = { a: [structuresMockWithChangesMulti[0],structuresMockWithChangesMulti[1],structuresMockWithChangesMulti[2]], b: true,c:undefined,d:[structuresMockWithChangesMulti[0],structuresMockWithChangesMulti[1],structuresMockWithChangesMulti[2],structuresMockWithChangesMulti[3]],e:structuresMockWithChangesMulti };
      expectObservable(service.structures).toBe('500ms (aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaddddeeee)', values);
    })
  })

  it('should fetch structures single with diff', () => {
    service.DiffMode = true;
    service.difference = changeReportMock;
    service.subMessageId="201301"
    scheduler.run(({ expectObservable, cold }) => {
      service.branchId = '10';
      const values = { a: structureRepeatingWithChanges, b: true,c:undefined };
      expectObservable(service.getStructureRepeating('10')).toBe('(a)', values);
    })
  })
  afterEach(() => {
    ui.DiffMode = false;
    ui.difference = [];
  })
});
