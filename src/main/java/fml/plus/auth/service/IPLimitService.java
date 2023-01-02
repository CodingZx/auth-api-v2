package fml.plus.auth.service;

import fml.plus.auth.common.constants.RedisConstants;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.Pager;
import fml.plus.auth.common.transaction.IAfterCommitExecutor;
import fml.plus.auth.dto.resp.IPLimitResp;
import fml.plus.auth.mapper.IPLimitMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(rollbackFor = Exception.class)
public class IPLimitService {
    private IPLimitMapper ipLimitMapper;
    private IAfterCommitExecutor afterCommitExecutor;
    private StringRedisTemplate redis;

    public Pager<IPLimitResp> list(Page page) {
        var wp = ipLimitMapper.wrapper().page(page.toRow());

        var list = wp.list();
        var count = wp.count();
        return Pager.of(list.stream().map(IPLimitResp::new).toList(), count);
    }

    public void delete(List<UUID> ids) {
        ids.forEach(id -> {
            var model = ipLimitMapper.findById(id);
            if(model == null) return;

            ipLimitMapper.deleteById(id);
            afterCommitExecutor.run(() -> redis.delete(RedisConstants.LoginKeys.ipLimitKey(model.getIpAddr())));
        });
    }
}
