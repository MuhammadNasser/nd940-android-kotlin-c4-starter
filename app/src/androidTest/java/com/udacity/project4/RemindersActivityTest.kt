package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.ToastManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get


@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
//    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(ToastManager.getIdlingResource())
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(ToastManager.getIdlingResource())
    }


//    (DONE) TODO: add End to End testing to the app


    @Test
    fun remindersScreen_clickOnAddReminder_openSaveReminderScreen() = runBlockingTest {

        // start RemindersActivity
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
//        dataBindingIdlingResource.monitorActivity(scenario)

        // click on add Reminders FAB
        onView(withId(R.id.addReminderFAB)).perform(click())

        // check that we are on SaveReminder screen
        onView(withId(R.id.reminderTitle)).check(matches(isDisplayed()))

        //Make sure the Activity is closed
        scenario.close()
    }

    @Test
    fun addReminderLocationFromMap_saveReminder() {
        // start RemindersActivity
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
//        dataBindingIdlingResource.monitorActivity(scenario)

        // click on add Reminders FAB
        onView(withId(R.id.addReminderFAB)).perform(click())

        // check that we are on SaveReminder screen
        onView(withId(R.id.reminderTitle)).check(matches(isDisplayed()))

        // add title in reminderTitle editText
        onView(withId(R.id.reminderTitle)).perform(replaceText("Test title"))

        // add title in reminderDescription editText
        onView(withId(R.id.reminderDescription)).perform(replaceText("Test description"))

        // click on selectLocation to open map screen
        onView(withId(R.id.selectLocation)).perform(click())

        // select location on the map
        onView(withId(R.id.mapView)).perform(longClick())

        //click on confirm btn
        onView(withId(R.id.confirm_button)).perform(click())

        //click saveReminder btn
        onView(withId(R.id.saveReminder)).perform(click())

        //make sure the added Toast message is displayed
        onView(withText("Geofence added"))
            .inRoot(withDecorView(not(getActivity(scenario)?.window?.decorView)))
            .check(
                matches(isDisplayed())
            )

        ToastManager.increment()

        //make sure the saved Toast message is displayed
        onView(withText("Reminder Saved!"))
            .inRoot(withDecorView(not(getActivity(scenario)?.window?.decorView)))
            .check(
                matches(isDisplayed())
            )
        //close activity
        scenario.close()
    }

    private fun getActivity(scenario: ActivityScenario<RemindersActivity>): Activity? {
        var activity: Activity? = null
        scenario.onActivity {
            activity = it
        }

        return activity
    }

    @Test
    fun checkReminderLocationValidation_showSnackbarMessage() {
        // start RemindersActivity
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
//        dataBindingIdlingResource.monitorActivity(scenario)

        // click on add Reminders FAB
        onView(withId(R.id.addReminderFAB)).perform(click())

        // check that we are on SaveReminder screen
        onView(withId(R.id.reminderTitle)).check(matches(isDisplayed()))

        // add title in reminderDescription editText
        onView(withId(R.id.reminderDescription)).perform(replaceText("Test description"))

        // click on selectLocation to open map screen
        onView(withId(R.id.selectLocation)).perform(click())

        // select location on the map
        onView(withId(R.id.mapView)).perform(longClick())

        //click on confirm btn
        onView(withId(R.id.confirm_button)).perform(click())

        //click saveReminder btn
        onView(withId(R.id.saveReminder)).perform(click())

        //make sure the validation snackbar message is displayed
        onView(withId(R.id.snackbar_text)).check(matches(withText("Please enter title")))

        //close activity
        scenario.close()
    }
}
