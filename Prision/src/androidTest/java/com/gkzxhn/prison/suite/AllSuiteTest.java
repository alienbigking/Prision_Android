package com.gkzxhn.prison.suite;

import com.gkzxhn.prison.uinttest.CallUserUnitTest;
import com.gkzxhn.prison.uinttest.ConfigUnitTest;
import com.gkzxhn.prison.uinttest.LoginUnitTest;
import com.gkzxhn.prison.uinttest.MainUnitTest;
import com.gkzxhn.prison.uinttest.SettingUnitTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Raleigh.Luo on 17/5/3.
 * 集成测试 使用一个Runner进行异步测试
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({LoginUnitTest.class,MainUnitTest.class,SettingUnitTest.class,
        ConfigUnitTest.class, CallUserUnitTest.class})
public class AllSuiteTest {
}
