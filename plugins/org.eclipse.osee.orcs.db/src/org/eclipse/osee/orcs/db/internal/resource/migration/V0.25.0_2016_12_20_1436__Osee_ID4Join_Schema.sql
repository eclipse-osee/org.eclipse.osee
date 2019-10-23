-- 4 Element Join
CREATE TABLE OSEE_JOIN_ID4 (
       QUERY_ID int NOT NULL,
       ID1 ${db.bigint} NOT NULL,
       ID2 ${db.bigint} NOT NULL,
       ID3 ${db.bigint} NOT NULL,
       ID4 ${db.bigint} NOT NULL,
       CONSTRAINT OSEE_JOIN_ID4_Q_I1_I2_I3_I4_PK PRIMARY KEY (QUERY_ID, ID1, ID2, ID3, ID4))
       ${db.organization_index_2}
       ${db.tablespace.osee_join};

CREATE INDEX OSEE_JOIN_ID4__Q_IDX ON OSEE_JOIN_ID4 (QUERY_ID) ${db.tablespace.osee_index};