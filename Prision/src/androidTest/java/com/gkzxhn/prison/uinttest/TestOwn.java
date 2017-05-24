package com.gkzxhn.prison.uinttest;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.gkzxhn.prison.activity.MainActivity;
import com.gkzxhn.prison.uinttest.common.Constants;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Raleigh.Luo on 17/5/9.
 */

public class TestOwn {
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>
            (MainActivity.class);
    @Test
    public void activityResult_IsHandledProperly() throws InterruptedException {
        // Build a result to return when a particular activity is launched.
//        Intent resultData = new Intent();
////        String phoneNumber = "123-345-6789";
////        resultData.putExtra("phone", phoneNumber);
////        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
//        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
//        // Set up result stubbing when an intent sent to "contacts" is seen.
//        intending(toPackage(Constants.CURRENT_PACKAGENAME)).respondWith(result);
//        Thread.sleep(Constants.WAIT_TIMEOUT);

        //检测目标activity收到了intent中包含的相应包名和消息
        intended(allOf(
                hasComponent(hasShortClassName(".activity.CallUserActivity")),
                toPackage(Constants.CURRENT_PACKAGENAME),
                hasExtra(com.gkzxhn.prison.common.Constants.EXTRA, "149")));

    }
}
