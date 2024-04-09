# How to Create Users

This set of curl commands will allow you to create users and assign roles using OSEE's API.

## Note

Any items in this file with '{}' **needs** to be replaced with an actual value.

## Create New User

NOTE: You can add as many login ids as you want to the attributes list. Just add another object with typeName as "Login Id" and value as whatever additional value you want available for the login ID.

```
curl -v --location --request POST '{application-server-url}/orcs/txs' --header 'Authorization: Basic 3333' --header 'Content-Type: application/json' --data-raw '{
    "branch": "570",
    "txComment": "Created user",
    "createArtifacts": [
        {
            "typeId": "5",
            "name": "{User's name (i.e. Joe Smith)}",
            "attributes": [
                {
                    "typeName": "Email",
                    "value": "{email address}"
                },
                {
                    "typeName": "Active",
                    "value": "true"
                },
                {
                    "typeName": "User Id",
                    "value": "{Use a production-worthy ID}"
                },
                {
                    "typeName": "Login Id",
                    "value": "{windows login ID OR email address}"
                }
            ]
        }
    ]
}'
```

## Clear Server User Cache

```
curl -v -s -X DELETE {application-server-url}/orcs/datastore/user/cache --header 'Authorization: Basic 3333'
```

## Verify New User is in the Server User Cache (after cache clear)

```
curl -v -X GET -H "Authorization: Basic {your login ID}" {application-server-url}/orcs/datastore/user
```

## Add Roles (Users Relation) to your User Manually

### Note

You will need to clear the user cache (and possibly browser cache) after adding any role to your user.

### Format to Add Any Role

```
curl -v --request POST '{application-server-url}/orcs/branch/570/relation/createRelationByType/sideA/{User group ID}/sideB/{User ID}/relationTypeToken/2305843009213694308' --header 'Authorization: Basic 3333'
```

### Examples

#### Everyone

curl -v --request POST '{application-server-url}/orcs/branch/570/relation/createRelationByType/sideA/48656/sideB/{User ID}/relationTypeToken/2305843009213694308' --header 'Authorization: Basic 3333'

#### Agile User

curl -v --request POST '{application-server-url}/orcs/branch/570/relation/createRelationByType/sideA/10635635/sideB/{User ID}/relationTypeToken/2305843009213694308' --header 'Authorization: Basic 3333'

#### Default Artifact Editor

curl -v --request POST '{application-server-url}/orcs/branch/570/relation/createRelationByType/sideA/10862351/sideB/{User ID}/relationTypeToken/2305843009213694308' --header 'Authorization: Basic 3333'

#### Account Admin

curl -v --request POST '{application-server-url}/orcs/branch/570/relation/createRelationByType/sideA/8033604/sideB/{User ID}/relationTypeToken/2305843009213694308' --header 'Authorization: Basic 3333'

#### OSEE Access Admin

curl -v --request POST '{application-server-url}/orcs/branch/570/relation/createRelationByType/sideA/8033605/sideB/{User ID}/relationTypeToken/2305843009213694308' --header 'Authorization: Basic 3333'

#### Requirements

curl -v --request POST '{application-server-url}/orcs/branch/570/relation/createRelationByType/sideA/200059/sideB/{User ID}/relationTypeToken/2305843009213694308' --header 'Authorization: Basic 3333'

#### Code

curl -v --request POST '{application-server-url}/orcs/branch/570/relation/createRelationByType/sideA/200060/sideB/{User ID}/relationTypeToken/2305843009213694308' --header 'Authorization: Basic 3333'

#### ARB

curl -v --request POST '{application-server-url}/orcs/branch/570/relation/createRelationByType/sideA/150338509/sideB/{User ID}/relationTypeToken/2305843009213694313' --header 'Authorization: Basic 3333'
