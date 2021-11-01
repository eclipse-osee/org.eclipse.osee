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
import { transactionInfo } from '../types/change-report/transaction';
import { transaction } from './transaction';

export const transactionMock: transaction = {
  branch: '10',
  txComment: 'Comment',
};

export const transactionInfoMock: transactionInfo = {
  txId: {
    id: "-1",
    branchId:"-1"
  },
  branchUuid: 0,
  txType: {
    id: '',
    name: '',
    baseline: false,
    idString: '',
    idIntValue: 0
  },
  comment: '',
  author: {
    id: '',
    name: '',
    userId: '',
    active: false,
    email: '',
    loginIds: [],
    roles: []
  },
  timeStamp: '',
  commitArt: ''
}
