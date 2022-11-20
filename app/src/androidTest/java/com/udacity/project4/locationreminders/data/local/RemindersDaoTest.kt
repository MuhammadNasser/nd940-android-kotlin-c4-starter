package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    (DONE) TODO: Add testing implementation to the RemindersDao.kt

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    private fun createReminder(): ReminderDTO {
        return ReminderDTO(
            "Title",
            "Description",
            "Location name",
            1.0,
            1.1,
            "id"
        )
    }

    private fun checkReminder(reminder1: ReminderDTO, reminder2: ReminderDTO): Boolean {
        return reminder1 == reminder2
    }

    @Test
    fun insertReminderInDatabase_getReminderById_returnTheSame() = runBlockingTest {
        val testReminderDTO = createReminder()

        database.reminderDao().saveReminder(testReminderDTO)

        val result = database.reminderDao().getReminderById("id")?.let {
            checkReminder(testReminderDTO, it)
        }

        assertThat(result, `is`(true))
    }
}