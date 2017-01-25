-- 4 Element Join
CREATE TABLE OSEE_JOIN_ID4 (
       QUERY_ID int NOT NULL,
       ID1 ${db.bigint} NOT NULL,
       ID2 ${db.bigint} NOT NULL,
       ID3 ${db.bigint} NOT NULL,
       ID4 ${db.bigint} NOT NULL,
       CONSTRAINT OSEE_JOIN_ID4__T_ID1_ID2_ID3_ID4_PK PRIMARY KEY (QUERY_ID, ID1, ID2, ID3, ID4))
       ${db.organization_index};

CREATE INDEX OSEE_JOIN_ID4__G_IDX ON OSEE_JOIN_ID4 (QUERY_ID);