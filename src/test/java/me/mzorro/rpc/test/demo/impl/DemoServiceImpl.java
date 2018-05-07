package me.mzorro.rpc.test.demo.impl;

import me.mzorro.rpc.test.demo.api.DemoService;

/**
 * Created On 05/07 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        return "hello " + name;
    }
}
