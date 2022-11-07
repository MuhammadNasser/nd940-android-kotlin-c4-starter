package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//   (DONE) TODO: Add testing implementation to the RemindersLocalRepository.kt

    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var remindersDao: RemindersDao
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun setup() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        remindersDao = remindersDatabase.reminderDao()

        remindersLocalRepository = RemindersLocalRepository(remindersDao)
    }

    @After
    fun closeDatabase() {
        remindersDatabase.close()
    }

    private fun createReminder(): MutableList<ReminderDTO> {

        val remindersList: MutableList<ReminderDTO> = ArrayList()

        for (i in 0..10) {
            remindersList.add(
                ReminderDTO(
                    "Reminder $i",
                    "Description for reminder $i",
                    "Location name for reminder $i",
                    i.toDouble(),
                    i.toDouble(),
                    "id $i"
                )
            )
        }
        return remindersList
    }

    private fun checkReminder(reminder1: ReminderDTO, reminder2: ReminderDTO): Boolean {
        return reminder1 == reminder2
    }

    @Test
    fun insertAllRemindersInDatabase_getReminderById_returnTheSame() = runBlockingTest {
        val testRemindersList = createReminder()

        for (reminderDTO in testRemindersList) {
            remindersDatabase.reminderDao().saveReminder(reminderDTO)
        }

        val getAllReminders = remindersDao.getReminders()
        for (i in 0..10) {
            val result = checkReminder(getAllReminders[i], testRemindersList[i])
            assertThat(result, `is`(true))
        }
    }

    @Test
    fun dataNotFound_byPassingInvalidId() = runBlockingTest {
        val testReminderDTO = remindersLocalRepository.getReminder("Invalid Id")

        val error = testReminderDTO as Result.Error

        assertThat(error.message, `is`("Reminder not found!"))
    }
}