import { PlConfigApplicUIBranchMapping } from "../types/pl-config-applicui-branch-mapping"
import { PlConfigBranchListingBranch } from "../types/pl-config-branch"

export const testBranchInfo =
{
  id: "3182843164128526558",
  name : "TW195 aaa",
  viewId : "-1",
  associatedArtifact : "200578",
  baselineTx : "937",
  parentTx : "21",
  parentBranch:
  {
    id : "8",
    viewId : "-1"  
  },
  branchState : "0",
  branchType : "0",
  inheritAccessControl : false,
  archived : false,
  shortName : "TW195 aaa",
  idIntValue : -1918287650
}
  
export const testBranchApplicability: PlConfigApplicUIBranchMapping =
{
  associatedArtifactId: "200578",
  branch:
  {
    id: "3182843164128526558",
    name : "TW195 aaa",
    viewId : "-1",
    idIntValue : -1918287650  
  },
  editable: true,
  featureIdToFeature:
  {  
    758071644:
    {  
      id: "758071644",
      name : "JHU_CONTROLLER",
      values:
        [
          "Included",
          "Excluded"
        ],  
      defaultValue: "Included",
      description : "A small point of variation",
      multiValued : false,
      valueType : "String",
      type : null,
      productApplicabilities:
        [

        ],
      idIntValue : 758071644,
      idString : "758071644"  
    },  
    130553732:
    {
      id : "130553732",
      name : "ROBOT_ARM_LIGHT",
      values:
        [
          "Included",
          "Excluded"
        ],  
      defaultValue: "Included",
      description : "A significant capability",
      multiValued : false,
      valueType : "String",
      type : null,
      productApplicabilities:
        [
          "OFP"
        ],
      idIntValue : 130553732,
      idString : "130553732"  
    },  
    1939294030:
    {
      id : "1939294030",
      name : "ENGINE_5",
      values : [ "A2543", "B5543" ],
      defaultValue : "A2543",
      description : "Used select type of engine",
      multiValued : false,
      valueType : "String",
      type : null,
      productApplicabilities:
        [
          "OFP"
        ],
      idIntValue : 1939294030,
      idString : "1939294030"  
    },  
    293076452:
    {
      id : "293076452",
      name : "ROBOT_SPEAKER",
      values:
        [
          "SPKR_A",
          "SPKR_B",
          "SPKR_C"
        ],
      defaultValue : "SPKR_A",
      description : "This feature is multi-select.",
      multiValued : true,
      valueType : "String",
      type : null,
      productApplicabilities:
        [

        ],
      idIntValue : 293076452,
      idString : "293076452"  
    }  
  },  
  featureToValueMaps:
    [
      {
        description: "This is a feature",
        feature: "feature1",
        id:"12345"
      }
    ],
  features:
    [
      {
        id: "1939294030",
        name : "ENGINE_5",
        values:
          [
            "A2543",
            "B5543"
          ],
        defaultValue : "A2543",
        description : "Used select type of engine",
        multiValued : false,
        valueType : "String",
        type : null,
        productApplicabilities:
          [
            "OFP"
          ],
        idIntValue: 1939294030,
        idString: "1939294030",
        setValueStr(): void {
        
        },
        setProductAppStr(): void{
        
        }
      },
      {
        id: "758071644",
        name : "JHU_CONTROLLER",
        values:
          [
            "Included",
            "Excluded"
          ],
        defaultValue : "Included",
        description : "A small point of variation",
        multiValued : false,
        valueType : "String",
        type : null,
        productApplicabilities:
          [

          ],
        idIntValue : 758071644,
        idString: "758071644",
        setValueStr(): void {
        
        },
        setProductAppStr(): void{
        
        }
      },
      {
        id : "130553732",
        name : "ROBOT_ARM_LIGHT",
        values:
          [
            "Included",
            "Excluded"
          ],
        defaultValue : "Included",
        description : "A significant capability",
        multiValued : false,
        valueType : "String",
        type : null,
        productApplicabilities:
          [
            "OFP"
          ],
        idIntValue : 130553732,
        idString : "130553732",
        setValueStr(): void {
        
        },
        setProductAppStr(): void{
        
        }
      },
      {
        id : "293076452",
        name : "ROBOT_SPEAKER",
        values:
          [
            "SPKR_A",
            "SPKR_B",
            "SPKR_C"
          ],
        defaultValue : "SPKR_A",
        description : "This feature is multi-select.",
        multiValued : true,
        valueType : "String",
        type : null,
        productApplicabilities:
          [

          ],
        idIntValue : 293076452,
        idString: "293076452",
        setValueStr(): void {
        
        },
        setProductAppStr(): void{
        
        }
      }
    ],
  featuresOrdered:
    [
      "ENGINE_5",
      "JHU_CONTROLLER",
      "ROBOT_ARM_LIGHT",
      "ROBOT_SPEAKER"
    ],
  groups:
    [
      {
        id : "736857919",
        name : "abGroup"
      }
    ],
  parentBranch:
  {
    id : "8",
    name : "SAW Product Line",
    viewId : "-1",
    idIntValue : 8  
  },  
  views:
    [
      {
        id: "200045",
        name : "Product A"
      },
      {
        id : "200046",
        name : "Product B"
      },
      {
        id : "200047",
        name : "Product C"
      },
      {
        id : "200048",
        name : "Product D"
      }
    ],
  viewsOrdered:
    [
      "Product A",
      "Product B",
      "Product C",
      "Product D"
    ],  
}
export const testBranchListing: PlConfigBranchListingBranch[] = [
  {
    id: '890328402',
    name: "Branch 1",
    viewId: "-1",
    associatedArtifact: "-1",
    baselineTx: "937",
    parentTx: "21",
    parentBranch:
    {
      id: "8",
      viewId:"-1"
    },
    branchState: "1",
    branchType: "2",
    inheritAccessControl: false,
    archived: false,
    shortName: "Product Line",
    idIntValue:890328402
  },
  {
    id: '890328403',
    name: "Branch 2",
    viewId: "-1",
    associatedArtifact: "-1",
    baselineTx: "937",
    parentTx: "21",
    parentBranch:
    {
      id: "890328402",
      viewId:"-1"
    },
    branchState: "1",
    branchType: "0",
    inheritAccessControl: false,
    archived: false,
    shortName: "Working Branch",
    idIntValue:890328403
  },
  {
    id: '890328404',
    name: "Branch 3",
    viewId: "-1",
    associatedArtifact: "46512388465",
    baselineTx: "937",
    parentTx: "21",
    parentBranch:
    {
      id: "890328402",
      viewId:"-1"
    },
    branchState: "0",
    branchType: "0",
    inheritAccessControl: false,
    archived: false,
    shortName: "TW197- Actioned Branch",
    idIntValue:890328404
  }, 
]  