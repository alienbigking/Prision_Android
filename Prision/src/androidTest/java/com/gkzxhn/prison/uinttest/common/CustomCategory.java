package com.gkzxhn.prison.uinttest.common;

/**
 * Created by Raleigh.Luo on 17/5/5.
 * 自定义组合(集成)测试分类(可以是class，也可以是interface)
 *
 * 单类单元测试声明  @Category(RunCases.class)
 * 集合单元测试声明  @Category({RunCases.class,TestTwo.class})
 * Suite中组合(集成)测试过滤声明，也可使用集合，同上
 * Suite中必须指定Runner为 @RunWith(Categories.class)
 *  1.包含 @Categories.IncludeCategory(CustomCategory.NormalTestCategory.class)
 *  2.不包含 @Categories.ExcludeCategory(CustomCategory.NormalTestCategory.class)
 */

public class CustomCategory {
    public interface NormalTestCategory {}//正常测试分类，一般指主功能测试
    public interface AbnormalTestCategory{}//异常测试分类
    public interface NoPrimaryFunctionTestCategory{}//非主功能测试分类
}
