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
import { filter, reduce, switchMap, tap } from 'rxjs/operators';
import { DiffReportService } from '../../shared/services/ui/diff-report.service';
import { DiffHeaderType, nodeDiffItem } from '../../shared/types/DifferenceReport.d';

@Component({
  selector: 'app-node-diffs',
  templateUrl: './node-diffs.component.html',
  styleUrls: ['./node-diffs.component.sass']
})
export class NodeDiffsComponent implements OnInit {


  constructor(private diffReportService: DiffReportService) { }

  ngOnInit(): void {
  }

  headers:(keyof nodeDiffItem)[] = [
    'name',
    'description',
    'address',
    'color',
    'applicability'
  ];

  headerType = DiffHeaderType.NODE;

  allNodes = this.diffReportService.nodes;

  nodesChanged = this.allNodes.pipe(
    switchMap(nodes => from(nodes).pipe(
      filter(node => !node.diffInfo?.added && !node.diffInfo?.deleted),
      reduce((acc, curr) => [...acc, curr], [] as nodeDiffItem[])
    ))
  )

  nodesAdded = this.allNodes.pipe(
    switchMap(nodes => from(nodes).pipe(
      filter(node => node.diffInfo?.added === true),
      reduce((acc, curr) => [...acc, curr], [] as nodeDiffItem[])
    ))
  )

  nodesDeleted = this.allNodes.pipe(
    switchMap(nodes => from(nodes).pipe(
      filter(node => node.diffInfo?.deleted === true),
      reduce((acc, curr) => [...acc, curr], [] as nodeDiffItem[])
    ))
  )

}
