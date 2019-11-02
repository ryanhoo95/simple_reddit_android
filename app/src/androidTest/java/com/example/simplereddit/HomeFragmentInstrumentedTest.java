package com.example.simplereddit;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.example.simplereddit.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(JUnit4.class)
public class HomeFragmentInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> rule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        // wait for network call
        Thread.sleep(5000);
    }

    @After
    public void tearDown() {
        // finish activity
        rule.getActivity().finish();
    }

    @Test
    public void sendClickShouldDisplayEmptyInputErrorMessage() {
        // check that topic input is present
        Espresso.onView((withId(R.id.edt_topic))).check(matches(notNullValue()));
        Espresso.onView(withId(R.id.edt_topic)).check(matches(withHint(R.string.txt_new_topic)));

        // clear input
        Espresso.onView(withId(R.id.edt_topic)).perform(clearText());

        // click send
        Espresso.onView((withId(R.id.btn_send))).check(matches(notNullValue()));
        Espresso.onView((withId(R.id.btn_send))).check(matches(withText(R.string.txt_send)));
        Espresso.onView((withId(R.id.btn_send)))
                .perform(click());

        // should have error text
        Espresso.onView((withId(R.id.edt_topic))).check(matches(hasErrorText(
                rule.getActivity().getResources().getString(R.string.txt_field_required))));
    }
}
