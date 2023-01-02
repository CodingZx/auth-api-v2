package fml.plus.auth.controller;

import fml.plus.auth.common.annotation.Authority;
import fml.plus.auth.common.aop.log.BusinessType;
import fml.plus.auth.common.aop.log.Log;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.Pager;
import fml.plus.auth.common.model.R;
import fml.plus.auth.dto.resp.IPLimitResp;
import fml.plus.auth.service.IPLimitService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/ip/limit")
public class IPLimitController {
    private IPLimitService ipLimitService;

    @Authority("ip:limit:list")
    @GetMapping("/list")
    @Log(title = "IP限制列表", businessType = BusinessType.SELECT)
    public R<Pager<IPLimitResp>> list(@RequestParam("page") int page,
                                      @RequestParam("size") int size) {
        return R.ok(ipLimitService.list(Page.of(page, size)));
    }

    @Authority("ip:limit:delete")
    @DeleteMapping("/{ids}")
    @Log(businessType = BusinessType.DELETE, title = "删除IP限制记录")
    public R<?> delete(@PathVariable UUID[] ids) {
        ipLimitService.delete(Arrays.asList(ids));
        return R.ok();
    }
}
