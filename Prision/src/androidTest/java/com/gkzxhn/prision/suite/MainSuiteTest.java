package com.gkzxhn.prision.suite;

import com.gkzxhn.prision.uinttest.LoginUnitTest;
import com.gkzxhn.prision.uinttest.MainUnitTest;
import com.gkzxhn.prision.uinttest.common.CustomCategory;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Raleigh.Luo on 17/5/5.
 */
//@RunWith(Suite.class)
@RunWith(Categories.class)
@Categories.ExcludeCategory({CustomCategory.AbnormalTestCategory.class})//指定测试分类
@Suite.SuiteClasses({LoginUnitTest.class,MainUnitTest.class})
public class MainSuiteTest {}
