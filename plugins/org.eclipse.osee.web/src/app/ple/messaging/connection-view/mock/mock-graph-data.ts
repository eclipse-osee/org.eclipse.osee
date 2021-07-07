import { mockData } from "../types/mockdata";

export const data:mockData[] = [
    {
        id:'-1',
        nodes: [],
        edges:[]
    },
    {
        id: '8',
        nodes: [
            {
                id: '0',
                // dimension:
                // {
                //     width: 500,
                //     height:500
                // },
                label: "Basic Node",
                
            },
            {
                id: '1',
                // dimension:
                // {
                //     width: 500,
                //     height:500
                // },
                label: "Basic Node 2",
                
            }
        ],
        edges: [
            {
                id: 'a',
                source: '0',
                target: '1',
                label:'Ethernet Connection'
            }
        ]
    }
]