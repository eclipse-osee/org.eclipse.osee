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
import { element } from '../../../message-element-interface/types/element';
import { structure } from '../../../message-element-interface/types/structure';
import { message } from '../../../message-interface/types/messages';
import { subMessage } from '../../../message-interface/types/sub-messages';
import { elementHeaderDetail, messageHeaderDetail, structureHeaderDetail, subMessageHeaderDetail } from '../../types/headerDetail';

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
    { header: 'beginWord', description: '(Computed) Beginning Word of Element/Element Array', humanReadable: 'Begin Word' },
    { header: 'endWord', description: '(Computed) Ending Word of Element/Element Array', humanReadable: 'End Word' },
    { header: 'beginByte', description: '(Computed) Beginning Byte of Element/Element Array', humanReadable: 'Begin Byte' },
    { header: 'endByte', description: '(Computed) Ending Byte of Element/Element Array', humanReadable: 'End Byte' },
    { header: 'interfaceElementAlterable', description: 'Whether or not a given Element is alterable', humanReadable: 'Alterable' },
    { header: 'description', description: 'Description of a given element', humanReadable: 'Description' },
    { header: 'notes', description: 'Notes corresponding to a given element, for example, specific enum literal descriptions for a given element', humanReadable: 'Notes' },
    { header: 'applicability', description: 'Applicability of a given element', humanReadable: 'Applicability' },
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
    // { header: 'GenerationIndicator', description: 'TBD?(need to figure out)', humanReadable: 'Indicator' },
    { header: 'applicability', description: 'Applicability of a given structure', humanReadable: 'Applicability' },
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

  getHeaderByName(value: keyof structure|keyof element|keyof message|keyof subMessage|string,type:string) {
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
          
          )
        )
      )
    )
  }
}
