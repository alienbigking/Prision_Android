package com.gkzxhn.prision.suite;

import com.gkzxhn.prision.uinttest.CallUserUnitTest;
import com.gkzxhn.prision.uinttest.LoginUnitTest;
import com.gkzxhn.prision.uinttest.MainUnitTest;
import com.gkzxhn.prision.uinttest.common.CustomCategory;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Raleigh.Luo on 17/5/8.
 */
@RunWith(Categories.class)
@Categories.IncludeCategory({CustomCategory.NormalTestCategory.class})
@Suite.SuiteClasses({LoginUnitTest.class, CallUserUnitTest.class})
public class CallUserSuitTest {}
