package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // (DONE) TODO: provide testing to the RemindersListViewModel and its live data objects

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var remindersFakeRepository: FakeDataSource


    @Before
    fun setupViewModel() {
        stopKoin()

        remindersFakeRepository = FakeDataSource()

        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(), remindersFakeRepository
        )
    }

    @Test
    fun loadReminders_loading() = runBlockingTest {
        mainCoroutineRule.stop() // stop the dispatcher
        remindersListViewModel.loadReminders()

        MatcherAssert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true)
        )

        mainCoroutineRule.reRun() // start the dispatcher

        MatcherAssert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(false)
        )
    }

    fun loadRemindersWhenUnavailable() = runBlockingTest {
        remindersFakeRepository.loadData = false

        remindersListViewModel.loadReminders()

        MatcherAssert.assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(), CoreMatchers.`is`("Fail to load data")
        )
    }
}