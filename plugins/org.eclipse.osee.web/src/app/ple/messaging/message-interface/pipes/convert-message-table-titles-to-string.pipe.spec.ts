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
import { ConvertMessageTableTitlesToStringPipe } from './convert-message-table-titles-to-string.pipe';

describe('ConvertMessageTableTitlesToStringPipe', () => {
  it('create an instance', () => {
    const pipe = new ConvertMessageTableTitlesToStringPipe();
    expect(pipe).toBeTruthy();
  });
  it('should return value back if not in list', () => {
    const pipe = new ConvertMessageTableTitlesToStringPipe();
    expect(pipe.transform('abcdef')).toEqual('abcdef')
  })
});
