package fml.plus.auth.controller;

import fml.plus.auth.common.model.Pager;
import fml.plus.auth.dto.resp.LogDetailResp;
import fml.plus.auth.dto.resp.LogResp;
import fml.plus.auth.service.LogService;
import fml.plus.auth.common.annotation.Authority;
import fml.plus.auth.common.aop.log.Log;
import fml.plus.auth.common.aop.log.BusinessType;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.R;
import fml.plus.auth.common.util.time.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/log")
public class LogController {
    private LogService logService;

    @Authority("auth:log")
    @GetMapping("/list")
    @Log(title = "日志列表", businessType = BusinessType.SELECT)
    public R<Pager<LogResp>> list(@RequestParam("page") int page,
                                  @RequestParam("size") int size,
                                  @RequestParam(value = "operName", required = false) String operName,
                                  @RequestParam(value = "start", required = false) String startTime,
                                  @RequestParam(value = "end", required = false) String endTime) {
        LocalDateTime start = null;
        try {
            start = LocalDateTimeUtils.parseTime(startTime);
        } catch (Exception ignore){}
        LocalDateTime end = null;
        try {
            end = LocalDateTimeUtils.parseTime(endTime);
        } catch (Exception ignore){}
        return R.ok(logService.list(Page.of(page, size), operName, start, end));
    }

    @Authority("auth:log:delete")
    @DeleteMapping("/remove/{ids}")
    @Log(title = "删除日志", businessType = BusinessType.DELETE)
    public R<?> remove(@PathVariable("ids") UUID[] ids) {
        logService.remove(Arrays.asList(ids));
        return R.ok();
    }

    @Authority("auth:log:detail")
    @GetMapping("/{id}/detail")
    @Log(title = "日志详情", businessType = BusinessType.SELECT)
    public R<LogDetailResp> detail(@PathVariable UUID id) {
        return R.ok(logService.detail(id));
    }

    @Authority("auth:log:clear")
    @DeleteMapping("/clear")
    @Log(title = "清空日志", businessType = BusinessType.DELETE)
    public R<?> clear() {
        logService.clear();
        return R.ok();
    }

}
