-- 2 Element Tuple
CREATE TABLE OSEE_TUPLE2 (
	TUPLE_TYPE ${db.bigint} NOT NULL,
	E1 ${db.bigint} NOT NULL,
	E2 ${db.bigint} NOT NULL,
	GAMMA_ID ${db.bigint} NOT NULL,
	CONSTRAINT TUPLE2__T_E1_E2_PK PRIMARY KEY (TUPLE_TYPE, E1, E2))
	${db.organization_index};
 
CREATE INDEX OSEE_TUPLE2__G_IDX ON OSEE_TUPLE2 (GAMMA_ID);

-- 3 Element Tuple
CREATE TABLE OSEE_TUPLE3 (
	TUPLE_TYPE ${db.bigint} NOT NULL,
	E1 ${db.bigint} NOT NULL,
	E2 ${db.bigint} NOT NULL,
	E3 ${db.bigint} NOT NULL,
	GAMMA_ID ${db.bigint} NOT NULL,
	CONSTRAINT TUPLE3__T_E1_E2_E3_PK PRIMARY KEY (TUPLE_TYPE, E1, E2, E3))
	${db.organization_index};
 
CREATE INDEX OSEE_TUPLE3__G_IDX ON OSEE_TUPLE3 (GAMMA_ID);

-- 4 Element Tuple
CREATE TABLE OSEE_TUPLE4 (
	TUPLE_TYPE ${db.bigint} NOT NULL,
	E1 ${db.bigint} NOT NULL,
	E2 ${db.bigint} NOT NULL,
	E3 ${db.bigint} NOT NULL,
	E4 ${db.bigint} NOT NULL,
	GAMMA_ID ${db.bigint} NOT NULL,
	CONSTRAINT OSEE_TUPLE4__T_E1_E2_E3_E4_PK PRIMARY KEY (TUPLE_TYPE, E1, E2, E3, E4))
	${db.organization_index_3};

CREATE INDEX OSEE_TUPLE4__G_IDX ON OSEE_TUPLE4 (GAMMA_ID);

CREATE TABLE OSEE_KEY_VALUE (
	KEY ${db.bigint} NOT NULL,
	VALUE varchar(4000) NOT NULL,
	CONSTRAINT OSEE_KEY_VALUE__K_PK PRIMARY KEY (KEY))
	${db.organization_index_key_value}
	${db.tablespace}
	${db.pctthreshold}
	${db.overflow};
	
CREATE INDEX OSEE_KEY_VALUE__V_IDX ON OSEE_KEY_VALUE (VALUE);