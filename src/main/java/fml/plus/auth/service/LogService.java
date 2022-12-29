package fml.plus.auth.service;

import com.google.common.base.Strings;
import fml.plus.auth.common.exception.BusinessException;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.Pager;
import fml.plus.auth.common.util.SQLUtils;
import fml.plus.auth.dto.resp.LogDetailResp;
import fml.plus.auth.dto.resp.LogResp;
import fml.plus.auth.entity.LogEntity;
import fml.plus.auth.mapper.LogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class LogService {
    @Autowired
    private LogMapper logMapper;

    public Pager<LogResp> list(Page page, String operName, LocalDateTime start, LocalDateTime end) {
        var query = logMapper.wrapper().page(page.toRow());
        if(!Strings.isNullOrEmpty(operName)) query.like(LogEntity::getOperName, SQLUtils.fuzzyAll(operName));
        if(start != null) query.gt(LogEntity::getCreateTime, start);
        if(end != null) query.le(LogEntity::getCreateTime, end);
        query.orderByDesc(LogEntity::getCreateTime);

        var list = query.list();
        var count = query.count();

        if(list.isEmpty()) {
            return Pager.ofEmpty(count);
        }
        return Pager.of(list.stream().map(LogResp::new).collect(Collectors.toList()), count);
    }

    public LogDetailResp detail(UUID id) {
        var detail = logMapper.findById(id);
        if(detail == null) throw new BusinessException("数据ID错误");
        return new LogDetailResp(detail);
    }

    public void remove(List<UUID> ids) {
        for(var id : ids) {
            logMapper.deleteById(id);
        }
    }

    public void clear() {
        logMapper.wrapper().delete();
    }
}
