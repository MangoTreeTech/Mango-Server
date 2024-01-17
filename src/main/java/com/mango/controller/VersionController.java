package com.mango.controller;

import com.mango.config.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
@Slf4j
public class VersionController {
    @Autowired
    private Constant constant;

    /**
     * 返回最新版本，更多功能等待添加
     * @return
     */
    @GetMapping("/latest")
    public String getLatestVersion() {
        return constant.LATEST_VERSION;
    }
}
