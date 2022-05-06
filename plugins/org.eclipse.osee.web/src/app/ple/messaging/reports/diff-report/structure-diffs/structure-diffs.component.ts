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
import { Component, OnInit } from '@angular/core';
import { from } from 'rxjs';
import { filter, reduce, switchMap } from 'rxjs/operators';
import { DiffReportService } from '../../../shared/services/ui/diff-report.service';
import { DiffHeaderType, elementDiffItem, structureDiffItem } from '../../../shared/types/DifferenceReport.d';

@Component({
  selector: 'app-structure-diffs',
  templateUrl: './structure-diffs.component.html',
  styleUrls: ['./structure-diffs.component.sass']
})
export class StructureDiffsComponent implements OnInit {

  constructor(private diffReportService: DiffReportService) { }

  ngOnInit(): void {
  }

  headers:(keyof structureDiffItem)[] = [
    'name',
    'description',
    'interfaceMinSimultaneity',
    'interfaceMaxSimultaneity',
    'interfaceTaskFileType',
    'interfaceStructureCategory'
  ]

  elementHeaders:(keyof elementDiffItem)[] = [
    'name',
    'description',
    'logicalType',
    'interfaceElementIndexStart',
    'interfaceElementIndexEnd',
    'elementSizeInBits',
    'interfacePlatformTypeMinval',
    'interfacePlatformTypeMaxval',
    'interfacePlatformTypeDefaultValue',
    'units',
    'enumeration',
    'interfaceElementAlterable',
    'notes',
    'applicability'
  ]

  headerType = DiffHeaderType.STRUCTURE;
  elementHeaderType = DiffHeaderType.ELEMENT;

  allStructures = this.diffReportService.structuresWithElements;

  structuresChanged = this.allStructures.pipe(
    switchMap(structures => from(structures).pipe(
      filter(structure => !structure.diffInfo?.added && !structure.diffInfo?.deleted),
      reduce((acc, curr) => [...acc, curr], [] as structureDiffItem[])
    ))
  )

  structuresAdded = this.allStructures.pipe(
    switchMap(structures => from(structures).pipe(
      filter(structure => structure.diffInfo?.added === true),
      reduce((acc, curr) => [...acc, curr], [] as structureDiffItem[])
    ))
  )

  structuresDeleted = this.allStructures.pipe(
    switchMap(structures => from(structures).pipe(
      filter(structure => structure.diffInfo?.deleted === true),
      reduce((acc, curr) => [...acc, curr], [] as structureDiffItem[])
    ))
  )

  getArrayLength(arr: structureDiffItem[]) {
    return [...Array(arr.length).keys()];
  }

}
