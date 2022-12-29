package fml.plus.auth.common.mybatis.mapper;

import fml.plus.auth.common.mybatis.mapper.mapper.EntityMapper;
import fml.plus.auth.common.mybatis.mapper.mapper.ExampleMapper;

import java.io.Serializable;

public interface BaseMapper<I extends Serializable, T> extends EntityMapper<I, T>, ExampleMapper<T> {

}
