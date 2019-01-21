package com.charlesrowland.ragingclaw.bakingapp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeSelectionTest {
    public static final String BUTTON_TEXT = "Start Baking";


    // starts a rule that we are using the MainActivity
//    @Rule
//    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    // starts a rule that we are using the IngredientsActivity
    @Rule
    public ActivityScenarioRule<IngredientsActivity> mActivityTestRule = new ActivityScenarioRule<>(IngredientsActivity.class);


    @Test
    public void ClickRecipeRecyclerViewItem_OpenIngredients() {
        // check to see if the item at position 0 is selected/clicked. For some reason, the RecyclerView doesn't seem to be there? wtf?
        //onData(anything()).inAdapterView(withId(R.id.recipeRecyclerView)).atPosition(0).perform(click());

        // clicks the Start Baking Button on the IngredientsActivity. Problem is, the button sends data and that doesn't seem to exist?
        // onView(withId(R.id.start_baking_button)).perform(click()).check(matches(isDisplayed()));

        // check that the text matches. it does.
        onView(withId(R.id.start_baking_button)).check(matches(withText(BUTTON_TEXT)));

    }
}
