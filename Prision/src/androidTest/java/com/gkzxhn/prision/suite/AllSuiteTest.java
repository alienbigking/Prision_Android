package com.gkzxhn.prision.suite;

import com.gkzxhn.prision.uinttest.CallUserUnitTest;
import com.gkzxhn.prision.uinttest.ConfVideoUnitTest;
import com.gkzxhn.prision.uinttest.ConfigUnitTest;
import com.gkzxhn.prision.uinttest.LoginUnitTest;
import com.gkzxhn.prision.uinttest.MainUnitTest;
import com.gkzxhn.prision.uinttest.SettingUnitTest;

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
