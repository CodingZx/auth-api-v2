package fml.plus.auth.controller;

import fml.plus.auth.common.model.R;
import fml.plus.auth.dto.resp.MonitorResp;
import fml.plus.auth.dto.resp.SysInfoResp;
import fml.plus.auth.service.MonitorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/system")
public class SystemController {
    private MonitorService monitorService;

    @GetMapping("/env")
    public R<SysInfoResp> env() {
        return R.ok(new SysInfoResp());
    }

    @GetMapping("/servers")
    public R<List<String>> servers() {
        return R.ok(monitorService.servers());
    }

    @GetMapping("/monitor")
    public R<List<MonitorResp>> monitor(@RequestParam int minutes,
                                        @RequestParam String server) {
        return R.ok(monitorService.monitor(server, minutes));
    }
}
