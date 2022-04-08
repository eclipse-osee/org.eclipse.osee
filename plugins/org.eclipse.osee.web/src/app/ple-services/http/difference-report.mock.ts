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

// This nocheck is here in order to avoid correcting the mock's values every time it needs an update
// @ts-nocheck
import { DifferenceReport } from "src/app/ple/messaging/shared/types/DifferenceReport";
export const differenceReportMock: DifferenceReport = {
  "changeItems" : {
    "200391" : {
      "item" : {
        "id" : "200391",
        "name" : "Node2(Edit)",
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "address" : "",
        "color" : "#7993b4",
        "description" : "Node 2 description"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200391",
        "itemId" : "38933",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71711",
          "modType" : "1",
          "value" : "Node2",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "352",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72028",
          "modType" : "2",
          "value" : "Node2(Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "294",
            "branchId" : "9"
          },
          "gammaId" : "71711",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72028",
          "modType" : "2",
          "value" : "Node2(Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200391",
        "itemId" : "200391",
        "itemTypeId" : "6039606571486514295",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71711",
          "modType" : "1",
          "value" : "Node2",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72028",
          "modType" : "2",
          "value" : "Node2(Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71711",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72028",
          "modType" : "2",
          "value" : "Node2(Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ ]
    },
    "200390" : {
      "item" : {
        "id" : "200390",
        "name" : "Node1",
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "address" : "1111",
        "color" : "#893e3e",
        "description" : "Edited this node"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200390",
        "itemId" : "38930",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71707",
          "modType" : "1",
          "value" : "Node 1 description",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "351",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72027",
          "modType" : "2",
          "value" : "Edited this node",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "293",
            "branchId" : "9"
          },
          "gammaId" : "71707",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72027",
          "modType" : "2",
          "value" : "Edited this node",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200390",
        "itemId" : "38931",
        "itemTypeId" : "5726596359647826656",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71705",
          "modType" : "1",
          "value" : "111",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "351",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72026",
          "modType" : "2",
          "value" : "1111",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "293",
            "branchId" : "9"
          },
          "gammaId" : "71705",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72026",
          "modType" : "2",
          "value" : "1111",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200390",
        "itemId" : "38932",
        "itemTypeId" : "5221290120300474048",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71704",
          "modType" : "1",
          "value" : "#854c4c",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "351",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72025",
          "modType" : "2",
          "value" : "#893e3e",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "293",
            "branchId" : "9"
          },
          "gammaId" : "71704",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72025",
          "modType" : "2",
          "value" : "#893e3e",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200390",
        "itemId" : "200390",
        "itemTypeId" : "6039606571486514295",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71704",
          "modType" : "1",
          "value" : "#854c4c",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72025",
          "modType" : "2",
          "value" : "#893e3e",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71704",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72025",
          "modType" : "2",
          "value" : "#893e3e",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ ]
    },
    "200394" : {
      "item" : {
        "id" : "200394",
        "name" : "Node4",
        "applicability" : {
          "id" : "1009971623404681232",
          "name" : "Config = Product C"
        },
        "address" : "444",
        "color" : "",
        "description" : "Node      4"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200394",
        "itemId" : "200394",
        "itemTypeId" : "6039606571486514295",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71724",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "353",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71724",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1009971623404681232",
            "name" : "Config = Product C"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "297",
            "branchId" : "9"
          },
          "gammaId" : "71724",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71724",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1009971623404681232",
            "name" : "Config = Product C"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ ]
    },
    "200392" : {
      "item" : {
        "id" : "200392",
        "name" : "Connection1",
        "primaryNode" : 200390,
        "secondaryNode" : 200391,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "Added a description",
        "transportType" : "HSDN"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200392",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "6039606571486514298",
          "name" : "Interface Connection Content",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : 1955695738,
          "idString" : "6039606571486514298"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72046",
          "modType" : "1",
          "value" : "-1,786432",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72046",
          "modType" : "1",
          "value" : "-1,786432",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200432",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "DELETED_AND_DNE_ON_DESTINATION",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200392",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "6039606571486514298",
          "name" : "Interface Connection Content",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : 1955695738,
          "idString" : "6039606571486514298"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "358",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71791",
          "modType" : "3",
          "value" : "-1,786432",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71791",
          "modType" : "1",
          "value" : "-1,786432",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200401",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "DELETED_AND_DNE_ON_DESTINATION",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200392",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "6039606571486514298",
          "name" : "Interface Connection Content",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : 1955695738,
          "idString" : "6039606571486514298"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71801",
          "modType" : "3",
          "value" : "-1,1048576",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71801",
          "modType" : "1",
          "value" : "-1,1048576",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200402",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200392",
        "itemId" : "38937",
        "itemTypeId" : "4522496963078776538",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71714",
          "modType" : "1",
          "value" : "ETHERNET",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "354",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72029",
          "modType" : "2",
          "value" : "HSDN",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "295",
            "branchId" : "9"
          },
          "gammaId" : "71714",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72029",
          "modType" : "2",
          "value" : "HSDN",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200392",
        "itemId" : "39148",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "354",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72030",
          "modType" : "1",
          "value" : "Added a description",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72030",
          "modType" : "1",
          "value" : "Added a description",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200392",
        "itemId" : "200392",
        "itemTypeId" : "126164394421696910",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71714",
          "modType" : "1",
          "value" : "ETHERNET",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72030",
          "modType" : "1",
          "value" : "Added a description",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71714",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72030",
          "modType" : "2",
          "value" : "Added a description",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ ]
    },
    "200399" : {
      "item" : {
        "id" : "200399",
        "name" : "Message1",
        "subMessages" : [ ],
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "initiatingNode" : null,
        "description" : "This is message 1",
        "interfaceMessageNumber" : "1",
        "interfaceMessagePeriodicity" : "Aperiodic",
        "interfaceMessageRate" : "5",
        "interfaceMessageType" : "Connection",
        "interfaceMessageWriteAccess" : false
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200399",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "2455059983007225780",
          "name" : "Interface Message SubMessage Content",
          "order" : "USER_DEFINED",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : 225187764,
          "idString" : "2455059983007225780"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "371",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72074",
          "modType" : "1",
          "value" : "-1,1310720",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72074",
          "modType" : "1",
          "value" : "-1,1310720",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200436",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200399",
        "itemId" : "38974",
        "itemTypeId" : "3899709087455064789",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71768",
          "modType" : "1",
          "value" : "OnDemand",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "355",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72031",
          "modType" : "2",
          "value" : "Aperiodic",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71768",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72031",
          "modType" : "2",
          "value" : "Aperiodic",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200399",
        "itemId" : "200399",
        "itemTypeId" : "2455059983007225775",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71768",
          "modType" : "1",
          "value" : "OnDemand",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72031",
          "modType" : "2",
          "value" : "Aperiodic",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71768",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72031",
          "modType" : "2",
          "value" : "Aperiodic",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200392" ]
    },
    "200396" : {
      "item" : {
        "id" : "200396",
        "name" : "Node D",
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "address" : "000",
        "color" : "",
        "description" : "Delete this"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200396",
        "itemId" : "38950",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71739",
          "modType" : "1",
          "value" : "Node D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "346",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71739",
          "modType" : "5",
          "value" : "Node D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "299",
            "branchId" : "9"
          },
          "gammaId" : "71739",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71739",
          "modType" : "5",
          "value" : "Node D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200396",
        "itemId" : "38951",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71740",
          "modType" : "1",
          "value" : "Delete this",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "346",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71740",
          "modType" : "5",
          "value" : "Delete this",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "299",
            "branchId" : "9"
          },
          "gammaId" : "71740",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71740",
          "modType" : "5",
          "value" : "Delete this",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200396",
        "itemId" : "38952",
        "itemTypeId" : "5726596359647826656",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71738",
          "modType" : "1",
          "value" : "000",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "346",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71738",
          "modType" : "5",
          "value" : "000",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "299",
            "branchId" : "9"
          },
          "gammaId" : "71738",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71738",
          "modType" : "5",
          "value" : "000",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200396",
        "itemId" : "38953",
        "itemTypeId" : "5221290120300474048",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71737",
          "modType" : "1",
          "value" : "",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "346",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71737",
          "modType" : "5",
          "value" : "",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "299",
            "branchId" : "9"
          },
          "gammaId" : "71737",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71737",
          "modType" : "5",
          "value" : "",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200396",
        "itemId" : "200396",
        "itemTypeId" : "6039606571486514295",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71736",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "346",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71736",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "299",
            "branchId" : "9"
          },
          "gammaId" : "71736",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71736",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      } ],
      "parents" : [ ]
    },
    "200403" : {
      "item" : {
        "id" : "200403",
        "name" : "Submessage1",
        "applicability" : {
          "id" : "1009971623404681232",
          "name" : "Config = Product C"
        },
        "description" : "This is submessage 1",
        "interfaceSubMessageNumber" : "0"
      },
      "changes" : [ {
        "ignoreType" : "DELETED_AND_DNE_ON_DESTINATION",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200403",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "126164394421696914",
          "name" : "Interface SubMessage Content",
          "order" : "USER_DEFINED",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : 684636562,
          "idString" : "126164394421696914"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "365",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71856",
          "modType" : "3",
          "value" : "-1,1048576",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71856",
          "modType" : "1",
          "value" : "-1,1048576",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200410",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200403",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "126164394421696914",
          "name" : "Interface SubMessage Content",
          "order" : "USER_DEFINED",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : 684636562,
          "idString" : "126164394421696914"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "368",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72055",
          "modType" : "1",
          "value" : "-1,1048576",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72055",
          "modType" : "1",
          "value" : "-1,1048576",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200433",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200403",
        "itemId" : "39001",
        "itemTypeId" : "2455059983007225769",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71806",
          "modType" : "1",
          "value" : "1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "356",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72032",
          "modType" : "2",
          "value" : "0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71806",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72032",
          "modType" : "2",
          "value" : "0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200403",
        "itemId" : "200403",
        "itemTypeId" : "126164394421696908",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71803",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "379",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71803",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1009971623404681232",
            "name" : "Config = Product C"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71803",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71803",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1009971623404681232",
            "name" : "Config = Product C"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200399" ]
    },
    "200402" : {
      "item" : {
        "id" : "200402",
        "name" : "Message D",
        "subMessages" : [ ],
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "initiatingNode" : null,
        "description" : "Delete this message",
        "interfaceMessageNumber" : "4",
        "interfaceMessagePeriodicity" : "Periodic",
        "interfaceMessageRate" : "10",
        "interfaceMessageType" : "Operational",
        "interfaceMessageWriteAccess" : false
      },
      "changes" : [ {
        "ignoreType" : "DELETED_AND_DNE_ON_DESTINATION",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200402",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "6039606571486514299",
          "name" : "Interface Message Sending Node",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_ONE",
          "idIntValue" : 1955695739,
          "idString" : "6039606571486514299"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71802",
          "modType" : "3",
          "value" : "-1,0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71802",
          "modType" : "1",
          "value" : "-1,0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200391",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200402",
        "itemId" : "38993",
        "itemTypeId" : "2455059983007225754",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71794",
          "modType" : "1",
          "value" : "false",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71794",
          "modType" : "5",
          "value" : "false",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71794",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71794",
          "modType" : "5",
          "value" : "false",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200402",
        "itemId" : "38994",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71795",
          "modType" : "1",
          "value" : "Message D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71795",
          "modType" : "5",
          "value" : "Message D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71795",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71795",
          "modType" : "5",
          "value" : "Message D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200402",
        "itemId" : "38995",
        "itemTypeId" : "3899709087455064789",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71798",
          "modType" : "1",
          "value" : "Periodic",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71798",
          "modType" : "5",
          "value" : "Periodic",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71798",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71798",
          "modType" : "5",
          "value" : "Periodic",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200402",
        "itemId" : "38996",
        "itemTypeId" : "2455059983007225770",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71799",
          "modType" : "1",
          "value" : "Operational",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71799",
          "modType" : "5",
          "value" : "Operational",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71799",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71799",
          "modType" : "5",
          "value" : "Operational",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200402",
        "itemId" : "38997",
        "itemTypeId" : "2455059983007225768",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71800",
          "modType" : "1",
          "value" : "4",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71800",
          "modType" : "5",
          "value" : "4",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71800",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71800",
          "modType" : "5",
          "value" : "4",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200402",
        "itemId" : "38998",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71797",
          "modType" : "1",
          "value" : "Delete this message",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71797",
          "modType" : "5",
          "value" : "Delete this message",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71797",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71797",
          "modType" : "5",
          "value" : "Delete this message",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200402",
        "itemId" : "38999",
        "itemTypeId" : "2455059983007225763",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71796",
          "modType" : "1",
          "value" : "10",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71796",
          "modType" : "5",
          "value" : "10",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71796",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71796",
          "modType" : "5",
          "value" : "10",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200402",
        "itemId" : "200402",
        "itemTypeId" : "2455059983007225775",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71793",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "357",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71793",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71793",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71793",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200392" ]
    },
    "200400" : {
      "item" : {
        "id" : "200400",
        "name" : "Message2",
        "subMessages" : [ {
          "id" : "200406",
          "name" : "Submessage D",
          "applicability" : {
            "id" : "1",
            "name" : "Base"
          },
          "description" : "Delete this submessage",
          "interfaceSubMessageNumber" : "4"
        } ],
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "initiatingNode" : null,
        "description" : "This is message 2",
        "interfaceMessageNumber" : "2",
        "interfaceMessagePeriodicity" : "Aperiodic",
        "interfaceMessageRate" : "10",
        "interfaceMessageType" : "Operational",
        "interfaceMessageWriteAccess" : true
      },
      "changes" : [ ],
      "parents" : [ "200392" ]
    },
    "200407" : {
      "item" : {
        "id" : "200407",
        "name" : "Structure1 (Edit)",
        "elements" : [ ],
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "This is structure 1",
        "interfaceMaxSimultaneity" : "2",
        "bytesPerSecondMinimum" : 0.0,
        "interfaceStructureCategory" : "Flight Test",
        "interfaceTaskFileType" : 1,
        "interfaceMinSimultaneity" : "1",
        "incorrectlySized" : false,
        "bytesPerSecondMaximum" : 0.0,
        "sizeInBytes" : 0.0,
        "numElements" : 0
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200407",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "2455059983007225781",
          "name" : "Interface Structure Content",
          "order" : "USER_DEFINED",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : 225187765,
          "idString" : "2455059983007225781"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "370",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72068",
          "modType" : "1",
          "value" : "-1,2359296",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72068",
          "modType" : "1",
          "value" : "-1,2359296",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200435",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200407",
        "itemId" : "39012",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71828",
          "modType" : "1",
          "value" : "Structure1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "364",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72036",
          "modType" : "2",
          "value" : "Structure1 (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71828",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72036",
          "modType" : "2",
          "value" : "Structure1 (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200407",
        "itemId" : "200407",
        "itemTypeId" : "2455059983007225776",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71828",
          "modType" : "1",
          "value" : "Structure1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72036",
          "modType" : "2",
          "value" : "Structure1 (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71828",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72036",
          "modType" : "2",
          "value" : "Structure1 (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200403" ]
    },
    "200406" : {
      "item" : {
        "id" : "200406",
        "name" : "Submessage D",
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "Delete this submessage",
        "interfaceSubMessageNumber" : "4"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200406",
        "itemId" : "39009",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71819",
          "modType" : "1",
          "value" : "Submessage D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "359",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71819",
          "modType" : "5",
          "value" : "Submessage D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71819",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71819",
          "modType" : "5",
          "value" : "Submessage D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200406",
        "itemId" : "39010",
        "itemTypeId" : "2455059983007225769",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71821",
          "modType" : "1",
          "value" : "4",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "359",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71821",
          "modType" : "5",
          "value" : "4",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71821",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71821",
          "modType" : "5",
          "value" : "4",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200406",
        "itemId" : "39011",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71820",
          "modType" : "1",
          "value" : "Delete this submessage",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "359",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71820",
          "modType" : "5",
          "value" : "Delete this submessage",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71820",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71820",
          "modType" : "5",
          "value" : "Delete this submessage",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200406",
        "itemId" : "200406",
        "itemTypeId" : "126164394421696908",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71818",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "359",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71818",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71818",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71818",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200400", "200399" ]
    },
    "200405" : {
      "item" : {
        "id" : "200405",
        "name" : "Submessage UR Edited",
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "Unrelate this submessage",
        "interfaceSubMessageNumber" : "3"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200405",
        "itemId" : "39006",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71814",
          "modType" : "1",
          "value" : "Submessage UR",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "360",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72033",
          "modType" : "2",
          "value" : "Submessage UR Edited",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71814",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72033",
          "modType" : "2",
          "value" : "Submessage UR Edited",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200405",
        "itemId" : "200405",
        "itemTypeId" : "126164394421696908",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71814",
          "modType" : "1",
          "value" : "Submessage UR",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72033",
          "modType" : "2",
          "value" : "Submessage UR Edited",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71814",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72033",
          "modType" : "2",
          "value" : "Submessage UR Edited",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200400", "200399" ]
    },
    "200404" : {
      "item" : {
        "id" : "200404",
        "name" : "Submessage2 (Edit)",
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "This is submessage 2",
        "interfaceSubMessageNumber" : "2"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200404",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "126164394421696914",
          "name" : "Interface SubMessage Content",
          "order" : "USER_DEFINED",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : 684636562,
          "idString" : "126164394421696914"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "366",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72037",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72037",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200409",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200404",
        "itemId" : "39003",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71809",
          "modType" : "1",
          "value" : "Submessage2",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "372",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72075",
          "modType" : "2",
          "value" : "Submessage2 (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71809",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72075",
          "modType" : "2",
          "value" : "Submessage2 (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200404",
        "itemId" : "200404",
        "itemTypeId" : "126164394421696908",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71809",
          "modType" : "1",
          "value" : "Submessage2",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72075",
          "modType" : "2",
          "value" : "Submessage2 (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71809",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72075",
          "modType" : "2",
          "value" : "Submessage2 (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200399" ]
    },
    "200410" : {
      "item" : {
        "id" : "200410",
        "name" : "Structure D",
        "elements" : [ ],
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "Delete this structure",
        "interfaceMaxSimultaneity" : "2",
        "bytesPerSecondMinimum" : 0.0,
        "interfaceStructureCategory" : "BIT Status",
        "interfaceTaskFileType" : 5,
        "interfaceMinSimultaneity" : "0",
        "incorrectlySized" : false,
        "bytesPerSecondMaximum" : 0.0,
        "sizeInBytes" : 0.0,
        "numElements" : 0
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200410",
        "itemId" : "39030",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71852",
          "modType" : "1",
          "value" : "Structure D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "365",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71852",
          "modType" : "5",
          "value" : "Structure D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71852",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71852",
          "modType" : "5",
          "value" : "Structure D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200410",
        "itemId" : "39031",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71853",
          "modType" : "1",
          "value" : "Delete this structure",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "365",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71853",
          "modType" : "5",
          "value" : "Delete this structure",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71853",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71853",
          "modType" : "5",
          "value" : "Delete this structure",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200410",
        "itemId" : "39032",
        "itemTypeId" : "2455059983007225756",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71851",
          "modType" : "1",
          "value" : "2",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "365",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71851",
          "modType" : "5",
          "value" : "2",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71851",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71851",
          "modType" : "5",
          "value" : "2",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200410",
        "itemId" : "39033",
        "itemTypeId" : "2455059983007225755",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71850",
          "modType" : "1",
          "value" : "0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "365",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71850",
          "modType" : "5",
          "value" : "0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71850",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71850",
          "modType" : "5",
          "value" : "0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200410",
        "itemId" : "39034",
        "itemTypeId" : "2455059983007225764",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71855",
          "modType" : "1",
          "value" : "BIT Status",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "365",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71855",
          "modType" : "5",
          "value" : "BIT Status",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71855",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71855",
          "modType" : "5",
          "value" : "BIT Status",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200410",
        "itemId" : "39035",
        "itemTypeId" : "2455059983007225760",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71854",
          "modType" : "1",
          "value" : "5",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "365",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71854",
          "modType" : "5",
          "value" : "5",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71854",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71854",
          "modType" : "5",
          "value" : "5",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200410",
        "itemId" : "200410",
        "itemTypeId" : "2455059983007225776",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71849",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "365",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71849",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71849",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71849",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200403" ]
    },
    "200408" : {
      "item" : {
        "id" : "200408",
        "name" : "Structure2",
        "elements" : [ {
          "id" : "200412",
          "name" : "Element1",
          "beginByte" : 0.0,
          "beginWord" : 0.0,
          "applicability" : null,
          "logicalType" : "",
          "autogenerated" : false,
          "units" : "",
          "description" : "This is element 1 (Edited)",
          "interfacePlatformTypeDefaultValue" : "0",
          "interfacePlatformTypeDescription" : "",
          "interfacePlatformTypeMinval" : "0",
          "interfacePlatformTypeMaxval" : "0",
          "interfaceElementAlterable" : false,
          "elementSizeInBytes" : 0.0,
          "elementSizeInBits" : 0.0,
          "interfaceElementIndexStart" : 0,
          "platformTypeName2" : "",
          "interfaceElementIndexEnd" : 0,
          "notes" : "This is a note",
          "platformTypeId" : -1,
          "endByte" : -1.0,
          "endWord" : -1.0
        } ],
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "This is structure 2",
        "interfaceMaxSimultaneity" : "0",
        "bytesPerSecondMinimum" : 0.0,
        "interfaceStructureCategory" : "Miscellaneous",
        "interfaceTaskFileType" : 2,
        "interfaceMinSimultaneity" : "0",
        "incorrectlySized" : false,
        "bytesPerSecondMaximum" : 0.0,
        "sizeInBytes" : 0.0,
        "numElements" : 1
      },
      "changes" : [ ],
      "parents" : [ "200403" ]
    },
    "200415" : {
      "item" : {
        "id" : "200415",
        "name" : "Element D",
        "beginByte" : 0.0,
        "beginWord" : 0.0,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "logicalType" : "integer",
        "autogenerated" : false,
        "units" : "Nm",
        "description" : "Delete this element",
        "interfacePlatformTypeDefaultValue" : "0",
        "interfacePlatformTypeDescription" : "A 32 bit integer",
        "interfacePlatformTypeMinval" : "0",
        "interfacePlatformTypeMaxval" : "199",
        "interfaceElementAlterable" : false,
        "elementSizeInBytes" : 4.0,
        "elementSizeInBits" : 32.0,
        "interfaceElementIndexStart" : 0,
        "platformTypeName2" : "Integer1",
        "interfaceElementIndexEnd" : 0,
        "notes" : "To be deleted",
        "platformTypeId" : 200411,
        "endByte" : 3.0,
        "endWord" : 0.0
      },
      "changes" : [ {
        "ignoreType" : "DELETED_AND_DNE_ON_DESTINATION",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200415",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "3899709087455064781",
          "name" : "Interface Element Platform Type",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : -450940211,
          "idString" : "3899709087455064781"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "363",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71894",
          "modType" : "3",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71894",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200411",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200415",
        "itemId" : "39057",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71891",
          "modType" : "1",
          "value" : "Element D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "363",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71891",
          "modType" : "5",
          "value" : "Element D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71891",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71891",
          "modType" : "5",
          "value" : "Element D",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200415",
        "itemId" : "39058",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71892",
          "modType" : "1",
          "value" : "Delete this element",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "363",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71892",
          "modType" : "5",
          "value" : "Delete this element",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71892",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71892",
          "modType" : "5",
          "value" : "Delete this element",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200415",
        "itemId" : "39059",
        "itemTypeId" : "1152921504606847085",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71889",
          "modType" : "1",
          "value" : "To be deleted",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "363",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71889",
          "modType" : "5",
          "value" : "To be deleted",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71889",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71889",
          "modType" : "5",
          "value" : "To be deleted",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200415",
        "itemId" : "39060",
        "itemTypeId" : "2455059983007225788",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71890",
          "modType" : "1",
          "value" : "false",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "363",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71890",
          "modType" : "5",
          "value" : "false",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71890",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71890",
          "modType" : "5",
          "value" : "false",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200415",
        "itemId" : "200415",
        "itemTypeId" : "2455059983007225765",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71888",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "363",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71888",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71888",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71888",
          "modType" : "3",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : true,
        "applicabilityCopy" : false
      } ],
      "parents" : [ ]
    },
    "200413" : {
      "item" : {
        "id" : "200413",
        "name" : "Element2",
        "beginByte" : 0.0,
        "beginWord" : 0.0,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "logicalType" : "double",
        "autogenerated" : false,
        "units" : "Feet^2",
        "description" : "This is element 2",
        "interfacePlatformTypeDefaultValue" : "0",
        "interfacePlatformTypeDescription" : "",
        "interfacePlatformTypeMinval" : "0",
        "interfacePlatformTypeMaxval" : "1000",
        "interfaceElementAlterable" : true,
        "elementSizeInBytes" : 8.0,
        "elementSizeInBits" : 64.0,
        "interfaceElementIndexStart" : 0,
        "platformTypeName2" : "Double1",
        "interfaceElementIndexEnd" : 0,
        "notes" : "Changed from int to double",
        "platformTypeId" : 200424,
        "endByte" : 3.0,
        "endWord" : 1.0
      },
      "changes" : [ {
        "ignoreType" : "DELETED_AND_DNE_ON_DESTINATION",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200413",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "3899709087455064781",
          "name" : "Interface Element Platform Type",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : -450940211,
          "idString" : "3899709087455064781"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "378",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "71880",
          "modType" : "3",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71880",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200411",
        "deleted" : true,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200413",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "3899709087455064781",
          "name" : "Interface Element Platform Type",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : -450940211,
          "idString" : "3899709087455064781"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "378",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72084",
          "modType" : "1",
          "value" : "-1,524288",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72084",
          "modType" : "1",
          "value" : "-1,524288",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200424",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200413",
        "itemId" : "39051",
        "itemTypeId" : "1152921504606847085",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71875",
          "modType" : "1",
          "value" : "",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72035",
          "modType" : "2",
          "value" : "Added a note",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "377",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72083",
          "modType" : "2",
          "value" : "Changed from int to double",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71875",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72083",
          "modType" : "2",
          "value" : "Changed from int to double",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200413",
        "itemId" : "200413",
        "itemTypeId" : "2455059983007225765",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71875",
          "modType" : "1",
          "value" : "",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72083",
          "modType" : "2",
          "value" : "Changed from int to double",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71875",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72083",
          "modType" : "2",
          "value" : "Changed from int to double",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200407" ]
    },
    "200412" : {
      "item" : {
        "id" : "200412",
        "name" : "Element1",
        "beginByte" : 0.0,
        "beginWord" : 0.0,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "logicalType" : "integer",
        "autogenerated" : false,
        "units" : "Nm",
        "description" : "This is element 1 (Edited)",
        "interfacePlatformTypeDefaultValue" : "0",
        "interfacePlatformTypeDescription" : "A 32 bit integer",
        "interfacePlatformTypeMinval" : "0",
        "interfacePlatformTypeMaxval" : "199",
        "interfaceElementAlterable" : false,
        "elementSizeInBytes" : 4.0,
        "elementSizeInBits" : 32.0,
        "interfaceElementIndexStart" : 0,
        "platformTypeName2" : "Integer1",
        "interfaceElementIndexEnd" : 0,
        "notes" : "This is a note",
        "platformTypeId" : 200411,
        "endByte" : 3.0,
        "endWord" : 0.0
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200412",
        "itemId" : "39046",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71871",
          "modType" : "1",
          "value" : "This is element 1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "361",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72034",
          "modType" : "2",
          "value" : "This is element 1 (Edited)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71871",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72034",
          "modType" : "2",
          "value" : "This is element 1 (Edited)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200412",
        "itemId" : "200412",
        "itemTypeId" : "2455059983007225765",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71871",
          "modType" : "1",
          "value" : "This is element 1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72034",
          "modType" : "2",
          "value" : "This is element 1 (Edited)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71871",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72034",
          "modType" : "2",
          "value" : "This is element 1 (Edited)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200407", "200408" ]
    },
    "200418" : {
      "item" : {
        "id" : "200418",
        "name" : "EnumSet1",
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "enumerations" : [ {
          "id" : "200419",
          "name" : "OPTION 1",
          "applicability" : {
            "id" : "1",
            "name" : "Base"
          },
          "ordinal" : 0
        }, {
          "id" : "200420",
          "name" : "OPTION 2",
          "applicability" : {
            "id" : "1",
            "name" : "Base"
          },
          "ordinal" : 1
        }, {
          "id" : "200421",
          "name" : "OPTION 3",
          "applicability" : {
            "id" : "1",
            "name" : "Base"
          },
          "ordinal" : 2
        }, {
          "id" : "200422",
          "name" : "OPTION 4",
          "applicability" : {
            "id" : "1",
            "name" : "Base"
          },
          "ordinal" : 3
        }, {
          "id" : "200437",
          "name" : "OPTION 5",
          "applicability" : {
            "id" : "1",
            "name" : "Base"
          },
          "ordinal" : 4
        } ],
        "description" : " OPTION 1=0 , OPTION 2=1 , OPTION 3=2 , OPTION 4=3"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200437",
        "itemId" : "39173",
        "itemTypeId" : "2455059983007225790",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "373",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72077",
          "modType" : "1",
          "value" : "4",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72077",
          "modType" : "1",
          "value" : "4",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200437",
        "itemId" : "39174",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "373",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72078",
          "modType" : "1",
          "value" : "OPTION 5",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72078",
          "modType" : "1",
          "value" : "OPTION 5",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200437",
        "itemId" : "200437",
        "itemTypeId" : "2455059983007225793",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "373",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72076",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72076",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200417" ]
    },
    "200416" : {
      "item" : {
        "id" : "200416",
        "name" : "Element Array",
        "beginByte" : 0.0,
        "beginWord" : 0.0,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "logicalType" : "integer",
        "autogenerated" : false,
        "units" : "Nm",
        "description" : "This is an element array (Edit)",
        "interfacePlatformTypeDefaultValue" : "0",
        "interfacePlatformTypeDescription" : "A 32 bit integer",
        "interfacePlatformTypeMinval" : "0",
        "interfacePlatformTypeMaxval" : "199",
        "interfaceElementAlterable" : false,
        "elementSizeInBytes" : 28.0,
        "elementSizeInBits" : 224.0,
        "interfaceElementIndexStart" : 0,
        "platformTypeName2" : "Integer1",
        "interfaceElementIndexEnd" : 6,
        "notes" : "",
        "platformTypeId" : 200411,
        "endByte" : 3.0,
        "endWord" : 6.0
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200416",
        "itemId" : "39062",
        "itemTypeId" : "2455059983007225802",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71899",
          "modType" : "1",
          "value" : "5",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "375",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72081",
          "modType" : "2",
          "value" : "6",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71899",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72081",
          "modType" : "2",
          "value" : "6",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200416",
        "itemId" : "39064",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71898",
          "modType" : "1",
          "value" : "This is an element array",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "374",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72080",
          "modType" : "2",
          "value" : "This is an element array (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71898",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72080",
          "modType" : "2",
          "value" : "This is an element array (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200416",
        "itemId" : "200416",
        "itemTypeId" : "6360154518785980502",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71898",
          "modType" : "1",
          "value" : "This is an element array",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72080",
          "modType" : "2",
          "value" : "This is an element array (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71898",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72080",
          "modType" : "2",
          "value" : "This is an element array (Edit)",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200407" ]
    },
    "200423" : {
      "item" : {
        "id" : "200423",
        "name" : "Element Enum",
        "beginByte" : 0.0,
        "beginWord" : 0.0,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "logicalType" : "enumeration",
        "autogenerated" : false,
        "units" : "",
        "description" : "Testing enums",
        "interfacePlatformTypeDefaultValue" : "",
        "interfacePlatformTypeDescription" : "",
        "interfacePlatformTypeMinval" : "",
        "interfacePlatformTypeMaxval" : "",
        "interfaceElementAlterable" : true,
        "elementSizeInBytes" : 4.0,
        "elementSizeInBits" : 32.0,
        "interfaceElementIndexStart" : 0,
        "platformTypeName2" : "Enum1",
        "interfaceElementIndexEnd" : 0,
        "notes" : "",
        "platformTypeId" : 200417,
        "endByte" : 3.0,
        "endWord" : 0.0
      },
      "changes" : [ ],
      "parents" : [ "200407" ]
    },
    "200427" : {
      "item" : {
        "id" : "200427",
        "name" : "Element Edit PT",
        "beginByte" : 0.0,
        "beginWord" : 0.0,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "logicalType" : "float",
        "autogenerated" : false,
        "units" : "dB",
        "description" : "Edit the platform type's values",
        "interfacePlatformTypeDefaultValue" : "0",
        "interfacePlatformTypeDescription" : "",
        "interfacePlatformTypeMinval" : "0",
        "interfacePlatformTypeMaxval" : "150",
        "interfaceElementAlterable" : true,
        "elementSizeInBytes" : 4.0,
        "elementSizeInBits" : 32.0,
        "interfaceElementIndexStart" : 0,
        "platformTypeName2" : "FLOAT1",
        "interfaceElementIndexEnd" : 0,
        "notes" : "",
        "platformTypeId" : 200426,
        "endByte" : 3.0,
        "endWord" : 0.0
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200426",
        "itemId" : "39108",
        "itemTypeId" : "3899709087455064783",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71962",
          "modType" : "1",
          "value" : "100",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "376",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72082",
          "modType" : "2",
          "value" : "150",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "337",
            "branchId" : "9"
          },
          "gammaId" : "71962",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72082",
          "modType" : "2",
          "value" : "150",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200426",
        "itemId" : "200426",
        "itemTypeId" : "6360154518785980503",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71962",
          "modType" : "1",
          "value" : "100",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72082",
          "modType" : "2",
          "value" : "150",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "71962",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72082",
          "modType" : "2",
          "value" : "150",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : true,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200407" ]
    },
    "200431" : {
      "item" : {
        "id" : "200431",
        "name" : "Connection A",
        "primaryNode" : 200393,
        "secondaryNode" : 200394,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "Added this connection",
        "transportType" : "HSDN"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200431",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "6039606571486514297",
          "name" : "Interface Connection Secondary Node",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_ONE",
          "idIntValue" : 1955695737,
          "idString" : "6039606571486514297"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "350",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72022",
          "modType" : "1",
          "value" : "-1,0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72022",
          "modType" : "1",
          "value" : "-1,0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200394",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200431",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "6039606571486514296",
          "name" : "Interface Connection Primary Node",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_ONE",
          "idIntValue" : 1955695736,
          "idString" : "6039606571486514296"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "350",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72023",
          "modType" : "1",
          "value" : "-1,0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72023",
          "modType" : "1",
          "value" : "-1,0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200393",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200431",
        "itemId" : "39145",
        "itemTypeId" : "4522496963078776538",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "350",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72019",
          "modType" : "1",
          "value" : "HSDN",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72019",
          "modType" : "1",
          "value" : "HSDN",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200431",
        "itemId" : "39146",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "350",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72020",
          "modType" : "1",
          "value" : "Connection A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72020",
          "modType" : "1",
          "value" : "Connection A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200431",
        "itemId" : "39147",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "350",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72021",
          "modType" : "1",
          "value" : "Added this connection",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72021",
          "modType" : "1",
          "value" : "Added this connection",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200431",
        "itemId" : "200431",
        "itemTypeId" : "126164394421696910",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "350",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72018",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72018",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ ]
    },
    "200430" : {
      "item" : {
        "id" : "200430",
        "name" : "Node A",
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "address" : "555",
        "color" : "#2c5926",
        "description" : "Added this node"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200430",
        "itemId" : "39141",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "349",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72016",
          "modType" : "1",
          "value" : "Node A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72016",
          "modType" : "1",
          "value" : "Node A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200430",
        "itemId" : "39142",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "349",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72017",
          "modType" : "1",
          "value" : "Added this node",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72017",
          "modType" : "1",
          "value" : "Added this node",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200430",
        "itemId" : "39143",
        "itemTypeId" : "5726596359647826656",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "349",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72015",
          "modType" : "1",
          "value" : "555",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72015",
          "modType" : "1",
          "value" : "555",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200430",
        "itemId" : "39144",
        "itemTypeId" : "5221290120300474048",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "349",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72014",
          "modType" : "1",
          "value" : "#2c5926",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72014",
          "modType" : "1",
          "value" : "#2c5926",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200430",
        "itemId" : "200430",
        "itemTypeId" : "6039606571486514295",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "349",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72013",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72013",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ ]
    },
    "200435" : {
      "item" : {
        "id" : "200435",
        "name" : "Element Added",
        "beginByte" : 0.0,
        "beginWord" : 0.0,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "logicalType" : "double",
        "autogenerated" : false,
        "units" : "Feet^2",
        "description" : "Added this element to Structure1",
        "interfacePlatformTypeDefaultValue" : "0",
        "interfacePlatformTypeDescription" : "",
        "interfacePlatformTypeMinval" : "0",
        "interfacePlatformTypeMaxval" : "1000",
        "interfaceElementAlterable" : true,
        "elementSizeInBytes" : 8.0,
        "elementSizeInBits" : 64.0,
        "interfaceElementIndexStart" : 0,
        "platformTypeName2" : "Double1",
        "interfaceElementIndexEnd" : 0,
        "notes" : "",
        "platformTypeId" : 200424,
        "endByte" : 3.0,
        "endWord" : 1.0
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200435",
        "itemId" : "39168",
        "itemTypeId" : "1152921504606847085",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "370",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72064",
          "modType" : "1",
          "value" : "",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72064",
          "modType" : "1",
          "value" : "",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200435",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "3899709087455064781",
          "name" : "Interface Element Platform Type",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : -450940211,
          "idString" : "3899709087455064781"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "370",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72069",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72069",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200424",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200435",
        "itemId" : "39169",
        "itemTypeId" : "2455059983007225788",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "370",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72065",
          "modType" : "1",
          "value" : "true",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72065",
          "modType" : "1",
          "value" : "true",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200435",
        "itemId" : "200435",
        "itemTypeId" : "2455059983007225765",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "370",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72063",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72063",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200435",
        "itemId" : "39166",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "370",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72066",
          "modType" : "1",
          "value" : "Element Added",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72066",
          "modType" : "1",
          "value" : "Element Added",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200435",
        "itemId" : "39167",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "370",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72067",
          "modType" : "1",
          "value" : "Added this element to Structure1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72067",
          "modType" : "1",
          "value" : "Added this element to Structure1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200407" ]
    },
    "200434" : {
      "item" : {
        "id" : "200434",
        "name" : "Element A",
        "beginByte" : 0.0,
        "beginWord" : 0.0,
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "logicalType" : "integer",
        "autogenerated" : false,
        "units" : "Nm",
        "description" : "Added this element",
        "interfacePlatformTypeDefaultValue" : "0",
        "interfacePlatformTypeDescription" : "A 32 bit integer",
        "interfacePlatformTypeMinval" : "0",
        "interfacePlatformTypeMaxval" : "199",
        "interfaceElementAlterable" : true,
        "elementSizeInBytes" : 4.0,
        "elementSizeInBits" : 32.0,
        "interfaceElementIndexStart" : 0,
        "platformTypeName2" : "Integer1",
        "interfaceElementIndexEnd" : 0,
        "notes" : "",
        "platformTypeId" : 200411,
        "endByte" : 3.0,
        "endWord" : 0.0
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200434",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "3899709087455064781",
          "name" : "Interface Element Platform Type",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : -450940211,
          "idString" : "3899709087455064781"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "369",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72062",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72062",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200411",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200434",
        "itemId" : "200434",
        "itemTypeId" : "2455059983007225765",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "369",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72056",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72056",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200434",
        "itemId" : "39162",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "369",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72059",
          "modType" : "1",
          "value" : "Element A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72059",
          "modType" : "1",
          "value" : "Element A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200434",
        "itemId" : "39163",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "369",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72060",
          "modType" : "1",
          "value" : "Added this element",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72060",
          "modType" : "1",
          "value" : "Added this element",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200434",
        "itemId" : "39164",
        "itemTypeId" : "1152921504606847085",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "369",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72057",
          "modType" : "1",
          "value" : "",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72057",
          "modType" : "1",
          "value" : "",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200434",
        "itemId" : "39165",
        "itemTypeId" : "2455059983007225788",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "369",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72058",
          "modType" : "1",
          "value" : "true",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72058",
          "modType" : "1",
          "value" : "true",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200433" ]
    },
    "200433" : {
      "item" : {
        "id" : "200433",
        "name" : "Structure A",
        "elements" : [ ],
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "Added this structure",
        "interfaceMaxSimultaneity" : "1",
        "bytesPerSecondMinimum" : 0.0,
        "interfaceStructureCategory" : "Tactical Status",
        "interfaceTaskFileType" : 6,
        "interfaceMinSimultaneity" : "1",
        "incorrectlySized" : false,
        "bytesPerSecondMaximum" : 0.0,
        "sizeInBytes" : 0.0,
        "numElements" : 0
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200433",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "2455059983007225781",
          "name" : "Interface Structure Content",
          "order" : "USER_DEFINED",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_MANY",
          "idIntValue" : 225187765,
          "idString" : "2455059983007225781"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "369",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72061",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72061",
          "modType" : "1",
          "value" : "-1,262144",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200434",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200433",
        "itemId" : "200433",
        "itemTypeId" : "2455059983007225776",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "368",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72048",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72048",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200433",
        "itemId" : "39156",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "368",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72051",
          "modType" : "1",
          "value" : "Structure A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72051",
          "modType" : "1",
          "value" : "Structure A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200433",
        "itemId" : "39157",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "368",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72052",
          "modType" : "1",
          "value" : "Added this structure",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72052",
          "modType" : "1",
          "value" : "Added this structure",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200433",
        "itemId" : "39158",
        "itemTypeId" : "2455059983007225756",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "368",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72050",
          "modType" : "1",
          "value" : "1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72050",
          "modType" : "1",
          "value" : "1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200433",
        "itemId" : "39159",
        "itemTypeId" : "2455059983007225755",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "368",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72049",
          "modType" : "1",
          "value" : "1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72049",
          "modType" : "1",
          "value" : "1",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200433",
        "itemId" : "39160",
        "itemTypeId" : "2455059983007225764",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "368",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72054",
          "modType" : "1",
          "value" : "Tactical Status",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72054",
          "modType" : "1",
          "value" : "Tactical Status",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200433",
        "itemId" : "39161",
        "itemTypeId" : "2455059983007225760",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "368",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72053",
          "modType" : "1",
          "value" : "6",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72053",
          "modType" : "1",
          "value" : "6",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200403" ]
    },
    "200432" : {
      "item" : {
        "id" : "200432",
        "name" : "Message A",
        "subMessages" : [ ],
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "initiatingNode" : null,
        "description" : "Added this message",
        "interfaceMessageNumber" : "3",
        "interfaceMessagePeriodicity" : "Periodic",
        "interfaceMessageRate" : "20",
        "interfaceMessageType" : "Connection",
        "interfaceMessageWriteAccess" : true
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "333",
          "name" : "RelationChange",
          "typeId" : 2834799904,
          "notRelationChange" : false,
          "notAttributeChange" : true,
          "idIntValue" : 333,
          "idString" : "333"
        },
        "artId" : "200432",
        "itemId" : "-1",
        "itemTypeId" : {
          "id" : "6039606571486514299",
          "name" : "Interface Message Sending Node",
          "order" : "LEXICOGRAPHICAL_ASC",
          "relationArtifactType" : "-1",
          "newRelationTable" : true,
          "ordered" : true,
          "multiplicity" : "MANY_TO_ONE",
          "idIntValue" : 1955695739,
          "idString" : "6039606571486514299"
        },
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72047",
          "modType" : "1",
          "value" : "-1,0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72047",
          "modType" : "1",
          "value" : "-1,0",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "200390",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200432",
        "itemId" : "39149",
        "itemTypeId" : "2455059983007225754",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72039",
          "modType" : "1",
          "value" : "true",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72039",
          "modType" : "1",
          "value" : "true",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200432",
        "itemId" : "39150",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72040",
          "modType" : "1",
          "value" : "Message A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72040",
          "modType" : "1",
          "value" : "Message A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200432",
        "itemId" : "39151",
        "itemTypeId" : "3899709087455064789",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72043",
          "modType" : "1",
          "value" : "Periodic",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72043",
          "modType" : "1",
          "value" : "Periodic",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200432",
        "itemId" : "39152",
        "itemTypeId" : "2455059983007225770",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72044",
          "modType" : "1",
          "value" : "Connection",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72044",
          "modType" : "1",
          "value" : "Connection",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200432",
        "itemId" : "39153",
        "itemTypeId" : "2455059983007225768",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72045",
          "modType" : "1",
          "value" : "3",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72045",
          "modType" : "1",
          "value" : "3",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200432",
        "itemId" : "39154",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72042",
          "modType" : "1",
          "value" : "Added this message",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72042",
          "modType" : "1",
          "value" : "Added this message",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200432",
        "itemId" : "39155",
        "itemTypeId" : "2455059983007225763",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72041",
          "modType" : "1",
          "value" : "20",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72041",
          "modType" : "1",
          "value" : "20",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200432",
        "itemId" : "200432",
        "itemTypeId" : "2455059983007225775",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "367",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72038",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72038",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200392" ]
    },
    "200436" : {
      "item" : {
        "id" : "200436",
        "name" : "Submessage A",
        "applicability" : {
          "id" : "1",
          "name" : "Base"
        },
        "description" : "Added this submessage",
        "interfaceSubMessageNumber" : "3"
      },
      "changes" : [ {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200436",
        "itemId" : "39170",
        "itemTypeId" : "1152921504606847088",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "371",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72071",
          "modType" : "1",
          "value" : "Submessage A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72071",
          "modType" : "1",
          "value" : "Submessage A",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200436",
        "itemId" : "39171",
        "itemTypeId" : "2455059983007225769",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "371",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72073",
          "modType" : "1",
          "value" : "3",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72073",
          "modType" : "1",
          "value" : "3",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "222",
          "name" : "AttributeChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : false,
          "idIntValue" : 222,
          "idString" : "222"
        },
        "artId" : "200436",
        "itemId" : "39172",
        "itemTypeId" : "1152921504606847090",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "371",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72072",
          "modType" : "1",
          "value" : "Added this submessage",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72072",
          "modType" : "1",
          "value" : "Added this submessage",
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      }, {
        "ignoreType" : "NONE",
        "changeType" : {
          "id" : "111",
          "name" : "ArtifactChange",
          "typeId" : 2834799904,
          "notRelationChange" : true,
          "notAttributeChange" : true,
          "idIntValue" : 111,
          "idString" : "111"
        },
        "artId" : "200436",
        "itemId" : "200436",
        "itemTypeId" : "126164394421696908",
        "baselineVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "firstNonCurrentChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "currentVersion" : {
          "transactionToken" : {
            "id" : "371",
            "branchId" : "1628313979987671715"
          },
          "gammaId" : "72070",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "destinationVersion" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : null,
          "modType" : "-1",
          "value" : null,
          "uri" : "",
          "valid" : false,
          "applicabilityToken" : null
        },
        "netChange" : {
          "transactionToken" : {
            "id" : "-1",
            "branchId" : "-1"
          },
          "gammaId" : "72070",
          "modType" : "1",
          "value" : null,
          "uri" : "",
          "valid" : true,
          "applicabilityToken" : {
            "id" : "1",
            "name" : "Base"
          }
        },
        "synthetic" : false,
        "artIdB" : "-1",
        "deleted" : false,
        "applicabilityCopy" : false
      } ],
      "parents" : [ "200399" ]
    }
  },
  "nodes" : [ "200391", "200390", "200394", "200430", "200396" ],
  "connections" : [ "200392", "200431" ],
  "messages" : [ "200402", "200432", "200399" ],
  "subMessages" : [ "200403", "200406", "200405", "200436", "200404" ],
  "structures" : [ "200433", "200407", "200410" ],
  "elements" : [ "200435", "200434", "200416", "200415", "200413", "200412", "200427", "200423" ],
  "enumSets" : [ "200418" ]
}