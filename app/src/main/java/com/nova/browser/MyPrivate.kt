package com.nova.browser

import androidx.room.*

// My Private: shortcuts saved ONLY inside Nova, never on the phone home screen.
@Entity(tableName = "my_private")
data class MyPrivateItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String,
    val faviconUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface MyPrivateDao {
    @Query("SELECT * FROM my_private ORDER BY createdAt DESC")
    fun getAll(): List<MyPrivateItem>

    @Query("SELECT * FROM my_private WHERE name LIKE '%' || :q || '%' OR url LIKE '%' || :q || '%' ORDER BY createdAt DESC")
    fun search(q: String): List<MyPrivateItem>

    @Insert
    fun insert(item: MyPrivateItem)

    @Query("DELETE FROM my_private WHERE id = :id")
    fun delete(id: Int)
}

@Database(entities = [MyPrivateItem::class], version = 1, exportSchema = false)
abstract class NovaDatabase : RoomDatabase() {
    abstract fun myPrivateDao(): MyPrivateDao
}
