@org.hibernate.annotations.TypeDef(name = EntityConst.PG_ENUM_TYPE, typeClass =
    PostgreSQLEnumType.class)
@org.hibernate.annotations.TypeDef(name = EntityConst.PG_JSONB_TYPE, typeClass =
    JsonBinaryType.class)
@org.hibernate.annotations.TypeDef(name = EntityConst.PG_INT_ARRAY_TYPE, typeClass =
    IntArrayType.class)
@org.hibernate.annotations.TypeDef(name = EntityConst.PG_LONG_ARRAY_TYPE, typeClass =
    LongArrayType.class)
@org.hibernate.annotations.TypeDef(name = EntityConst.PG_STRING_ARRAY_TYPE, typeClass =
    StringArrayType.class)
@org.hibernate.annotations.TypeDef(name = EntityConst.PG_DOUBLE_ARRAY_TYPE, typeClass =
    DoubleArrayType.class)
package com.trp.ballgame.model.entities;

import com.vladmihalcea.hibernate.type.array.DoubleArrayType;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.LongArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
