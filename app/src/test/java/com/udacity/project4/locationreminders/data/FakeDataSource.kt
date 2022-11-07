package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

//    (DONE) TODO: Create a fake data source to act as a double to the real data source

    var dataSource: HashMap<String, ReminderDTO> = HashMap()
    var loadData: Boolean = true

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return try {
            if (loadData) {
                val tmpList: MutableList<ReminderDTO> = ArrayList()
                dataSource.forEach {
                    tmpList.add(it.value)
                }
                Result.Success(tmpList)
            } else {
                throw Exception("Fail to load data")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        dataSource[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return try {
            if (loadData) {
                dataSource[id]?.let {
                    Result.Success(it)
                }
                return Result.Error("Item not fount")
            } else {
                throw Exception("Fail to load data")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
        dataSource.clear()
    }
}