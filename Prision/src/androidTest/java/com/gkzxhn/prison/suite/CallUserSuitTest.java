package com.gkzxhn.prison.suite;

import com.gkzxhn.prison.uinttest.CallUserUnitTest;
import com.gkzxhn.prison.uinttest.LoginUnitTest;
import com.gkzxhn.prison.uinttest.common.CustomCategory;

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
