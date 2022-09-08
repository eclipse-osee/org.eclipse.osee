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
import { Injectable } from '@angular/core';
import { BehaviorSubject, from, iif } from 'rxjs';
import { filter, map, mergeMap, reduce, shareReplay } from 'rxjs/operators';
import { element } from '../../types/element';
import { structure } from '../../types/structure';
import { message } from '../../../message-interface/types/messages';
import { subMessage } from '../../../message-interface/types/sub-messages';
import { branchSummaryHeaderDetail, connectionDiffHeaderDetail, diffReportSummaryHeaderDetail, elementDiffHeaderDetail, elementHeaderDetail, messageDiffHeaderDetail, messageHeaderDetail, nodeDiffHeaderDetail, structureDiffHeaderDetail, structureHeaderDetail, submessageDiffHeaderDetail, subMessageHeaderDetail } from '../../types/headerDetail';
import { branchSummary, connectionDiffItem, DiffHeaderType, diffReportSummaryItem, elementDiffItem, messageDiffItem, nodeDiffItem, structureDiffItem, submessageDiffItem } from '../../types/DifferenceReport.d';

@Injectable({
  providedIn: 'root'
})
export class HeaderService {
  private _allElements = new BehaviorSubject<elementHeaderDetail[]>([
    { header: 'name', description: 'Name of element', humanReadable: 'Name' },
    { header: 'platformTypeName2', description: 'Platform Type of Element', humanReadable: 'Type' },
    { header: 'interfaceElementIndexStart', description: 'Starting Index of Element Array', humanReadable: 'Start Index' },
    { header: 'interfaceElementIndexEnd', description: 'End Index of Element Array', humanReadable: 'End Index' },
    { header: 'logicalType', description: 'Primitive Type of Element', humanReadable: 'Logical Type' },
    { header: 'interfacePlatformTypeDefaultValue', description: 'Default value of Element or Element Array', humanReadable: 'Default' },
    { header: 'interfacePlatformTypeMinval', description: 'Minimum Value of Element', humanReadable: 'Min' },
    { header: 'interfacePlatformTypeMaxval', description: 'Maximum Value of Element', humanReadable: 'Max' },
    { header: 'interfacePlatformTypeDescription', description: 'Description of the Type', humanReadable: 'Type Description' },
    { header: 'beginWord', description: '(Computed) Beginning Word of Element/Element Array', humanReadable: 'Begin Word' },
    { header: 'endWord', description: '(Computed) Ending Word of Element/Element Array', humanReadable: 'End Word' },
    { header: 'beginByte', description: '(Computed) Beginning Byte of Element/Element Array', humanReadable: 'Begin Byte' },
    { header: 'endByte', description: '(Computed) Ending Byte of Element/Element Array', humanReadable: 'End Byte' },
    { header: 'interfaceElementAlterable', description: 'Whether or not a given Element is alterable', humanReadable: 'Alterable' },
    { header: 'description', description: 'Description of a given element', humanReadable: 'Description' },
    { header: 'notes', description: 'Notes corresponding to a given element, for example, specific enum literal descriptions for a given element', humanReadable: 'Notes' },
    { header: 'applicability', description: 'Applicability of a given element', humanReadable: 'Applicability' },
    { header: 'units', description: 'Units of the platform type associated with the given element', humanReadable: 'Units' },
    { header: 'enumLiteral', description: 'Enumerated Literals of Element', humanReadable: 'Enumerated Literals' },
  ]).pipe(
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allStructures = new BehaviorSubject<structureHeaderDetail[]>([
    { header: 'name', description: 'Name of structure', humanReadable: 'Name' },
    { header: 'description', description: 'Description of a given structure', humanReadable: 'Description' },
    { header: 'interfaceMinSimultaneity', description: 'Minimum occurences of a given structure', humanReadable: 'Min Simult.' },
    { header: 'interfaceMaxSimultaneity', description: 'Maximum occurences of a given structure', humanReadable: 'Max Simult.' },
    { header: 'interfaceTaskFileType', description: 'Type of Task File', humanReadable: 'Task File Type' },
    { header: 'interfaceStructureCategory', description: 'Category of Structure', humanReadable: 'Category' },
    { header: 'numElements', description: '(Computed) Number of elements in a given structure', humanReadable: 'Num. Elements' },
    { header: 'sizeInBytes', description: '(Computed) Size of structure, given in bytes', humanReadable: 'Size(B)' },
    { header: 'bytesPerSecondMinimum', description: '(Computed) Minimum rate of a given structure calculated as Minimum Simultaneity x Size In Bytes', humanReadable: 'Min BPS' },
    { header: 'bytesPerSecondMaximum', description: '(Computed) Maximum rate of a given structure calculated as Maximum Simultaneity x Size In Bytes', humanReadable: 'Max BPS' },
    { header: 'applicability', description: 'Applicability of a given structure', humanReadable: 'Applicability' },
    { header: 'txRate', description: 'Transmission Rate of Message', humanReadable: 'Tx Rate'}
  ]).pipe(
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allMessages = new BehaviorSubject<messageHeaderDetail[]>([
    { header: 'name', description: 'Name of message', humanReadable: 'Name' },
    { header: 'description', description: 'Description of a given message', humanReadable: 'Description' },
    { header: 'interfaceMessageNumber', description: 'Order of message', humanReadable: 'Message Number' },
    { header: 'interfaceMessagePeriodicity', description: 'Periodicity of message(i.e. Aperiodic,OnDemand, Periodic)', humanReadable: 'Periodicity' },
    { header: 'interfaceMessageRate', description: 'Rate at which message is transmitted', humanReadable: 'TxRate' },
    { header: 'interfaceMessageWriteAccess', description: 'TBD', humanReadable: 'Read/Write' },
    { header: 'interfaceMessageType', description: 'Type of Message', humanReadable: 'Type' },
    { header: 'applicability', description: 'Applicability of a given message', humanReadable: 'Applicability' },
    { header: 'initiatingNode', description: 'Sender of the message', humanReadable: 'Initiating Node'}
  ])

  private _allSubMessages = new BehaviorSubject<subMessageHeaderDetail[]>([
    { header: 'name', description: 'Name of submessage', humanReadable: 'SubMessage Name' },
    { header: 'description', description: 'Description of submessage', humanReadable: 'SubMessage Description' },
    { header: 'interfaceSubMessageNumber', description: 'Order of submessage', humanReadable: 'SubMessage Number' },
    { header: 'applicability', description: 'Applicability of a given submessage', humanReadable: 'Applicability' },
  ])

  private _allNodeDiffs = new BehaviorSubject<nodeDiffHeaderDetail[]>([
    { header: 'name', description: 'Name of node', humanReadable: 'Name' },
    { header: 'description', description: 'Description of node', humanReadable: 'Description' },
    { header: 'address', description: 'Address of node', humanReadable: 'Address' },
    { header: 'color', description: 'Color of node', humanReadable: 'Color' },
    { header: 'applicability', description: 'Applicability of node', humanReadable: 'Applicability' },
  ])

  private _allConnectionDiffs = new BehaviorSubject<connectionDiffHeaderDetail[]>([
    { header: 'name', description: 'Name of connection', humanReadable: 'Name' },
    { header: 'description', description: 'Description of connection', humanReadable: 'Description' },
    { header: 'transportType', description: 'Transport Type of connection', humanReadable: 'Transport Type' },
    { header: 'applicability', description: 'Applicability of connection', humanReadable: 'Applicability' },
  ])

  private _allMessageDiffs = new BehaviorSubject<messageDiffHeaderDetail[]>([
    { header: 'name', description: 'Name of message', humanReadable: 'Name' },
    { header: 'description', description: 'Description of message', humanReadable: 'Description' },
    { header: 'interfaceMessageNumber', description: 'Message Number', humanReadable: 'Message Number' },
    { header: 'interfaceMessagePeriodicity', description: 'Periodicity of message', humanReadable: 'Periodicity' },
    { header: 'interfaceMessageRate', description: 'Transmission rate of message', humanReadable: 'TxRate' },
    { header: 'interfaceMessageWriteAccess', description: 'Write access of message', humanReadable: 'Write Access' },
    { header: 'interfaceMessageType', description: 'Type of message', humanReadable: 'Type' },
    { header: 'applicability', description: 'Applicability of message', humanReadable: 'Applicability' },
  ])

  private _allSubmessageDiffs = new BehaviorSubject<submessageDiffHeaderDetail[]>([
    { header: 'name', description: 'Name of submessage', humanReadable: 'Name' },
    { header: 'description', description: 'Description of submessage', humanReadable: 'Description' },
    { header: 'interfaceSubMessageNumber', description: 'Submessage number', humanReadable: 'SubMessage Number' },
    { header: 'applicability', description: 'Applicability of submessage', humanReadable: 'Applicability' },
  ])

  private _allStructureDiffs = new BehaviorSubject<structureDiffHeaderDetail[]>([
    { header: 'name', description: 'Name of structure', humanReadable: 'Name' },
    { header: 'description', description: 'Description of structure', humanReadable: 'Description' },
    { header: 'interfaceMinSimultaneity', description: 'Minimum simultaneity of structure', humanReadable: 'Min Simult.' },
    { header: 'interfaceMaxSimultaneity', description: 'Maximum simultaneity of structure', humanReadable: 'Max Simult.' },
    { header: 'interfaceTaskFileType', description: 'Task file type of structure', humanReadable: 'Task File Type' },
    { header: 'interfaceStructureCategory', description: 'Category of structure', humanReadable: 'Category' },
    { header: 'applicability', description: 'Applicability of the structure', humanReadable: 'Applicability' },
  ])

  private _allElementDiffs = new BehaviorSubject<elementDiffHeaderDetail[]>([
    { header: 'name', description: 'Name of element', humanReadable: 'Name' },
    { header: 'description', description: 'Description of element', humanReadable: 'Description' },
    { header: 'logicalType', description: 'Logical type of element', humanReadable: 'Logical Type' },
    { header: 'elementSizeInBits', description: 'Bit size of element', humanReadable: 'Bit Size' },
    { header: 'interfaceElementIndexStart', description: 'Minimum value of element', humanReadable: 'Min. Val' },
    { header: 'interfaceElementIndexEnd', description: 'Minimum value of element', humanReadable: 'Min. Val' },
    { header: 'interfacePlatformTypeMinval', description: 'Minimum value of element', humanReadable: 'Min. Val' },
    { header: 'interfacePlatformTypeMaxval', description: 'Maximum value of element', humanReadable: 'Max. Val' },
    { header: 'interfacePlatformTypeDefaultValue', description: 'Default value of element', humanReadable: 'Default Val' },
    { header: 'units', description: 'Element units', humanReadable: 'Units' },
    { header: 'enumeration', description: 'Element enumeration', humanReadable: 'Enumeration' },
    { header: 'interfaceElementAlterable', description: 'Alterability of element', humanReadable: 'Alterable' },
    { header: 'notes', description: 'Element notes', humanReadable: 'Notes' },
    { header: 'applicability', description: 'Applicability of the element', humanReadable: 'Applicability' },
  ])

  private _allBranchSummary = new BehaviorSubject<branchSummaryHeaderDetail[]>([
    { header: 'pcrNo', description: 'Workflow number of the branch\'s associated artifact', humanReadable: 'Workflow' },
    { header: 'description', description: 'Description of the branch', humanReadable: 'Description' },
    { header: 'compareBranch', description: 'Branch being compared against', humanReadable: 'Compare Against' },
    { header: 'reportDate', description: 'Date the report was generated', humanReadable: 'Report Date' },
  ]) 

  private _allDiffReportSummary = new BehaviorSubject<diffReportSummaryHeaderDetail[]>([
    { header: 'changeType', description: 'Type of object that was changed', humanReadable: 'Change Type' },
    { header: 'action', description: 'Action taken on the changed object', humanReadable: 'Action' },
    { header: 'name', description: 'Name of the changed object', humanReadable: 'Name' },
    { header: 'details', description: 'Change details', humanReadable: 'Details' },
  ]) 


  private _allNodeDiffHeaders = this._allNodeDiffs.pipe(
    mergeMap((nodes) => from(nodes).pipe(
      map((node) => node.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof nodeDiffItem,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allConnectionDiffHeaders = this._allConnectionDiffs.pipe(
    mergeMap((connections) => from(connections).pipe(
      map((connection) => connection.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof connectionDiffItem,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allMessageDiffHeaders = this._allMessageDiffs.pipe(
    mergeMap((messages) => from(messages).pipe(
      map((message) => message.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof messageDiffItem,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allSubmessageDiffHeaders = this._allSubmessageDiffs.pipe(
    mergeMap((submessages) => from(submessages).pipe(
      map((submessage) => submessage.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof submessageDiffItem,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allStructureDiffHeaders = this._allStructureDiffs.pipe(
    mergeMap((structures) => from(structures).pipe(
      map((structure) => structure.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof structureDiffItem,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allElementDiffHeaders = this._allElementDiffs.pipe(
    mergeMap((elements) => from(elements).pipe(
      map((element) => element.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof elementDiffItem,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allBranchSummaryHeaders = this._allBranchSummary.pipe(
    mergeMap((summary) => from(summary).pipe(
      map((sum) => sum.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof branchSummary,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allDiffReportSummaryHeaders = this._allDiffReportSummary.pipe(
    mergeMap((summary) => from(summary).pipe(
      map((sum) => sum.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof diffReportSummaryItem,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allElementsHeaders = this._allElements.pipe(
    mergeMap((elements) => from(elements).pipe(
      map((element) => element.header),
      reduce((acc, curr) => [...acc, curr], [] as (keyof element)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allStructureHeaders =this._allStructures.pipe(
    mergeMap((structures) => from(structures).pipe(
      map((structure) => structure.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof structure,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allMessageHeaders =this._allMessages.pipe(
    mergeMap((messages) => from(messages).pipe(
      map((message) => message.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof message,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )

  private _allSubMessageHeaders =this._allSubMessages.pipe(
    mergeMap((submessages) => from(submessages).pipe(
      map((submessage) => submessage.header),
      reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof subMessage,string>)[])
    )),
    shareReplay({bufferSize:1,refCount:true})
  )
  constructor () { }
  
  get AllNodeDiffHeaders() {
    return this._allNodeDiffHeaders;
  }

  get AllNodeDiffs() {
    return this._allNodeDiffs;
  }

  get AllConnectionDiffHeaders() {
    return this._allConnectionDiffHeaders;
  }

  get AllConnectionDiffs() {
    return this._allConnectionDiffs;
  }

  get AllMessageDiffHeaders() {
    return this._allMessageDiffHeaders;
  }

  get AllMessageDiffs() {
    return this._allMessageDiffs;
  }

  get AllSubmessageDiffHeaders() {
    return this._allSubmessageDiffHeaders;
  }

  get AllSubmessageDiffs() {
    return this._allSubmessageDiffs;
  }

  get AllStructureDiffHeaders() {
    return this._allStructureDiffHeaders;
  }

  get AllStructureDiffs() {
    return this._allStructureDiffs;
  }

  get AllElementDiffHeaders() {
    return this._allElementDiffHeaders;
  }

  get AllElementDiffs() {
    return this._allElementDiffs;
  }

  get AllBranchSummaryHeaders() {
    return this._allBranchSummaryHeaders;
  }

  get AllBranchSummary() {
    return this._allBranchSummary;
  }

  get AllDiffReportSummaryHeaders() {
    return this._allDiffReportSummaryHeaders;
  }

  get AllDiffReportSummary() {
    return this._allDiffReportSummary;
  }

  get AllElementHeaders() {
    return this._allElementsHeaders;
  }

  get AllElements() {
    return this._allElements;
  }

  get AllStructureHeaders() {
    return this._allStructureHeaders;
  }

  get AllStructures() {
    return this._allStructures;
  }

  get AllMessages() {
    return this._allMessages;
  }

  get AllMessageHeaders() {
    return this._allMessageHeaders;
  }

  get AllSubMessages() {
    return this._allSubMessages;
  }

  get AllSubMessageHeaders() {
    return this._allSubMessageHeaders;
  }

  getHeaderByName(value: keyof structure|keyof element|keyof message|keyof subMessage|keyof nodeDiffItem|string,type:string) {
    return iif(() => type === 'message',
      this.AllMessages.pipe(
        mergeMap((messages) => from(messages).pipe(
          filter((message)=>message.header===value)
        ))
      ), //message obs
      iif(() => type === 'submessage',
        this.AllSubMessages.pipe(
          mergeMap((submessages) => from(submessages).pipe(
            filter((submessage)=>submessage.header===value)
          ))
        ), //submessage obs
        iif(() => type === 'structure',
          this.AllStructures.pipe(
            mergeMap((structures) => from(structures).pipe(
              filter((structure)=>structure.header===value)
            ))
          ), //structure obs
          iif(() => type === 'element',
            this.AllElements.pipe(
              mergeMap((elements) => from(elements).pipe(
                filter((element)=>element.header===value)
              ))
            ), //element obs
            iif(() => type === DiffHeaderType.NODE,
              this.AllNodeDiffs.pipe(
                mergeMap((nodes) => from(nodes).pipe(
                  filter((node)=>node.header===value)
                ))
              ), //nodeDiff obs
              iif(() => type === DiffHeaderType.CONNECTION,
                this.AllConnectionDiffs.pipe(
                  mergeMap((connections) => from(connections).pipe(
                    filter((connection)=>connection.header===value)
                  ))
                ), //connectionDiff obs
                iif(() => type === DiffHeaderType.MESSAGE,
                  this.AllMessageDiffs.pipe(
                    mergeMap((messages) => from(messages).pipe(
                      filter((message)=>message.header===value)
                    ))
                  ), //messageDiff obs
                  iif(() => type === DiffHeaderType.SUBMESSAGE,
                    this.AllSubmessageDiffs.pipe(
                      mergeMap((submessages) => from(submessages).pipe(
                        filter((submessage)=>submessage.header===value)
                      ))
                    ), //submessageDiff obs
                    iif(() => type === DiffHeaderType.STRUCTURE,
                      this.AllStructureDiffs.pipe(
                        mergeMap((structures) => from(structures).pipe(
                          filter((structure)=>structure.header===value)
                        ))
                      ), //structureDiffs obs
                      iif(() => type === DiffHeaderType.ELEMENT,
                        this.AllElementDiffs.pipe(
                          mergeMap((elements) => from(elements).pipe(
                            filter((element)=>element.header===value)
                          ))
                        ), //elementDiffs obs
                        iif(() => type === 'branchSummary',
                          this.AllBranchSummary.pipe(
                            mergeMap((summary) => from(summary).pipe(
                              filter((sum)=>sum.header===value)
                            ))
                          ), //branchSummary obs
                          iif(() => type === 'diffReportSummary',
                            this.AllDiffReportSummary.pipe(
                              mergeMap((summary) => from(summary).pipe(
                                filter((sum)=>sum.header===value)
                              ))
                            ), //diffReportSummary obs
                          )
                        )
                      )
                    )
                  )
                )
              )
            ) 
          )
        )
      )
    )
  }
}
