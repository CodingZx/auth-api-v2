package fml.plus.auth.controller;

import fml.plus.auth.common.annotation.Authority;
import fml.plus.auth.common.aop.log.BusinessType;
import fml.plus.auth.common.aop.log.Log;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.Pager;
import fml.plus.auth.common.model.R;
import fml.plus.auth.dto.resp.ExceptionLogResp;
import fml.plus.auth.service.ExceptionLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/exception/log")
public class ExceptionLogController {
    private ExceptionLogService exceptionLogService;

    @Authority("exception:log:list")
    @GetMapping("/list")
    public R<Pager<ExceptionLogResp>> list(@RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam("status") int status) {
        return R.ok(exceptionLogService.list(Page.of(page, size), status));
    }

    @Authority("exception:log:list")
    @Log(businessType = BusinessType.SELECT, title = "错误上报详情")
    @GetMapping("/{id}/detail")
    public R<ExceptionLogResp> findById(@PathVariable UUID id) {
        return R.ok(exceptionLogService.findById(id));
    }

    @Authority("exception:log:status")
    @Log(businessType = BusinessType.SAVE, title = "错误上报状态修改")
    @PutMapping("/{id}/set/{status}")
    public R<?> updateStatus(@PathVariable UUID id, @PathVariable int status) {
        exceptionLogService.updateStatus(id, status);
        return R.ok();
    }

    @Authority("exception:log:delete")
    @Log(businessType = BusinessType.DELETE, title = "删除错误上报")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable UUID id) {
        exceptionLogService.delete(id);
        return R.ok();
    }

    @Authority("exception:log:download")
    @Log(businessType = BusinessType.OTHER, title = "下载错误信息")
    @PostMapping("/down/{id}")
    public void download(@PathVariable UUID id, HttpServletResponse response) throws IOException {
        var model = exceptionLogService.findById(id);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;fileName=error.txt");
        try(var out = response.getOutputStream()) {
            String builder = "信息:" + model.getLogMsg() + "\n" +
                    "详情:" + model.getMsgDetail() + "\n";
            out.write(builder.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }
}
