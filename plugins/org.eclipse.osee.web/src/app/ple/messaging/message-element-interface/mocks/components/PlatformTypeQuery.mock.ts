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
import { Component, EventEmitter, Input, Output } from "@angular/core";
import { PlatformTypeQuery } from '../../../shared/types/MimQuery';
import { PlatformType } from '../../../shared/types/platformType';

@Component({
    selector: 'app-platform-type-query',
    template:'<p>Dummy</p>'
})
export class PlatformTypeQueryMock{
    @Input() platformTypes: PlatformType[] = [];
    @Input() bitSizeSliderStepSize: number = 0.05;
    @Output('query') returnQuery = new EventEmitter<PlatformTypeQuery>();
  }