package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    // (DONE) TODO: provide testing to the SaveReminderView and its live data objects

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var reminderFakeRepository: ReminderDataSource
    lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun initTheRepoAndViewModel() {
        stopKoin()
        reminderFakeRepository = FakeDataSource()

        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(), reminderFakeRepository
        )
    }

    private fun createReminder(isInvalid: Boolean): ReminderDataItem {
        return if (isInvalid) {
            ReminderDataItem(null, "Description", "Location name", 1.0, 1.1, "id")
        } else {
            ReminderDataItem("Title", "Description", "Location name", 1.0, 1.1, "id")
        }
    }

    @Test
    fun validateReminder_InvalidReminder() {
        val tmpReminder = createReminder(true)

        val result = saveReminderViewModel.validateEnteredData(tmpReminder)

        assertThat(result, `is`(false))
    }

    @Test
    fun validateReminder_validReminder() {
        val tmpReminder = createReminder(false)

        val result = saveReminderViewModel.validateEnteredData(tmpReminder)

        assertThat(result, `is`(true))
    }
}