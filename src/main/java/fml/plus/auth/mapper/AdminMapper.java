package fml.plus.auth.mapper;

import fml.plus.auth.common.mybatis.mapper.BaseMapper;
import fml.plus.auth.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface AdminMapper extends BaseMapper<UUID, AccountEntity> {

    /**
     * 根据用户名查询
     */
    default AccountEntity findByUserName(String userName) {
        return wrapper().eq(AccountEntity::getUserName, userName).one();
    }
}
